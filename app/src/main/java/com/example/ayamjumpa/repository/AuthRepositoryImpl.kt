package com.example.ayamjumpa.repository


import android.app.Activity
import android.view.View
import com.example.ayamjumpa.dataClass.User
import com.example.ayamjumpa.eventBus.StatusMessage
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.greenrobot.eventbus.EventBus

class AuthRepositoryImpl() : AuthRepository {

    private val firebaseAuth = Firebase.auth
    private val firebaseFirestore = Firebase.firestore

    override fun login(email: String, password: String): Boolean {
        var result = false
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {

                result = true


            } else {
                EventBus.getDefault().post(StatusMessage(it.exception.toString()))
                }

        }

        return result

    }

    override fun register(user: User): Boolean {
        var result = false
        firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result = addtoFireStore(user, firebaseAuth.uid!!)

                } else {
                    EventBus.getDefault().post(StatusMessage(it.exception.toString()))
                }

            }
        return result
    }

    private fun addtoFireStore(user: User, uid: String): Boolean {
        var result = false
        firebaseFirestore.collection("users")
            .document(uid).set(user)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    result = true

                    firebaseFirestore.collection("users").document(uid).update("noHPlain",
                        FieldValue.arrayUnion(user.noHp))



                } else {

                    EventBus.getDefault().post(StatusMessage(it.exception.toString()))


                }

            }
        return result

    }


}