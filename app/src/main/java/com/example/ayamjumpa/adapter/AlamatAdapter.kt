package com.example.ayamjumpa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.OnClickMapViewHolder
import com.example.ayamjumpa.dataClass.Alamat
import com.example.ayamjumpa.util.DiffUtilRepo

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

        val expandView = itemView.findViewById<LinearLayout>(R.id.expanded)

        val btnEdit = itemView.findViewById<LinearLayout>(R.id.btnEdit)
        val btnHapus= itemView.findViewById<LinearLayout>(R.id.btnHapus)


    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
     val currentItem =differ.currentList[position]

        val visible = currentItem.expandview

        holder.expandView.visibility = if(visible) View.VISIBLE else View.GONE
        holder.keterangan.visibility = if(visible) View.VISIBLE else View.GONE
        holder.alamatlengkap.text = currentItem.alamat
        holder.buttonExpand.setImageResource(if(visible) R.drawable.ic_baseline_expand_less_24 else R.drawable.ic_baseline_expand_more_24)


        holder.buttonExpand.setOnClickListener {

          currentItem.expandview = !currentItem.expandview
            notifyItemChanged(position)

        }

        holder.btnEdit.setOnClickListener {
            alamatListener.editClik(currentItem)
        }

        holder.btnHapus.setOnClickListener {
            alamatListener.removeClik(currentItem)
        }

        holder.judul.text = currentItem.label
        holder.keterangan.text =currentItem.keterangan


    }
}