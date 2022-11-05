package com.example.ayamjumpa.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.MenuListener
import com.example.ayamjumpa.interfaces.RecycleClickListener
import com.example.ayamjumpa.adapter.MenuAdapter
import com.example.ayamjumpa.dataClass.Cart
import com.example.ayamjumpa.dataClass.Menu
import com.example.ayamjumpa.databinding.FragmentMenuBinding


import com.example.ayamjumpa.eventBus.StatusMessage
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.greenrobot.eventbus.EventBus


class MenuFragment : Fragment(), MenuListener, RecycleClickListener {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var clickListener: RecycleClickListener
    private lateinit var menuListener: MenuListener
    private lateinit var mContext:Context
    private var berdasarkan = "makanan"
    private lateinit var kacrut : Task<QuerySnapshot>

    private var selected = 0
    private val arrayBerdasarkan = arrayOf("All", "Makanan", "Minuman", "Menu", "Rasa")
    private val menuModels: MutableList<Menu> = ArrayList()

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    val auth = Firebase.auth

    val firestore = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu, container, false)

        clickListener = this
        menuListener = this

        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        scope.launch {

            loadMenu()


        }



        binding.menuSerach.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {


                val searchVal = arrayListOf<Menu>()

                menuModels.forEach {
                    if (berdasarkan.equals(
                            it.tipe,
                            true
                        ) && it.nama!!.contains(newText as CharSequence, true)
                    ) {

                        searchVal.add(it)


                    } else if (selected == 0 && it.nama!!.contains(newText as CharSequence, true)
                    ) {
                        searchVal.add(it)
                    } else if (selected == 3 && it.tipe == "makanan" && it.menuKey!!.contains(
                            newText as CharSequence,
                            true
                        )
                    ) {
                        searchVal.add(it)

                    } else if (selected == 4 && it.tipe == "makanan" && it.rasaKey!!.contains(
                            newText as CharSequence,
                            true
                        )
                    ) {
                        searchVal.add(it)
                    }
                }

                menuAdapter.differ.submitList(searchVal)

                binding.scroller.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )


                return true
            }
        })





        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.berdasarkan.setOnClickListener {
            val prevSelected = selected
            val prevBerdasarkan = berdasarkan
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cari Berdasarkan")
                .setSingleChoiceItems(arrayBerdasarkan, selected) { dialog, which ->
                    selected = which

                    berdasarkan = arrayBerdasarkan[which]

                }.setPositiveButton("OK") { dialog, which ->

                    if (selected <= 2 && selected != 0) {
                        val x = menuModels
                        clearQuery()
                        menuAdapter.differ.submitList(x.filter { menu ->
                            menu.tipe.equals(
                                berdasarkan,
                                true
                            )
                        })
                    } else if (selected == 0) {
                        clearQuery()
                        menuAdapter.differ.submitList(menuModels as List<Menu>)


                    } else if (selected > 2) {
                        clearQuery()
                        menuAdapter.differ.submitList(menuModels.filter { menu ->
                            menu.tipe.equals(
                                "makanan",
                                true
                            )
                        })
                    }


                }.setCancelable(false)
                .setNegativeButton("Cancel") { dialog, which ->
                    selected = prevSelected
                    berdasarkan = prevBerdasarkan
                    dialog.dismiss()
                }
                .show()

        }
    }

    private fun clearQuery() {
        binding.menuSerach.setQuery("", false)
    }

    override fun onDestroyView() {

          super.onDestroyView()


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    suspend fun loadMenu() {
        withContext(Dispatchers.IO) {


            val menucollection = firestore.collection("Menu").get()



    kacrut =   menucollection.addOnCompleteListener {
                if (it.result != null && it.isSuccessful) {
                    menuModels.clear()
                    for (datax in it.result) {

                        val menuModel = datax.toObject<Menu>()
                        menuModel.key = datax.id
                        menuModels.add(menuModel)
                    }

                    onMenuLoadSucces(menuModels)
                } else {
                    EventBus.getDefault().post(StatusMessage(it.exception.toString()))
                }
            }
        }
    }


    fun dataInitMenu(layoutManager: LinearLayoutManager, adapter: MenuAdapter) {


        val recyclerViewMenu = binding.listMenu

        recyclerViewMenu.layoutManager = layoutManager

        recyclerViewMenu.adapter = adapter

        //  recyclerViewMenu.setHasFixedSize(true)


    }

    fun addToCart(menu: Menu, uid: String) {

        val userCart = firestore
            .collection("Cart")
            .document(uid).collection("myCart")

        userCart.document(menu.key!!)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful && it.result.exists()) {
                    val mycart = it.result.toObject<Cart>()
                    val qty = mycart!!.qty + 1
                    val total = qty * mycart.harga!!.toInt()
                    userCart.document(menu.key!!)
                        .update("qty", qty.toLong(), "totalharga", total.toLong())
                        .addOnSuccessListener {
                            Snackbar.make(
                                requireView(),
                                "Barang berhasil dimasukan ke dalam keranjang",
                                Snackbar.LENGTH_SHORT
                            ).show()


                        }.addOnCanceledListener {
                            EventBus.getDefault().post(StatusMessage(it.exception.toString()))
                        }
                } else {
                    val mycart = Cart()

                    mycart.key = menu.key
                    mycart.qty = 1
                    mycart.totalharga = menu.harga.toLong()
                    mycart.name = menu.nama
                    mycart.harga = menu.harga.toString()
                    mycart.foto = menu.foto

                    userCart.document(mycart.key!!).set(mycart).addOnSuccessListener {
                        //  EventBus.getDefault().post(StatusMessage("Barang berhasil dimasukan ke dalam keranjang"))

                    }.addOnCanceledListener {
                        EventBus.getDefault().post(StatusMessage(it.exception.toString()))


                    }


                }
            }


    }

    override fun onMenuLoadSucces(menuList: MutableList<Menu>) {


        if (!this::menuAdapter.isInitialized) {
            menuAdapter = MenuAdapter(mContext, clickListener)

            dataInitMenu(layoutManager, menuAdapter)
        }
        val bundle = arguments
        if (!bundle!!.isEmpty) {
            val args = MenuFragmentArgs.fromBundle(bundle)
            val nama = args.query[0]
            val tipe = args.query[1]
            binding.menuSerach.setQuery(nama, false)
            val temp = mutableListOf<Menu>()
            if (tipe == "menu") {
                selected = 3
                berdasarkan = "menu"
            } else {
                selected = 4
                berdasarkan = "rasa"
            }
            menuList.forEach { menu ->


                if (if (tipe == "menu") menu.menuKey == nama else menu.rasaKey == nama) {

                    temp.add(menu)
                }

            }

            Log.d("bgst", temp.size.toString())

            menuAdapter.differ.submitList(temp)


        } else {

            menuAdapter.differ.submitList(menuList)
        }


    }

    override fun onItemClickListener(menu: Menu) {

        addToCart(menu, auth.uid!!)
    }

    override fun onMenuLoadFailed(message: String?) {

    }
}