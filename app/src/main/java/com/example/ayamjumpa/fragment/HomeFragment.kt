package com.example.ayamjumpa.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat.animate
import androidx.core.view.contains
import androidx.core.view.isVisible
import androidx.core.view.marginStart
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.ayamjumpa.R
import com.example.ayamjumpa.adapter.*
import com.example.ayamjumpa.interfaces.MenuListener
import com.example.ayamjumpa.interfaces.RecycleClickListener
import com.example.ayamjumpa.dataClass.*
import com.example.ayamjumpa.databinding.FragmentHomeBinding

import com.example.ayamjumpa.dialog.OptionDialog

import com.example.ayamjumpa.eventBus.StatusMessage
import com.example.ayamjumpa.interfaces.Navigation
import com.example.ayamjumpa.util.AlertDialogBuilder
import com.example.ayamjumpa.viewModel.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

import com.google.android.material.snackbar.Snackbar
import com.google.api.Distribution
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import org.greenrobot.eventbus.EventBus
import java.lang.Runnable
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment(), MenuListener, RecycleClickListener, Navigation {
    private lateinit var menuListener: MenuListener
    private lateinit var clickListener: RecycleClickListener
    private lateinit var binding: FragmentHomeBinding
    private var adaitem: Boolean = false
    private lateinit var firestore: FirebaseFirestore
    private lateinit var cartLoader: ListenerRegistration
    private lateinit var auth: FirebaseAuth
    private lateinit var userData: User
    private lateinit var arrayKategori: ArrayList<Kategori>
    private val firestoreViewModel: AuthViewModel by viewModels()
    private var listImage = arrayListOf<ImageData>()
    private val dots: ArrayList<TextView> = ArrayList()
    private lateinit var mContext: Context

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var optionDialog: OptionDialog
    private lateinit var navAdapter: NavAdapter

    private lateinit var pop: Animation
    private lateinit var exitt: Animation
    private var animStart: Boolean = true

    private lateinit var allMenuArray: MutableList<Menu>
    private lateinit var menuArray: MutableList<String>
    private lateinit var rvMakananArray: MutableList<RecyclerView>

    private lateinit var rvNonMakananArray: List<RecyclerView>

    private lateinit var dialogLoading:AlertDialogBuilder

    private var scope: CoroutineScope? = null

    private lateinit var timeout:Runnable

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        scope = CoroutineScope(Job() + Dispatchers.IO)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        dialogLoading = AlertDialogBuilder(requireActivity())
        dialogLoading.startAlertDialog("mohon tunggu sebentar..")

        timeout = timeOut()

        navAdapter = NavAdapter(mContext, this)


        menuArray = arrayListOf()
        rvMakananArray = arrayListOf()
        // 0 -> minuman
        // 1 -> paket

        // 2 -> promo
        // 3 -> rekom
        // 4 -> stok habis
        val rv1 = newRv()
        rv1.contentDescription = "minuman"

        val rv2 = newRv()
        rv2.contentDescription = "paket"

        rvNonMakananArray = listOf(rv1, rv2, newRv(), newRv(), newRv())



        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            var index = 0
            override fun run() {
                if (index == listImage.size)
                    index = 0


                binding.viewPager.currentItem = index
                index++
                handler.postDelayed(this, 2000)

            }
        }

        handler.postDelayed(timeout, 15000)




        pop = AnimationUtils.loadAnimation(mContext, R.anim.enter)

        exitt = AnimationUtils.loadAnimation(mContext, R.anim.keluarr)


        firestore = Firebase.firestore
        auth = Firebase.auth
        userData = User("Loading..", "Loading..", "Loading..", noHp = "Loading..", isAdmin = false)
        arrayKategori = arrayListOf()
        firestoreViewModel.getUser(auth.uid!!)


        binding.searchh.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToMenuFragment(arrayOf(query, "All"))
                )


                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


      /*  navAdapter = NavAdapter(mContext, this)
        val navArray = listOf("order", "menu", "rasa", "minuman", "promo", "pesanan", "saya")
        navAdapter.differ.submitList(navArray)

        binding.recyclerMenu.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerMenu.adapter = navAdapter

        binding.recyclerMenu.itemAnimator = null
        binding.recyclerMenu.setHasFixedSize(true)


        firestoreViewModel.getTotal().observe(viewLifecycleOwner) {
            if (it.size == 0) {
                adaitem = false
                binding.floatingActionButton.visibility = View.GONE
                navAdapter.cartCount = 0
                navAdapter.notifyItemChanged(navArray.indexOf("order"))
            }

            if (it.size > 0) {
                adaitem = true
                //  binding.floatingActionButton.visibility = View.VISIBLE
                val item = if (it[1].toInt() > 1) "items" else "item"
                binding.itemtotal.text = it[1] + " " + item
                binding.hargatotal.text = formatRupiah(it[0].toInt())

                navAdapter.cartCount = it[1].toInt()
                navAdapter.notifyItemChanged(navArray.indexOf("order"))


            }

        }*/

        KeyboardVisibilityEvent.setEventListener(
            requireActivity()
        ) { isOpen ->
            if (isOpen) {
                binding.constraint11.visibility = View.GONE
            } else {
                if (adaitem) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.constraint11.visibility = View.VISIBLE
                    }, 500)
                }
            }
        }

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment2)
        }



        firestoreViewModel.getImagedata().observe(viewLifecycleOwner, Observer {

            listImage = it as ArrayList<ImageData>

            binding.indicatorDot.removeAllViews()
            dots.clear()


            for (i in 0 until it.size) {

                dots.add(TextView(requireContext()))

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dots[i].text = Html.fromHtml("&#9679", Html.FROM_HTML_MODE_LEGACY).toString()
                } else {
                    dots[i].text = Html.fromHtml("&#9679")
                }
                dots[i].textSize = 18f

                binding.indicatorDot.addView(dots[i])


            }

            val sliderAdapter = ImageSliderAdapter(listImage,
                onSubmitClickListener = {
                    //  findNavController().navigate(R.id.action_homeFragment_to_menuFragment)
                }
            )
            binding.viewPager.adapter = sliderAdapter


        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectedDot(position)
                super.onPageSelected(position)
            }
        })

        binding.imageViewhome.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_menuprofile)
        }

        menuListener = this
        clickListener = this



        binding.btnCart.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_menuFragment)


        }


        scope?.launch {
            loadTerlaris()
            loadCart(auth.uid!!)


        }


        return binding.root
    }



    private fun formatRupiah(number: Int): String {


        val localeID = Locale("in", "ID")

        val format = NumberFormat.getCurrencyInstance(localeID)
        var withrp = format.format(number)
        withrp = withrp.substring(2, withrp.lastIndex + 1)

        if (withrp.contains(',')) {

            val lastidx = withrp.indexOf(',')

            withrp = withrp.substring(0, lastidx)

        }


        return withrp

    }


    override fun onDestroyView() {

        arrayKategori.clear()
        super.onDestroyView()


    }

    override fun onStart() {
        super.onStart()
        handler.post(runnable)



        val navArray = listOf("order","promo","paket" ,"ayam", "rasa", "minum snack", "pesanan", "saya")
        binding.recyclerMenu.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerMenu.adapter = navAdapter

        navAdapter.differ.submitList(navArray)
        binding.recyclerMenu.itemAnimator = null
        binding.recyclerMenu.setHasFixedSize(true)



        firestoreViewModel.getTotal().observe(viewLifecycleOwner) {

            if (it.size == 0) {

                adaitem = false
                binding.floatingActionButton.visibility = View.GONE
                navAdapter.cartCount = 0
                navAdapter.notifyItemChanged(navArray.indexOf("order"))
            }

            if (it.size > 0) {


                adaitem = true
                //  binding.floatingActionButton.visibility = View.VISIBLE
                val item = if (it[1].toInt() > 1) "items" else "item"
                binding.itemtotal.text = it[1] + " " + item
                binding.hargatotal.text = formatRupiah(it[0].toInt())

                navAdapter.cartCount = it[1].toInt()
                navAdapter.notifyItemChanged(navArray.indexOf("order"))


            }

        }


    }

    override fun onStop() {

        super.onStop()
        handler.removeCallbacks(runnable)


    }


    private fun selectedDot(position: Int) {
        for (i in 0 until listImage.size) {
            if (i == position) {
                dots[i].setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary
                    )
                )
            } else {
                dots[i].setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey
                    )
                )
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        firestoreViewModel.user.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                userData = it
                setProfile()
            }

        })

        firestoreViewModel.getKategori().observe(viewLifecycleOwner, Observer {


        })


    }


    private fun setProfile() {

        binding.homeUsername.text = userData.username


        if (userData.foto == null) {
            Glide.with(requireContext())
                .load(R.drawable.user)
                .into(binding.imageViewhome)

        } else {
            Glide.with(requireContext())
                .load(userData.foto)
                .into(binding.imageViewhome)
        }
    }

    //ambil menu terlaris
    suspend fun loadTerlaris() {



        withContext(Dispatchers.IO) {


            val menuModels: MutableList<Menu> = ArrayList()
            val menucollection =
                firestore.collection("Menu")


            menucollection.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.size()>0) {
                        menuModels.clear()
                        for (datax in it.result) {
                            Log.d("datanya", datax.toObject<Menu>().deskripsi.toString())

                            val menuModel = datax.toObject<Menu>()

                            menuModel.key = datax.id
                            menuModels.add(menuModel)

                            Log.d("datanya", "memek")


                        }


                        onMenuLoadSucces(menuModels)
                    } else {
                        EventBus.getDefault().post(StatusMessage(it.exception.toString()))
                    }
                } else {
                    EventBus.getDefault().post(StatusMessage(it.exception.toString()))
                }
            }


        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    private fun addViewGroup(rv: RecyclerView, menu: String): LinearLayout {

        val linearLayout = LinearLayout(mContext)
        linearLayout.orientation = LinearLayout.VERTICAL
        val vgParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        vgParam.width = LinearLayout.LayoutParams.MATCH_PARENT
        vgParam.height = LinearLayout.LayoutParams.WRAP_CONTENT

        linearLayout.layoutParams = vgParam

        val tvParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        tvParam.marginStart = dpToPx(20)

        val tv = TextView(mContext)

        tv.layoutParams = tvParam

        val tf = ResourcesCompat.getFont(mContext, R.font.centurygothic)

        tv.setTypeface(tf, Typeface.BOLD)
        linearLayout.setPadding(0, dpToPx(10), 0, dpToPx(10))

        tv.text = menu



        tv.textSize = 18f
        tv.setTextColor(Color.BLACK)



        linearLayout.addView(
            tv
        )
        linearLayout.addView(
            rv
        )


        return linearLayout

    }

    private fun generateRvMakanan() {

        menuArray.forEach { menuKey ->

            val rv = newRv()

            rv.contentDescription = menuKey

            val adapter = rv.adapter as TerlarisAdapter

            adapter.differ.submitList(allMenuArray.filter { x -> x.menuKey == menuKey && x.stok > 0 })

            val linearLayout = addViewGroup(rv, menuKey)

            rvMakananArray.add(rv)

            if (allMenuArray.any { x -> x.menuKey == menuKey && x.stok > 0 })
                binding.linearRv.addView(
                    linearLayout
                )
        }

    }

    private fun generateRvNonMakanan() {

        rvNonMakananArray.forEachIndexed { idx, rv ->

            val adapter = rv.adapter as TerlarisAdapter

            var list = allMenuArray.filter { data ->
                data.tipe == rv.contentDescription
                        && data.stok > 0
            }

            when (idx) {
                2 -> {
                    rv.contentDescription = "promo"

                    list = allMenuArray.filter { data -> data.promo && data.stok > 0 }
                }
                3 -> {
                    rv.contentDescription = "rekomendasi"
                    list = allMenuArray.filter { data -> data.rekom && data.stok > 0 }
                }
                4 -> {
                    rv.contentDescription = "stok habis"
                    list = allMenuArray.filter { x -> x.stok <= 0 }
                }
            }


            adapter.differ.submitList(list)

            val linear = addViewGroup(rv, rv.contentDescription.toString())

            var condition = allMenuArray.any { x -> x.tipe == rv.contentDescription }

            when (idx) {
                2 -> {

                    condition = allMenuArray.any { x -> x.promo && x.stok > 0 }
                }
                3 -> {
                    condition = allMenuArray.any { x -> x.rekom && x.stok > 0 }
                }
                4 -> {
                    condition = allMenuArray.any { x -> x.stok <= 0 }
                }
            }


            if (condition) {
                binding.linearRv.addView(
                    linear
                )
            }

        }

    }

    private fun updatedataRv() {
        rvMakananArray.forEach {
            val adapter = it.adapter as TerlarisAdapter

            adapter.differ.submitList(allMenuArray.filter { data -> data.menuKey == it.contentDescription })

        }


    }

    private fun updatedataRvNonMakanan() {
        rvNonMakananArray.forEachIndexed { idx, rv ->
            val adapter = rv.adapter as TerlarisAdapter

            var list = allMenuArray.filter { data -> data.tipe == rv.contentDescription }

            when (idx) {
                2 -> {
                    list = allMenuArray.filter { data -> data.promo }
                }
                3 -> {
                    list = allMenuArray.filter { data -> data.rekom }
                }
            }



            adapter.differ.submitList(list)


        }

    }

    private fun newRv(): RecyclerView {
        val rv = RecyclerView(mContext)

        val rvParam = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

        rv.layoutParams = rvParam
        rv.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        val adapter = TerlarisAdapter(mContext, this)


        rv.adapter = adapter




        rv.isNestedScrollingEnabled = false
        rv.overScrollMode = View.OVER_SCROLL_NEVER

        return rv
    }


    private fun dpToPx(dp: Int): Int {
        val scale = mContext.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }


    suspend fun loadCart(uid: String) {
        val mutable = arrayListOf<String>()
        withContext(Dispatchers.IO) {


            val cartModels: MutableList<Cart> = arrayListOf()

            val cartcollection = firestore.collection("Cart").document(uid).collection("myCart")

            cartLoader = cartcollection.addSnapshotListener { value, error ->
                mutable.clear()
                cartModels.clear()

                if (value!!.size() > 0) {
                    var temp = 0
                    var total = 0

                    for (x in value) {


                        val cartModel = x.toObject<Cart>()

                        cartModel.key = x.id

                        cartModels.add(cartModel)

                        temp += cartModel.qty

                        total += cartModel.totalharga.toInt()


                    }

                    mutable.add(total.toString())
                    mutable.add(temp.toString())

                    firestoreViewModel.setTotalItem(mutable)


                } else {
                    firestoreViewModel.setTotalItem(mutable)
                }
            }

        }

    }


    @SuppressLint("UseRequireInsteadOfGet")
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
                                activity!!.findViewById(R.id.parent),
                                "Barang berhasil dimasukan ke keranjang",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }.addOnCanceledListener {
                            Snackbar.make(
                                activity!!.findViewById(R.id.parent),
                                it.exception?.message.toString(),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    val mycart = Cart()

                    //jika ada promo maka total harga akan menjadi (harga-potongan) atau promo
                    val harga =
                        if (menu.potongan > 0L) (menu.harga - menu.potongan).toString() else menu.harga.toString()

                    mycart.key = menu.key
                    mycart.qty = 1
                    mycart.totalharga = harga.toLong()
                    mycart.name = menu.nama
                    mycart.harga = harga
                    mycart.foto = menu.foto
                    mycart.desc1 = menu.deskripsi
                    mycart.desc2 = menu.deskripsi1
                    mycart.potongan = menu.potongan



                    userCart.document(mycart.key!!).set(mycart).addOnSuccessListener {
                        Snackbar.make(
                            activity!!.findViewById(R.id.parent),
                            "Barang berhasil dimasukan ke keranjang",
                            Snackbar.LENGTH_SHORT
                        ).show()

                    }.addOnCanceledListener {
                        Snackbar.make(
                            activity!!.findViewById(R.id.parent),
                            it.exception?.message.toString(),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }


                }
            }


    }

    override fun onMenuLoadSucces(menuList: MutableList<Menu>) {

        allMenuArray = menuList

        allMenuArray.forEach { data ->
            if (!menuArray.contains(data.menuKey) && data.menuKey != null) {
                menuArray.add(data.menuKey!!)
            }
        }

        if (binding.linearRv.childCount > 0) {
            updatedataRv()
            updatedataRvNonMakanan()


        } else {
            generateRvMakanan()
            generateRvNonMakanan()
        }



        optionDialog = OptionDialog(navigasi = {
            optionDialog.dismiss()
            val navi = HomeFragmentDirections.actionHomeFragmentToMenuFragment(it)
            findNavController().navigate(navi)
        }, menuList, {
            val oldPos = navAdapter.position
            navAdapter.position = -1
            navAdapter.notifyItemChanged(oldPos)
        })


        dialogLoading.dismiss()
        handler.removeCallbacks(timeout)

        setRekomdanPromoDiatas()




       /* val allRv = rvMakananArray + rvNonMakananArray

        allRv.forEach { rv ->
            val linear = rv.layoutManager as LinearLayoutManager

            linear.scrollToPositionWithOffset(2, -100)

        }*/



    }

    override fun onMenuLoadFailed(message: String?) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }


    override fun onItemClickListener(menu: Menu) {


        val dialog = BottomSheetDialog(requireContext())

        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        val view = layoutInflater.inflate(R.layout.tambah_konfirmasi, null)


        val foto = view.findViewById<ImageView>(R.id.foto_makanan)
        val harga = view.findViewById<TextView>(R.id.hargamakanan)
        val desc = view.findViewById<TextView>(R.id.desc_konfirmasi)
        val nama = view.findViewById<TextView>(R.id.judulmakanan)
        val hargaPromo = view.findViewById<TextView>(R.id.hargamakananpromo)

        //deskripsi gimmick
        val desc1 = view.findViewById<TextView>(R.id.desc1_tambah)

        if (menu.tipe == "minuman") {
            desc.visibility = View.GONE
        }




        if (menu.nama!!.contains("|")) {
            val idx = menu.nama!!.indexOf("|")
            val judulnya = menu.nama!!.substring(0, idx)
            nama.text = judulnya

        } else {
            nama.text =
                menu.nama!!.lowercase()
        }



        harga.text = formatRupiah(menu.harga.toInt())
        desc.text = menu.deskripsi!!.lowercase()

        desc1.text = menu.deskripsi1!!.lowercase()

        if (menu.promo) {
            hargaPromo.visibility = View.VISIBLE
            harga.textSize = 15f


            harga.paintFlags = harga.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            hargaPromo.text = formatRupiah((menu.harga - menu.potongan).toInt())
        }


        Glide.with(requireContext())
            .load(menu.foto)
            .into(foto)


        dialog.setContentView(view)

        val buttonf = view.findViewById<Button>(R.id.cardButton)

        buttonf.setOnClickListener {
            dialog.dismiss()
            addToCart(menu, auth.uid!!)

        }



        dialog.show()


        // addToCart(menu, auth.uid!!)

    }


    override fun showRasa() {
        if (binding.floatingActionButton.isVisible) {
            binding.floatingActionButton.visibility = View.GONE
        }

        optionDialog.indikator = "rasa"
        optionDialog.show(parentFragmentManager, "dialog")

    }

    override fun showCart(cartCount: Int) {

        if (cartCount > 0) {
            val pop = AnimationUtils.loadAnimation(mContext, R.anim.enter)
            val exitt = AnimationUtils.loadAnimation(mContext, R.anim.keluarr)

            if (binding.floatingActionButton.visibility == View.GONE) {
                binding.floatingActionButton.visibility = View.VISIBLE
                binding.floatingActionButton.startAnimation(pop)
            } else {
                binding.floatingActionButton.startAnimation(exitt)

                animationListener("exit", binding.floatingActionButton)
            }


        } else {
            Toast.makeText(mContext, "Keranjang belanja kamu kosong", Toast.LENGTH_SHORT).show()
        }

    }


    override fun showMenu() {


        if (binding.floatingActionButton.isVisible) {
            binding.floatingActionButton.visibility = View.GONE
        }

        //mencegah crash, jika optiondialog belum tampil maka menampilkan option dialog

        optionDialog.indikator = "menu"
        optionDialog.show(parentFragmentManager, "dialog")


    }

    private fun setRekomdanPromoDiatas(){


        val parent1 = rvNonMakananArray[3].parent as LinearLayout
        val parent2 = rvNonMakananArray[2].parent as LinearLayout

      binding.linearRv.removeView(parent1)
      binding.linearRv.removeView(parent2)

        binding.linearRv.addView(parent1, 0)
        binding.linearRv.addView(parent2, 1)
    }


    override fun showMinuman() {
        if (binding.floatingActionButton.isVisible) {
            binding.floatingActionButton.visibility = View.GONE
        }



        optionDialog.indikator = "minuman"
        optionDialog.show(parentFragmentManager, "dialog")

    }

    private fun timeOut() = Runnable{
        Toast.makeText(requireContext(),"Tidak dapat terhubung, mohon periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
        dialogLoading.dismiss()

    }


    private fun animationListener(jenis: String, menuListViewcard: View) {
        menuListViewcard.animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                menuListViewcard.clearAnimation()
                if (jenis == "exit") {
                    menuListViewcard.visibility = View.GONE
                }
                if (jenis == "pop") {
                    animStart = true
                }

            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
    }

    override fun toPesanan() {
        findNavController().navigate(R.id.action_homeFragment_to_pesanan_saya)

    }

    override fun toProfile() {
        findNavController().navigate(R.id.action_homeFragment_to_menuprofile)
    }

    override fun toPromo() {

        val nav = HomeFragmentDirections.actionHomeFragmentToMenuFragment(arrayOf("", "promo"))

        findNavController().navigate(nav)

    }

    override fun toPaket() {
        val nav = HomeFragmentDirections.actionHomeFragmentToMenuFragment(arrayOf("","paket"))
        findNavController().navigate(nav)
    }
}