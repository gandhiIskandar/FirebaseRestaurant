package com.example.ayamjumpa.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.ayamjumpa.R
import com.example.ayamjumpa.databinding.MenuVhBinding
import com.example.ayamjumpa.interfaces.Navigation
import com.example.ayamjumpa.util.DiffUtilRepo

class NavAdapter(private val context: Context, private val navigation: Navigation) :
    RecyclerView.Adapter<NavAdapter.MyViewHolder>() {
    private val diffUtilRepo = DiffUtilRepo<String>()

    val differ = AsyncListDiffer(this, diffUtilRepo)
    var cartCount = 0
    var position = -1

    class MyViewHolder(binding: MenuVhBinding) : RecyclerView.ViewHolder(binding.root) {

        val text = binding.navMenu
        val badge = binding.cartBadge
        val circleImageView = binding.circleImageView2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MenuVhBinding.inflate(LayoutInflater.from(context))
        return MyViewHolder(binding)

    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val curItem = differ.currentList[position]

        if(this.position!=-1){
            Log.d("post",this.position.toString())
        }



        if (this.position == position) {
            holder.circleImageView.setImageResource(R.color.primary)

            holder.text.setTextColor(context.resources.getColor(R.color.abu_tua))

        } else {
            holder.circleImageView.setImageResource(R.color.abu_tua)

            holder.text.setTextColor(context.resources.getColor(R.color.primary))

        }



        if(curItem=="order"){

        }


        if (curItem.equals("order", true) && cartCount > 0) {



            holder.badge.visibility = View.VISIBLE
            holder.badge.setNumber(cartCount)
        } else {


            holder.badge.visibility = View.GONE
        }

        holder.text.text = curItem

        holder.itemView.setOnClickListener {

            if (curItem == "order" && cartCount == 0) {
        //kosongiin ajee bro
            } else {
                if (this.position != position) {
                    if (this.position >= 0) {
                        notifyItemChanged(this.position)
                    }


                    this.position = position
                    notifyItemChanged(position)
                } else {
                    this.position = -1
                    notifyItemChanged(position)
                }
            }


            when (curItem) {
                "menu" -> {
                    navigation.showMenu()
                }
                "rasa" -> {
                    navigation.showRasa()
                }
                "order" -> {
                    navigation.showCart(cartCount)
                }
                "pesanan" -> {
                    navigation.toPesanan()
                }
                "saya" -> {
                    navigation.toProfile()
                }
            }


        }

    }

    override fun getItemCount(): Int = differ.currentList.size
}