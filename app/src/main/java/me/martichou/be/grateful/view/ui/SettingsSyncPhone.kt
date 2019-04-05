package me.martichou.be.grateful.view.ui

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentSyncdataphoneBinding
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SettingsSyncPhone : Fragment() {

    private lateinit var binding: FragmentSyncdataphoneBinding

    private lateinit var auth: FirebaseAuth

    private var verificationInProgress = false
    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var buttonVerif: CircularProgressButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSyncdataphoneBinding.inflate(inflater, container, false)
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
        val s = binding.phoneNumber.text
        if(s.isNullOrEmpty()){

            binding.phonenumberTil.error = "You must provide a phone number to continue"

        } else {
            buttonVerif = v as CircularProgressButton

            if (buttonVerif?.tag == "firststep") {
                Timber.d("Called")
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        s.toString(),        // Phone number to verify
                        60,                                         // Timeout duration
                        TimeUnit.SECONDS,                           // Unit of timeout
                        requireActivity(),                          // Activity (for callback binding)
                        callbacks())                                // OnVerificationStateChangedCallbacks
            } else if (buttonVerif?.tag == "secondstep") {
                Timber.d("Called2")
                if(!binding.phoneNumber.text.isNullOrEmpty()){
                    verifyPhoneNumberWithCode(storedVerificationId, binding.phoneNumber.text.toString())
                }
            }

            buttonVerif?.startAnimation()
        }
    }

    private fun callbacks() = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Timber.d("onVerificationCompleted: $credential")
            // [START_EXCLUDE silent]
            verificationInProgress = false
            // [END_EXCLUDE]

            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Timber.d("onVerificationFailed: $e")
            // [START_EXCLUDE silent]
            verificationInProgress = false
            // [END_EXCLUDE]

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // [START_EXCLUDE]
                Timber.e("Invalid phone number.")
                // [END_EXCLUDE]
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            buttonVerif?.revertAnimation {
                buttonVerif?.background = resources.getDrawable(R.drawable.round_error)
                buttonVerif?.text = "Wrong number"
            }

        }

        override fun onCodeSent(
                verificationId: String?,
                token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Timber.d("onCodeSent: $verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token

            buttonVerif?.revertAnimation{
                binding.phonenumberTil.hint = "Enter the code sent received by SMS"
                binding.phoneNumber.text = null
                binding.phoneNumber.inputType = InputType.TYPE_CLASS_NUMBER
                buttonVerif?.tag = "secondstep"
                buttonVerif?.background = resources.getDrawable(R.drawable.round_success)
                buttonVerif?.text = "Continue"
            }
        }
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.d("signInWithCredential:success")

                    val user = task.result?.user
                    // ...
                    Timber.d("User: $user")
                    buttonVerif?.revertAnimation {
                        buttonVerif?.background = resources.getDrawable(R.drawable.round_success)
                        buttonVerif?.text = "Success !"
                    }
                    findNavController().navigate(SettingsSyncPhoneDirections.actionSettingsSyncPhoneToSettingsSyncDataReal())
                } else {
                    // Sign in failed, display a message and update the UI
                    Timber.d("signInWithCredential:failure ${task.exception}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }

                    buttonVerif?.revertAnimation {
                        buttonVerif?.background = resources.getDrawable(R.drawable.round_error)
                        buttonVerif?.text = "Error..."
                    }
                }
            }
    }

}