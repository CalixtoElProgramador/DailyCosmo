package com.listocalixto.dailycosmo.ui.apod_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.listocalixto.dailycosmo.R
import com.listocalixto.dailycosmo.databinding.FragmentApodDetailsBinding

class APODDetailsFragment : Fragment(R.layout.fragment_apod_details) {

    /*
    private var copyright: String? = ""
    private var date: String? = ""
    private var explanation: String? = ""
    private var hdurl: String? = ""
    private var mediaType: String? = ""
    private var title: String? = ""
    private var url: String? = ""*/

    private lateinit var binding: FragmentApodDetailsBinding
    private val args by navArgs<APODDetailsFragmentArgs>()

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        //sharedElementEnterTransition = MaterialShapeDrawable()
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        copyright = requireArguments().getString(COPYRIGHT)
        date = requireArguments().getString(DATE)
        explanation = requireArguments().getString(EXPLANATION)
        hdurl = requireArguments().getString(HDURL)
        mediaType = requireArguments().getString(MEDIA_TYPE)
        title = requireArguments().getString(TITLE)
        url = requireArguments().getString(URL)*/

        /*activity?.window?.addFlags((WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS))
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        activity?.window?.statusBarColor = requireActivity().resources.getColor(R.color.colorSecondaryVariant)

    }*/

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentApodDetailsBinding.bind(view)

        activity?.window?.addFlags((WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS))

        if (args.hdurl.isEmpty()) {
            Glide.with(requireContext()).load(args.url).into(binding.imgApodPicture)
        } else {
            Glide.with(requireContext()).load(args.hdurl).into(binding.imgApodPicture)
        }

        binding.textApodTitle.text = args.title
        binding.textApodDate.text = args.date

        if (args.explanation.isEmpty()) {
            binding.textApodExplanation.text = "No description"
        } else {
            binding.textApodExplanation.text = args.explanation
        }

        if (args.copyright.isEmpty()) {
            binding.textApodCopyright.visibility = View.GONE
        } else {
            binding.textApodCopyright.text = "Copyright: ${args.copyright}"
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
                val action = APODDetailsFragmentDirections.actionAPODDetailsFragment2ToAPODImageFragment(
                    args.hdurl,
                    args.title,
                    args.url
                )
                findNavController().navigate(action)
            }
        }
    }

    /*companion object {
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
    }*/
}