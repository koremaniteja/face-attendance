package com.smartattend.ai.data.repository

import com.smartattend.ai.data.firebase.FirestoreDataSource
import com.smartattend.ai.data.model.AttendanceRecord
import com.smartattend.ai.data.model.StudentWithEmbedding

/**
 * Repository for attendance operations
 * Bridges between UI and data sources
 */
class AttendanceRepository(private val firestoreDataSource: FirestoreDataSource) {

    /**
     * Get all enrolled students for a class with their face embeddings
     */
    suspend fun getEnrolledStudents(classId: String): List<StudentWithEmbedding> {
        return firestoreDataSource.getEnrolledStudentsWithEmbeddings(classId)
    }

    /**
     * Mark a student as present when face is recognized
     */
    suspend fun markStudentPresent(
        studentId: String,
        classId: String,
        date: String,
        confidence: Float
    ): Boolean {
        return firestoreDataSource.markStudentAttendance(
            studentId,
            classId,
            date,
            confidence
        )
    }

    /**
     * Get attendance history for a student
     */
    suspend fun getStudentAttendanceHistory(
        studentId: String,
        classId: String
    ): List<AttendanceRecord> {
        return firestoreDataSource.getStudentAttendance(studentId, classId)
    }

    /**
     * Get attendance summary for a class on a specific date
     */
    suspend fun getClassAttendanceSummary(
        classId: String,
        date: String
    ): Map<String, Boolean> {
        return firestoreDataSource.getClassAttendanceSummary(classId, date)
    }
}
