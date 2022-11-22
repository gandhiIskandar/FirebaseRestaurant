package com.example.ayamjumpa.adapter

import android.media.Image
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
import com.example.ayamjumpa.dataClass.Pesanan
import com.example.ayamjumpa.util.DiffUtilRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PesananAdapter(private val onClick: (Pesanan) -> Unit) :
    RecyclerView.Adapter<PesananAdapter.MyViewHolder>() {
    private val diffUtilRepo = DiffUtilRepo<Pesanan>()
    val differ = AsyncListDiffer(this, diffUtilRepo)
    private lateinit var holder:MyViewHolder


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LayoutInflater.from(parent.context).inflate(R.layout.pesanan_viewholder, parent, false)
        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        this.holder= holder

        setStatus(currentItem.status!!)

        if (currentItem.expanded) {
            holder.groupView.visibility = View.VISIBLE
            holder.expandView.setImageResource(R.drawable.ic_baseline_expand_less_24)

        } else {
            holder.groupView.visibility = View.GONE
            holder.expandView.setImageResource(R.drawable.ic_baseline_expand_more_24)

        }

        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault())
        val date = currentItem.tanggal.toDate()

        val dateString = simpleDateFormat.format(date)


        holder.text.text = currentItem.alamat!!.alamat
        holder.tanggal.text = dateString
        holder.total.text = formatRupiah(currentItem.totalHarga!!)
        holder.id.text = currentItem.id!!.substring(0,7)
        holder.hargaItem.text =
            formatRupiah(currentItem.totalHarga!!.minus(currentItem.alamat!!.ongkir!!))

        holder.ongkir.text = formatRupiah(currentItem.alamat!!.ongkir!!)
        holder.totalsemua.text = formatRupiah(currentItem.totalHarga!!)
        holder.catatan.text = currentItem.catatan
        holder.expandView.setOnClickListener {


     differ.currentList.forEach {
          if(it.expanded && it.id != currentItem.id){
              it.expanded =false
              notifyItemChanged(differ.currentList.indexOf(it))
          }
      }
            if (!currentItem.expanded) {
                currentItem.expanded = true
                notifyItemChanged(position)
            }else{
                currentItem.expanded = false
                notifyItemChanged(position)
            }

        }


    }

    override fun getItemCount(): Int = differ.currentList.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val text = itemView.findViewById<TextView>(R.id.statusAlamat)
        val tanggal = itemView.findViewById<TextView>(R.id.statusTanggal)
        val total = itemView.findViewById<TextView>(R.id.statusTotal)
        val id = itemView.findViewById<TextView>(R.id.detailid)
        val hargaItem = itemView.findViewById<TextView>(R.id.detail_harga)
        val ongkir = itemView.findViewById<TextView>(R.id.detail_ongkir)
        val totalsemua = itemView.findViewById<TextView>(R.id.detail_totalsemua)
        val catatan = itemView.findViewById<TextView>(R.id.detail_catatan)

        val expandView = itemView.findViewById<ImageView>(R.id.expands)
        val groupView = itemView.findViewById<LinearLayout>(R.id.linearr)

        val ditolak = itemView.findViewById<ImageView>(R.id.indicator_ditolak)

        val proses = itemView.findViewById<ImageView>(R.id.indicator_proses)

        val pengiriman = itemView.findViewById<ImageView>(R.id.indicator_pengiriman)
    }

    private fun formatRupiah(number: Double): String {


        val localeID = Locale("in", "ID")

        val format = NumberFormat.getCurrencyInstance(localeID)

        return format.format(number)


    }

    private fun setStatus(current:String){

       when(current){
           "ditolak" -> ditolak()
           "proses1" -> proses1()
           "proses2" -> proses2()
           "pengiriman1" -> pengiriman1()
           "selesai" -> selesai()
           "pending" -> pending()
       }

    }

    private fun ditolak(){

        holder.ditolak.setImageResource(R.drawable.redddd)
        holder.proses.setImageResource(R.drawable.kosongeer)
        holder.pengiriman.setImageResource(R.drawable.kosongeer)

    }

    private fun proses1(){
        holder.ditolak.setImageResource(R.drawable.off)
        holder.proses.setImageResource(R.drawable.proses1)
        holder.pengiriman.setImageResource(R.drawable.kosongeer)
    }

    private fun proses2(){
        holder.proses.setImageResource(R.drawable.proses2)
        holder.pengiriman.setImageResource(R.drawable.kosongeer)
    }

    private fun pengiriman1(){
        holder.pengiriman.setImageResource(R.drawable.proses1)
    }

    private fun selesai(){
        holder.pengiriman.setImageResource(R.drawable.proses2)
    }
private fun pending(){
    holder.proses.setImageResource(R.drawable.kosongeer)
    holder.pengiriman.setImageResource(R.drawable.kosongeer)
}


}