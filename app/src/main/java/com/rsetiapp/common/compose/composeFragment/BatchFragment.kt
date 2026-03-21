package com.rsetiapp.common.compose.composeFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rsetiapp.common.compose.ui.BatchScreen
import com.rsetiapp.common.compose.viewmodel.BatchViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Rishi Porwal
 */
@AndroidEntryPoint
class BatchFragment : Fragment() {

    private val viewModel: BatchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {

            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                MaterialTheme {
                    BatchScreen(viewModel){findNavController().navigateUp()}
                }
            }
        }
    }
}