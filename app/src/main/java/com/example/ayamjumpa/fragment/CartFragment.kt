package com.example.ayamjumpa.fragment


import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible

import androidx.databinding.DataBindingUtil

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ayamjumpa.MainMenuActivity
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.CartLoadListener
import com.example.ayamjumpa.adapter.MyCartAdapter
import com.example.ayamjumpa.dataClass.Alamat
import com.example.ayamjumpa.dataClass.Cart
import com.example.ayamjumpa.dataClass.Pesanan
import com.example.ayamjumpa.dataClass.User
import com.example.ayamjumpa.databinding.DialogCheckoutBinding

import com.example.ayamjumpa.databinding.FragmentCartBinding
import com.example.ayamjumpa.dialog.PilihAlamatDialog
import com.example.ayamjumpa.dialog.PilihNoHpDialog
import com.example.ayamjumpa.eventBus.StatusMessage
import com.example.ayamjumpa.interfaces.OnKlikk
import com.example.ayamjumpa.repository.RepositoryImpl
import com.example.ayamjumpa.util.AlertDialogBuilder

import com.example.ayamjumpa.viewModel.CartViewModel
import com.example.ayamjumpa.viewModel.CartViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CartFragment : Fragment(), CartLoadListener, OnKlikk<Any> {

    var adakahnohp: Boolean = false
    private lateinit var cartModelx: MutableList<Cart>
    private var totalHargaplusongkir: Double = 0.0
    private lateinit var binding: FragmentCartBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var user: User
    private var reqCode = 0
    private lateinit var cartListener: CartLoadListener
    private var totalHarga: Int = 0
    private var expander: String = ""
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
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

    private lateinit var dialogBinding: DialogCheckoutBinding
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var bitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        dialogBinding = DialogCheckoutBinding.inflate(layoutInflater)

        resultLauncher = resultLauncer()


        firebaseStorage = FirebaseStorage.getInstance()

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


    override fun onClickEdit(data: Any){
        val alamat= data as Alamat

        pilihAlamatDialog.dismiss()

        val nav = CartFragmentDirections.actionCartFragment2ToAlamatFragment(alamat)

        findNavController().navigate(nav)
    }

    override fun onClickHapus(data: Any) {

        val alamat = data as Alamat

        val builder = AlertDialog.Builder(activity)

        with(builder) {
            setTitle("Hapus alamat")
            setMessage(getString(com.example.ayamjumpa.R.string.yakinn, alamat.alamat))
            setPositiveButton(
                "Iya",
                android.content.DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                    val alamatRef =
                        firestore.collection("users").document(auth.uid!!).collection("alamat")

                    alamats.remove(alamat)

//                    alamats.forEach {
//                        Log.d("okee", it.alamat!!)
//                    }

                    alamatRef.document(alamat.id.toString()).delete().addOnCompleteListener {
                        if (it.isSuccessful) {
                            com.google.android.material.snackbar.Snackbar.make(
                                requireView(),
                                "Alamat berhasil dihapus",
                                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                            ).show()

                            if (alamat.id == alamatPesan?.id) {
                               vm.clearAlamat()
                            }

                           pilihAlamatDialog.alamat.remove(alamat)
                            pilihAlamatDialog.differListBaruSetelahHapus()

                        } else if (it.isCanceled) {
                            com.google.android.material.snackbar.Snackbar.make(
                                requireView(),
                                "Alamat gagal dihapus,mohon cek koneksi internet anda",
                                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }

                })
            setNegativeButton("Tidak", android.content.DialogInterface.OnClickListener { dialog, which ->

                dialog.dismiss()

            })


        }
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
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


        //CartFragmentDirections.actionCartFragment2ToAlamatFragment()


        val dialoge = BottomSheetDialog(requireContext())
        dialoge.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialoge.setContentView(dialogBinding.root)

        binding.expand.setOnClickListener {
            expand()
        }

        binding.gopayExpand.setOnClickListener {
            expandGopay()
        }

        binding.expandDana.setOnClickListener {
            expandDana()
        }

        binding.mandiriCopy.setOnClickListener {
            copyToclipBoard("1140004824465")
        }

        binding.gopayCopy.setOnClickListener {
            copyToclipBoard("081367649994")
        }

        binding.danaCopy.setOnClickListener {
            copyToclipBoard("081367649994")
        }

        binding.uploadBukti.setOnClickListener {
            selectImage()
        }

        binding.btnBayar.setOnClickListener {
            prosesPesanan("")
        }

//        binding.btnBayar.setOnClickListener {
//            temporaryDisable(it)
//
//            dialogBinding.expand.setOnClickListener {
//                expand()
//            }
//
//          dialogBinding.gopayExpand.setOnClickListener {
//              expandGopay()
//          }
//
//            dialogBinding.submitBayar.isEnabled = false
//            dialogBinding.submitBayar.setBackgroundResource(R.color.grey)
//
//          dialogBinding.submitBayar.setOnClickListener {
//                val builder = AlertDialog.Builder(activity)
//                builder.apply {
//                    setTitle("Konfirmasi Pembayaran")
//                    setMessage("Setelah pembayaran, staff kami akan memeriksa mutasi, mohon menunggu ")
//                    setPositiveButton("OK") { dialog, which ->
//                        prosesPesanan(dialogBinding.checkoutCatatan.text.toString(), dialoge)
//                    }
//                    show()
//                }
//            }
//            dialogBinding.totale.text =
//                formatRupiah(totalHargaplusongkir.toInt())
//            dialogBinding.uploadBukti.setOnClickListener {
//                selectImage()
//            }
//
//            dialoge.show()
//        }

        pilihNoHpDialog = PilihNoHpDialog(onClick = {
            nomorPesan = it

            vm.setNomor(it)

        })

        pilihAlamatDialog = PilihAlamatDialog(onClick = {
            alamatPesan = it
            Log.d("edann", it.nomorHp.toString())
            vm.setAlamat(alamatPesan!!)


        }, pindah = {
            findNavController().navigate(R.id.action_cartFragment2_to_alamatFragment)


        },this)

        vm.getAlamat()
        vm.getNomor()

        scope.launch {
            loadNoHp(auth.uid!!)
            vm.alamatkuX.observe(viewLifecycleOwner) {


                if (it.ongkir != null) {
                    alamatPesan = it

                    binding.ongkirr.text = formatRupiah(it.ongkir!!.toInt())

                    binding.dikirimkan.text = buildString {
                        append("dikirimkan ke: ")
                        append(it.label!!.lowercase())
                    }
                    pilihAlamatDialog.setChecked(it.id!!)


                    setTotal(it.ongkir!!, totalHarga.toDouble())

                    //    binding.btnBayar.isEnabled = true

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

    private fun checkButtonBayarEnabled() {

        if (this::bitmap.isInitialized && alamatPesan != null) {
            binding.btnBayar.isEnabled = true
        }
    }

    private fun copyToclipBoard(data:String){
            val clipboardmanager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text",data)
            clipboardmanager.setPrimaryClip(clipData)
            Toast.makeText(requireContext(),"text telah dicopy ke clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun expand() {

        val norek = binding.norek

        val expand = binding.expand


        val v = if (norek.visibility == View.GONE) View.VISIBLE else View.GONE

        if (v == View.GONE) {
            expand.setImageResource(R.drawable.ic_baseline_expand_more_24)
        } else {
            expand.setImageResource(R.drawable.ic_baseline_expand_less_24)
        }

        if (expander == "") {
            norek.visibility = v
            expander = "mandiri"
        } else if (expander == "gojek") {
            norek.visibility = v
            binding.norekGopay.visibility = View.GONE
            binding.gopayExpand.setImageResource(R.drawable.ic_baseline_expand_more_24)
            expander = "mandiri"
        } else if (expander == "mandiri") {
            norek.visibility = v
            expander = ""
        } else if(expander =="dana"){
            norek.visibility = v
            binding.norekDana.visibility = View.GONE
            binding.expandDana.setImageResource(R.drawable.ic_baseline_expand_more_24)
            expander ="mandiri"
        }


    }

    private fun expandGopay() {

        val norek = binding.norekGopay

        val expand = binding.gopayExpand

        val v = if (norek.visibility == View.GONE) View.VISIBLE else View.GONE

        if (v == View.GONE) {
            expand.setImageResource(R.drawable.ic_baseline_expand_more_24)
        } else {
            expand.setImageResource(R.drawable.ic_baseline_expand_less_24)
        }

        if (expander == "") {
            norek.visibility = v
            expander = "gojek"
        } else if (expander == "mandiri") {
            norek.visibility = v
            binding.norek.visibility = View.GONE
            binding.expand.setImageResource(R.drawable.ic_baseline_expand_more_24)
            expander = "gojek"
        } else if (expander == "gojek") {
            norek.visibility = v
            expander = ""
        }else if(expander =="dana"){
            norek.visibility = v
            binding.norekDana.visibility = View.GONE
            binding.expandDana.setImageResource(R.drawable.ic_baseline_expand_more_24)
            expander ="gojek"
        }


    }

    private fun expandDana(){

        val norek = binding.norekDana

        val expand = binding.expandDana

        val v = if (norek.visibility == View.GONE) View.VISIBLE else View.GONE

        if (v == View.GONE) {
            expand.setImageResource(R.drawable.ic_baseline_expand_more_24)
        } else {
            expand.setImageResource(R.drawable.ic_baseline_expand_less_24)
        }

        if (expander == "") {
            norek.visibility = v
            expander = "dana"
        } else if (expander == "mandiri") {
            norek.visibility = v
            binding.norek.visibility = View.GONE
            binding.expand.setImageResource(R.drawable.ic_baseline_expand_more_24)
            expander = "dana"
        } else if (expander == "dana") {
            norek.visibility = v
            expander = ""
        }else if(expander =="gojek"){
            norek.visibility = v
            binding.norekGopay.visibility = View.GONE
            binding.gopayExpand.setImageResource(R.drawable.ic_baseline_expand_more_24)
            expander ="dana"
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        alamats.clear()

    }

    fun prosesPesanan(catatan: String) {

        loadingg?.startAlertDialog("Menyelesaikan Transaksi")

        val refPesanan = firestore.collection("Pesanan")

        val id_pesanan = refPesanan.document().id

        refPesanan.document(id_pesanan).set(
            Pesanan(
                id_pesanan,
                cartModelx,
                user.username,
                auth.uid,
                alamatPesan,
                "pending",
                totalHargaplusongkir,
                if (catatan == "") "-" else catatan,

                )
        ).addOnCompleteListener {
            if (it.isSuccessful) {

                uploadImage(bitmap, id_pesanan)

                val cartcollection =
                    firestore.collection("Cart").document(auth.uid!!).collection("myCart")

                val batch = firestore.batch()

                cartModelx.forEach { cart ->
                    val each = cartcollection.document(cart.key!!)
                    batch.delete(each)

                }



                batch.commit().addOnSuccessListener {
                    loadingg?.dismiss()
                    findNavController().navigate(R.id.action_cartFragment2_to_homeFragment2)
                }
            } else {
                Toast.makeText(
                    context,
                    "Transaksi gagal, ${it.exception!!.message}",
                    Toast.LENGTH_SHORT
                ).show()
                loadingg?.dismiss()
            }

        }

    }

    suspend fun loadAlamat(uid: String) {
        withContext(Dispatchers.IO) {
            val alamat: MutableList<Alamat> = arrayListOf()
            val ref = firestore.collection("users").document(uid)

            ref.get().addOnSuccessListener {
                user = it.toObject<User>()!!
            }.continueWith {
                ref.collection("alamat").get().addOnSuccessListener { value ->
                    if (value?.size()!! > 0) {
                        for (x in value) {

                            val alamatnyaaa = x.toObject<Alamat>()
                            alamat.add(alamatnyaaa)
                        }

                        alamats.addAll(alamat)



                        pilihAlamatDialog.setAlamat(alamats.sortedBy{ it.time} as MutableList<Alamat>)


                    }

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

                        if (!adakahnohp) {
                            vm.setNomor(nohp!![0])
                        }

                        pilihNoHpDialog.setData(nohp!!)
                    }
                }

            }


        }
    }

    private fun selectImage() {


        val items = arrayOf<String>("ambil foto", "pilih dari galeri", "batal")
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("upload foto bukti pembayaran")



        builder.setItems(items) { dialog, item ->
            if (item == 0) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra("requestCode", "10")
                reqCode = 10


                resultLauncher.launch(intent)


            } else if (item == 1) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                intent.putExtra("requestCode", "20")
                reqCode = 20


                resultLauncher.launch(intent)


            } else {
                dialog.dismiss()
            }


        }
        builder.show()

    }

    fun resultLauncer(): ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->


        if (result.resultCode == Activity.RESULT_OK) {

            binding.tecter.visibility = View.GONE

//            (binding.buktiTransfer.layoutParams as LinearLayout.LayoutParams).weight = 1f
//
//            binding.buktiTransfer.adjustViewBounds = true
//
//            val params = binding.buktiTransfer.layoutParams
//
//            params.height = ViewGroup.LayoutParams.MATCH_PARENT
//            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
//
//            binding.buktiTransfer.layoutParams = params


            binding.buktiTransfer.visibility = View.GONE
            binding.imagerr.visibility = View.VISIBLE


           binding.uploadBukti.requestLayout()

            if (reqCode == 10) {
                val extras = result!!.data?.extras
                bitmap = extras!!.get("data") as Bitmap
                Log.d("requCd", "masukk")

                Glide.with(requireContext())
                    .load(bitmap)
                    .into(binding.imagerr)

                checkButtonBayarEnabled()


            } else if (reqCode == 20) {
                try {
                    Log.d("requCd", "masukk1")
                    val path = result!!.data?.data
                    val inputStream = context?.contentResolver!!.openInputStream(path!!)
                    bitmap = BitmapFactory.decodeStream(inputStream)

                    Glide.with(requireContext())
                        .load(bitmap)
                        .into(binding.imagerr)
                    checkButtonBayarEnabled()


                } catch (e: IOException) {
                    e.printStackTrace()
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

        checkButtonBayarEnabled()
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

    fun uploadImage(bitmap: Bitmap, idpesanan: String) {


        val storage = firebaseStorage.reference.child("images/transaksi/${idpesanan}.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storage.putBytes(data)

        uploadTask.addOnSuccessListener {

            it.metadata?.reference?.downloadUrl?.addOnCompleteListener { up ->

                if (up.isSuccessful) {

                    firestore.collection("Pesanan").document(idpesanan).update(
                        "buktiTF", up.result
                    )

                }
            }

        }.addOnFailureListener {


            Snackbar.make(requireView(), it.toString(), Snackbar.LENGTH_LONG).show()

            Log.d("storagee", it.toString())


        }


    }


}