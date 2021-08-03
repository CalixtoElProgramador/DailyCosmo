package com.listocalixto.dailycosmo.ui.apod_daily

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
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
import com.listocalixto.dailycosmo.presentation.apod.DataStoreViewModel
import com.listocalixto.dailycosmo.ui.apod_daily.adapter.ViewPagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.listocalixto.dailycosmo.core.Result
import com.listocalixto.dailycosmo.data.model.APOD
import com.listocalixto.dailycosmo.databinding.ItemApodDailyBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

private const val MIN_SCALE = 0.75f

@Suppress("DEPRECATION")
class APODDailyFragment : Fragment(R.layout.fragment_apod_daily),
    ViewPagerAdapter.OnImageAPODClickListener, ViewPager2.PageTransformer {

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
    private lateinit var newStartDate: Calendar
    private var isLoading = false

    private var sizeList: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars(view)
        configWindow()
        readFromDataStore()
        isAdapterInit()
        configViewPager() //And loadMoreResults when ViewPager.currentItem == results.lastIndex
    }

    private fun isAdapterInit() {
        if (!::adapterViewPager.isInitialized) {
            getResults(sdf.format(endDate.time), sdf.format(startDate.time))
        } else {
            binding.vpPhotoToday.adapter = adapterViewPager
        }
    }

    private fun configViewPager() {
        binding.vpPhotoToday.setPageTransformer(this)
    }

    private fun configWindow() {
        activity?.window?.addFlags((WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS))
    }

    private fun initVars(view: View) {
        binding = FragmentApodDailyBinding.bind(view)
        dataStoreViewModel =
            ViewModelProvider(requireActivity()).get(DataStoreViewModel::class.java)
    }

    private fun readFromDataStore() {
        dataStoreViewModel.readLastDateFromDataStore.observe(viewLifecycleOwner, { date ->
            startDate.time = sdf.parse(date)!!
        })
        dataStoreViewModel.readSizeListFromDataStore.observe(viewLifecycleOwner, { size ->
            sizeList = size
        })
    }

    private fun getResults(end: String, start: String) {
        isLoading = true
        viewModel.fetchAPODResults(end, start)
            .observe(viewLifecycleOwner, { result ->
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
                            sizeList += 10
                            adapterViewPager.setData(result.data)
                            dataStoreViewModel.saveLastDateToDataStore(result.data[result.data.lastIndex].date)
                            dataStoreViewModel.saveNewSizeListToDataStore(sizeList)
                            Log.d("ViewModelDaily", "Result... Adapter is Initialized")
                        } else {
                            Log.d("ViewModelDaily", "Result... Adapter is NOT Initialized")
                            adapterViewPager = ViewPagerAdapter(result.data, this@APODDailyFragment)
                            binding.vpPhotoToday.adapter = adapterViewPager
                        }
                        isLoading = false
                        Log.d("ViewModelDaily", "Results: ${result.data}")
                    }
                    is Result.Failure -> {
                        Log.d("ViewModelDaily", "ViewModel error: ${result.exception}")
                    }
                }
            })
    }

    override fun onImageClick(apod: APOD, itemBinding: ItemApodDailyBinding) {
        if (itemBinding.imgApodPicture.drawable == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.wait_for_the_image_to_load),
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

    override fun transformPage(page: View, position: Float) {
        page.apply {
            val pageWidth = width
            when {
                position < -1 -> {
                    alpha = 0f
                }
                position <= 0 -> {
                    alpha = 1f
                    translationX = 0f
                    translationZ = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                position <= 1 -> {
                    alpha = 1 - position
                    translationX = pageWidth * -position
                    translationZ = -1f
                    val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position)))
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                }
                else -> {
                    alpha = 0f
                }
            }
        }
        loadMoreResults()
    }

    private fun loadMoreResults() {
        Log.d("ViewPager2", "Position of the ViewPager: ${binding.vpPhotoToday.currentItem}")
            Log.d("ViewPager2", "List size: $sizeList")
            if (!isLoading) {
                Log.d("ViewPager2", "isLoading: $isLoading")
                if (binding.vpPhotoToday.currentItem >= sizeList - 6) {
                    getResults(newDates()[0], newDates()[1])
                }
            }
    }


    private fun newDates(): Array<String> {
        val newEndDate = Calendar.getInstance().apply {
            set(
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DATE)
            )
            add(Calendar.DATE, -1)
        }
        newStartDate = Calendar.getInstance().apply {
            set(
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DATE)
            )
            add(Calendar.DATE, -10)
        }
        return arrayOf(sdf.format(newEndDate.time), sdf.format(newStartDate.time))
    }

}