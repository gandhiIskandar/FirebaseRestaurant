package com.example.ayamjumpa.adapter

import android.content.Context
import android.graphics.Paint
import android.text.Editable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.CartLoadListener
import com.example.ayamjumpa.dataClass.Cart
import org.w3c.dom.Text
import java.text.NumberFormat
import java.util.*

class MyCartAdapter(
    private val context: Context,
    private val cartModelList:List<Cart>,
    private val cartLoadListener: CartLoadListener
    ):RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {

        class MyCartViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

            var btnMinus:ImageView? = null
            var btnPlus:ImageView? = null
            var imageView:ImageView? = null
            var txtName: TextView?=null
            var txtPrice:TextView?=null
            var qty:TextView?=null
            var deskripsi1:TextView?=null
            var deskripsi:TextView?=null

            var cartdesc = itemView.findViewById<TextView>(R.id.cartdesc)
            var cartdesc1 = itemView.findViewById<TextView>(R.id.cartdesc1)
            var promotext = itemView.findViewById<TextView>(R.id.carthargapromo)


            init {
                btnMinus = itemView.findViewById(R.id.btnMinus)
                btnPlus = itemView.findViewById(R.id.btnPlus)
                imageView = itemView.findViewById(R.id.foto_cart)
                txtName = itemView.findViewById(R.id.cartnama)
                txtPrice = itemView.findViewById(R.id.cartharga)
                qty = itemView.findViewById(R.id.qty)
               deskripsi = itemView.findViewById(R.id.cartdesc)
               deskripsi1 = itemView.findViewById(R.id.cartdesc1)


            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
       return MyCartViewHolder(LayoutInflater.from(context)
           .inflate(R.layout.cart_viewholder,parent,false))


    }

    private fun formatRupiah(number:Long) : String{


        val localeID = Locale("in", "ID")

        val format = NumberFormat.getCurrencyInstance(localeID)

        return format.format(number)



    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        val currentItem = cartModelList[position]

        Glide.with(context)
            .load(cartModelList[position].foto)
            .into(holder.imageView!!)


        holder.txtName!!.text = currentItem.name!!.lowercase()

        holder.cartdesc.text = currentItem.desc1!!.lowercase()
        holder.cartdesc1.text = currentItem.desc2!!.lowercase()
        holder.txtPrice!!.text = formatRupiah(currentItem.totalharga)
       // holder.txtTotalPrice!!.text = currentItem.totalharga.toString()


        if(currentItem.potongan>0L){
            holder.promotext.visibility = View.VISIBLE
            holder.txtPrice!!.textSize = 13f

            val hargaasli = currentItem.harga!!.toLong() + currentItem.potongan

            holder.txtPrice!!.text = formatRupiah(hargaasli)

            holder.txtPrice!!.paintFlags = holder.txtPrice!!.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.promotext.text = formatRupiah(currentItem.harga!!.toLong())

        }


        holder.qty!!.text = currentItem.qty.toString()

        holder.btnMinus!!.setOnClickListener {
            if(holder.qty!!.text.toString() == "1"){
                cartLoadListener.deleteItem(currentItem)
            }else{
                minusCartItem(holder,currentItem)
            }


        }

        holder.btnPlus!!.setOnClickListener {
            plusCartItem(holder,currentItem)
        }





    }

    private fun minusCartItem(holder: MyCartViewHolder, cart: Cart) {

        if(cart.qty>1){
            cart.qty -=1
            cart.totalharga = cart.qty * cart.harga!!.toLong()
            holder.qty!!.text = cart.qty.toString()
            cartLoadListener.UpdateData(cart)

        }


    }
    private fun plusCartItem(holder: MyCartViewHolder, cart: Cart) {

            cart.qty +=1
            cart.totalharga = cart.qty * cart.harga!!.toLong()
            holder.qty!!.text = cart.qty.toString()
            cartLoadListener.UpdateData(cart)


    }




    override fun getItemCount(): Int {
       return cartModelList.size
    }


    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
}

