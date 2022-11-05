package com.example.ayamjumpa.fragment


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible

import androidx.databinding.DataBindingUtil

import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ayamjumpa.MainMenuActivity
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.CartLoadListener
import com.example.ayamjumpa.adapter.MyCartAdapter
import com.example.ayamjumpa.dataClass.Alamat
import com.example.ayamjumpa.dataClass.Cart
import com.example.ayamjumpa.dataClass.Pesanan
import com.example.ayamjumpa.dataClass.User

import com.example.ayamjumpa.databinding.FragmentCartBinding
import com.example.ayamjumpa.dialog.CheckOutDialog
import com.example.ayamjumpa.dialog.PilihAlamatDialog
import com.example.ayamjumpa.dialog.PilihNoHpDialog
import com.example.ayamjumpa.eventBus.StatusMessage
import com.example.ayamjumpa.repository.RepositoryImpl
import com.example.ayamjumpa.util.AlertDialogBuilder

import com.example.ayamjumpa.viewModel.CartViewModel
import com.example.ayamjumpa.viewModel.CartViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CartFragment : Fragment(), CartLoadListener {

    var adakahnohp: Boolean = false
    private lateinit var cartModelx: MutableList<Cart>
    private var totalHargaplusongkir: Double = 0.0
    private lateinit var binding: FragmentCartBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var cartListener: CartLoadListener
    private var totalHarga: Int = 0
    private lateinit var mContext: Context
    private lateinit var vm: CartViewModel
    private var alamatPesan: Alamat? = null
    private var nomorPesan: String? = null
    private var alamats: MutableList<Alamat> = arrayListOf()
    private lateinit var repositoryImpl: RepositoryImpl
    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var pilihAlamatDialog: PilihAlamatDialog
    private lateinit var pilihNoHpDialog: PilihNoHpDialog
    private var nohp: ArrayList<String>? = null
    private var loadingg: AlertDialogBuilder? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cartListener = this

        loadingg = AlertDialogBuilder(requireActivity())
        val viewModel: CartViewModel by lazy {
            ViewModelProvider(
                this,
                CartViewModelFactory(mContext),
            )[CartViewModel::class.java]
        }

        vm = viewModel


        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val current = formatter.format(time)




        repositoryImpl = RepositoryImpl()

        repositoryImpl.getInstance()

        firestore = Firebase.firestore
        auth = Firebase.auth
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.alamatkirim.setOnClickListener {
            temporaryDisable(it)
            pilihAlamatDialog.show(parentFragmentManager, "dialog")
        }

        binding.pilihnohp.setOnClickListener {
            temporaryDisable(it)
            pilihNoHpDialog.show(parentFragmentManager, "dialogue")
            pilihNoHpDialog.setChecked(nomorPesan.toString())
        }
        binding.cartRecyclerView.layoutManager = layoutManager

        hideKeyboard(activity as MainMenuActivity)
        binding.tanggall.text = current

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().hasSubscriberForEvent(StatusMessage::class.java))
            EventBus.getDefault().removeStickyEvent(StatusMessage::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun gagal(statusMessage: StatusMessage) {

        binding.cartKosong.isVisible = true
        binding.cartScrollView.isVisible = false
        binding.linearr.isVisible = false

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnBayar.setOnClickListener {
            temporaryDisable(it)


            val dialog = BottomSheetDialog(requireContext())
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            val vieww = layoutInflater.inflate(R.layout.dialog_checkout, null)
            val buttonBayar = vieww.findViewById<LinearLayout>(R.id.submitBayar)

            vieww.findViewById<ImageView>(R.id.expand).setOnClickListener {
                expand(vieww)
            }

            buttonBayar.setOnClickListener {
                val builder = AlertDialog.Builder(activity)
                builder.apply {
                    setTitle("Konfirmasi Pembayaran")
                    setMessage("Setelah pembayaran, staff kami akan memeriksa mutasi, mohon menunggu ")
                    setPositiveButton("OK") { dialog, which ->
                        prosesPesanan()

                    }
                    show()
                }


            }

            vieww.findViewById<TextView>(R.id.totale).text =
                formatRupiah(totalHargaplusongkir.toInt())


            dialog.setContentView(vieww)
            dialog.show()

        }



        pilihNoHpDialog = PilihNoHpDialog(onClick = {
            nomorPesan = it

            vm.setNomor(it)

        })

        pilihAlamatDialog = PilihAlamatDialog(onClick = {
            alamatPesan = it
            Log.d("edann", it.keterangan.toString())
            vm.setAlamat(alamatPesan!!)


        })

        vm.getAlamat()
        vm.getNomor()

        scope.launch {
            loadNoHp(auth.uid!!)
            vm.alamatkuX.observe(viewLifecycleOwner) {


                if (it.ongkir != null) {
                    alamatPesan = it

                    binding.ongkirr.text = formatRupiah(it.ongkir!!.toInt())

                    binding.dikirimkan.text = buildString {
                        append("Dikirimkan ke: ")
                        append(it.alamat)
                    }
                    pilihAlamatDialog.setChecked(it.id!!)


                    setTotal(it.ongkir!!, totalHarga.toDouble())

                    binding.btnBayar.isEnabled = true

                }

                if (it.ongkir == null) {
                    binding.dikirimkan.text = "Mohon pilih alamat"
                    binding.ctotal.text = "-"
                    binding.btnBayar.isEnabled = false
                }

            }

            vm.nomorkuX?.observe(viewLifecycleOwner) {
                if (it != "") {
                    nomorPesan = it
                    pilihNoHpDialog.setChecked(it)
                    adakahnohp = true


                } else {

                    adakahnohp = false
                }
            }


            loadCart(auth.uid!!)
            loadAlamat(auth.uid!!)

        }


    }


    private fun expand(view: View) {

        val norek = view.findViewById<TextView>(R.id.norek)

        val expand = view.findViewById<ImageView>(R.id.expand)

        val v = if (norek.visibility == View.GONE) View.VISIBLE else View.GONE
//val transition = android.transition.AutoTransition()
//        transition.addTarget(binding.norek)
        if (v == View.GONE) {
            expand.setImageResource(R.drawable.ic_baseline_expand_more_24)
        } else {
            expand.setImageResource(R.drawable.ic_baseline_expand_less_24)
        }

        //  TransitionManager.beginDelayedTransition(binding.linearr as ViewGroup, transition)

        norek.visibility = v

    }


    override fun onDestroyView() {
        super.onDestroyView()

    }

    fun prosesPesanan() {

        loadingg?.startAlertDialog("Menyelesaikan Transaksi")

        val refPesanan = firestore.collection("Pesanan")

        val id_pesanan = refPesanan.document().id

        refPesanan.document(id_pesanan).set(
            Pesanan(
                id_pesanan,
                cartModelx,
                null,
                auth.uid,
                nomorPesan,
                alamatPesan,
                "proses",
                totalHargaplusongkir

            )
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                loadingg?.dismiss()}
            else {
                loadingg?.dismiss()
            }

        }

    }

    suspend fun loadAlamat(uid: String) {
        withContext(Dispatchers.IO) {
            val alamat: MutableList<Alamat> = arrayListOf()

            val alamatCollection = firestore.collection("users").document(uid).collection("alamat")

            alamatCollection.addSnapshotListener { value, error ->

                if (value?.size()!! > 0) {
                    for (x in value) {

                        val alamatnyaaa = x.toObject<Alamat>()
                        alamat.add(alamatnyaaa)
                    }

                    alamats.addAll(alamat)

                    pilihAlamatDialog.setAlamat(alamats)


                }


            }


        }
    }

    suspend fun loadNoHp(uid: String) {
        withContext(Dispatchers.IO) {

            nohp = arrayListOf()

            firestore.collection("users").document(uid).addSnapshotListener { value, error ->
                nohp!!.clear()
                if (value?.toObject<User>()?.noHPlain != null) {
                    if (value.toObject<User>()?.noHPlain?.size!! > 0) {
                        val res: List<String> = value.toObject<User>()?.noHPlain!!

                        nohp!!.addAll(res as ArrayList<String>)

                        if (adakahnohp == false) {
                            vm.setNomor(nohp!![0])
                        }

                        pilihNoHpDialog.setData(nohp!!)
                    }
                }

            }


        }
    }


    suspend fun loadCart(uid: String) {
        withContext(Dispatchers.IO) {
            cartModelx = arrayListOf()

            val cartcollection = firestore.collection("Cart").document(uid).collection("myCart")

            cartcollection.addSnapshotListener { value, error ->

                if (value!!.size() > 0) {
                    cartModelx.clear()


                    for (x in value) {


                        val cartModel = x.toObject<Cart>()

                        cartModel.key = x.id

                        cartModelx.add(cartModel)

                        Log.d("data", cartModel.name.toString())


                    }

                    onLoadCartSuccess(cartModelx)

                } else {
                    EventBus.getDefault().post(StatusMessage(error.toString()))
                }

            }
        }


    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context

    }

    fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // Check if no view has focus
        val currentFocusedView = activity.currentFocus
        currentFocusedView?.let {
            inputMethodManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }


    private fun setTotal(ongkir: Double, harga: Double) {
        binding.ctotal.text = formatRupiah(ongkir.toInt() + harga.toInt())
        totalHargaplusongkir = ongkir + harga

        binding.btnBayar.isEnabled = true
    }

    override fun onLoadCartSuccess(cartModelList: List<Cart>) {


        totalHarga = 0
        val adapter = MyCartAdapter(mContext, cartModelList, cartListener)
        binding.cartRecyclerView.adapter = adapter



        scope.launch {
            val _totalHarga = async {
                delay(1000L)
                var temp = 0

                for (x in cartModelList) {
                    temp += x.totalharga.toInt()
                }

                return@async temp

            }
            withContext(Dispatchers.Main) {
                totalHarga = _totalHarga.await()

                binding.hargaa.text = formatRupiah(totalHarga)

                if (alamatPesan != null) {
                    setTotal(alamatPesan?.ongkir!!, totalHarga.toDouble())

                }

            }

        }
    }

    override fun onLoadCartMessage(message: String?) {

    }


    private fun formatRupiah(number: Int): String {


        val localeID = Locale("in", "ID")

        val format = NumberFormat.getCurrencyInstance(localeID)

        return format.format(number)


    }


    override fun UpdateData(cart: Cart) {
        binding.btnBayar.isEnabled = false
        val userCart = Firebase.firestore
            .collection("Cart")
            .document(auth.uid!!).collection("myCart").document(cart.key!!)

        userCart.set(cart).addOnCompleteListener {

            if (it.isCanceled) {
                EventBus.getDefault().post(StatusMessage(it.exception.toString()))
            }
        }


    }

    override fun deleteItem(cart: Cart) {
        firestore.collection("Cart").document(auth.uid!!)
            .collection("myCart")
            .document(cart.key!!)
            .delete().addOnSuccessListener {
                Toast.makeText(mContext, "Menu Berhasil dihapus", Toast.LENGTH_LONG).show()
            }
    }

    private fun temporaryDisable(view: View) {
        view.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({
            view.isEnabled = true
        }, 1500)

    }


}