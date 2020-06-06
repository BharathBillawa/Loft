package com.example.classifier

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.PriorityQueue
import kotlin.collections.ArrayList
import kotlin.experimental.and

class TFHelper {
    companion object {
        @JvmStatic private val RESULTS_TO_SHOW = 3

        /**
         * Memory-map the model file in Assets.
         */
        @Throws(IOException::class)
        fun loadModelFile(
            context: Context,
            modelFile: String?
        ): MappedByteBuffer {
            val fileDescriptor = context.assets.openFd(modelFile!!)
            val inputStream =
                FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            return fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                startOffset,
                declaredLength
            )
        }

        fun readLabels(
            context: Context,
            labelsFile: String
        ): List<String> {
            val assetManager = context.assets
            val result = ArrayList<String>()
            try {
                assetManager.open(labelsFile).use { `is` ->
                    BufferedReader(InputStreamReader(`is`)).use { br ->
                        var line: String? = br.readLine()
                        while (line != null) {
                            result.add(line)
                            line = br.readLine()
                        }
                        return result
                    }
                }
            } catch (ex: IOException) {
                throw IllegalStateException("Cannot read labels from $labelsFile")
            }
        }

        /**
         * Find the best classifications.
         */
        fun getBestResults(
            labelProbArray: Array<ByteArray>,
            labelList: List<String?>
        ): Collection<Recognition> {
            val sortedLabels: PriorityQueue<Recognition> =
                PriorityQueue(RESULTS_TO_SHOW,
                    Comparator { lhs, rhs ->
                        lhs.getConfidence().compareTo(rhs.getConfidence())
                    }
                )

            for (i in labelList.indices) {
                val r = Recognition(
                    i.toString(),
                    labelList[i], (labelProbArray[0][i] and 0xff.toByte()) / 255.0f
                )
                sortedLabels.add(r)
                if (r.getConfidence() > 0) {
                    Log.d("ImageRecognition", r.toString())
                }
                if (sortedLabels.size > RESULTS_TO_SHOW) {
                    sortedLabels.poll()
                }
            }

            val results: ArrayList<Recognition> = ArrayList(RESULTS_TO_SHOW)
            for (r in sortedLabels) {
                results.add(0, r)
            }
            return results
        }

        /** Writes Image data into a `ByteBuffer`.  */
        fun convertBitmapToByteBuffer(
            bitmap: Bitmap,
            intValues: IntArray,
            imgData: ByteBuffer?
        ) {
            if (imgData == null) {
                return
            }
            imgData.rewind()
            bitmap.getPixels(
                intValues, 0, bitmap.width, 0, 0,
                bitmap.width, bitmap.height
            )
            // Encode the image pixels into a byte buffer representation matching the expected
            // input of the Tensorflow model
            var pixel = 0
            for (i in 0 until bitmap.width) {
                for (j in 0 until bitmap.height) {
                    val `val` = intValues[pixel++]
                    imgData.put((`val` shr 16 and 0xFF).toByte())
                    imgData.put((`val` shr 8 and 0xFF).toByte())
                    imgData.put((`val` and 0xFF).toByte())
                }
            }
        }
    }
}
