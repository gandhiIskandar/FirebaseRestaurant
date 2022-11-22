package com.example.ayamjumpa.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.ayamjumpa.MainActivity
import com.example.ayamjumpa.R
import com.example.ayamjumpa.dataClass.User
import com.example.ayamjumpa.databinding.FragmentProfileBinding


import com.example.ayamjumpa.dialog.GantiPasswordDialog
import com.example.ayamjumpa.util.AlertDialogBuilder
import com.example.ayamjumpa.viewModel.AuthViewModel
import com.example.ayamjumpa.viewModel.CartViewModel
import com.example.ayamjumpa.viewModel.CartViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.security.auth.PrivateCredentialPermission


@Suppress("RedundantIf")
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var userData =
        User("Loading..", "Loading..", "Loading..", noHp = "Loading..", isAdmin = false)
    var firebaseAuth = Firebase.auth
    var firebstore = Firebase.firestore

    var reqCode = 0
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var authCredentialPermission: AuthCredential

    private lateinit var firebseStorage: FirebaseStorage
    private lateinit var dialogLoading: AlertDialogBuilder
    private lateinit var mactivity: Activity
    private lateinit var vm: CartViewModel
    private val nomoraing = arrayOfNulls<String>(2)
    private val firestoreViewModel: AuthViewModel by viewModels()


    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewModel: CartViewModel by lazy {
            ViewModelProvider(
                this,
                CartViewModelFactory(requireContext()),
            )[CartViewModel::class.java]
        }

        vm = viewModel

        resultLauncher = resultLauncer()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        firebseStorage = FirebaseStorage.getInstance()
        binding.btnGantiFoto.setOnClickListener {
            selectImage()
        }

        val dialog = GantiPasswordDialog(
            onSubmitClickListener = { data ->
                firebaseAuth.currentUser!!.reauthenticate(authCredentialPermission)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (data[1] == data[2] && data[0] == userData.password) {
                                firebaseAuth.currentUser?.updatePassword(data[1])!!
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            firebstore.collection("users")
                                                .document(firebaseAuth.uid!!).update(
                                                    "password", data[1]
                                                )
                                            Snackbar.make(
                                                requireView(),
                                                "Password berhasil diupdate",
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                "Password gagal diupdate ${it.exception}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Snackbar.make(
                                    requireView(),
                                    "Password salah, mohon cek kembali",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }

            }
        )

        binding.editButtonProfile.setOnClickListener {

            updateFirestoreAndEditEmail(
                User(
                    binding.profileEtEmail.text.toString(),
                    userData.password,
                    binding.profileEtNama.text.toString(),
                    noHp = binding.profileEtNohp.text.toString(),
                    isAdmin = false,
                    foto = userData.foto
                    // binding.profileEtAlamat.text.toString()


                )
            )

        }

        scope.launch {

            withContext(Dispatchers.IO) {
                getUser(firebaseAuth.uid!!)
            }


            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (binding.profileEtEmail.text.toString() != userData.email || binding.profileEtNama.text.toString() != userData.username ||
                        binding.profileEtNohp.text.toString() != userData.noHp
                    ) {
                        if (binding.profileEtEmail.text.toString() !="" && !binding.profileEtNama.text!!.isEmpty() && binding.profileEtNohp.text.toString() != "") {
                            binding.editButtonProfile.isEnabled = true
                        } else {
                            binding.editButtonProfile.isEnabled = false
                        }


                    } else {
                        binding.editButtonProfile.isEnabled = false
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            }

            binding.profileEtEmail.addTextChangedListener(textWatcher)
            binding.profileEtNama.addTextChangedListener(textWatcher)
            binding.profileEtNohp.addTextChangedListener(textWatcher)

            authCredentialPermission =
                EmailAuthProvider.getCredential(userData.email, userData.password)


        }


        //set default sebagai loading..
        binding.user = userData

        binding.btnGantiPassword.setOnClickListener {
            dialog.show(parentFragmentManager, "dialog")
        }

        firestoreViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                dialogLoading = AlertDialogBuilder(mactivity)
                dialogLoading.startAlertDialog("Uploading..")
            }
            if (it == false) {
                dialogLoading.dismiss()
            }


        })
        vm.getNomor()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        vm.nomorkuX?.observe(viewLifecycleOwner, Observer {
            nomoraing[1] = it

            Log.d("bbbv", it)

        })


    }

    fun uploadImage(bitmap: Bitmap) {
        firestoreViewModel.isLoading()

        val storage = firebseStorage.reference.child("images/${firebaseAuth.uid}.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storage.putBytes(data)

        uploadTask.addOnSuccessListener {

            it.metadata?.reference?.downloadUrl?.addOnCompleteListener { up ->

                if (up.isSuccessful) {
                    firebstore.collection("users").document(firebaseAuth.uid.toString()).update(
                        "foto", up.result
                    )
                    userData.foto = up.result.toString()
                    firestoreViewModel.isNotLoading()
                }
            }

            Snackbar.make(requireView(), "Foto berhasil diganti", Snackbar.LENGTH_LONG).show()


        }.addOnFailureListener {

            firestoreViewModel.isNotLoading()

            Snackbar.make(requireView(), it.toString(), Snackbar.LENGTH_LONG).show()

            Log.d("storagee", it.toString())


        }


    }

    fun resultLauncer(): ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->


            if (result.resultCode == Activity.RESULT_OK) {


                if (reqCode == 10) {
                    val extras = result!!.data?.extras
                    val bitmap = extras!!.get("data") as Bitmap
                    Log.d("requCd", "masukk")

                    Glide.with(requireContext())
                        .load(bitmap)
                        .into(binding.fotoku)

                    uploadImage(bitmap)


                } else if (reqCode == 20) {
                    try {
                        Log.d("requCd", "masukk1")
                        val path = result!!.data?.data
                        val inputStream = context?.contentResolver!!.openInputStream(path!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        if (bitmap != null) {

                            Glide.with(requireContext())
                                .load(bitmap)
                                .into(binding.fotoku)

                            uploadImage(bitmap)
                        }


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }


    suspend fun getUser(uid: String) {
        withContext(Dispatchers.IO) {

            val ref = firebstore.collection("users").document(uid)

            try {
                ref.get().addOnCompleteListener {
                    if (it.isSuccessful) {

                        userData = it.result.toObject<User>()!!
                        binding.user = userData
                        Glide.with(requireContext()).load(userData.foto).into(binding.fotoku)
                        nomoraing[0] = userData.noHp!!

                        Log.d("linkfoto", userData.foto.toString())
                    }
                }
            } catch (e: FirebaseFirestoreException) {

            }


        }
    }


    fun updateFirestoreAndEditEmail(user: User) {
        val ref = firebstore.collection("users").document(firebaseAuth.uid!!)

        ref.set(user)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    val mutable: MutableList<String> = userData.noHPlain as MutableList<String>
                    mutable[0] = user.noHp!!

                    ref.update("noHPlain", mutable)

                    //bug seharusnya nomor hp sebelum diganti baru dibandingkan
                    if (nomoraing[0] == nomoraing[1]) {
                        vm.setNomor(user.noHp!!)
                    }



                    Snackbar.make(requireView(), "Data berhasil diupdate", Snackbar.LENGTH_SHORT)
                        .show()

                    if (user.email != userData.email) {
                        firebaseAuth.currentUser?.updateEmail(user.email)!!
                            .addOnCompleteListener { }
                    }

                }
            }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu2, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_menu -> {
                firebaseAuth.signOut()
                Intent(activity, MainActivity::class.java).also { intent ->
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        mactivity = activity

    }

    private fun selectImage() {


        val items = arrayOf<String>("Take Photo", "Choose from Library", "Cancel")
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Ganti Foto Profile")



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


}