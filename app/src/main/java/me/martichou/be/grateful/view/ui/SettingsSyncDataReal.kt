package me.martichou.be.grateful.view.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.dialog_password.view.*
import me.martichou.be.grateful.R
import me.martichou.be.grateful.data.model.DatabaseObject
import me.martichou.be.grateful.databinding.FragmentSyncdatarealBinding
import me.martichou.be.grateful.persistance.local.AppDatabase
import me.martichou.be.grateful.persistance.local.UtilsDb
import me.martichou.be.grateful.utils.encryption.EncryptUtils
import me.martichou.be.grateful.utils.encryption.HashUtils
import timber.log.Timber
import java.util.*

class SettingsSyncDataReal : Fragment() {

    private lateinit var binding: FragmentSyncdatarealBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var buttonbackup: CircularProgressButton? = null
    private var buttonrestore: CircularProgressButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSyncdatarealBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        Timber.d("UID2: ${auth.uid}")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Bind databinding val
        binding.lifecycleOwner = viewLifecycleOwner
        binding.hdl = this

        checkIfAlreadyBacked()

        Snackbar.make(binding.root, "Logged with ${auth.currentUser?.phoneNumber}", Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        buttonbackup?.dispose()
    }

    fun back(v: View) {
        findNavController().popBackStack()
    }

    fun btnClick(v: View) {
        buttonbackup = v as CircularProgressButton
        showDialog()
    }

    fun restoreData(v: View) {
        buttonrestore = v as CircularProgressButton
    }

    private fun setupListener(date: Date?) {
        binding.lottieLoading.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                Timber.d("Animation: end")
                if(date != null) {
                    binding.backupStatus.text = "Last backup done:\n$date"

                    binding.buttonrestore.alpha = 0f
                    binding.buttonrestore.visibility = View.VISIBLE

                    binding.buttonrestore.animate().setDuration(450).alpha(1f).setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            binding.buttonrestore.visibility = View.VISIBLE
                        }
                    }).startDelay = 100
                } else {
                    binding.backupStatus.text = "No previous backup"
                }
                binding.backupStatus.alpha = 0f
                binding.backupStatus.visibility = View.VISIBLE

                binding.backupStatus.animate().setDuration(450).alpha(1f).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.backupStatus.visibility = View.VISIBLE
                    }
                }).startDelay = 100

                binding.lottieLoading.animate().setDuration(350).alpha(0f).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.lottieLoading.visibility = View.GONE
                    }
                })
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun checkIfAlreadyBacked() {
        val docRef = db.collection("users").document(auth.uid!!)
        docRef.get().addOnSuccessListener { result ->
            if(result.data != null) {
                // Backup exist, let's get date of lastest
                val databaseObject = result.toObject(DatabaseObject::class.java)
                setupListener(databaseObject!!.date)
                binding.lottieLoading.repeatCount = 0
            } else {
                // No previous backup
                setupListener(null)
                binding.lottieLoading.repeatCount = 0
            }
        }.addOnFailureListener { exception ->
            // Error retrieving backup
            Timber.d("Error getting documents. $exception")
        }
    }

    private fun showDialog() {
        val v = layoutInflater.inflate(R.layout.dialog_password, null)

        val d = AlertDialog.Builder(requireContext())
            .setTitle("Enter a password to encrypt your data")
            .setView(v)
            .setPositiveButton("Confirm", null)
            .setNegativeButton("Cancel", null)
            .create()

        d.setOnShowListener {
            val buttonP = d.getButton(AlertDialog.BUTTON_POSITIVE)
            val buttonN = d.getButton(AlertDialog.BUTTON_NEGATIVE)
            buttonP.setOnClickListener {
                // TODO Do something
                if(v.password.text.toString().isEmpty()){
                    v.password_til.error = "You must enter a password"
                } else {
                    buttonbackup?.startAnimation()
                    // Encrypt and send data
                    encryptAndSend(HashUtils.sha1(v.password.text.toString()))

                    d.dismiss()
                }
            }
            buttonN.setOnClickListener {
                d.dismiss()
            }
        }
        d.show()
    }

    private fun encryptAndSend(pass: String){
        val users = db.collection("users")
        val date = Date()
        users.document(auth.uid!!).set(DatabaseObject(
                date,
                EncryptUtils.encryptString(UtilsDb().getResults(AppDatabase.getInstance(requireContext())).toString(), pass))
        ).addOnSuccessListener {
            buttonbackup?.revertAnimation {
                buttonbackup?.background = resources.getDrawable(R.drawable.round_success, null)
                buttonbackup?.text = "Success"
            }
            binding.backupStatus.text = "Last backup done:\n$date"
        }.addOnFailureListener {
            buttonbackup?.revertAnimation {
                buttonbackup?.background = resources.getDrawable(R.drawable.round_error, null)
                buttonbackup?.text = "Failed"
            }
        }

        // TODO - Backup the images
    }

    /**
     * @param pass is the password hashed by SHA-1
     */
    private fun retreiveData(pass: String) {
        val docRef = db.collection("users").document(auth.uid!!)
        docRef.get().addOnSuccessListener { result ->
                if(result.data != null) {
                    val databaseObject = result.toObject(DatabaseObject::class.java)
                    try{
                        val p = JsonParser().parse(EncryptUtils.decryptString(databaseObject!!.cryptedData!!, pass)) // Data as JsonElement
                        val jsonArray = p.asJsonArray
                        Timber.d("Decrypted: $jsonArray")

                        // TODO - Ask to apply the backup and restore it
                    } catch (e: Exception) {
                        // Password is incorrect
                        Timber.e("Password incorrect: $e")
                    }
                } else {
                    Timber.d("No data")
                }
            }.addOnFailureListener { exception ->
                Timber.d("Error getting documents. $exception")
            }
    }
}