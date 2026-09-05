package com.smartattend.ai.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder

class FaceEmbeddingManager(context: Context) {
    private val modelBuffer: ByteBuffer
    private var interpreter: Interpreter? = null
    private val inputSize = 160  // FaceNet input size
    private val outputSize = 128 // Embedding size

    init {
        // Load TFLite model for face embeddings
        modelBuffer = loadModelFile(context, "face_embedding_model.tflite")
        val options = Interpreter.Options()
        try {
            if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                options.addDelegate(org.tensorflow.lite.gpu.GpuDelegate())
            }
        } catch (e: Exception) {
            // GPU delegate not available, fallback to CPU
        }
        interpreter = Interpreter(modelBuffer, options)
    }

    /**
     * Generate face embedding from bitmap
     * @param bitmap Face image to process
     * @return FloatArray of embeddings (128 dimensions for FaceNet)
     */
    fun generateEmbedding(bitmap: Bitmap): FloatArray {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val inputBuffer = bitmapToByteBuffer(resizedBitmap)
        val output = Array(1) { FloatArray(outputSize) }
        
        interpreter?.run(inputBuffer, output)
        
        return output[0]
    }

    /**
     * Convert bitmap to ByteBuffer for TFLite input
     */
    private fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
        buffer.order(ByteOrder.nativeOrder())
        
        val pixels = IntArray(inputSize * inputSize)
        bitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)
        
        for (pixel in pixels) {
            buffer.putFloat(((pixel shr 16) and 0xFF) / 255f)
            buffer.putFloat(((pixel shr 8) and 0xFF) / 255f)
            buffer.putFloat((pixel and 0xFF) / 255f)
        }
        
        buffer.rewind()
        return buffer
    }

    /**
     * Load model file from assets
     */
    private fun loadModelFile(context: Context, filename: String): ByteBuffer {
        val inputStream = context.assets.open(filename)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        
        return ByteBuffer.wrap(buffer)
    }

    fun close() {
        interpreter?.close()
    }
}
