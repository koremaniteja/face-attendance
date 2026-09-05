package com.smartattend.ai.data.model

/**
 * Student data class for local state management
 */
data class Student(
    val name: String,
    val roll: String,
    val present: Boolean = false
)

/**
 * Student with face embedding for recognition
 * Used when processing camera frames for attendance
 */
data class StudentWithEmbedding(
    val id: String,                          // Unique student ID
    val name: String,
    val roll: String,
    val faceEmbedding: FloatArray,           // Face embedding (128 dimensions for FaceNet)
    val present: Boolean = false,
    val matchConfidence: Float = 0f          // How confident the match is (0-1)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StudentWithEmbedding

        if (id != other.id) return false
        if (name != other.name) return false
        if (roll != other.roll) return false
        if (!faceEmbedding.contentEquals(other.faceEmbedding)) return false
        if (present != other.present) return false
        if (matchConfidence != other.matchConfidence) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + roll.hashCode()
        result = 31 * result + faceEmbedding.contentHashCode()
        result = 31 * result + present.hashCode()
        result = 31 * result + matchConfidence.hashCode()
        return result
    }
}

/**
 * Attendance record for a student in a class
 */
data class AttendanceRecord(
    val id: String,                          // Unique record ID
    val studentId: String,
    val classId: String,
    val date: String,                        // Date in YYYY-MM-DD format
    val status: AttendanceStatus,            // PRESENT, ABSENT, LATE
    val recognitionConfidence: Float = 0f,   // How confident the face match was
    val timestamp: Long = System.currentTimeMillis()
)

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    LATE
}

/**
 * Class session data
 */
data class ClassSession(
    val id: String,
    val name: String,
    val code: String,
    val semester: String,
    val enrolledStudents: List<String>,      // List of student IDs
    val createdAt: Long = System.currentTimeMillis()
)
