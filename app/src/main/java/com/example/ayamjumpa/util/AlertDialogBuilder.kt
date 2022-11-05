package com.example.ayamjumpa.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.widget.TextView
import com.example.ayamjumpa.R

class AlertDialogBuilder(private val activity: Activity){

  lateinit var alertDialog: AlertDialog

 fun startAlertDialog(statusnya:String){
    val alertBuilder = AlertDialog.Builder(activity)

    val inflater = activity.layoutInflater

     val inf = inflater.inflate(R.layout.loadingbar, null)
     inf.findViewById<TextView>(R.id.statuss).text = statusnya

    alertBuilder.setView(inf)
    alertBuilder.setCancelable(false)

    alertDialog = alertBuilder.create()

    alertDialog.show()


}




    fun dismiss(){
        alertDialog.dismiss()
    }


}



