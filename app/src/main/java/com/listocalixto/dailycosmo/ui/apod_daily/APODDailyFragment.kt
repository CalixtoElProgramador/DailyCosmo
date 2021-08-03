package com.listocalixto.dailycosmo.ui.apod_daily

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.listocalixto.dailycosmo.R
import com.listocalixto.dailycosmo.data.local.AppDatabase
import com.listocalixto.dailycosmo.data.local.LocalAPODDataSource
import com.listocalixto.dailycosmo.data.remote.apod.RemoteAPODDataSource
import com.listocalixto.dailycosmo.databinding.FragmentApodDailyBinding
import com.listocalixto.dailycosmo.presentation.apod.APODViewModel
import com.listocalixto.dailycosmo.presentation.apod.APODViewModelFactory
import com.listocalixto.dailycosmo.repository.apod.APODRepositoryImpl
import com.listocalixto.dailycosmo.repository.apod.RetrofitClient
import com.listocalixto.dailycosmo.ui.apod.DataStoreViewModel
import com.listocalixto.dailycosmo.ui.apod_daily.adapter.ViewPagerAdapter
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.listocalixto.dailycosmo.core.Result
import com.listocalixto.dailycosmo.data.model.APOD
import com.listocalixto.dailycosmo.databinding.ItemApodDailyBinding
import com.listocalixto.dailycosmo.ui.apod_details.APODDetailsFragmentDirections
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class APODDailyFragment : Fragment(R.layout.fragment_apod_daily),
    ViewPagerAdapter.OnImageAPODClickListener {

    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val viewModel by viewModels<APODViewModel> {
        APODViewModelFactory(
            APODRepositoryImpl(
                RemoteAPODDataSource(RetrofitClient.webservice),
                LocalAPODDataSource(AppDatabase.getDatabase(requireContext()).apodDao())
            )
        )
    }

    private var endDate: Calendar = Calendar.getInstance()
    private var startDate: Calendar = Calendar.getInstance().apply {
        set(
            endDate.get(Calendar.YEAR),
            endDate.get(Calendar.MONTH),
            endDate.get(Calendar.DATE)
        )
        add(Calendar.DATE, -10)
    }

    private lateinit var binding: FragmentApodDailyBinding
    private lateinit var dataStoreViewModel: DataStoreViewModel
    private lateinit var adapterViewPager: ViewPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentApodDailyBinding.bind(view)

        binding.vpPhotoToday.clipToPadding = false
        binding.vpPhotoToday.clipChildren = false
        binding.vpPhotoToday.offscreenPageLimit = 3
        binding.vpPhotoToday.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(50))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.95f + r * 0.05f
        }

        binding.vpPhotoToday.setPageTransformer(compositePageTransformer)

        dataStoreViewModel =
            ViewModelProvider(requireActivity()).get(DataStoreViewModel::class.java)

        if (!::adapterViewPager.isInitialized) {
            readDataStore()
            getResults(sdf.format(endDate.time), sdf.format(startDate.time))
        } else {
            binding.vpPhotoToday.adapter = adapterViewPager
        }

        readDataStore()

        activity?.window?.addFlags((WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS))
    }

    private fun readDataStore() {
        dataStoreViewModel.readFromDataStore.observe(viewLifecycleOwner, Observer {
            startDate.time = sdf.parse(it)!!
        })
    }

    private fun getResults(end: String, start: String) {
        viewModel.fetchAPODResults(end, start)
            .observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Result.Loading -> {
                        if (!::adapterViewPager.isInitialized) {
                            Log.d("ViewModelDaily", "Loading... Adapter is NOT Initialized")
                        } else {
                            Log.d("ViewModelDaily", "Loading... Adapter is Initialized")
                        }
                    }
                    is Result.Success -> {
                        if (::adapterViewPager.isInitialized) {
                            adapterViewPager.setData(result.data)
                            Log.d("ViewModelDaily", "Result... Adapter is Initialized")
                        } else {
                            Log.d("ViewModelDaily", "Result... Adapter is NOT Initialized")
                            adapterViewPager = ViewPagerAdapter(result.data, this@APODDailyFragment)
                            binding.vpPhotoToday.adapter = adapterViewPager
                        }
                        Log.d("ViewModelDaily", "Results: ${result.data}")
                    }
                    is Result.Failure -> {
                        Toast.makeText(context, "Hubo un error", Toast.LENGTH_SHORT).show()
                        Log.d("ViewModelDaily", "Error: ${result.exception}")
                    }
                }
            })
    }

    override fun onImageClick(apod: APOD, itemBinding: ItemApodDailyBinding) {
        if (itemBinding.imgApodPicture.drawable == null) {
            Toast.makeText(
                requireContext(),
                "Espera a que cargue la imagen",
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            val action = APODDailyFragmentDirections.actionAPODDailyFragmentToAPODImageFragment(
                apod.hdurl,
                apod.title,
                apod.url
            )
            findNavController().navigate(action)
        }
    }

}