package com.example.ayamjumpa.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.ayamjumpa.R

class AlertDialogBuilder(private val activity: Activity) {

    lateinit var alertDialog: AlertDialog

    fun startAlertDialog(statusnya: String) {
        val alertBuilder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater

        val inf = inflater.inflate(R.layout.loadingbar, null)
        inf.findViewById<TextView>(R.id.statuss).text = statusnya

        val image = inf.findViewById<ImageView>(R.id.progressBar)

        Glide.with(activity).load(R.raw.ayamloading).into(image)

        alertBuilder.setView(inf)
        alertBuilder.setCancelable(false)

        alertDialog = alertBuilder.create()

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()


    }


    fun dismiss() {
        alertDialog.dismiss()
    }


}



