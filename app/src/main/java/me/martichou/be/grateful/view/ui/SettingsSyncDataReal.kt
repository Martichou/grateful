package me.martichou.be.grateful.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.google.firebase.auth.FirebaseAuth
import me.martichou.be.grateful.databinding.FragmentSyncdatarealBinding
import me.martichou.be.grateful.persistance.local.AppDatabase
import me.martichou.be.grateful.persistance.local.UtilsDb
import me.martichou.be.grateful.utils.encryption.EncryptUtils
import timber.log.Timber

class SettingsSyncDataReal : Fragment() {

    private lateinit var binding: FragmentSyncdatarealBinding

    private lateinit var auth: FirebaseAuth

    private var buttonVerif: CircularProgressButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSyncdatarealBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Bind databinding val
        binding.lifecycleOwner = viewLifecycleOwner
        binding.hdl = this
    }

    override fun onDestroy() {
        super.onDestroy()
        buttonVerif?.dispose()
    }

    fun btnClick(v: View) {
        buttonVerif = v as CircularProgressButton
        val e = EncryptUtils.encryptString(UtilsDb().getResults(AppDatabase.getInstance(requireContext())).toString(), "azerty")
        Timber.d("Encrypted: $e")


    }

    fun idk(){
        /*
        val d = EncryptUtils.decryptString(e, "azerty")
        val p = JsonParser().parse(d)
        Timber.d("Decrypted: ${p.asJsonArray.get(0).asJsonObject.get("title")}")
         */
    }

}