package com.example.loft.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.loft.R
import java.io.IOException

class HomeFragment : Fragment() {
    companion object {
        @JvmStatic private val LOGGER_TAG = "HomeFragment"
        @JvmStatic private val SELECT_IMAGE_INTENT_TITLE = "Select a Image"
        @JvmStatic private val SELECT_IMAGE_REQUEST_CODE = 7
        @JvmStatic private val INTENT_TYPE_IMAGE = "image/*"
    }

    private lateinit var selectedImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        setupImageSelection(rootView)
        return rootView
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
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
        try {
            val source  = ImageDecoder.createSource(
                requireActivity().contentResolver,
                uri
            )
            val bitmap = ImageDecoder.decodeBitmap(source)
            displaySelectedImage(bitmap)

        } catch (ioException: IOException) {
            Log.e(
                LOGGER_TAG,
                "Unable to select image, because of exception ${ioException.message}"
            )
        }
    }

    private fun displaySelectedImage(bitmap: Bitmap) {
        val selectedImageAsDrawable = BitmapDrawable(resources, bitmap)
        selectedImageView.setImageDrawable(selectedImageAsDrawable)
    }

    private fun setupImageSelection(rootView: View) {
        val selectButton = rootView.findViewById<Button>(R.id.select_button)
        selectedImageView = rootView.findViewById(R.id.selected_image)

        selectButton.setOnClickListener {
            val intent = Intent()
            intent.type = INTENT_TYPE_IMAGE
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, SELECT_IMAGE_INTENT_TITLE),
                SELECT_IMAGE_REQUEST_CODE
            )
        }
    }
}
