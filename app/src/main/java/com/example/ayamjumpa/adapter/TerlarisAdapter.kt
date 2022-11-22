package com.example.ayamjumpa.adapter

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.RecycleClickListener
import com.example.ayamjumpa.dataClass.Menu
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.NumberFormat
import java.util.*

class TerlarisAdapter(
    val context: Context,
    private val terlaisList: MutableList<Menu>,
    private val clickListener: RecycleClickListener,

) : RecyclerView.Adapter<TerlarisAdapter.MyViewHolder>() {


    private lateinit var holder:MyViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.terlaris_viewholder, parent, false)


        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = terlaisList[position]
    this.holder = holder
        Glide.with(context)
            .load(currentItem.foto)
            .into(holder.image)


        holder.textnya.text = currentItem.nama!!.lowercase()

       // holder.textnya.paintFlags = holder.textnya.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.harga.text = currentItem.harga.toString()

        if(currentItem.promo){
            setPromo(currentItem)
        }

        holder.tambah.setOnClickListener {

            clickListener.onItemClickListener(currentItem)

        }


        holder.share.setOnClickListener {

      CoroutineScope(Dispatchers.IO).launch {
        val x=async {
         return@async   Glide.with(context)
            .asBitmap()
            .load(currentItem.foto)
            .submit()
            .get()  }

          withContext(Dispatchers.Main){
              val shareIntent = Intent(Intent.ACTION_SEND)

              shareIntent.type = "image/*"
              shareIntent.putExtra(Intent.EXTRA_TEXT, "Share ajalah iseng")

              shareIntent.putExtra(Intent.EXTRA_STREAM, saveImageToGallery(x.await()))



              context.startActivity(Intent.createChooser(shareIntent, "share"))
          }


      }






        }


    }

    override fun getItemCount(): Int {
        return terlaisList.size
    }

    private fun setPromo(item:Menu){
        holder.harga.textSize = 10f
        holder.harga.paintFlags = holder.harga.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.promoText.text = (item.harga - item.potongan).toString()
        holder.promoText.visibility = View.VISIBLE
        holder.promoTag.visibility = View.VISIBLE
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val share: ImageView = itemView.findViewById(R.id.buttonShare)
        val image: ImageView = itemView.findViewById(R.id.fotoTerlaris)
        val textnya: TextView = itemView.findViewById(R.id.produkTerlaris)
        val harga: TextView = itemView.findViewById(R.id.hargaTerlaris)
        val tambah: Button = itemView.findViewById(R.id.tambahTerlaris)
        val promoText:TextView=itemView.findViewById(R.id.hargaPromo)
        val promoTag:Button = itemView.findViewById(R.id.materialButtonpromo)

    }

    // cara kerja : Jika data ada maka akan tambah kuantitas item di cart,
    // jika data kosong maka akan tambah data baru
    //



    private fun saveImageToGallery(bitmap: Bitmap): Uri {

        lateinit var fos: OutputStream

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "share")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //this one
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + File.separator + "TestFolder"
                )
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )!!

        fos = context.contentResolver.openOutputStream(uri)!!

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

        return uri


    }

}

