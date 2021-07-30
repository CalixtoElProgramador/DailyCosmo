package com.listocalixto.dailycosmo.ui.apod

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.listocalixto.dailycosmo.R
import com.listocalixto.dailycosmo.core.Result
import com.listocalixto.dailycosmo.data.model.APOD
import com.listocalixto.dailycosmo.data.remote.apod.APODDataSource
import com.listocalixto.dailycosmo.databinding.FragmentApodBinding
import com.listocalixto.dailycosmo.presentation.apod.APODViewModel
import com.listocalixto.dailycosmo.presentation.apod.APODViewModelFactory
import com.listocalixto.dailycosmo.repository.apod.APODRepositoryImpl
import com.listocalixto.dailycosmo.repository.apod.RetrofitClient
import com.listocalixto.dailycosmo.ui.apod.adapter.APODAdapter
import java.text.SimpleDateFormat
import java.util.*

class APODFragment : Fragment(R.layout.fragment_apod), APODAdapter.OnAPODClickListener {

    private lateinit var binding: FragmentApodBinding
    private var limit = 0

    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private var endDate: Calendar = Calendar.getInstance()
    private var startDate: Calendar = Calendar.getInstance().apply {
        set(
            endDate.get(Calendar.YEAR),
            endDate.get(Calendar.MONTH),
            endDate.get(Calendar.DATE)
        )
        add(Calendar.DATE, -10)
    }

    private var isLoading = false
    private lateinit var adapter: APODAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var newEndDate: Calendar
    private lateinit var newStartDate: Calendar
    private lateinit var results: List<APOD>
    private lateinit var newResults: List<APOD>

    private val viewModel by viewModels<APODViewModel> {
        APODViewModelFactory(
            APODRepositoryImpl(
                APODDataSource(RetrofitClient.webservice)
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentApodBinding.bind(view)

        layoutManager = LinearLayoutManager(context)
        binding.rvApod.layoutManager = layoutManager

        getResults(endDate, startDate)


        /*activity?.findViewById<NestedScrollView>(R.id.scrollView_main)
            ?.setOnScrollChangeListener { v, _, scrollY, _, _ ->
                if (scrollY == (activity?.findViewById<NestedScrollView>(R.id.scrollView_main)
                        ?.getChildAt(0)?.measuredHeight?.minus(
                            v.measuredHeight
                        ))
                ) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                    val total = adapter.itemCount

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItem) >= total) {

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
                            startDate = newStartDate
                            getResults(newEndDate, newStartDate)
                        }
                    }
                }
            }*/
        binding.rvApod.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                    val total = adapter.itemCount

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItem) >= total) {

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
                            startDate = newStartDate
                            getResults(newEndDate, newStartDate)
                        }
                    }
                }
            }
        })
    }

    private fun getResults(end: Calendar, start: Calendar) {
        isLoading = true
        viewModel.fetchAPODResults(sdf.format(end.time), sdf.format(start.time))
            .observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Result.Loading -> {
                        if (!::adapter.isInitialized) {
                            binding.pbRvAPOD.visibility = View.VISIBLE
                            Log.d("ViewModel", "Loading... ")
                            binding.pbMoreResults.visibility = View.GONE
                        } else {
                            binding.pbMoreResults.visibility = View.VISIBLE
                        }
                    }
                    is Result.Success -> {
                        if (::adapter.isInitialized) {
                            newResults = result.data.asReversed()
                            results = results.plus(newResults)
                            adapter.setData(results)
                            binding.pbMoreResults.visibility = View.GONE
                        } else {
                            binding.pbRvAPOD.visibility = View.GONE
                            results = result.data.asReversed()
                            adapter = APODAdapter(results, this@APODFragment)
                            binding.rvApod.adapter = adapter
                        }
                        isLoading = false
                        Log.d("ViewModel", "Resultados: ${result.data}")
                    }
                    is Result.Failure -> {
                        Log.d("ViewModel", "${result.exception}")
                    }
                }
            })
    }

    override fun onAPODClick(apod: APOD) {
        Toast.makeText(context, "Presionaste ${apod.title}", Toast.LENGTH_SHORT).show()
    }
}