package com.example.ayamjumpa.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ayamjumpa.adapter.HpAdapter
import com.example.ayamjumpa.adapter.PilihAlamatAdapter
import com.example.ayamjumpa.dataClass.Alamat
import com.example.ayamjumpa.databinding.PilihalamatdialogBinding
import com.example.ayamjumpa.databinding.PilihnohpdialogBinding
import com.example.ayamjumpa.interfaces.Clicker

class PilihNoHpDialog(private val onClick: (String) -> Unit) : DialogFragment(), Clicker {
    private lateinit var binding: PilihnohpdialogBinding
    private lateinit var adapter: HpAdapter
    private lateinit var recycler: RecyclerView
    private var nomorku: String? = null
    private var nomorarray: MutableList<String> = arrayListOf()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = PilihnohpdialogBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        recycler = binding.recylernohp
        recycler.layoutManager = layoutManager
        recycler.setHasFixedSize(true)

        adapter = HpAdapter(this)

        adapter.differ.submitList(nomorarray)

        if (nomorku != null) {
            adapter.noHp = nomorku
        }

        recycler.adapter = adapter

        binding.gantiPasswordSubmit.setOnClickListener {

            if (nomorku != null) {
                onClick.invoke(nomorku!!)
            }

            dismiss()
        }

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    override fun clickk(alamat: Alamat) {

    }

    override fun alamatAwal(alamat: Alamat) {

    }

    fun setChecked(id: String) {
        nomorku = id
    }

    override fun removeAlamat(id: String) {
        if(nomorku==id){
            nomorku=null
        }

    }

    fun setData(nomor: MutableList<String>) {
        nomorarray = nomor
    }

    override fun setNoHP(hp: String) {
        nomorku = hp
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun clickhp(hp: String) {
        recycler.post { adapter.notifyDataSetChanged() }
        nomorku = hp
    }


}