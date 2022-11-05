package com.example.ayamjumpa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ayamjumpa.dataClass.ImageData
import com.example.ayamjumpa.databinding.ItemSlideBinding

class ImageSliderAdapter (
    private val item : List<ImageData>,
    private val onSubmitClickListener: () -> Unit
        ) : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ItemSlideBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
      holder.bind(item[position])
        holder.pic.setOnClickListener {

            onSubmitClickListener.invoke()

        }
    }

    override fun getItemCount(): Int = item.size

    inner class ImageViewHolder(itemView: ItemSlideBinding) : RecyclerView.ViewHolder(itemView.root){
        private val binding = itemView
        val pic = binding.slideImage

        fun bind(data:ImageData){
            with(binding){
                Glide.with(itemView)
                    .load(data.imageUrl)
                    .into(slideImage)


            }
        }
    }

}