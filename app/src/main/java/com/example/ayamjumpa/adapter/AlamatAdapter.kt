package com.example.ayamjumpa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.OnClickMapViewHolder
import com.example.ayamjumpa.dataClass.Alamat
import com.example.ayamjumpa.util.DiffUtilRepo
import java.lang.StringBuilder

class AlamatAdapter( private val alamatListener :OnClickMapViewHolder): RecyclerView.Adapter<AlamatAdapter.MyViewHolder>() {

    private val diffUtilRepo = DiffUtilRepo<Alamat>()
    val differ = AsyncListDiffer(this, diffUtilRepo)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.alamatviewholder, parent, false)
        return AlamatAdapter.MyViewHolder(itemView)
    }

    override fun getItemCount(): Int =differ.currentList.size


    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val alamatlengkap = itemView.findViewById<TextView>(R.id.alamatlengkap)
        val keterangan = itemView.findViewById<TextView>(R.id.keteranganalamat)
        val judul = itemView.findViewById<TextView>(R.id.status)
        val buttonExpand = itemView.findViewById<ImageView>(R.id.expand)

        val bookmark = itemView.findViewById<ImageView>(R.id.bookmark)

        val expandView = itemView.findViewById<LinearLayout>(R.id.expanded)

        val btnEdit = itemView.findViewById<Button>(R.id.edittss)
        val btnHapus= itemView.findViewById<Button>(R.id.hapusss)


    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
     val currentItem =differ.currentList[position]

        val visible = currentItem.checked

        holder.expandView.visibility = if(visible) View.VISIBLE else View.GONE
        holder.keterangan.visibility = if(visible) View.VISIBLE else View.GONE
        holder.alamatlengkap.text = currentItem.alamat
        holder.buttonExpand.setImageResource(if(visible) R.drawable.ic_baseline_expand_less_24 else R.drawable.ic_baseline_expand_more_24)




        holder.buttonExpand.setOnClickListener {

          currentItem.checked = !currentItem.checked
            notifyItemChanged(position)

        }

        holder.btnEdit.setOnClickListener {
            alamatListener.editClik(currentItem)
        }

        holder.btnHapus.setOnClickListener {
            alamatListener.removeClik(currentItem)
        }
        holder.judul.text = currentItem.label
        if(holder.judul.text.length>15){
            val text = holder.judul.text.substring(0,15)
            val fix = "${text}...."
            holder.judul.text = fix
        }

        if(holder.adapterPosition == 0){
            holder.bookmark.setImageResource(R.drawable.ic_baseline_person_24)
            holder.btnHapus.visibility =View.GONE
        }


        holder.keterangan.text =currentItem.nomorHp


    }
}