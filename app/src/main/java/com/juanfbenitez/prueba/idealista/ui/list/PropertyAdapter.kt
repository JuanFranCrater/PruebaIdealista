package com.juanfbenitez.prueba.idealista.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.isVisible
import coil.load
import com.juanfbenitez.prueba.idealista.R
import com.juanfbenitez.prueba.idealista.databinding.ItemPropertyBinding
import java.text.SimpleDateFormat
import java.util.*

class PropertyAdapter(
    private val onPropertyClick: (String) -> Unit,
    private val onFavoriteClick: (String) -> Unit
) : ListAdapter<PropertyItemUiModel, PropertyAdapter.PropertyViewHolder>(PropertyDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val binding = ItemPropertyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PropertyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PropertyViewHolder(private val binding: ItemPropertyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PropertyItemUiModel) {
            val property = item.property
            binding.apply {
                priceText.text = root.context.getString(
                    R.string.price_format,
                    property.priceInfo.price.amount,
                    property.priceInfo.price.currencySuffix
                )
                addressText.text = property.address
                detailsText.text = root.context.getString(
                    R.string.property_details_format,
                    property.rooms,
                    property.bathrooms,
                    property.size
                )
                
                propertyImage.load(property.thumbnail) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder)
                    error(R.drawable.placeholder)
                }

                favoriteButton.setImageResource(
                    if (item.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                )

                favoriteDateText.isVisible = item.isFavorite && item.dateFavorited != null
                if (item.isFavorite && item.dateFavorited != null) {
                    favoriteDateText.text = root.context.getString(
                        R.string.favorited_on,
                        dateFormat.format(Date(item.dateFavorited))
                    )
                }

                root.setOnClickListener { onPropertyClick(property.propertyCode) }
                favoriteButton.setOnClickListener { onFavoriteClick(property.propertyCode) }
            }
        }
    }

    class PropertyDiffCallback : DiffUtil.ItemCallback<PropertyItemUiModel>() {
        override fun areItemsTheSame(oldItem: PropertyItemUiModel, newItem: PropertyItemUiModel): Boolean {
            return oldItem.property.propertyCode == newItem.property.propertyCode
        }

        override fun areContentsTheSame(oldItem: PropertyItemUiModel, newItem: PropertyItemUiModel): Boolean {
            return oldItem == newItem
        }
    }
}
