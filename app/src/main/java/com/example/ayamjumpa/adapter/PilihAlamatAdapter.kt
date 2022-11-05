package com.example.ayamjumpa.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.Clicker
import com.example.ayamjumpa.dataClass.Alamat
import com.example.ayamjumpa.util.DiffUtilRepo

class PilihAlamatAdapter(
   val clicker: Clicker): RecyclerView.Adapter<PilihAlamatAdapter.MyViewHolder>() {
    private val diffUtilRepo = DiffUtilRepo<Alamat>()
    val differ = AsyncListDiffer(this, diffUtilRepo)
    var positionSelect = -1
    var idalamat:String?=null

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val keterangan = itemView.findViewById<TextView>(R.id.keteranganalamat)
        val alamat = itemView.findViewById<TextView>(R.id.alamatlengkap)
        val judul = itemView.findViewById<TextView>(R.id.status)
        val radioButton = itemView.findViewById<CheckBox>(R.id.expand)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.alamatholderuntukpilih, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        holder.keterangan.text = currentItem.keterangan
        holder.alamat.text = currentItem.alamat
        holder.judul.text = currentItem.label

        holder.radioButton.isChecked = position == positionSelect

        if(idalamat!=null){
            if(currentItem.id == idalamat){
                holder.radioButton.isChecked =true
                clicker.alamatAwal(currentItem)

                idalamat = null
            }
        }


        holder.radioButton.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked){

                positionSelect = holder.adapterPosition
                clicker.clickk(currentItem)
                Log.d("kocakk3", "dicek konfl")


            }else{
                    clicker.removeAlamat(currentItem.id.toString())
            }


        }


    }



    override fun getItemCount(): Int = differ.currentList.size
}