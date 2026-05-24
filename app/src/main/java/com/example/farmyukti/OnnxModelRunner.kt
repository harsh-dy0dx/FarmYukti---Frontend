package com.example.farmyukti

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import java.nio.FloatBuffer

class OnnxModelRunner(context: Context, modelName: String) {
    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession

    // Standard labels for the 22-crop recommendation dataset.
    // Ensure this array matches the exact order your model was trained on!
    private val cropLabels = listOf(
        "rice", "maize", "chickpea", "kidneybeans", "pigeonpeas",
        "mothbeans", "mungbean", "blackgram", "lentil", "pomegranate",
        "banana", "mango", "grapes", "watermelon", "muskmelon", "apple",
        "orange", "papaya", "coconut", "cotton", "jute", "coffee"
    )

    init {
        // Load the .onnx file bundled in your assets folder
        val modelBytes = context.assets.open(modelName).readBytes()
        session = env.createSession(modelBytes)
    }

    fun predictCrop(features: FloatArray): String {
        // The dataset uses exactly 7 features: [N, P, K, Temp, Humidity, pH, Rainfall]
        val shape = longArrayOf(1, 7)
        val inputTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(features), shape)
        val inputName = session.inputNames.iterator().next()

        // Run the inference session
        val result = session.run(mapOf(inputName to inputTensor))

        val prediction = try {
            val outputValue = result[0].value
            extractPredictedCrop(outputValue)
        } finally {
            // Clean up C++ allocations immediately to avoid memory leaks
            result.close()
            inputTensor.close()
        }

        return prediction
    }

    private fun extractPredictedCrop(outputValue: Any): String {
        // We must verify the actual data type before attempting any casting.
        return when (outputValue) {
            is Array<*> -> {
                val firstElement = outputValue.firstOrNull()
                when (firstElement) {
                    is String -> {
                        // Fact: The model directly outputs the class name string (e.g., ["rice"])
                        firstElement.replaceFirstChar { it.uppercase() }
                    }
                    is FloatArray -> {
                        // Fallback: If it actually outputs probabilities [1, 22]
                        val probabilities = firstElement
                        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
                        if (maxIndex < cropLabels.size) cropLabels[maxIndex].replaceFirstChar { it.uppercase() } else "Unknown Crop"
                    }
                    is Long -> {
                        // Fallback: If it outputs an index array
                        val maxIndex = firstElement.toInt()
                        if (maxIndex < cropLabels.size) cropLabels[maxIndex].replaceFirstChar { it.uppercase() } else "Unknown Crop"
                    }
                    else -> "Error: Unexpected array content type (${firstElement?.javaClass?.name})"
                }
            }
            is String -> {
                // If the model outputs a raw string without wrapping it in an array
                outputValue.replaceFirstChar { it.uppercase() }
            }
            is LongArray -> {
                // If the model outputs a raw long array of indices
                val maxIndex = outputValue[0].toInt()
                if (maxIndex < cropLabels.size) cropLabels[maxIndex].replaceFirstChar { it.uppercase() } else "Unknown Crop"
            }
            else -> "Error: Unrecognized model output type (${outputValue.javaClass.name})"
        }
    }

    fun close() {
        session.close()
        env.close()
    }
}