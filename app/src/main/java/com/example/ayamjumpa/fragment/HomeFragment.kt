package com.example.ayamjumpa.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.ayamjumpa.R
import com.example.ayamjumpa.interfaces.MenuListener
import com.example.ayamjumpa.interfaces.RecycleClickListener
import com.example.ayamjumpa.adapter.ImageSliderAdapter
import com.example.ayamjumpa.adapter.KateogriAdapter
import com.example.ayamjumpa.adapter.RasaAdapter
import com.example.ayamjumpa.adapter.TerlarisAdapter
import com.example.ayamjumpa.dataClass.*
import com.example.ayamjumpa.databinding.FragmentHomeBinding

import com.example.ayamjumpa.eventBus.StatusMessage
import com.example.ayamjumpa.viewModel.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import org.greenrobot.eventbus.EventBus
import java.lang.Runnable


class HomeFragment : Fragment(), MenuListener, RecycleClickListener {
    private lateinit var menuListener: MenuListener
    private lateinit var clickListener: RecycleClickListener
    private var adapterKategori: KateogriAdapter? = null
    private var adapterRasa: RasaAdapter? = null
    private lateinit var binding: FragmentHomeBinding
    private var adaitem: Boolean = false
    private lateinit var firestore: FirebaseFirestore
    private lateinit var cartLoader: ListenerRegistration
    private lateinit var cartLoader1: ListenerRegistration
    private lateinit var auth: FirebaseAuth
    lateinit var layoutManager: LinearLayoutManager
    lateinit var layoutManagerRasa: LinearLayoutManager
    lateinit var layoutManager1: GridLayoutManager
    private lateinit var userData: User
    private lateinit var arrayKategori: ArrayList<Kategori>
    private val firestoreViewModel: AuthViewModel by viewModels()
    private var listImage = arrayListOf<ImageData>()
    val dots: ArrayList<TextView> = ArrayList()
    private lateinit var mContext: Context

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private var scope: CoroutineScope? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            var index = 0
            override fun run() {
                if (index == listImage.size)
                    index = 0
                Log.d("ngawur",index.toString())

                binding.viewPager.setCurrentItem(index)
                index++
                handler.postDelayed(this, 2000)

            }
        }
        scope = CoroutineScope(Job() + Dispatchers.Main)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        firestore = Firebase.firestore
        auth = Firebase.auth
        userData = User("Loading..", "Loading..", "Loading..", noHp = "Loading..", isAdmin = false)
        arrayKategori = arrayListOf()
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        layoutManagerRasa = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        layoutManager1 = GridLayoutManager(requireContext(), 2)

        firestoreViewModel.getUser(auth.uid!!).observe(viewLifecycleOwner, Observer {
            userData = it
            setProfile()
        })

        firestoreViewModel.getTotal().observe(viewLifecycleOwner) {


            if (it.size > 0) {
                adaitem = true
                binding.constraint11.visibility = View.VISIBLE
                binding.itemtotal.text = it[1]
                binding.hargatotal.text = it[0]
            } else {
                adaitem = false
                binding.constraint11.visibility = View.GONE
            }

        }

        KeyboardVisibilityEvent.setEventListener(
            requireActivity(),
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    if (isOpen) {
                        binding.constraint11.visibility = View.GONE
                    } else {
                        if (adaitem) {
                            Handler().postDelayed({
                                binding.constraint11.visibility = View.VISIBLE
                            }, 500)
                        }
                    }
                }
            })

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



        return binding.root
    }

    override fun onDestroyView() {

        arrayKategori.clear()
        super.onDestroyView()
        cartLoader.remove()
        cartLoader1.remove()



    }

    override fun onStart() {
        super.onStart()
        handler.post(runnable)
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


        firestoreViewModel.getKategori().observe(viewLifecycleOwner, Observer {

            if (arrayKategori.size == 0) {

                Log.d("datautil", "ini data awal")

                arrayKategori = it as ArrayList
                val filtered = arrayKategori.filter { kateogri -> kateogri.tipe == "menu" }
                val filtered_rasa = arrayKategori.filter { kateogri -> kateogri.tipe == "rasa" }

                dataInitKategori(layoutManager, filtered as ArrayList)
                dataInitRasa(filtered_rasa as ArrayList)
            } else {
                Log.d("datautil", "ini data baru")
                adapterKategori!!.differ.submitList(it.filter { kategori -> kategori.tipe == "menu" })
                adapterRasa!!.differ.submitList(it.filter { kategori -> kategori.tipe == "rasa" })

            }

        })

        scope?.launch {
            loadTerlaris()
            loadCart(auth.uid!!)


        }


    }

    suspend fun getUser(uid: String) {
        withContext(Dispatchers.IO) {

            val ref = firestore.collection("users").document(uid)


            try {

                ref.get().addOnCompleteListener {
                    if (it.isSuccessful) {

                        userData = it.result.toObject<User>()!!
                        setProfile()

                    }
                }


            } catch (e: FirebaseFirestoreException) {

            }


        }
    }

    override fun onPause() {
        super.onPause()
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
            val menucollection = firestore.collection("Menu")


            cartLoader1 = menucollection.addSnapshotListener { value, error ->


                if (value != null) {
                    menuModels.clear()
                    for (datax in value) {
                        Log.d("datanya", datax.toObject<Menu>().deskripsi.toString())

                        val menuModel = datax.toObject<Menu>()

                        menuModel.key = datax.id
                        menuModels.add(menuModel)


                    }

                    onMenuLoadSucces(menuModels)
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

    fun dataInitKategori(layoutManager: LinearLayoutManager, kategori: ArrayList<Kategori>) {


        adapterKategori = KateogriAdapter(mContext,
            invoker = {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToMenuFragment(
                        it
                    )
                )

            })

        adapterKategori!!.differ.submitList(kategori)

        val recyclerViewKategori = binding.recyclerViewKategori



        recyclerViewKategori.layoutManager = layoutManager
        recyclerViewKategori.setHasFixedSize(true)
        recyclerViewKategori.adapter = adapterKategori


    }


    fun dataInitRasa(kategori: ArrayList<Kategori>) {


        adapterRasa = RasaAdapter(mContext)

        adapterRasa!!.differ.submitList(kategori)

        val recyclerViewRasa = binding.recyclerViewAnekarasa



        recyclerViewRasa.layoutManager = layoutManagerRasa
        recyclerViewRasa.setHasFixedSize(true)
        recyclerViewRasa.adapter = adapterRasa


    }

    fun dataInitTerlaris(layoutManager: LinearLayoutManager, adapter: TerlarisAdapter) {


        val recyclerViewTerlaris = binding.recyclerViewTerlaris

        recyclerViewTerlaris.layoutManager = layoutManager
        recyclerViewTerlaris.setHasFixedSize(true)
        recyclerViewTerlaris.adapter = adapter


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

                    Log.d("bajingj", total.toString())


                } else {
                    firestoreViewModel.setTotalItem(mutable)
                }


            }

        }


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
                                "Barang berhasil dimasukan ke keranjang",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }.addOnCanceledListener {
                            Snackbar.make(
                                requireView(),
                                it.exception?.message.toString(),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    val mycart = Cart()

                    mycart.key = menu.key
                    mycart.qty = 1
                    mycart.totalharga = menu.harga
                    mycart.name = menu.nama
                    mycart.harga = menu.harga.toString()
                    mycart.foto = menu.foto

                    userCart.document(mycart.key!!).set(mycart).addOnSuccessListener {
                        Snackbar.make(
                            requireView(),
                            "Barang berhasil dimasukan ke keranjang",
                            Snackbar.LENGTH_SHORT
                        ).show()

                    }.addOnCanceledListener {
                        Snackbar.make(
                            requireView(),
                            it.exception?.message.toString(),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }


                }
            }


    }

    override fun onMenuLoadSucces(menuList: MutableList<Menu>) {
        val terlarisAdapter =
            TerlarisAdapter(requireActivity(), menuList, clickListener, auth.uid!!)

        dataInitTerlaris(layoutManager1, terlarisAdapter)

    }

    override fun onMenuLoadFailed(message: String?) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }


    override fun onItemClickListener(menu: Menu) {


        val dialog = BottomSheetDialog(requireContext())

        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        val view = layoutInflater.inflate(R.layout.tambah_konfirmasi, null)


        val foto = view.findViewById<ImageView>(R.id.foto_makanan)


        Glide.with(requireContext())
            .load(menu.foto)
            .into(foto)


        dialog.setContentView(view)

        val buttonf = view.findViewById<LinearLayout>(R.id.cardButton)

        buttonf.setOnClickListener {
            dialog.dismiss()
            addToCart(menu, auth.uid!!)

        }





        dialog.show()


        // addToCart(menu, auth.uid!!)

    }


}