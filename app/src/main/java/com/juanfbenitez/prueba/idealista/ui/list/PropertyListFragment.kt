package com.juanfbenitez.prueba.idealista.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.juanfbenitez.prueba.idealista.R
import com.juanfbenitez.prueba.idealista.data.prefs.DetailDisplayMode
import com.juanfbenitez.prueba.idealista.data.prefs.DetailDisplayPreferences
import com.juanfbenitez.prueba.idealista.databinding.FragmentPropertyListBinding
import com.juanfbenitez.prueba.idealista.ui.detail.compose.PropertyDetailDrawer
import com.juanfbenitez.prueba.idealista.ui.theme.PruebaIdealistaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PropertyListFragment : Fragment() {

    private var _binding: FragmentPropertyListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertyListViewModel by viewModels()

    @Inject
    lateinit var detailDisplayPreferences: DetailDisplayPreferences

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
        setupToolbar()
        setupDetailDrawer()
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
            tab.text = when (position) {
                0 -> getString(R.string.tab_sale)
                1 -> getString(R.string.tab_rent)
                else -> getString(R.string.tab_favorites)
            }
        }.attach()
    }

    /** Wires the toolbar's gear icon to a popup menu letting the user pick the detail display mode. */
    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_settings) {
                showDetailDisplayModeMenu()
                true
            } else {
                false
            }
        }
    }

    private fun showDetailDisplayModeMenu() {
        val anchor = binding.toolbar.findViewById<View>(R.id.action_settings) ?: binding.toolbar
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.menu_detail_display_mode, popup.menu)

        val checkedId = when (detailDisplayPreferences.mode.value) {
            DetailDisplayMode.DRAWER -> R.id.mode_drawer
            DetailDisplayMode.FULL_SCREEN -> R.id.mode_full_screen
        }
        popup.menu.findItem(checkedId)?.isChecked = true

        popup.setOnMenuItemClickListener { item ->
            val newMode = when (item.itemId) {
                R.id.mode_drawer -> DetailDisplayMode.DRAWER
                R.id.mode_full_screen -> DetailDisplayMode.FULL_SCREEN
                else -> return@setOnMenuItemClickListener false
            }
            detailDisplayPreferences.setMode(newMode)
            true
        }
        popup.show()
    }

    /**
     * Hosts the [PropertyDetailDrawer] Compose asset on top of the (XML) list screen so the
     * "drawer" [DetailDisplayMode] can live side-by-side with the property list without
     * navigating away, while reusing the exact same detail UI as the full-screen destination.
     */
    private fun setupDetailDrawer() {
        binding.detailDrawerComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PruebaIdealistaTheme {
                    val selectedCode by viewModel.selectedPropertyForDrawer.collectAsStateWithLifecycle()
                    PropertyDetailDrawer(
                        propertyCode = selectedCode,
                        onDismiss = { viewModel.clearDrawerSelection() }
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
