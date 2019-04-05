package me.martichou.be.grateful.utils.encryption

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

internal class EncryptUtils {
    companion object {

        private const val ALGORITHM_NAME: String = "AES/GCM/NoPadding"
        private const val ALGORITHM_NONCE_SIZE: Int = 12
        private const val ALGORITHM_TAG_SIZE: Int = 128
        private const val ALGORITHM_KEY_SIZE: Int = 128
        private const val PBKDF2_NAME: String = "PBKDF2WithHmacSHA256"
        private const val PBKDF2_SALT_SIZE: Int = 16
        private const val PBKDF2_ITERATIONS = 32767

        fun encryptString(obj: String, password: String): String {
            // Generate a 128-bit salt using a CSPRNG.
            val rand = SecureRandom()
            val salt = ByteArray(PBKDF2_SALT_SIZE)
            rand.nextBytes(salt)

            // Create an instance of PBKDF2 and derive a key.
            val pwSpec = PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, ALGORITHM_KEY_SIZE)
            val keyFactory: SecretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_NAME)
            val key: ByteArray = keyFactory.generateSecret(pwSpec).encoded

            // Encrypt and prepend salt.
            val ciphertextAndNonce: ByteArray = encrypt(obj.toByteArray(StandardCharsets.UTF_8), key)
            val ciphertextAndNonceAndSalt = ByteArray(salt.size + ciphertextAndNonce.size)
            System.arraycopy(salt, 0, ciphertextAndNonceAndSalt, 0, salt.size)
            System.arraycopy(ciphertextAndNonce, 0, ciphertextAndNonceAndSalt, salt.size, ciphertextAndNonce.size)

            return Base64.encodeToString(ciphertextAndNonceAndSalt, Base64.DEFAULT)
        }

        fun decryptString(base64CiphertextAndNonceAndSalt: String, password: String): String {
            // Decode the base64.
            val ciphertextAndNonceAndSalt: ByteArray = Base64.decode(base64CiphertextAndNonceAndSalt, Base64.DEFAULT)

            // Retrieve the salt and ciphertextAndNonce.
            val salt = ByteArray(PBKDF2_SALT_SIZE)
            val ciphertextAndNonce = ByteArray(ciphertextAndNonceAndSalt.size - PBKDF2_SALT_SIZE)
            System.arraycopy(ciphertextAndNonceAndSalt, 0, salt, 0, salt.size)
            System.arraycopy(ciphertextAndNonceAndSalt, salt.size, ciphertextAndNonce, 0, ciphertextAndNonce.size)

            // Create an instance of PBKDF2 and derive the key.
            val pwSpec = PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, ALGORITHM_KEY_SIZE)
            val keyFactory: SecretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_NAME)
            val key: ByteArray = keyFactory.generateSecret(pwSpec).encoded

            // Decrypt and return result.
            return String(decrypt(ciphertextAndNonce, key))
        }

        private fun encrypt(plaintext: ByteArray, key: ByteArray): ByteArray {
            // Generate a 96-bit nonce using a CSPRNG.
            val rand = SecureRandom()
            val nonce = ByteArray(ALGORITHM_NONCE_SIZE)
            rand.nextBytes(nonce)

            // Create the cipher instance and initialize.
            val cipher: Cipher = Cipher.getInstance(ALGORITHM_NAME)
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(ALGORITHM_TAG_SIZE, nonce))

            // Encrypt and prepend nonce.
            val ciphertext: ByteArray = cipher.doFinal(plaintext)
            val ciphertextAndNonce = ByteArray(nonce.size + ciphertext.size)
            System.arraycopy(nonce, 0, ciphertextAndNonce, 0, nonce.size)
            System.arraycopy(ciphertext, 0, ciphertextAndNonce, nonce.size, ciphertext.size)

            return ciphertextAndNonce
        }

        private fun decrypt(ciphertextAndNonce: ByteArray, key: ByteArray): ByteArray {
            // Retrieve the nonce and ciphertext.
            val nonce = ByteArray(ALGORITHM_NONCE_SIZE)
            val ciphertext = ByteArray(ciphertextAndNonce.size - ALGORITHM_NONCE_SIZE)
            System.arraycopy(ciphertextAndNonce, 0, nonce, 0, nonce.size)
            System.arraycopy(ciphertextAndNonce, nonce.size, ciphertext, 0, ciphertext.size)

            // Create the cipher instance and initialize.
            val cipher: Cipher = Cipher.getInstance(ALGORITHM_NAME)
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(ALGORITHM_TAG_SIZE, nonce))

            // Decrypt and return result.
            return cipher.doFinal(ciphertext)
        }
    }
}
