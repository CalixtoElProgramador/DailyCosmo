package com.listocalixto.dailycosmo.ui.apod

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.listocalixto.dailycosmo.R
import com.listocalixto.dailycosmo.core.Result
import com.listocalixto.dailycosmo.data.local.AppDatabase
import com.listocalixto.dailycosmo.data.local.LocalAPODDataSource
import com.listocalixto.dailycosmo.data.model.APOD
import com.listocalixto.dailycosmo.data.remote.apod.RemoteAPODDataSource
import com.listocalixto.dailycosmo.databinding.FragmentApodBinding
import com.listocalixto.dailycosmo.presentation.apod.APODViewModel
import com.listocalixto.dailycosmo.presentation.apod.APODViewModelFactory
import com.listocalixto.dailycosmo.repository.apod.APODRepositoryImpl
import com.listocalixto.dailycosmo.repository.apod.RetrofitClient
import com.listocalixto.dailycosmo.ui.apod.adapter.APODAdapter
import java.text.SimpleDateFormat
import java.util.*

class APODFragment : Fragment(R.layout.fragment_apod), APODAdapter.OnAPODClickListener {

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

    private var isLoading = false
    private var endDate: Calendar = Calendar.getInstance()
    private var startDate: Calendar = Calendar.getInstance().apply {
        set(
            endDate.get(Calendar.YEAR),
            endDate.get(Calendar.MONTH),
            endDate.get(Calendar.DATE)
        )
        add(Calendar.DATE, -10)
    }

    private lateinit var binding: FragmentApodBinding
    private lateinit var dataStoreViewModel: DataStoreViewModel
    private lateinit var adapter: APODAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private lateinit var newEndDate: Calendar
    private lateinit var newStartDate: Calendar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentApodBinding.bind(view)

        activity?.window?.addFlags((WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS))
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        activity?.window?.statusBarColor = requireActivity().resources.getColor(R.color.colorPrimaryVariant)

        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvApod.layoutManager = layoutManager
        dataStoreViewModel =
            ViewModelProvider(requireActivity()).get(DataStoreViewModel::class.java)

        if (!::adapter.isInitialized) {
            readDataStore()
            getResults(sdf.format(endDate.time), sdf.format(startDate.time))
        } else {
            binding.rvApod.adapter = adapter
        }
        readDataStore()

        binding.rvApod.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    Log.d("RecyclerView", "onScrolled: Primera condición")
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem =
                        layoutManager.findFirstCompletelyVisibleItemPositions(null)
                    val total = adapter.itemCount
                    if (!isLoading) {
                        Log.d("RecyclerView", "onScrolled: Segunda condición")
                        if ((visibleItemCount + pastVisibleItem[pastVisibleItem.lastIndex]) >= total) {
                            Log.d("RecyclerView", "onScrolled: Tercera condición")
                            initNewDates()
                            getResults(sdf.format(newEndDate.time), sdf.format(newStartDate.time))
                        }
                    }
                }
            }
        })
    }

    private fun readDataStore() {
        dataStoreViewModel.readFromDataStore.observe(viewLifecycleOwner, Observer {
            startDate.time = sdf.parse(it)!!
        })
    }

    private fun initNewDates() {
        newEndDate = Calendar.getInstance().apply {
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
    }

    private fun getResults(end: String, start: String) {
        isLoading = true
        viewModel.fetchAPODResults(end, start)
            .observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Result.Loading -> {
                        if (!::adapter.isInitialized) {
                            binding.pbRvAPOD.visibility = View.VISIBLE
                            Log.d("ViewModel", "Loading... Adapter is NOT Initialized")
                            binding.pbMoreResults.visibility = View.GONE
                        } else {
                            Log.d("ViewModel", "Loading... Adapter is Initialized")
                            binding.pbMoreResults.visibility = View.VISIBLE
                        }
                    }
                    is Result.Success -> {
                        if (::adapter.isInitialized) {
                            adapter.setData(result.data)
                            binding.pbMoreResults.visibility = View.GONE
                            dataStoreViewModel.saveToDataStore(newStartDate)
                            Log.d("ViewModel", "Result... Adapter is Initialized")
                        } else {
                            Log.d("ViewModel", "Result... Adapter is NOT Initialized")
                            binding.pbRvAPOD.visibility = View.GONE
                            adapter = APODAdapter(result.data, this@APODFragment)
                            binding.rvApod.adapter = adapter
                        }
                        isLoading = false
                        Log.d("ViewModel", "Results: ${result.data}")
                    }
                    is Result.Failure -> {
                        Toast.makeText(context, "Hubo un error", Toast.LENGTH_SHORT).show()
                        Log.d("ViewModel", "Error: ${result.exception}")
                    }
                }
            })
    }

    override fun onAPODClick(apod: APOD) {
        val action = APODFragmentDirections.actionAPODFragmentToAPODDetailsFragment2(
            apod.copyright,
            apod.date,
            apod.explanation,
            apod.hdurl,
            apod.media_type,
            apod.title,
            apod.url
        )
        findNavController().navigate(action)

    }
}