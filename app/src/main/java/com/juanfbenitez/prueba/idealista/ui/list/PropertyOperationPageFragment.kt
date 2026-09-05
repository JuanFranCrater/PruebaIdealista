package com.juanfbenitez.prueba.idealista.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanfbenitez.prueba.idealista.data.prefs.DetailDisplayMode
import com.juanfbenitez.prueba.idealista.databinding.FragmentPropertyPageBinding
import com.juanfbenitez.prueba.idealista.di.Dependencies
import kotlinx.coroutines.launch

/**
 * A single swipeable page of the property list (one per "operation": sale/rent). Shares the
 * [PropertyListViewModel] instance with the parent [PropertyListFragment] so both pages read
 * from the same underlying data/favorites, each filtering to its own operation.
 */
class PropertyOperationPageFragment : Fragment() {

    private var _binding: FragmentPropertyPageBinding? = null
    private val binding get() = _binding!!

    private val operation: String by lazy {
        requireArguments().getString(ARG_OPERATION) ?: PropertyOperation.SALE
    }

    private val viewModel: PropertyListViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { PropertyListViewModelFactory(Dependencies.providePropertyRepository(requireContext())) }
    )

    private lateinit var adapter: PropertyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeUiState()
    }

    private fun setupRecyclerView() {
        val detailDisplayPreferences = Dependencies.provideDetailDisplayPreferences(requireContext())
        adapter = PropertyAdapter(
            onPropertyClick = { propertyCode ->
                when (detailDisplayPreferences.mode.value) {
                    DetailDisplayMode.DRAWER -> viewModel.selectPropertyForDrawer(propertyCode)
                    DetailDisplayMode.FULL_SCREEN -> {
                        val action = PropertyListFragmentDirections.actionPropertyListFragmentToPropertyDetailFragment(propertyCode)
                        findNavController().navigate(action)
                    }
                }
            },
            onFavoriteClick = { propertyCode ->
                viewModel.toggleFavorite(propertyCode)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeProperties(operation).collect { state ->
                    when (state) {
                        is PropertyListUiState.Loading -> {
                            binding.progressBar.isVisible = true
                        }
                        is PropertyListUiState.Success -> {
                            binding.progressBar.isVisible = false
                            adapter.submitList(state.properties)
                        }
                        is PropertyListUiState.Error -> {
                            binding.progressBar.isVisible = false
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_OPERATION = "operation"

        fun newInstance(operation: String): PropertyOperationPageFragment =
            PropertyOperationPageFragment().apply {
                arguments = Bundle().apply { putString(ARG_OPERATION, operation) }
            }
    }
}
