package com.example.ayamjumpa.adapter

import android.content.Context
import android.graphics.Paint
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
import com.example.ayamjumpa.interfaces.RecycleClickListener
import com.example.ayamjumpa.dataClass.Menu
import com.example.ayamjumpa.util.DiffUtilRepo
import java.text.NumberFormat
import java.util.*

class MenuAdapter(val context: Context
                  , private val clickListener: RecycleClickListener,): RecyclerView.Adapter<MenuAdapter.MyViewHolder>()  {

    private val diffUtilRepo = DiffUtilRepo<Menu>()
    val differ = AsyncListDiffer(this, diffUtilRepo)




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.menu_viewholder, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {



        val currentItem = differ.currentList[position]

        holder.judul.text = currentItem.nama!!.lowercase()
        holder.desc1.text = currentItem.deskripsi!!.lowercase()
        holder.desc2.text = currentItem.deskripsi1!!.lowercase()
        holder.harga.text = formatRupiah(currentItem.harga)


        Glide.with(context)
            .load(currentItem.foto)
            .into(holder.gambar)

        holder.btnTambah.setOnClickListener {
            clickListener.onItemClickListener(currentItem)
        }



        if (currentItem.promo){
            holder.harga.textSize = 13f
            holder.harga.paintFlags = holder.harga.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.promoText.text = formatRupiah(currentItem.harga - currentItem.potongan)
            holder.promoText.visibility = View.VISIBLE

        }

    }



    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val judul = itemView.findViewById<TextView>(R.id.judul_menu)
        val harga = itemView.findViewById<TextView>(R.id.harga_menu)
        val gambar = itemView.findViewById<ImageView>(R.id.gambar_menu)
        val btnTambah = itemView.findViewById<Button>(R.id.button_tambah_menu)
        val desc1 = itemView.findViewById<TextView>(R.id.isian_menu)
        val desc2 = itemView.findViewById<TextView>(R.id.desc_menu)
        val promoText = itemView.findViewById<TextView>(R.id.harga_menupromo)
    }

    private fun formatRupiah(number:Long) : String{


        val localeID = Locale("in", "ID")

        val format = NumberFormat.getCurrencyInstance(localeID)

        return format.format(number)



    }
}