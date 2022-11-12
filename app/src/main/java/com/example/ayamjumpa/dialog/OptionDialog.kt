package com.example.ayamjumpa.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ayamjumpa.R
import com.example.ayamjumpa.adapter.KategoriListAdapter
import com.example.ayamjumpa.dataClass.Menu
import com.example.ayamjumpa.databinding.DialogoptionBinding


class OptionDialog(private val navigasi:(Array<String>)->Unit, private val menuList:MutableList<Menu>,private val disableColor:()->Unit) : DialogFragment() {

    private lateinit var binding:DialogoptionBinding
    private lateinit var dialog:AlertDialog
    private lateinit var kategoriAdapter:KategoriListAdapter
    var indikator ="cau"


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding =DialogoptionBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())

        builder.setView(binding.root)

        dialog = builder.create()



        setupList()

setIndicatior(indikator)

    dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val wmlp = dialog.window!!

        wmlp.setGravity(Gravity.BOTTOM)

        val params = wmlp.attributes

        params.y =200

        wmlp.attributes = params

      wmlp.setWindowAnimations(R.style.DialogNoAnimation)




        return dialog
    }




    @SuppressLint("NotifyDataSetChanged")
    fun setIndicatior(indicator:String){
        kategoriAdapter.indicator = indicator
        kategoriAdapter.notifyDataSetChanged()
    }



    fun setupList(){
     kategoriAdapter = KategoriListAdapter(requireContext(), navigaton = {
            navigasi.invoke(it)
        })

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.recyclerKategori.adapter = kategoriAdapter
        binding.recyclerKategori.layoutManager = layoutManager
        kategoriAdapter.indicator = "menu"
        kategoriAdapter.differ.submitList(menuList.filter { menu ->
            menu.tipe.equals(
                "makanan",
                true
            )
        })

    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        disableColor.invoke()
    }
}