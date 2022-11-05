package com.example.ayamjumpa.dialog


import android.animation.LayoutTransition
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.ayamjumpa.R
import com.example.ayamjumpa.databinding.DialogCheckoutBinding

import java.io.IOException


class CheckOutDialog(
private val total:String, private val proses:()->Unit
) : DialogFragment() {
    private lateinit var binding: DialogCheckoutBinding
    private var reqCode = -1
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = DialogCheckoutBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

//        binding.linearr.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        resultLauncher = resultLauncer()

        binding.uploadBukti.setOnClickListener {
            selectImage()

        }
//        binding.gantiPasswordSubmit.setOnClickListener {
//            proses.invoke()
//        }
        binding.totale.text = total

        binding.expand.setOnClickListener {
          expand()
        }

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    private fun expand(){


        val v =if(binding.norek.visibility==View.GONE) View.VISIBLE else View.GONE
//val transition = android.transition.AutoTransition()
//        transition.addTarget(binding.norek)
        if(v == View.GONE){
            binding.expand.setImageResource(R.drawable.ic_baseline_expand_more_24)
        }else{
            binding.expand.setImageResource(R.drawable.ic_baseline_expand_less_24)
        }

      //  TransitionManager.beginDelayedTransition(binding.linearr as ViewGroup, transition)

        binding.norek.visibility = v

    }


    private fun selectImage() {


        val items = arrayOf<String>("Take Photo", "Choose from Library", "Cancel")
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Ganti Foto Profile")



        builder.setItems(items) { dialog, item ->
            if (item == 0) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra("requestCode", "10")
                reqCode = 10


                resultLauncher.launch(intent)


            } else if (item == 1) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                intent.putExtra("requestCode", "20")
                reqCode = 20


                resultLauncher.launch(intent)


            } else {
                dialog.dismiss()
            }


        }
        builder.show()

    }

    fun resultLauncer(): ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->


        if (result.resultCode == Activity.RESULT_OK) {

         binding.tecter.visibility = View.GONE

            ( binding.buktiTransfer.layoutParams as LinearLayout.LayoutParams).weight = 1f

            binding.buktiTransfer.adjustViewBounds = true

            binding.uploadBukti.requestLayout()

            if (reqCode == 10) {
                val extras = result!!.data?.extras
                val bitmap = extras!!.get("data") as Bitmap
                Log.d("requCd", "masukk")

                Glide.with(requireContext())
                    .load(bitmap)
                    .into(binding.buktiTransfer)


            } else if (reqCode == 20) {
                try {
                    Log.d("requCd", "masukk1")
                    val path = result!!.data?.data
                    val inputStream = context?.contentResolver!!.openInputStream(path!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    if (bitmap != null) {

                        Glide.with(requireContext())
                            .load(bitmap)
                            .into(binding.buktiTransfer)


                    }


                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}