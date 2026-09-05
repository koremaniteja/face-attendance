package com.smartattend.ai.ml

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlin.math.abs

/**
 * Manages face detection from camera frames using Google ML Kit
 */
class FaceDetectorManager {

    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()
    )

    /**
     * Detect faces in a bitmap
     * @param bitmap Image to process
     * @return List of detected faces
     */
    suspend fun detectFaces(bitmap: Bitmap): List<Face> {
        return try {
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            val detectedFaces = faceDetector.process(inputImage).result
            inputImage.close()
            detectedFaces
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Extract face region from bitmap
     */
    fun extractFaceRegion(bitmap: Bitmap, face: Face): Bitmap? {
        return try {
            val boundingBox = face.boundingBox
            val width = boundingBox.width().toInt()
            val height = boundingBox.height().toInt()

            // Add padding around face
            val padding = (minOf(width, height) * 0.2).toInt()
            val left = maxOf(0, (boundingBox.left - padding).toInt())
            val top = maxOf(0, (boundingBox.top - padding).toInt())
            val w = minOf(width + padding * 2, bitmap.width - left)
            val h = minOf(height + padding * 2, bitmap.height - top)

            if (w > 0 && h > 0) {
                Bitmap.createBitmap(bitmap, left, top, w, h)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Check if face quality is good enough for recognition
     */
    fun isFaceQualityGood(face: Face): Boolean {
        // Check face size (should be reasonably large in frame)
        val boundingBox = face.boundingBox
        val faceArea = boundingBox.width() * boundingBox.height()
        val minArea = 10000f // Minimum face area in pixels

        // Check head rotation (shouldn't be too tilted)
        val maxRotation = 45f // degrees
        val isNotTooRotated = abs(face.headEulerAngleX) < maxRotation &&
                              abs(face.headEulerAngleY) < maxRotation &&
                              abs(face.headEulerAngleZ) < maxRotation

        return faceArea > minArea && isNotTooRotated
    }

    fun close() {
        faceDetector.close()
    }
}
