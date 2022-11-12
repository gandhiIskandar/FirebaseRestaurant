package com.example.ayamjumpa.fragment


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ayamjumpa.R
import com.example.ayamjumpa.adapter.ListHpAdapter
import com.example.ayamjumpa.dataClass.User
import com.example.ayamjumpa.databinding.FragmentNomorHandphoneBinding
import com.example.ayamjumpa.dialog.TambahNomordialog

import com.example.ayamjumpa.interfaces.OnKlikk
import com.example.ayamjumpa.viewModel.CartViewModel
import com.example.ayamjumpa.viewModel.CartViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*


//pengaplikasian view binding bukan data binding

class NomorHandphoneFragment : Fragment(), OnKlikk<Any> {

    private lateinit var binding: FragmentNomorHandphoneBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var listHpAdapter: ListHpAdapter
    private lateinit var uid: String
    private lateinit var hudsgthe: Array<String>
    private lateinit var nomoraingg: String
    private lateinit var vm: CartViewModel
    private lateinit var baba: ListenerRegistration


    private lateinit var nohpList: MutableList<String>

    private val job = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNomorHandphoneBinding.inflate(inflater, container, false)

        firebaseAuth = Firebase.auth
        firebaseFirestore = Firebase.firestore
        uid = firebaseAuth.uid!!

        listHpAdapter = ListHpAdapter(this)
        val viewModel: CartViewModel by lazy {
            ViewModelProvider(
                this,
                CartViewModelFactory(requireContext()),
            )[CartViewModel::class.java]
        }

        vm = viewModel
        vm.getNomor()

        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.recyclerHP.layoutManager = linearLayoutManager
        binding.recyclerHP.adapter = listHpAdapter
        binding.recyclerHP.setHasFixedSize(true)


        binding.tambahNomor.setOnClickListener {
            TambahNomordialog(
                tambahNomor = { nomor ->
                    val yaya = job.launch { updateNomor(false, nomor, true) }
                    if (yaya.isCompleted) {
                        yaya.cancel()
                    }
                }
            ).show(parentFragmentManager, "dialog")
        }


        job.launch {
            getNomor()



            vm.nomorkuX?.observe(viewLifecycleOwner, Observer {
                Log.d("hhhhh", it)
                nomoraingg = it
            })
        }


    }

    override fun onClickEdit(data: Any) {

        hudsgthe = data as Array<String>
        val idx = nohpList.indexOf(hudsgthe[0])

        nohpList[idx] = hudsgthe[1]



        job.launch { updateNomor(false, hudsgthe[0], false) }


    }

    override fun onClickHapus(data: Any) {

        val idx = nohpList.indexOf(data)

        nohpList.removeAt(idx)

        alertBuilderDelete(data as String)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        baba.remove()
    }

    suspend fun getNomor() {
        withContext(Dispatchers.IO) {

            baba = firebaseFirestore.collection("users").document(uid)
                .addSnapshotListener { value, error ->
                    if (value?.toObject<User>()?.noHPlain != null) {
                        if (value.toObject<User>()!!.noHPlain?.size!! > 0) {

                            Log.d("bagongg", "belis")
                            nohpList = value.toObject<User>()!!.noHPlain as MutableList<String>

                            listHpAdapter.differ.submitList(value.toObject<User>()!!.noHPlain as MutableList<String>)

                            Log.d("bagongg", nohpList.size.toString())


                        } else {
                            Log.d("bagongg", "belis1")
                            listHpAdapter.differ.submitList(null)
                        }
                    }

                }
        }

    }


    suspend fun updateNomor(hapus: Boolean, data: String?, tambah: Boolean) {

        val ref = firebaseFirestore.collection("users").document(uid)

        withContext(Dispatchers.IO) {

            ref.update(
                "noHPlain", if (hapus) {
                    FieldValue.arrayRemove(data)
                } else if (tambah) {
                    FieldValue.arrayUnion(data)
                } else nohpList
            )
                .addOnCompleteListener {

                    if (it.isSuccessful) {
                        if (!hapus && !tambah) {

                            if (nomoraingg == data) {

                                vm.setNomor(hudsgthe[1])
                            }

                            Log.d("jajan", nohpList.indexOf(hudsgthe[1]).toString())
                            if (nohpList.indexOf(hudsgthe[1]) == 0)
                                ref.update("noHp", hudsgthe[1])

                        }

                        Toast.makeText(
                           activity,
                            if (hapus) "Nomor Handphone Berhasil Dihapus" else if (tambah) "Nomor Handphone Berhasil Ditambah" else "Nomor Handphone Berhasil Diupdate",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        Log.d("errrrrr", it.exception!!.message.toString())
                    }
                }


        }
    }


    fun alertBuilderDelete(data: String) {
        val builder = AlertDialog.Builder(activity)

        builder.apply {
            setTitle("Hapus Nomor Handphone")
            setMessage(getString(R.string.yakinn1, data))
            setPositiveButton("Ya", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {

                    job.launch { updateNomor(true, data, false) }

                }
            })
            setNegativeButton("Tidak", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                }
            })
        }

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false

    }
}
