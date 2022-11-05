package com.example.ayamjumpa.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.ayamjumpa.databinding.TambahnomordialogBinding

class TambahNomordialog(
    private val tambahNomor:(String) -> Unit
) : DialogFragment() {
    private lateinit var binding:TambahnomordialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog {
     binding = TambahnomordialogBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireActivity())

        builder.setView(binding.root)

        val nomor =binding.nomorBaru.text

        binding.gantiPasswordSubmit.setOnClickListener {
            tambahNomor.invoke(nomor.toString())
        }

        val dialog = builder.create()

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog

    }
}