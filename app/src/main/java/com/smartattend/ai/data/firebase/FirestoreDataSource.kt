package com.smartattend.ai.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smartattend.ai.data.model.AttendanceRecord
import com.smartattend.ai.data.model.AttendanceStatus
import com.smartattend.ai.data.model.ClassSession
import com.smartattend.ai.data.model.StudentWithEmbedding
import kotlinx.coroutines.tasks.await

class FirestoreDataSource {
    private val db: FirebaseFirestore = Firebase.firestore

    /**
     * Save face embedding when student is enrolled
     */
    suspend fun saveStudentWithFaceEmbedding(
        studentId: String,
        name: String,
        roll: String,
        faceEmbedding: FloatArray,
        classId: String
    ) {
        val studentData = mapOf(
            "name" to name,
            "roll" to roll,
            "faceEmbedding" to faceEmbedding.toList(), // Store as list for Firestore
            "enrolledAt" to System.currentTimeMillis()
        )
        
        db.collection("students")
            .document(studentId)
            .set(studentData)
            .await()
    }

    /**
     * Fetch all enrolled students for a class with their face embeddings
     */
    suspend fun getEnrolledStudentsWithEmbeddings(classId: String): List<StudentWithEmbedding> {
        return try {
            val snapshot = db.collection("classes")
                .document(classId)
                .collection("students")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val embedding = (doc["faceEmbedding"] as? List<*>)
                    ?.mapNotNull { it as? Double }
                    ?.map { it.toFloat() }
                    ?.toFloatArray()

                if (embedding != null) {
                    StudentWithEmbedding(
                        id = doc.id,
                        name = doc["name"] as? String ?: "",
                        roll = doc["roll"] as? String ?: "",
                        faceEmbedding = embedding
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Mark a student as present by saving attendance record
     * Only called when face is recognized during scan
     */
    suspend fun markStudentAttendance(
        studentId: String,
        classId: String,
        date: String,
        confidence: Float
    ): Boolean {
        return try {
            val attendanceId = "${studentId}_${classId}_${date}"
            val attendanceData = mapOf(
                "studentId" to studentId,
                "classId" to classId,
                "date" to date,
                "status" to AttendanceStatus.PRESENT.name,
                "recognitionConfidence" to confidence,
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("attendance")
                .document(attendanceId)
                .set(attendanceData)
                .await()

            // Also update student record in class
            db.collection("classes")
                .document(classId)
                .collection("students")
                .document(studentId)
                .update("lastMarkedPresent", System.currentTimeMillis())
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get attendance records for a student
     */
    suspend fun getStudentAttendance(
        studentId: String,
        classId: String
    ): List<AttendanceRecord> {
        return try {
            val snapshot = db.collection("attendance")
                .whereEqualTo("studentId", studentId)
                .whereEqualTo("classId", classId)
                .orderBy("date")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                AttendanceRecord(
                    id = doc.id,
                    studentId = doc["studentId"] as? String ?: "",
                    classId = doc["classId"] as? String ?: "",
                    date = doc["date"] as? String ?: "",
                    status = AttendanceStatus.valueOf(doc["status"] as? String ?: "ABSENT"),
                    recognitionConfidence = (doc["recognitionConfidence"] as? Number)?.toFloat() ?: 0f,
                    timestamp = (doc["timestamp"] as? Number)?.toLong() ?: 0L
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Get attendance summary for a class on a specific date
     */
    suspend fun getClassAttendanceSummary(
        classId: String,
        date: String
    ): Map<String, Boolean> {
        return try {
            val snapshot = db.collection("attendance")
                .whereEqualTo("classId", classId)
                .whereEqualTo("date", date)
                .whereEqualTo("status", AttendanceStatus.PRESENT.name)
                .get()
                .await()

            val presentStudents = snapshot.documents.mapNotNull { doc ->
                doc["studentId"] as? String
            }.toSet()

            presentStudents.associateWith { true }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    /**
     * Batch mark multiple students as present (from single scan session)
     */
    suspend fun batchMarkAttendance(
        attendanceRecords: List<AttendanceRecord>
    ): Boolean {
        return try {
            val batch = db.batch()

            for (record in attendanceRecords) {
                val attendanceRef = db.collection("attendance").document(record.id)
                val data = mapOf(
                    "studentId" to record.studentId,
                    "classId" to record.classId,
                    "date" to record.date,
                    "status" to record.status.name,
                    "recognitionConfidence" to record.recognitionConfidence,
                    "timestamp" to record.timestamp
                )
                batch.set(attendanceRef, data)
            }

            batch.commit().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Create a class session
     */
    suspend fun createClass(
        classId: String,
        name: String,
        code: String,
        semester: String
    ): Boolean {
        return try {
            val classData = mapOf(
                "name" to name,
                "code" to code,
                "semester" to semester,
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("classes")
                .document(classId)
                .set(classData)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }
}
