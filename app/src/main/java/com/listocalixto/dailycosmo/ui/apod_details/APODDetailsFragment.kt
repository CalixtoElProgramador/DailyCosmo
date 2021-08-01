package com.listocalixto.dailycosmo.ui.apod_details

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import com.bumptech.glide.Glide
import com.listocalixto.dailycosmo.R
import com.listocalixto.dailycosmo.databinding.FragmentApodDetailsBinding

class APODDetailsFragment : Fragment(R.layout.fragment_apod_details) {

    private lateinit var binding: FragmentApodDetailsBinding
    private var copyright: String? = ""
    private var date: String? = ""
    private var explanation: String? = ""
    private var hdurl: String? = ""
    private var mediaType: String? = ""
    private var title: String? = ""
    private var url: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        copyright = requireArguments().getString(COPYRIGHT)
        date = requireArguments().getString(DATE)
        explanation = requireArguments().getString(EXPLANATION)
        hdurl = requireArguments().getString(HDURL)
        mediaType = requireArguments().getString(MEDIA_TYPE)
        title = requireArguments().getString(TITLE)
        url = requireArguments().getString(URL)
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentApodDetailsBinding.bind(view)

        if (hdurl.isNullOrEmpty()) {
            Glide.with(requireContext()).load(url).into(binding.imgApodPicture)
        } else {
            Glide.with(requireContext()).load(hdurl).into(binding.imgApodPicture)
        }

        binding.textApodTitle.text = title
        binding.textApodDate.text = date

        if (explanation.isNullOrEmpty()) {
            binding.textApodExplanation.text = "No description"
        } else {
            binding.textApodExplanation.text = explanation
        }

        if (copyright.isNullOrEmpty()) {
            binding.textApodCopyright.visibility = View.GONE
        } else {
            binding.textApodCopyright.text = "Copyright: $copyright"
        }

        binding.imgApodPicture.setOnClickListener {

            if (binding.imgApodPicture.drawable == null) {
                Toast.makeText(
                    requireContext(),
                    "Espera a que cargue la imagen",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                requireActivity().supportFragmentManager.commit {
                    replace(R.id.nav_host_details, APODImageFragment.newInstance(hdurl, url))
                    addToBackStack("APODDetailsFragment")
                }
            }
        }
    }

    companion object {
        private const val COPYRIGHT = "copyright"
        private const val DATE = "date"
        private const val EXPLANATION = "explanation"
        private const val HDURL = "hdurl"
        private const val MEDIA_TYPE = "media_type"
        private const val TITLE = "title"
        private const val URL = "url"

        fun newInstance(
            copyright: String,
            date: String,
            explanation: String,
            hdUrl: String,
            mediaType: String,
            title: String,
            url: String
        ) = APODDetailsFragment().apply {
            arguments = bundleOf(
                COPYRIGHT to copyright,
                DATE to date,
                EXPLANATION to explanation,
                HDURL to hdUrl,
                MEDIA_TYPE to mediaType,
                TITLE to title,
                URL to url
            )
        }
    }
}