package com.example.ayamjumpa.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.Clicker
import com.example.ayamjumpa.adapter.PilihAlamatAdapter
import com.example.ayamjumpa.dataClass.Alamat
import com.example.ayamjumpa.databinding.PilihalamatdialogBinding

class PilihAlamatDialog(private val onClick: (Alamat) -> Unit,private val pindah: () -> Unit) : DialogFragment(), Clicker {
    private lateinit var binding: PilihalamatdialogBinding
    private lateinit var adapter: PilihAlamatAdapter
    private lateinit var recycler: RecyclerView
    private var alamatku: Alamat? = null
    private val alamat: MutableList<Alamat> = arrayListOf()
    private var idalamat: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = PilihalamatdialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())

        builder.setView(binding.root)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler = binding.recylerAlamat
        recycler.layoutManager = layoutManager
        recycler.setHasFixedSize(true)

        adapter = PilihAlamatAdapter(this)

        adapter.differ.submitList(alamat)

        if (idalamat != null) {
            adapter.idalamat = idalamat
        }

        binding.closee.setOnClickListener {
            dismiss()
        }
        binding.tambahAlamatt.setOnClickListener {
            dismiss()

           pindah.invoke()
        }
        recycler.adapter = adapter

        binding.gantiPasswordSubmit.setOnClickListener {


            if (alamatku != null) {

                Log.d("kocakk3", alamatku!!.alamat!!)

                onClick.invoke(alamatku!!)
            }


            dismiss()
        }

        val dialog = builder.create()

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun clickk(alamat: Alamat) {

        Log.d("bajingan", alamat.keterangan.toString())
        alamatku = alamat
        recycler.post { adapter.notifyDataSetChanged() }


    }

    override fun removeAlamat(id: String) {


        if (alamatku!!.id == id) {
            alamatku = null
        }


    }

    fun setAlamat(alamat: MutableList<Alamat>) {

        this.alamat.addAll(alamat)


    }

    fun setChecked(id: String) {

        idalamat = id

    }

    override fun alamatAwal(alamat: Alamat) {
        alamatku = alamat
    }

    override fun setNoHP(hp: String) {
//kosong


    }

    override fun clickhp(hp: String) {
        //kosong
    }
}