package com.listocalixto.dailycosmo.ui.apod_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.navigation.navArgs
import com.listocalixto.dailycosmo.R

class APODDetailsActivity : AppCompatActivity() {

    private val args by navArgs<APODDetailsActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apod_details)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.nav_host_details,
                APODDetailsFragment.newInstance(
                    args.copyright,
                    args.date,
                    args.explanation,
                    args.hdurl,
                    args.mediaType,
                    args.title,
                    args.url
                )
            )
            disallowAddToBackStack()
        }
    }
}