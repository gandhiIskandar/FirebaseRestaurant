package com.example.ayamjumpa.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ayamjumpa.R
import com.example.ayamjumpa.dataClass.Alamat
import com.example.ayamjumpa.databinding.FragmentAlamatBinding
import com.example.ayamjumpa.util.AlertDialogBuilder
import com.example.ayamjumpa.util.MyState
import com.example.ayamjumpa.util.NetworkStatusViewModel
import com.example.ayamjumpa.viewModel.CartViewModel
import com.example.ayamjumpa.viewModel.CartViewModelFactory
import com.example.ayamjumpa.viewModel.NetworkViewModelFactory
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*
import kotlin.collections.HashSet


class AlamatFragment : Fragment(), OnMapReadyCallback {

    var currentMarker: Marker? = null
    private lateinit var binding: FragmentAlamatBinding
    private var mMap: GoogleMap? = null
    private lateinit var mContext: Context
    private lateinit var mActivity: Activity

    private lateinit var locationRequestBuilder: LocationRequest.Builder
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var mGpsLocationClient: LocationManager
    private lateinit var locationListener: android.location.LocationListener


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var currentLocation: Location? = null
    var firestore = Firebase.firestore
    var auth = Firebase.auth
    var myLatlong: LatLng? = null

    var markerLatlong: LatLng? = null
    var alamatPendek: String? = null
    var isConnected: Boolean = false
    var km = ""
    private var kcak: CartViewModel? = null
    private var alamataing: Alamat? = null

    //digunakan untuk percabangan if melihat apakah edit alamat atau hanya tambah alamat
    var editLatLong: LatLng? = null
    var idalamatku: String? = null
    var alamatku_edit: String? = null

    var prevnav: String? = null

    //loading dialog
    var dialogLoading: AlertDialogBuilder? = null

    private val viewModel: NetworkStatusViewModel by activityViewModels()

    private val markerList: HashSet<Marker> = HashSet<Marker>()

    private val scope = CoroutineScope(Job() + Dispatchers.Main)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val handler = Handler(Looper.getMainLooper())




        prevnav = findNavController().previousBackStackEntry?.destination?.label.toString().trim()

        dialogLoading = AlertDialogBuilder(mActivity)


