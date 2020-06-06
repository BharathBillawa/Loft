package com.example.classifier

/**
 * parameter [id]:  A unique identifier for what has been recognized. Specific to the class,
 * not the instance of the object.
 * parameter [title]: Display name for the recognition.
 * parameter [confidence]: A sortable score for how good the recognition is relative to others.
 * Higher should be better.
 */
class Recognition(
    private val id: String? ,
    private val title: String?,
    private val confidence: Float?
) {

    fun getId(): String? {
        return id
    }

    fun getTitle(): String? {
        return title
    }

    fun getConfidence(): Float {
        return confidence ?: 0f
    }

    override fun toString(): String {
        var resultString = ""
        if (id != null) {
            resultString += "[$id]"
        }
        if (title != null) {
            resultString += "$title"
        }
        if (confidence != null) {
            resultString += String.format("(%.1f%%) ", confidence * 100.0f)
        }
        return resultString.trim { it <= ' ' }
    }
}
