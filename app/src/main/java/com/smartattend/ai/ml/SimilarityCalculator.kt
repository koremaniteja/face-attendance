package com.smartattend.ai.ml

import kotlin.math.sqrt

class SimilarityCalculator {
    
    companion object {
        // Threshold for face matching (0-1 scale, higher = more similar)
        // 0.6 is typical for FaceNet models
        const val FACE_MATCH_THRESHOLD = 0.6f
    }

    /**
     * Calculate Euclidean distance between two embeddings
     * Lower distance = more similar faces
     */
    fun euclideanDistance(embedding1: FloatArray, embedding2: FloatArray): Float {
        if (embedding1.size != embedding2.size) {
            throw IllegalArgumentException("Embeddings must have same size")
        }
        
        var sumSquare = 0f
        for (i in embedding1.indices) {
            val diff = embedding1[i] - embedding2[i]
            sumSquare += diff * diff
        }
        
        return sqrt(sumSquare)
    }

    /**
     * Calculate cosine similarity between two embeddings
     * Range: -1 to 1 (1 = identical, -1 = opposite)
     */
    fun cosineSimilarity(embedding1: FloatArray, embedding2: FloatArray): Float {
        if (embedding1.size != embedding2.size) {
            throw IllegalArgumentException("Embeddings must have same size")
        }
        
        var dotProduct = 0f
        var norm1 = 0f
        var norm2 = 0f
        
        for (i in embedding1.indices) {
            dotProduct += embedding1[i] * embedding2[i]
            norm1 += embedding1[i] * embedding1[i]
            norm2 += embedding2[i] * embedding2[i]
        }
        
        norm1 = sqrt(norm1)
        norm2 = sqrt(norm2)
        
        if (norm1 == 0f || norm2 == 0f) return 0f
        
        return dotProduct / (norm1 * norm2)
    }

    /**
     * Check if two faces match based on embedding similarity
     * @param embedding1 Captured face embedding
     * @param embedding2 Stored face embedding
     * @return true if similarity exceeds threshold
     */
    fun isFaceMatch(embedding1: FloatArray, embedding2: FloatArray): Boolean {
        val similarity = cosineSimilarity(embedding1, embedding2)
        return similarity > FACE_MATCH_THRESHOLD
    }

    /**
     * Find best matching embedding from list
     * @param capturedEmbedding Face captured during scan
     * @param studentEmbeddings List of stored student embeddings
     * @return Pair of (studentIndex, similarity) or null if no match
     */
    fun findBestMatch(
        capturedEmbedding: FloatArray,
        studentEmbeddings: List<FloatArray>
    ): Pair<Int, Float>? {
        var bestIdx = -1
        var bestSimilarity = FACE_MATCH_THRESHOLD
        
        studentEmbeddings.forEachIndexed { idx, embedding ->
            val similarity = cosineSimilarity(capturedEmbedding, embedding)
            if (similarity > bestSimilarity) {
                bestSimilarity = similarity
                bestIdx = idx
            }
        }
        
        return if (bestIdx >= 0) Pair(bestIdx, bestSimilarity) else null
    }

    /**
     * Normalize embedding vector
     */
    fun normalizeEmbedding(embedding: FloatArray): FloatArray {
        var norm = 0f
        for (value in embedding) {
            norm += value * value
        }
        norm = sqrt(norm)
        
        if (norm == 0f) return embedding
        
        return FloatArray(embedding.size) { i -> embedding[i] / norm }
    }
}
