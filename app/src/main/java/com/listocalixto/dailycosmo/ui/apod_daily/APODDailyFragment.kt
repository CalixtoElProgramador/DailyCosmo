package com.listocalixto.dailycosmo.ui.apod_daily

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.listocalixto.dailycosmo.R

class APODDailyFragment : Fragment(R.layout.fragment_apod_daily) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.addFlags((WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS))
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        activity?.window?.statusBarColor = requireActivity().resources.getColor(R.color.colorPrimaryVariant)

    }

}