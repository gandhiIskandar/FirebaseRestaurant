package com.example.ayamjumpa.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.ayamjumpa.databinding.GantiPasswordDialogBinding

class GantiPasswordDialog(
    private val onSubmitClickListener: (List<String>) -> Unit
) : DialogFragment() {

    private lateinit var binding: GantiPasswordDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = GantiPasswordDialogBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireActivity())

        builder.setView(binding.root)


        binding.gantiPasswordSubmit.setOnClickListener {
            onSubmitClickListener.invoke(
                listOf(
                    binding.passwordLama.text.toString(),
                    binding.passwordBaru.text.toString(),
                    binding.passwordBaru1.text.toString()
                )

            )
            dismiss()
        }

        val dialog = builder.create()

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog

    }


}