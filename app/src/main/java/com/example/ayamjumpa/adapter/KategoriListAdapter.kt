package com.example.ayamjumpa.adapter

import android.content.Context
import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.ayamjumpa.databinding.MenuListVhBinding
import com.example.ayamjumpa.util.DiffUtilRepo

class KategoriListAdapter(private val context: Context, private val navigaton:(Array<String>)->Unit) : RecyclerView.Adapter<KategoriListAdapter.MyViewHolder>() {
    private val diffUtilRepo = DiffUtilRepo<String>()

    val differ = AsyncListDiffer(this,diffUtilRepo)

    private var hasmap : Map<String?,Int>? = null



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

holder.namaKategori.text = currentItem
        holder.jumlahKategori.text = hasmap?.get(currentItem).toString()

        holder.itemView.setOnClickListener {

            if(indicator=="menu"){

                navigaton.invoke(arrayOf(currentItem,"menu"))
            }
            else if(indicator=="minuman"){
                navigaton.invoke(arrayOf(currentItem,"minuman"))
            }

            else{
                navigaton.invoke(arrayOf(currentItem,"rasa"))
            }



        }

    }



    override fun getItemCount(): Int = differ.currentList.size


    fun setHashMap(data:Map<String?,Int>){
        hasmap = data
    }
}