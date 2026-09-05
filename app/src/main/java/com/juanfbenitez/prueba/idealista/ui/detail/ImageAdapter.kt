package com.juanfbenitez.prueba.idealista.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.juanfbenitez.prueba.idealista.R
import com.juanfbenitez.prueba.idealista.data.api.model.ImageDTO
import com.juanfbenitez.prueba.idealista.databinding.ItemImageBinding

class ImageAdapter(private val images: List<ImageDTO>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: ImageDTO) {
            binding.propertyImage.load(image.url) {
                crossfade(true)
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }
        }
    }
}
