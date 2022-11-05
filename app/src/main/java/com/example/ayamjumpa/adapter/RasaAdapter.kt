package com.example.ayamjumpa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ayamjumpa.R
import com.example.ayamjumpa.dataClass.Kategori
import com.example.ayamjumpa.util.DiffUtilRepo
import de.hdodenhof.circleimageview.CircleImageView

class RasaAdapter(private val context:Context):RecyclerView.Adapter<RasaAdapter.MyViewHolder>() {

    private val diffUtilRepo = DiffUtilRepo<Kategori>()

    val differ = AsyncListDiffer(this,diffUtilRepo)

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val image:ImageView = itemView.findViewById(R.id.imageView_holder_kategori)
        val textnya:TextView = itemView.findViewById(R.id.text_holder_kategori)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.kategori_viewholder, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = differ.currentList[position]


        Glide.with(context).load(currentItem.foto).into(
            holder.image
        )
        holder.textnya.text = currentItem.nama


    }

    override fun getItemCount(): Int = differ.currentList.size
}