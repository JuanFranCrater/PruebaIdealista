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
import com.juanfbenitez.prueba.idealista.databinding.FragmentPropertyListBinding
import com.juanfbenitez.prueba.idealista.ui.list.PropertyListFragmentDirections
import com.juanfbenitez.prueba.idealista.di.Dependencies
import kotlinx.coroutines.launch

class PropertyListFragment : Fragment() {

    private var _binding: FragmentPropertyListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory(Dependencies.providePropertyRepository(requireContext()))
    }

    private lateinit var adapter: PropertyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeUiState()
    }

    private fun setupRecyclerView() {
        adapter = PropertyAdapter(
            onPropertyClick = { propertyCode ->
                val action = PropertyListFragmentDirections.actionPropertyListFragmentToPropertyDetailFragment(propertyCode)
                findNavController().navigate(action)
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
                viewModel.uiState.collect { state ->
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
}
