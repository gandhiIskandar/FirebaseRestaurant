package com.example.ayamjumpa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.ayamjumpa.R
import com.example.ayamjumpa.dataClass.Cart
import com.example.ayamjumpa.dataClass.Kategori
import com.example.ayamjumpa.databinding.DatapesananVhBinding
import com.example.ayamjumpa.util.DiffUtilRepo
import java.text.NumberFormat
import java.util.*

class ItemDetailAdapter(private val context: Context):RecyclerView.Adapter<ItemDetailAdapter.RecyclerViewHolder>() {

    private val diffUtilRepo = DiffUtilRepo<Cart>()

    val differ = AsyncListDiffer(this,diffUtilRepo)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(DatapesananVhBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

        val curItem= differ.currentList[position]


        holder.itemsTV.text = context.getString(R.string.items, curItem.name, curItem.qty.toString())
        holder.totalTV.text = formatRupiah(curItem.totalharga)

    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class RecyclerViewHolder(binding: DatapesananVhBinding):RecyclerView.ViewHolder(binding.root){

        val itemsTV = binding.datapesananItems
        val totalTV = binding.datapesananHarga


    }

    private fun formatRupiah(number: Long): String {


        val localeID = Locale("in", "ID")

        val format = NumberFormat.getCurrencyInstance(localeID)

        return format.format(number)


    }
}