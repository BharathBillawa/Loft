package com.example.loft.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.transition.TransitionManager
import com.example.classifier.Recognition
import com.example.classifier.TFImageClassifier
import com.example.loft.R

class HomeFragment : Fragment() {
    companion object {
        @JvmStatic private val LOGGER_TAG = "HomeFragment"
        @JvmStatic private val SELECT_IMAGE_REQUEST_CODE = 7
        @JvmStatic private val INTENT_TYPE_IMAGE = "image/*"
        @JvmStatic private val BUTTON_VERTICAL_BIAS_WITH_IMAGE = 0.7f
        @JvmStatic private val BUTTON_HORIZONTAL_BIAS_WITH_IMAGE = 0.7f
        @JvmStatic private val BUTTON__DEFAULT_VERTICAL_BIAS = 0.3f
        @JvmStatic private val BUTTON_DEFAULT_HORIZONTAL_BIAS = 0.5f
        @JvmStatic private val IMAGE_WIDTH = 224
        @JvmStatic private val IMAGE_HEIGHT = 224
    }

    private lateinit var selectButton: Button
    private lateinit var resetButton: Button
    private lateinit var selectedImageView: ImageView
    private lateinit var rootConstraintLayout: ConstraintLayout
    private lateinit var tfClassifier: TFImageClassifier

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        setupImageSelection(rootView)
        setupImageReset(rootView)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        tfClassifier = TFImageClassifier(requireContext(), IMAGE_WIDTH, IMAGE_HEIGHT)
    }

    private fun setupImageReset(rootView: View) {
        resetButton = rootView.findViewById<Button>(R.id.reset_button)
        rootConstraintLayout = rootView.findViewById(R.id.root_constraint_layout)

        resetButton.setOnClickListener {
            handleImageReset()
        }
    }

    private fun handleImageReset() {
        TransitionManager.beginDelayedTransition(rootConstraintLayout)
        selectedImageView.setImageDrawable(null)
        resetButton.visibility = View.GONE
        val layoutParams  = selectButton.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.verticalBias = BUTTON__DEFAULT_VERTICAL_BIAS
        layoutParams.horizontalBias = BUTTON_DEFAULT_HORIZONTAL_BIAS
        selectButton.layoutParams = layoutParams
        selectButton.text = getString(R.string.select_button_label)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (
            resultCode == Activity.RESULT_OK
            && requestCode == SELECT_IMAGE_REQUEST_CODE
            && data != null
        ) {
            val uri = data.data
            if (uri != null) {
                handleImageSelection(uri)
            }
        }
    }

    private fun handleImageSelection(uri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        //Using deprecated method here ImageDecoder method was yielding Config#HARDWARE bitmaps.

        displaySelectedImage(bitmap)
        startImageClassification(bitmap)
    }

    private fun startImageClassification(bitmap: Bitmap) {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, false)
        val results: Collection<Recognition?>? = tfClassifier.doRecognize(scaledBitmap)
        Log.d(
            LOGGER_TAG,
            "Got the following results from Tensorflow: $results"
        )
    }

    private fun displaySelectedImage(bitmap: Bitmap) {
        val selectedImageAsDrawable = BitmapDrawable(resources, bitmap)
        selectedImageView.setImageDrawable(selectedImageAsDrawable)

        TransitionManager.beginDelayedTransition(rootConstraintLayout)
        resetButton.visibility = View.VISIBLE
        val layoutParams  = selectButton.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.verticalBias = BUTTON_VERTICAL_BIAS_WITH_IMAGE
        layoutParams.horizontalBias = BUTTON_HORIZONTAL_BIAS_WITH_IMAGE
        selectButton.layoutParams = layoutParams
        selectButton.text = getString(R.string.select_new_button_label)
    }

    private fun setupImageSelection(rootView: View) {
        selectButton = rootView.findViewById(R.id.select_button)
        selectedImageView = rootView.findViewById(R.id.selected_image)

        selectButton.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            intent.type = INTENT_TYPE_IMAGE
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE)
        }
    }
}