        val runnabler = Runnable { fetchLocationTimeOut() }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity)
        locationRequestBuilder = LocationRequest.Builder(500)
        locationRequest = locationRequestBuilder.build().apply {

            Priority.PRIORITY_HIGH_ACCURACY
        }
      //  handler.postDelayed(runnabler, 30000)

        //kentircodeee


        //  getLocation()


        viewModel.currentLocationLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            currentLocation = it
            fetchLoaction()
        })


        //kentireee

        val viewModel: CartViewModel by lazy {
            ViewModelProvider(
                this,
                CartViewModelFactory(requireContext()),
            )[CartViewModel::class.java]
        }

        kcak = viewModel

        kcak!!.getAlamat()

        kcak!!.alamatkuX.observe(this.viewLifecycleOwner) {
            alamataing = it
        }



        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alamat, container, false)


        val bundle = arguments


        if (!bundle!!.isEmpty) {

            val args = AlamatFragmentArgs.fromBundle(bundle)

            val alamatku = args.alamatnya

            alamatku_edit = alamatku.alamat

            binding.labelalamat.text = alamatku.label?.toEditable()
            binding.alamatlengkap.text = alamatku_edit?.toEditable()
            binding.keteranganalamat.text = alamatku.nomorHp?.toEditable()
            editLatLong = LatLng(alamatku.lat!!, alamatku.long!!)

            binding.tambahalamat.text = "edit alamat"

            idalamatku = alamatku.id
            binding.alamatlengkap.text = alamatku_edit?.toEditable()

            // fetchLoaction()


        }

        binding.tambahalamat.setOnClickListener {
            tambahAtauEditAlamat(
                Alamat(
                    id = if (idalamatku != null) idalamatku else null,
                    lat = markerLatlong?.latitude,
                    long = markerLatlong?.longitude,
                    alamat = binding.alamatlengkap.text.toString(),
                    label = binding.labelalamat.text.toString(),
                    nomorHp = binding.keteranganalamat.text.toString(),
                    alamatPendek = alamatPendek,
                    ongkir = km.toDouble() * 8000

                )
            )

        }



        Log.d("mMap", mMap.toString())



        return binding.root
    }


    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )

            return

        } else {


            dialogLoading!!.startAlertDialog("Fetching Location..")
            mGpsLocationClient.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L, 0f, locationListener
            )
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val watcher: TextWatcher = object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                binding.tambahalamat.isEnabled =
                    binding.alamatlengkap.text!!.isNotEmpty() && binding.keteranganalamat.text!!.isNotEmpty() && binding.labelalamat.text!!.toString()
                        .isNotEmpty()
            }

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                //YOUR CODE
            }

            override fun afterTextChanged(s: Editable) {

            }
        }

        binding.alamatlengkap.addTextChangedListener(watcher)
        binding.keteranganalamat.addTextChangedListener(watcher)
        binding.labelalamat.addTextChangedListener(watcher)




        scope.launch {


            viewModel.state.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                when (it) {
                    MyState.Fetched -> {
                        isConnected = true

                        if (myLatlong != null && !markerList.contains(currentMarker))
                            if (editLatLong == null) {
                                drawMarker(myLatlong!!)
                            } else {
                                drawMarker(editLatLong!!)
                            }


                    }
                    MyState.Error -> {
                        isConnected = false
                        markerList.remove(currentMarker)
                        mMap?.clear()
                        currentMarker?.remove()

                    }
                }
            })


        }
        // fetchLoaction()

    }


    private val long_atl = 104.97843902822282
    private val lat_atl = -5.357588802257161

    private fun fetchLocationTimeOut() {
        mGpsLocationClient.removeUpdates(locationListener)
        dialogLoading!!.dismiss()

        Toast.makeText(
            mContext,
            "Tidak bisa mendapatkan lokasi, mohon coba lagi setelah beberapa saat",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onResume() {
        super.onResume()
        val statustempat = resources.getStringArray(R.array.statusTempat)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, statustempat)
        //binding.autoCompelete.setAdapter(arrayAdapter)
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.d("test1233", "location.toString()")

        mMap!!.uiSettings.isMyLocationButtonEnabled = true



        mMap!!.isMyLocationEnabled = true
        val latlong = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)
        myLatlong = latlong

        if (editLatLong == null) {
            mMap?.animateCamera(CameraUpdateFactory.newLatLng(latlong))

            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 15f))
            drawMarker(latlong)

        } else {
            mMap?.animateCamera(CameraUpdateFactory.newLatLng(editLatLong!!))

            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(editLatLong!!, 15f))
            drawMarker(editLatLong!!)

        }



        mMap!!.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(p0: Marker) {

            }

            override fun onMarkerDragStart(p0: Marker) {
                // binding.scrollView2.requestDisallowInterceptTouchEvent(true)
            }

            override fun onMarkerDragEnd(p0: Marker) {

                if (currentMarker != null) {
                    currentMarker?.remove()
                    val newLatLong = LatLng(p0.position.latitude, p0.position.longitude)
                    drawMarker(newLatLong)


                }
            }
        })

    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mActivity = activity
    }

    private fun drawMarker(latlong: LatLng) {
        val jarak = SphericalUtil.computeDistanceBetween(latlong, LatLng(lat_atl, long_atl))
        km = String.format(Locale.US, "%.1f", jarak / 1000)
        scope.launch {


            withContext(Dispatchers.Main) {
                try {


                    delay(2500)
                    val marker = MarkerOptions().position(latlong).title("Disini lokasi saya")
                        .snippet("${km} km")
                        .draggable(true)

                    mMap?.animateCamera(CameraUpdateFactory.newLatLng(latlong))

                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 15f))
                    currentMarker = mMap?.addMarker(marker)


                    currentMarker?.showInfoWindow()



                    markerLatlong = latlong

                    markerList.add(currentMarker!!)


                } catch (e: IOException) {
                    Snackbar.make(
                        requireView(),
                        "Jaringan sedang tidak stabil, mohon tunggu",
                        Snackbar.LENGTH_LONG
                    ).show()


                }


            }


        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            1000 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                fetchLoaction()
            }
        }

    }

    private fun fetchLoaction() {

        if (ActivityCompat.checkSelfPermission(
                mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                mContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED

        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )

            return

        } else {


            val mapFragment =
                childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment



            mapFragment.getMapAsync(this)


        }


    }

    private fun stopLocationListener() {
        mGpsLocationClient.removeUpdates(locationListener)

    }


    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)


    private fun tambahAtauEditAlamat(alamat: Alamat) {


        val alamatRef =
            firestore.collection("users").document(auth.uid!!).collection("alamat")

        val idAlamat = if (alamat.id == null) alamatRef.document().id else alamat.id


        val alamatFix = hashMapOf(
            "id" to idAlamat,
            "lat" to alamat.lat,
            "long" to alamat.long,
            "alamat" to alamat.alamat,
            "label" to alamat.label,
            "nomorHp" to alamat.nomorHp,
            "alamatPendek" to alamat.alamatPendek,
            "ongkir" to alamat.ongkir,
            "time" to alamat.time

        )

        alamatRef.document(idAlamat!!).set(alamatFix).addOnCompleteListener {
            if (it.isSuccessful) {

                if (alamatku_edit != null && alamataing != null) {
                    if (alamataing!!.id == idalamatku) {

                        kcak?.setAlamat(alamat)
                    }

                }

                Snackbar.make(
                    requireView(),
                    if (alamat.id == null) "Alamat Berhasil ditambahkan" else "Alamat Berhasil diedit",
                    Snackbar.LENGTH_SHORT
                )
                    .show()

                if (prevnav == "keranjang") {
                    findNavController().navigate(R.id.action_alamatFragment_to_cartFragment2)
                } else {
                    findNavController().navigate(R.id.action_alamatFragment_to_blankFragment)
                }


            } else {

                Snackbar.make(
                    requireView(),
                    "Alamat gagal ditambah atau diedit, mohon cek koneksi anda",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }


    }


}