package com.example.ayamjumpa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.Clicker
import com.example.ayamjumpa.util.DiffUtilRepo

class HpAdapter(val clicker: Clicker) : RecyclerView.Adapter<HpAdapter.MyViewHolder>() {
    private val diffUtilRepo = DiffUtilRepo<String>()
    val differ = AsyncListDiffer(this, diffUtilRepo)
    var positionSelect = -1
    var noHp: String? = null

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nohp = itemView.findViewById<TextView>(R.id.nohp)
        val check = itemView.findViewById<CheckBox>(R.id.check)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        holder.check.isChecked = position == positionSelect

        if (noHp != null) {
            if (currentItem == noHp) {
                holder.check.isChecked = true
                clicker.setNoHP(currentItem)

                noHp = null

            }
        }

        holder.nohp.text = currentItem

        holder.check.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                positionSelect = holder.adapterPosition
                clicker.clickhp(currentItem)
            }else{
                clicker.removeAlamat(currentItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.nohp_viewholder, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount() = differ.currentList.size

}