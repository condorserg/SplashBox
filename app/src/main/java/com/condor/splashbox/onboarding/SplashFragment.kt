package com.condor.splashbox.onboarding

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.condor.splashbox.App
import com.condor.splashbox.PreferencesManager
import com.condor.splashbox.R
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SplashFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val prefManager = PreferencesManager(requireActivity())
        launch {
            delay(2000)
            withContext(Dispatchers.Main) {
                if (prefManager.isFirstRun()) {
                    prefManager.setFirstRun()
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToViewPagerFragment())
                } else {
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToAuthFragment())
                }

            }
        }
    }

}