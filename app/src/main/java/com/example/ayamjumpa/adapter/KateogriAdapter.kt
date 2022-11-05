package com.example.ayamjumpa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ayamjumpa.R
import com.example.ayamjumpa.dataClass.Kategori
import com.example.ayamjumpa.dataClass.KategoriMenu
import com.example.ayamjumpa.util.DiffUtilRepo

class KateogriAdapter( private val context: Context, private val invoker:(Array<String>)->Unit) :
    RecyclerView.Adapter<KateogriAdapter.MyViewHolder>() {
    private val diffUtilRepo = DiffUtilRepo<Kategori>()

    val differ = AsyncListDiffer(this,diffUtilRepo)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.kategori_viewholder, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.setData(differ.currentList[position],context)

        holder.itemView.setOnClickListener {
            invoker.invoke(arrayOf(differ.currentList[position].nama!!,differ.currentList[position].tipe!!))
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView = itemView.findViewById(R.id.imageView_holder_kategori)
        val textnya: TextView = itemView.findViewById(R.id.text_holder_kategori)

        init{

        }

        fun setData(item:Kategori, context:Context){


            Glide.with(context).load(item.foto).into(
                image
            )
            textnya.text = item.nama



        }

    }

}