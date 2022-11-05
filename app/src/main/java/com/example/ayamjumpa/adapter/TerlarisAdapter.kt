package com.example.ayamjumpa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.RecycleClickListener
import com.example.ayamjumpa.dataClass.Menu
import java.text.NumberFormat
import java.util.*

class TerlarisAdapter(val context: Context
,private val terlaisList:MutableList<Menu>
, private val clickListener: RecycleClickListener,
private val uid:String) : RecyclerView.Adapter<TerlarisAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
      val itemView = LayoutInflater.from(parent.context).inflate(R.layout.terlaris_viewholder, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = terlaisList[position]

        Glide.with(context)
            .load(currentItem.foto)
            .into(holder.image)


        holder.textnya.text = currentItem.nama
        holder.harga.text = formatRupiah(currentItem.harga)

        holder.tambah.setOnClickListener {

            clickListener.onItemClickListener(currentItem)

        }



    }

    override fun getItemCount(): Int {
        return terlaisList.size
    }

    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){





            val image:ImageView= itemView.findViewById(R.id.fotoTerlaris)
            val textnya : TextView = itemView.findViewById(R.id.produkTerlaris)
            val harga : TextView = itemView.findViewById(R.id.hargaTerlaris)
            val tambah: LinearLayout= itemView.findViewById(R.id.tambahTerlaris)


    }

    // cara kerja : Jika data ada maka akan tambah kuantitas item di cart,
    // jika data kosong maka akan tambah data baru
   //

    private fun formatRupiah(number:Long) : String{


        val localeID = Locale("in", "ID")

        val format = NumberFormat.getCurrencyInstance(localeID)

        return format.format(number)



    }

    }

