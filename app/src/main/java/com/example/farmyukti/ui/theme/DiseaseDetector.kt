import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class DiseaseDetector(context: Context) {

    private var interpreter: Interpreter
    private var labels: List<String>

    init {
        // 1. Load the raw model file using standard Android Java I/O
        val modelBuffer = loadModelFile(context, "farmyukti_offline_model.tflite")
        interpreter = Interpreter(modelBuffer)

        // 2. Load labels manually
        labels = context.assets.open("labels.txt").bufferedReader().readLines()
    }

    // Helper function to load file into memory without the buggy Support Library
    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun analyzeImage(bitmap: Bitmap): String {
        // 1. Resize image using Native Android
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        // 2. Manually allocate memory buffer (224x224 pixels * 3 colors * 4 bytes per float)
        val inputBuffer = ByteBuffer.allocateDirect(1 * 224 * 224 * 3 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())

        // 3. Extract pixels from the image
        val intValues = IntArray(224 * 224)
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)

        // 4. The Math: Convert standard pixels (0-255) to AI Tensors (0.0 - 1.0)
        var pixel = 0
        for (i in 0 until 224) {
            for (j in 0 until 224) {
                val value = intValues[pixel++]
                // Extract Red, Green, Blue and normalize by dividing by 255.0
                inputBuffer.putFloat(((value shr 16) and 0xFF) / 255.0f) // Red
                inputBuffer.putFloat(((value shr 8) and 0xFF) / 255.0f)  // Green
                inputBuffer.putFloat((value and 0xFF) / 255.0f)          // Blue
            }
        }

        // 5. Prepare a container for the output (1 row, 38 columns for diseases)
        val outputBuffer = Array(1) { FloatArray(38) }

        // 6. RUN THE INFERENCE
        interpreter.run(inputBuffer, outputBuffer)

        // 7. Find the highest probability
        val probabilities = outputBuffer[0]
        var maxIndex = 0
        var maxConfidence = 0f

        for (i in probabilities.indices) {
            if (probabilities[i] > maxConfidence) {
                maxConfidence = probabilities[i]
                maxIndex = i
            }
        }

        // 8. Return formatted text
        val confidencePercentage = String.format("%.1f", maxConfidence * 100)
        return "${labels[maxIndex]} ($confidencePercentage%)"
    }

    fun close() {
        interpreter.close()
    }
}