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
import kotlinx.coroutines.*


class OptionDialog(
    private val navigasi: (Array<String>) -> Unit,
    private val menuList: MutableList<Menu>,
    private val disableColor: () -> Unit
) : DialogFragment() {

    private lateinit var binding: DialogoptionBinding
    private lateinit var dialog: AlertDialog
    private lateinit var kategoriAdapter: KategoriListAdapter
    var indikator = "cau"
    private lateinit var hashMap: HashMap<String?, Int>
    private var menumakanan = menuList.filter { menu -> menu.tipe.equals("makanan", true) }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogoptionBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())

        builder.setView(binding.root)

        dialog = builder.create()



        setupList()



        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val wmlp = dialog.window!!

        wmlp.setGravity(Gravity.BOTTOM)

        val params = wmlp.attributes

        params.y = 220

        wmlp.attributes = params

        wmlp.setWindowAnimations(R.style.DialogNoAnimation)




        return dialog
    }


    @SuppressLint("NotifyDataSetChanged")


    fun setupList() {

        kategoriAdapter = KategoriListAdapter(requireContext(), navigaton = {
            navigasi.invoke(it)
        })
        CoroutineScope(Dispatchers.Default).launch {
            when (indikator) {
                "menu" -> olahMenu()
                "rasa" -> olahRasa()
                "minuman" -> olahMinuman()
            }
        }


        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.recyclerKategori.adapter = kategoriAdapter
        binding.recyclerKategori.layoutManager = layoutManager


    }

    private suspend fun olahMinuman() {
        coroutineScope {
            var jumlah:Map<String?,Int> = mapOf()
            val arrayStringMenu = arrayListOf<String>()
            val menuminuman=menuList.filter { menu -> menu.tipe=="minuman"  }

            menuminuman.forEach {


                    if (!arrayStringMenu.contains(it.tipeMinuman)) {
                        arrayStringMenu.add(it.tipeMinuman!!)
                    }


                }

                 jumlah = menuminuman.groupingBy { it.tipeMinuman }.eachCount()



            kategoriAdapter.indicator = "minuman"
            kategoriAdapter.setHashMap(jumlah)

            kategoriAdapter.differ.submitList(arrayStringMenu)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        disableColor.invoke()
    }

    private suspend fun olahMenu() {
        coroutineScope {
            val arrayStringMenu = arrayListOf<String>()


            menumakanan.forEach {
                if (!arrayStringMenu.contains(it.menuKey)) {
                    arrayStringMenu.add(it.menuKey!!)
                }
            }

            val jumlah = menumakanan.groupingBy { it.menuKey }.eachCount()




            kategoriAdapter.indicator = "menu"
            kategoriAdapter.setHashMap(jumlah)

            kategoriAdapter.differ.submitList(arrayStringMenu)
        }


    }

    private suspend fun olahRasa() {
        coroutineScope {
            val arrayStringMenu = arrayListOf<String>()

            menumakanan.forEach {
                if (!arrayStringMenu.contains(it.rasaKey)) {
                    arrayStringMenu.add(it.rasaKey!!)
                }
            }

            val jumlah = menumakanan.groupingBy { it.rasaKey }.eachCount()

            Log.d("ayamm", jumlah["ayam geprek"].toString())


            kategoriAdapter.indicator = "rasa"
            kategoriAdapter.setHashMap(jumlah)

            kategoriAdapter.differ.submitList(arrayStringMenu)
        }


    }

}