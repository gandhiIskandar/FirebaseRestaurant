package com.example.ayamjumpa.adapter

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.ayamjumpa.R
import com.example.ayamjumpa.dataClass.Alamat
import com.example.ayamjumpa.interfaces.OnKlikk
import com.example.ayamjumpa.util.DiffUtilRepo

class ListHpAdapter(private val onKlikk: OnKlikk<Any>): RecyclerView.Adapter<ListHpAdapter.MyViewHolder>() {
    private val diffUtilRepo = DiffUtilRepo<String>()
    val differ = AsyncListDiffer(this, diffUtilRepo)

    private var editNomor:String?=null

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        //sebelum edit
        val nohptv = itemView.findViewById<TextView>(R.id.nohp)
        val editt = itemView.findViewById<LinearLayout>(R.id.editt)
        val hapuss = itemView.findViewById<LinearLayout>(R.id.hapuss)

        //saat edit
        val nohpet = itemView.findViewById<EditText>(R.id.nohp_edit)
        val editSubmit = itemView.findViewById<LinearLayout>(R.id.editSubmit)
        val editCancel = itemView.findViewById<LinearLayout>(R.id.editCancel)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.nohpvh,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = differ.currentList[position]





        holder.apply {

            //v = cek jika editnomor sudah diset maka akan memunuclkan tampilan edit
            if(editNomor==currentItem && editNomor!=null){
                nohptv.visibility = View.GONE
                hapuss.visibility = View.GONE
                editt.visibility =View.GONE

                nohpet.visibility = View.VISIBLE
                editSubmit.visibility = View.VISIBLE
                editCancel.visibility = View.VISIBLE

                nohpet.text = editNomor!!.toEditable()
                nohpet.requestFocus()


            }
            //v ending

           if(position == 0 ){
               hapuss.visibility = View.GONE
           }

            hapuss.setOnClickListener {
                onKlikk.onClickHapus(currentItem)
            }

            editSubmit.setOnClickListener {
                onKlikk.onClickEdit(arrayOf(currentItem, nohpet.text.toString()))
                editNomor = null
                notifyItemChanged(position)

            }

            editCancel.setOnClickListener {
                editNomor = null
                notifyItemChanged(position)
            }

nohptv.text = currentItem



            editt.setOnClickListener {
                editNomor = currentItem
                notifyItemChanged(position)

            }
        }




    }

    override fun getItemCount(): Int {
      return  differ.currentList.size
    }



    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

}



















