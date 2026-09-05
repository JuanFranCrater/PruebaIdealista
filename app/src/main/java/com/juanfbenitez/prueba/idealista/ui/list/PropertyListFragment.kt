package com.juanfbenitez.prueba.idealista.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.juanfbenitez.prueba.idealista.R
import com.juanfbenitez.prueba.idealista.databinding.FragmentPropertyListBinding
import com.juanfbenitez.prueba.idealista.di.Dependencies
import androidx.viewpager2.widget.ViewPager2

class PropertyListFragment : Fragment() {

    private var _binding: FragmentPropertyListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory(Dependencies.providePropertyRepository(requireContext()))
    }

    private lateinit var pagerAdapter: PropertyPagerAdapter

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
        setupStatusBarInsets()
        setupOperationTabs()
    }

    private fun setupStatusBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.statusBarScrim) { scrimView, insets ->
            val statusBarInset = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            scrimView.layoutParams = scrimView.layoutParams.apply {
                height = statusBarInset.top
            }
            scrimView.requestLayout()
            insets
        }
    }

    private fun setupOperationTabs() {
        pagerAdapter = PropertyPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        binding.viewPager.adapter = pagerAdapter

        val startPosition = pagerAdapter.positionOf(viewModel.selectedOperation.value)
        binding.viewPager.setCurrentItem(startPosition, false)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.selectOperation(pagerAdapter.operationAt(position))
            }
        })

        TabLayoutMediator(binding.operationTabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.tab_sale) else getString(R.string.tab_rent)
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
