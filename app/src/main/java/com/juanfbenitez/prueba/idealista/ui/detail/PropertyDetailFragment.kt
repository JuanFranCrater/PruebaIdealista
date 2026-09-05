package com.juanfbenitez.prueba.idealista.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.juanfbenitez.prueba.idealista.R
import com.juanfbenitez.prueba.idealista.data.api.model.PropertyDetailDTO
import com.juanfbenitez.prueba.idealista.data.db.FavoriteEntity
import com.juanfbenitez.prueba.idealista.databinding.FragmentPropertyDetailBinding
import com.juanfbenitez.prueba.idealista.di.Dependencies
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PropertyDetailFragment : Fragment() {

    private var _binding: FragmentPropertyDetailBinding? = null
    private val binding get() = _binding!!

    private val args: PropertyDetailFragmentArgs by navArgs()
    private val viewModel: PropertyDetailViewModel by viewModels {
        PropertyDetailViewModelFactory(Dependencies.providePropertyRepository(requireContext()), args.propertyCode)
    }

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupListeners()
        observeUiState()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupListeners() {
        binding.favoriteFab.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is PropertyDetailUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is PropertyDetailUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            bindDetail(state.detail, state.favorite)
                        }
                        is PropertyDetailUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            // Show error toast or snackbar
                        }
                    }
                }
            }
        }
    }

    private fun bindDetail(detail: PropertyDetailDTO, favorite: FavoriteEntity?) {
        binding.collapsingToolbar.title = detail.propertyType.replaceFirstChar { it.uppercase() }
        
        // Images
        detail.multimedia?.images?.let { images ->
            binding.propertyViewPager.adapter = ImageAdapter(images)
        }

        // Price
        binding.priceText.text = getString(
            R.string.price_format,
            detail.price,
            detail.priceInfo.currencySuffix
        )

        binding.typeText.text = detail.propertyType

        // Characteristics
        val characteristics = detail.moreCharacteristics
        binding.roomsText.text = characteristics?.roomNumber?.toString() ?: "-"
        binding.bathsText.text = characteristics?.bathNumber?.toString() ?: "-"
        binding.areaText.text = getString(R.string.area_format, characteristics?.constructedArea?.toFloat() ?: 0f)

        binding.descriptionText.text = detail.propertyComment

        // More characteristics text
        val sb = StringBuilder()
        val yes = getString(R.string.yes)
        val no = getString(R.string.no)
        characteristics?.let {
            sb.append(getString(R.string.lift)).append(": ").append(if (it.lift == true) yes else no).append("\n")
            sb.append(getString(R.string.exterior)).append(": ").append(if (it.exterior == true) yes else no).append("\n")
            it.status?.let { status -> sb.append(getString(R.string.status)).append(": ").append(status).append("\n") }
            it.floor?.let { floor -> sb.append(getString(R.string.floor)).append(": ").append(floor).append("\n") }
        }
        binding.characteristicsText.text = sb.toString()

        // Favorite
        if (favorite != null) {
            binding.favoriteFab.setImageResource(R.drawable.ic_favorite)
            binding.favoriteDateText.visibility = View.VISIBLE
            binding.favoriteDateText.text = getString(
                R.string.favorited_on,
                dateFormat.format(Date(favorite.dateFavorited))
            )
        } else {
            binding.favoriteFab.setImageResource(R.drawable.ic_favorite_border)
            binding.favoriteDateText.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
