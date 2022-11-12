package com.example.ayamjumpa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.ayamjumpa.R
import com.example.ayamjumpa.dataClass.Pesanan
import com.example.ayamjumpa.util.DiffUtilRepo
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PesananAdapter(private val onClick:(Pesanan)->Unit): RecyclerView.Adapter<PesananAdapter.MyViewHolder>() {
    private val diffUtilRepo = DiffUtilRepo<Pesanan>()
    val differ = AsyncListDiffer(this, diffUtilRepo)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
      val binding =LayoutInflater.from(parent.context).inflate(R.layout.pesanan_viewholder, parent, false)
        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem=differ.currentList[position]

      val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault())
        val date = currentItem.tanggal.toDate()

        val dateString = simpleDateFormat.format(date)


        holder.text.text = currentItem.alamat!!.alamat
        holder.tanggal.text = dateString
        holder.total.text = formatRupiah(currentItem.totalHarga!!)


        holder.buttonDetail.setOnClickListener {
            onClick.invoke(currentItem)

        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val text = itemView.findViewById<TextView>(R.id.statusAlamat)
        val tanggal = itemView.findViewById<TextView>(R.id.statusTanggal)
        val total = itemView.findViewById<TextView>(R.id.statusTotal)
        val buttonDetail = itemView.findViewById<Button>(R.id.statusButton)

    }

    private fun formatRupiah(number: Double): String {


        val localeID = Locale("in", "ID")

        val format = NumberFormat.getCurrencyInstance(localeID)

        return format.format(number)


    }


}