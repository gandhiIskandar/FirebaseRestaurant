package com.example.ayamjumpa.adapter

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.RecycleClickListener
import com.example.ayamjumpa.dataClass.Menu
import com.example.ayamjumpa.util.DiffUtilRepo
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.NumberFormat
import java.util.*

class TerlarisAdapter(
    val context: Context,
    private val clickListener: RecycleClickListener,

    ) : RecyclerView.Adapter<TerlarisAdapter.MyViewHolder>() {


    private lateinit var holder: MyViewHolder
    private val diffUtilRepo = DiffUtilRepo<Menu>()
    val differ = AsyncListDiffer(this, diffUtilRepo)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.terlaris_linearviewholder, parent, false)


        return MyViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = differ.currentList[position]
        this.holder = holder
        Glide.with(context)
            .load(currentItem.foto)
            .into(holder.image)


        holder.textnya.text = currentItem.nama!!.lowercase()

        holder.harga.text = formatRupiah(currentItem.harga)
        holder.rasaText.text = currentItem.rasaKey.toString()

        if(currentItem.stok.toInt()==0){
            holder.tambah.isEnabled = false
            val matrix = ColorMatrix()
            matrix.setSaturation(0F)
            holder.image.colorFilter = ColorMatrixColorFilter(matrix)
        }

        if(currentItem.tipe == "minuman"){
            holder.rasaText.text = currentItem.deskripsi1
        }


        if (holder.textnya.text.length > 15) {
            holder.textnya.text = "${holder.textnya.text.substring(0, 16).trim()}..."
        }

        if(currentItem.rasaKey.toString().length >15){
            holder.rasaText.text = "${currentItem.rasaKey?.substring(0, 15)?.trim()}..."
        }

        if (currentItem.promo && currentItem.rekom) {

            setPromo(currentItem)
            holder.rekomm.visibility = View.VISIBLE

        } else if (currentItem.promo) {
            setPromo(currentItem)
        } else if (currentItem.rekom) {
            holder.promoTag.visibility = View.VISIBLE
            holder.promoTag.text = "rekom"
        }



        holder.tambah.setOnClickListener {

            clickListener.onItemClickListener(currentItem)

        }


        holder.share.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                val x = async {
                    Glide.with(context)
                        .asBitmap()
                        .load(currentItem.foto)
                        .submit()
                        .get()
                }

                withContext(Dispatchers.Main) {
                    val shareIntent = Intent(Intent.ACTION_SEND)

                    shareIntent.type = "image/*"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, currentItem.deskripsi1)

                    shareIntent.putExtra(Intent.EXTRA_STREAM, saveImageToGallery(x.await()))


                    context.startActivity(Intent.createChooser(shareIntent, "share"))
                }


            }


        }


    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private fun setPromo(item: Menu) {
        holder.harga.textSize = 10f
        holder.harga.paintFlags = holder.harga.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.promoText.text = formatRupiah(item.harga - item.potongan)
        holder.promoText.visibility = View.VISIBLE
        holder.promoTag.visibility = View.VISIBLE
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val share: ImageView = itemView.findViewById(R.id.buttonShare)
        val image: ImageView = itemView.findViewById(R.id.fotoTerlaris)
        val textnya: TextView = itemView.findViewById(R.id.produkTerlaris)
        val harga: TextView = itemView.findViewById(R.id.hargaTerlaris)
        val tambah: Button = itemView.findViewById(R.id.tambahTerlaris)
        val promoText: TextView = itemView.findViewById(R.id.hargaPromo)
        val promoTag: Button = itemView.findViewById(R.id.materialButtonpromo)
        val rasaText: TextView = itemView.findViewById(R.id.produkrasa)
        val rekomm: Button = itemView.findViewById(R.id.materialButtonrekom)


    }

    private fun formatRupiah(number: Long): String {


        val localeID = Locale("in", "ID")

        val format = NumberFormat.getCurrencyInstance(localeID)
        var withrp = format.format(number)

        withrp = withrp.substring(2, withrp.lastIndex + 1)

        if (withrp.contains(',')) {

            val lastidx = withrp.indexOf(',')

            withrp = withrp.substring(0, lastidx)

        }


        return withrp


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

