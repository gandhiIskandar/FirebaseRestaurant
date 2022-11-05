package com.example.ayamjumpa.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.ayamjumpa.dataClass.Menu

import com.example.ayamjumpa.databinding.TambahKonfirmasiBinding

class TambahDialog(
    private val menu: Menu
    //private val onSubmitClickListener: (Menu) -> Unit
): DialogFragment() {
    private lateinit var binding:TambahKonfirmasiBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = TambahKonfirmasiBinding.inflate(LayoutInflater.from(context))



        val builder = AlertDialog.Builder(requireActivity())

        builder.setView(binding.root)

        Glide.with(requireContext()).load(menu.foto).into(binding.fotoMakanan)



        val dialog = builder.create()

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }


}