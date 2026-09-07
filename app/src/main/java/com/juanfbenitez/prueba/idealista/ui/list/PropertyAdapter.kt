package com.juanfbenitez.prueba.idealista.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
                    property.price.amount,
                    property.price.currencySuffix
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
                favoriteButton.contentDescription = root.context.getString(
                    if (item.isFavorite) R.string.remove_from_favorites else R.string.add_to_favorites
                )

                favoriteDateText.text = if (item.isFavorite && item.dateFavorited != null) {
                    root.context.getString(
                        R.string.favorited_on,
                        dateFormat.format(Date(item.dateFavorited))
                    )
                } else {
                    root.context.getString(R.string.favorite_prompt)
                }

                root.setOnClickListener { onPropertyClick(property.propertyCode) }
                favoriteButton.setOnClickListener {
                    if (item.isFavorite) {
                        confirmRemoveFavorite(root.context) { onFavoriteClick(property.propertyCode) }
                    } else {
                        onFavoriteClick(property.propertyCode)
                    }
                }
            }
        }

        private fun confirmRemoveFavorite(context: android.content.Context, onConfirm: () -> Unit) {
            AlertDialog.Builder(context)
                .setTitle(R.string.remove_favorite_title)
                .setMessage(R.string.remove_favorite_message)
                .setPositiveButton(R.string.remove) { dialog, _ ->
                    onConfirm()
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .show()
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
