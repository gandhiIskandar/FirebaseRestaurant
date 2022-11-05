package com.example.ayamjumpa.repository


import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.ayamjumpa.dataClass.Cart
import com.example.ayamjumpa.dataClass.Menu
import com.example.ayamjumpa.eventBus.StatusMessage
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class RepositoryImpl () : Repository {

    private lateinit var firestore: FirebaseFirestore
    private var instance:RepositoryImpl? = null

  public fun getInstance () {

      firestore = Firebase.firestore



  }

    override fun addToCart(menu: Menu, uid: String): String {
        var result = ""
        val userCart = firestore
            .collection("Cart")
            .document(uid).collection("myCart")

        userCart.document(menu.key!!)
            .get()
            .addOnCompleteListener {

                if (it.result!=null) {
                    val mycart = it.result.toObject<Cart>()
                    val qty = mycart!!.qty + 1
                    val total = qty * mycart.harga!!.toInt()
                    userCart.document(menu.key!!)
                        .update("qty", qty.toLong(), "totalharga", total.toLong())
                        .addOnSuccessListener {
                            EventBus.getDefault().post(StatusMessage("Barang berhasil dimasukan ke dalam keranjang"))

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
                        EventBus.getDefault().post(StatusMessage("Barang berhasil dimasukan ke dalam keranjang"))

                    }.addOnCanceledListener {
                        EventBus.getDefault().post(StatusMessage(it.exception.toString()))


                    }


                }
            }



        return result
    }

    override fun loadCart(uid: String) {

       val cartModels: MutableList<Cart> = arrayListOf()

        val cartcollection = firestore.collection("Cart").document(uid).collection("myCart")

        cartcollection.addSnapshotListener { value, error ->

            if (value!!.size() > 0) {
        cartModels.clear()

                for (x in value) {


                    val cartModel = x.toObject<Cart>()

                    cartModel.key = x.id

                    cartModels.add(cartModel)

                    Log.d("data", cartModel.name.toString())


                }

            }else{
                EventBus.getDefault().post(StatusMessage(error.toString()))
            }

        }




    }

    override fun loadTerlaris(): MutableLiveData<MutableList<Menu>> {
        val menuModels: MutableList<Menu> = ArrayList()
        val liveData = MutableLiveData<MutableList<Menu>>()
        val menucollection = firestore.collection("Menu")
            .orderBy("terjual", Query.Direction.DESCENDING).limit(6)

        menucollection.addSnapshotListener { value, error ->




            if (value != null) {
                for (datax in value) {
                    //  Log.d("datanya", datax.toObject<Menu>().deskripsi.toString())

                    val menuModel = datax.toObject<Menu>()

                    menuModel.key = datax.id
                    menuModels.add(menuModel)


                }


            } else {
                EventBus.getDefault().post(StatusMessage(error.toString()))
            }
        }

        liveData.value = menuModels
        return  liveData
    }


  override fun updateCart(cart:Cart, uid: String) {

            val userCart = Firebase.firestore
                .collection("Cart")
                .document(uid).collection("myCart").document(cart.key!!)

        userCart.set(cart).addOnCompleteListener {
            if(it.isCanceled){
                EventBus.getDefault().post(StatusMessage(it.exception.toString()))
            }
        }



    }


}