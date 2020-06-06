package com.example.classifier

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TFImageClassifier(
    context: Context,
    inputImageWidth: Int,
    inputImageHeight: Int
) {

    companion object {
        @JvmStatic private val TAG = "TFImageClassifier"

        @JvmStatic private val LABELS_FILE = "labels.txt"
        @JvmStatic private val MODEL_FILE = "mobilenet_quant_v1_224.tflite"

        /** Dimensions of inputs.  */
        @JvmStatic private val DIM_BATCH_SIZE = 1
        @JvmStatic private val DIM_PIXEL_SIZE = 3
    }

    private var tfLite: Interpreter? = null // TensorFlow Lite engine


    private lateinit var labels: List<String> //Labels for categories that the TensorFlow model is trained for.
    private lateinit var imgData: ByteBuffer // Cache to hold image data.
    private lateinit var confidencePerLabel: Array<ByteArray> // Inference results (Tensorflow Lite output).
    private lateinit var intValues: IntArray // Pre-allocated buffer for intermediate bitmap pixels.

    init {
        tfLite = Interpreter(TFHelper.loadModelFile(context, MODEL_FILE))
        labels = TFHelper.readLabels(context, LABELS_FILE)
        imgData = ByteBuffer.allocateDirect(
            DIM_BATCH_SIZE * inputImageWidth * inputImageHeight * DIM_PIXEL_SIZE
        )
        imgData.order(ByteOrder.nativeOrder())
        confidencePerLabel = Array(1) { ByteArray(labels.size) }
        intValues = IntArray(inputImageWidth * inputImageHeight)
    }

    /**
     * Clean up the resources used by the classifier.
     */
    fun destroyClassifier() {
        tfLite?.close()
    }


    /**
     * @param image Bitmap containing the image to be classified. The image can be
     * of any size, but preprocessing might occur to resize it to the
     * format expected by the classification process, which can be time
     * and power consuming.
     */
    fun doRecognize(image: Bitmap): Collection<Recognition?>? {
        TFHelper.convertBitmapToByteBuffer(image, intValues, imgData)
        val startTime = SystemClock.uptimeMillis()
        // Here's where the magic happens!!!
        tfLite?.run(imgData, confidencePerLabel)
        val endTime = SystemClock.uptimeMillis()
        Log.d(
            TAG,
            "Timecost to run model inference: " + (endTime - startTime).toString()
        )

        // Get the results with the highest confidence and map them to their labels
        return TFHelper.getBestResults(confidencePerLabel, labels)
    }
}
