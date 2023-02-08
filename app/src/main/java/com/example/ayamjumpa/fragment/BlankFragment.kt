package com.example.ayamjumpa.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.OnClickMapViewHolder
import com.example.ayamjumpa.adapter.AlamatAdapter
import com.example.ayamjumpa.dataClass.Alamat
import com.example.ayamjumpa.databinding.FragmentBlankBinding
import com.example.ayamjumpa.viewModel.CartViewModel
import com.example.ayamjumpa.viewModel.CartViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*


class BlankFragment : Fragment(), OnClickMapViewHolder {
    private lateinit var adapter: AlamatAdapter
    private lateinit var kcak: CartViewModel
    private lateinit var alamataing: Alamat
    private lateinit var listenerkacau: ListenerRegistration

    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    override fun editClik(alamat: Alamat) {

        val direction = BlankFragmentDirections.actionBlankFragmentToAlamatFragment(alamat)

        findNavController().navigate(direction)
    }

    override fun removeClik(alamat: Alamat) {

        val builder = AlertDialog.Builder(activity)

        with(builder) {
            setTitle("Hapus alamat")
            setMessage(getString(R.string.yakinn, alamat.alamat))
            setPositiveButton(
                "Iya",
                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                    val alamatRef =
                        firestore.collection("users").document(auth.uid!!).collection("alamat")

                    alamatRef.document(alamat.id.toString()).delete().addOnCompleteListener {
                        if (it.isSuccessful) {
                            Snackbar.make(
                                requireView(),
                                "Alamat berhasil dihapus",
                                Snackbar.LENGTH_LONG
                            ).show()

                            if (alamat.id == alamataing.id) {
                                kcak.clearAlamat()
                            }

                        } else if (it.isCanceled) {
                            Snackbar.make(
                                requireView(),
                                "Alamat gagal dihapus,mohon cek koneksi internet anda",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }

                })
            setNegativeButton("Tidak", DialogInterface.OnClickListener { dialog, which ->

                dialog.dismiss()

            })


        }
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false


    }


    val auth = Firebase.auth
    val firestore = Firebase.firestore


    private lateinit var binding: FragmentBlankBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val viewModel: CartViewModel by lazy {
            ViewModelProvider(
                this,
                CartViewModelFactory(requireContext()),
            )[CartViewModel::class.java]
        }

        kcak = viewModel

        kcak.getAlamat()

        kcak.alamatkuX.observe(viewLifecycleOwner, Observer {
            alamataing = it
        })

        adapter = AlamatAdapter(this)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_blank, container, false)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.alamatListk.layoutManager = layoutManager

        binding.alamatListk.setHasFixedSize(true)

        binding.tambahAlamat.setOnClickListener {
            findNavController().navigate(R.id.action_blankFragment_to_alamatFragment)
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

    override fun onResume() {
        super.onResume()



        scope.launch {
            getAlamat(auth.uid!!)
        }

    }

    override fun onStart() {
        super.onStart()



    }

    override fun onDestroyView() {
        super.onDestroyView()

        listenerkacau.remove()


    }

    suspend fun getAlamat(id: String) {
        withContext(Dispatchers.IO) {
            val alamatList: MutableList<Alamat> = arrayListOf()
            val fire = firestore.collection("users").document(id).collection("alamat").orderBy("time", Query.Direction.ASCENDING)

            listenerkacau = fire.addSnapshotListener { value, error ->
                alamatList.clear()

                if (value != null) {


                    for (data in value) {

                        val datanya = data.toObject<Alamat>()
                        datanya.id = data.id

                        Log.d("kacauu", datanya.label.toString())

                        alamatList.add(datanya)


                    }
                    setupRecycler(alamatList)
                } else {
                    Snackbar.make(
                        requireView(),
                        "Gagal mendapatkan alamat",
                        Snackbar.LENGTH_LONG
                    ).show()
                }


            }


        }


    }

    private fun editAlamat(alamat: Alamat) {


        val alamatRef =
            firestore.collection("users").document(auth.uid!!).collection("alamat")



        alamatRef.document(alamat.id.toString()).set(alamat).addOnCompleteListener {
            if (it.isSuccessful) {
                Snackbar.make(requireView(), "Alamat berhasil ditambah", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }


    fun setupRecycler(alamat: MutableList<Alamat>) {


        adapter.differ.submitList(alamat.sortedBy { data -> data.time })

        binding.alamatListk.adapter = adapter


    }


}