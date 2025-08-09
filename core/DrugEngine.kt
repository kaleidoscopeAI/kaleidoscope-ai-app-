package com.kaleidoscopeai.core

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp

class DrugEngine(private val context: Context) {
    private val tag = "DrugEngine"
    private var esm2Interpreter: Interpreter? = null
    private var molformerInterpreter: Interpreter? = null
    private val esm2ModelPath = "models/esm2_quant.tflite"
    private val molformerModelPath = "models/molformer.tflite"

    init {
        try {
            esm2Interpreter = loadModel(esm2ModelPath)
            molformerInterpreter = loadModel(molformerModelPath)
            Log.i(tag, "TFLite models initialized successfully")
        } catch (e: Exception) {
            Log.e(tag, "Failed to initialize TFLite models: ${e.message}", e)
            throw IllegalStateException("TFLite initialization failed")
        }
    }

    private fun loadModel(modelPath: String): Interpreter {
        val modelFile = File(context.filesDir, modelPath)
        if (!modelFile.exists()) {
            throw IllegalStateException("Model file not found at ${modelFile.absolutePath}")
        }
        return Interpreter(modelFile)
    }

    fun predictProteinEmbedding(sequence: String): FloatArray {
        try {
            val maxLength = 512
            val inputIds = tokenizeProtein(sequence, maxLength)
            val inputBuffer = ByteBuffer.allocateDirect(maxLength * 4).apply {
                order(ByteOrder.nativeOrder())
                inputIds.forEach { putInt(it) }
            }
            val outputShape = intArrayOf(1, 1280)
            val outputBuffer = TensorBuffer.createFixedSize(outputShape, org.tensorflow.lite.DataType.FLOAT32)
            esm2Interpreter?.run(inputBuffer, outputBuffer.buffer)
            return outputBuffer.floatArray
        } catch (e: Exception) {
            Log.e(tag, "Protein embedding error: ${e.message}", e)
            return FloatArray(1280) { 0f }
        }
    }

    fun generateHypothesis(compoundSmiles: String): String {
        try {
            val inputVector = smilesToFeatureVector(compoundSmiles)
            val inputBuffer = ByteBuffer.allocateDirect(inputVector.size * 4).apply {
                order(ByteOrder.nativeOrder())
                inputVector.forEach { putFloat(it) }
            }
            val outputShape = intArrayOf(1, 1)
            val outputBuffer = TensorBuffer.createFixedSize(outputShape, org.tensorflow.lite.DataType.FLOAT32)
            molformerInterpreter?.run(inputBuffer, outputBuffer.buffer)
            val affinityScore = outputBuffer.floatArray[0]
            return "Predicted binding affinity: ${String.format("%.2f", affinityScore)} (High affinity if > 0.7)"
        } catch (e: Exception) {
            Log.e(tag, "Hypothesis generation error: ${e.message}", e)
            return "Error generating hypothesis"
        }
    }

    private fun tokenizeProtein(sequence: String, maxLength: Int): IntArray {
        val vocab = mapOf('A' to 1, 'C' to 2, 'D' to 3, 'E' to 4, 'F' to 5, 'G' to 6) // Extend as needed
        val tokens = sequence.take(maxLength).map { vocab[it] ?: 0 }.toIntArray()
        return IntArray(maxLength) { if (it < tokens.size) tokens[it] else 0 }
    }

    private fun smilesToFeatureVector(smiles: String): FloatArray {
        val vector = FloatArray(100) { 0f }
        vector[0] = smiles.length.toFloat()
        smiles.forEachIndexed { i, c -> if (i < vector.size - 1) vector[i + 1] = c.code.toFloat() }
        return vector
    }

    fun close() {
        try {
            esm2Interpreter?.close()
            molformerInterpreter?.close()
            Log.i(tag, "TFLite resources released")
        } catch (e: Exception) {
            Log.e(tag, "Error closing TFLite: ${e.message}", e)
        }
    }
}
