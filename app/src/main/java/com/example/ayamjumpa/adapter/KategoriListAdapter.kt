package com.example.ayamjumpa.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.ayamjumpa.dataClass.Menu
import com.example.ayamjumpa.databinding.MenuListVhBinding
import com.example.ayamjumpa.util.DiffUtilRepo

class KategoriListAdapter(private val context: Context, private val navigaton:(Array<String>)->Unit) : RecyclerView.Adapter<KategoriListAdapter.MyViewHolder>() {
    private val diffUtilRepo = DiffUtilRepo<Menu>()

    val differ = AsyncListDiffer(this,diffUtilRepo)

    var indicator ="menu"





    class MyViewHolder(binding:MenuListVhBinding):RecyclerView.ViewHolder(binding.root){
        val namaKategori = binding.namaKategori
        val jumlahKategori = binding.jumlahKategori
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MenuListVhBinding.inflate(LayoutInflater.from(context))
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = differ.currentList[position]

        holder.namaKategori.text = if (indicator =="menu") currentItem.menuKey else currentItem.rasaKey

        var count= 0

        differ.currentList.forEach { menu->
            if(if(indicator=="menu")menu.menuKey == currentItem.menuKey else menu.rasaKey == currentItem.rasaKey){
                count++

            }


        }

        holder.jumlahKategori.text = count.toString()

        holder.itemView.setOnClickListener {

            if(indicator=="menu"){

                navigaton.invoke(arrayOf(currentItem.menuKey!!,"menu"))
            }

            else{
                navigaton.invoke(arrayOf(currentItem.rasaKey!!,"rasa"))
            }



        }

    }

    override fun getItemCount(): Int = differ.currentList.size
}