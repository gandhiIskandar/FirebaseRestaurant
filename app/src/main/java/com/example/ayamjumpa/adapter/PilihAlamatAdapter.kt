package com.example.ayamjumpa.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.Clicker
import com.example.ayamjumpa.dataClass.Alamat
import com.example.ayamjumpa.interfaces.OnKlikk
import com.example.ayamjumpa.util.DiffUtilRepo

class PilihAlamatAdapter(
    val clicker: Clicker,
    val editatauhapus: OnKlikk<Any>
) : RecyclerView.Adapter<PilihAlamatAdapter.MyViewHolder>() {
    private val diffUtilRepo = DiffUtilRepo<Alamat>()
    val differ = AsyncListDiffer(this, diffUtilRepo)
    var positionSelect = -1
    var idalamat: String? = null
    lateinit var context: Context

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val keterangan = itemView.findViewById<TextView>(R.id.noHpAlamatList)
        val alamat = itemView.findViewById<TextView>(R.id.alamatlengkap)
        val judul = itemView.findViewById<TextView>(R.id.status)
        val hapus = itemView.findViewById<Button>(R.id.hapused)
        val edit = itemView.findViewById<Button>(R.id.edited)
        val bookmark = itemView.findViewById<ImageView>(R.id.bookmark)

        val pilihed = itemView.findViewById<Button>(R.id.pilihed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.alamatholderuntukpilih, parent, false)



        context = parent.context

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        holder.hapus.setOnClickListener {
editatauhapus.onClickHapus(currentItem)
        }
        holder.edit.setOnClickListener {
            editatauhapus.onClickEdit(currentItem)
        }

        holder.keterangan.text = currentItem.nomorHp
        holder.alamat.text = currentItem.alamat
        holder.judul.text = currentItem.label

        holder.pilihed.setBackgroundColor(
            if (positionSelect == holder.adapterPosition) context.resources.getColor(
                R.color.primary
            ) else context.resources.getColor(R.color.abu_tua)
        )
        holder.pilihed.setTextColor(
            if (positionSelect == holder.adapterPosition) context.resources.getColor(
                R.color.abu_tua
            ) else context.resources.getColor(R.color.primary)
        )

        // holder.radioButton.isChecked = position == positionSelect

        if (idalamat != null) {
            if (currentItem.id == idalamat) {
                positionSelect = holder.adapterPosition
                holder.pilihed.setBackgroundColor(context.resources.getColor(R.color.primary))

                holder.pilihed.setTextColor(context.resources.getColor(R.color.abu_tua))
                clicker.alamatAwal(currentItem)

                idalamat = null
            }
        }


        holder.pilihed.setOnClickListener {

            if (positionSelect == holder.adapterPosition) {

                clicker.removeAlamat(currentItem.id.toString())

                positionSelect = -1
                notifyItemChanged(position)


            } else {
                //currentItem.checked = true
                positionSelect = holder.adapterPosition


                clicker.clickk(currentItem)
                Log.d("kocakk3", "dicek konfl")


            }


        }

        if(holder.adapterPosition == 0){

            holder.bookmark.setImageResource(R.drawable.ic_baseline_person_24)
            holder.hapus.visibility = View.GONE

        }


    }


    override fun getItemCount(): Int = differ.currentList.size
}