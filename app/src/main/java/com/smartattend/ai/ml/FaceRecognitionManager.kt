package com.smartattend.ai.ml

import android.graphics.Bitmap
import com.smartattend.ai.data.model.StudentWithEmbedding

/**
 * Main face recognition manager that orchestrates face detection,
 * embedding generation, and student matching
 */
class FaceRecognitionManager(
    private val embeddingManager: FaceEmbeddingManager,
    private val similarityCalculator: SimilarityCalculator = SimilarityCalculator()
) {

    /**
     * Recognize a student from captured face image
     * @param faceBitmap Detected face from camera
     * @param enrolledStudents List of students with face embeddings
     * @return Recognized student or null if no match found
     */
    fun recognizeStudent(
        faceBitmap: Bitmap,
        enrolledStudents: List<StudentWithEmbedding>
    ): StudentWithEmbedding? {
        // Generate embedding from captured face
        val capturedEmbedding = embeddingManager.generateEmbedding(faceBitmap)
        
        // Extract stored embeddings
        val storedEmbeddings = enrolledStudents.map { it.faceEmbedding }
        
        // Find best matching student
        val match = similarityCalculator.findBestMatch(capturedEmbedding, storedEmbeddings)
        
        return if (match != null) {
            val (studentIdx, similarity) = match
            // Return matched student with confidence score
            enrolledStudents[studentIdx].copy(matchConfidence = similarity)
        } else {
            null
        }
    }

    /**
     * Batch recognize multiple faces (for scanning entire class at once)
     * @param faceBitmaps List of detected faces from camera
     * @param enrolledStudents List of students with face embeddings
     * @return List of recognized students
     */
    fun recognizeMultipleStudents(
        faceBitmaps: List<Bitmap>,
        enrolledStudents: List<StudentWithEmbedding>
    ): List<StudentWithEmbedding> {
        return faceBitmaps.mapNotNull { bitmap ->
            recognizeStudent(bitmap, enrolledStudents)
        }.distinctBy { it.id } // Remove duplicates (same student detected twice)
    }

    /**
     * Enroll a new student by capturing and storing face embedding
     * @param faceBitmap Student's face image
     * @return FaceEmbedding array to store in database
     */
    fun enrollStudentFace(faceBitmap: Bitmap): FloatArray {
        return embeddingManager.generateEmbedding(faceBitmap)
    }

    /**
     * Verify a known student's face (for verification scenarios)
     * @param capturedBitmap Face from camera
     * @param storedEmbedding Student's stored embedding
     * @return true if faces match
     */
    fun verifyFace(capturedBitmap: Bitmap, storedEmbedding: FloatArray): Boolean {
        val capturedEmbedding = embeddingManager.generateEmbedding(capturedBitmap)
        return similarityCalculator.isFaceMatch(capturedEmbedding, storedEmbedding)
    }

    fun close() {
        embeddingManager.close()
    }
}
