package me.martichou.be.grateful.utils.encryption

import java.security.MessageDigest

/**
 * Hashing Utils
 * @author Sam Clarke <www.samclarke.com>
 * @license MIT
 *
 * Trimmed - Martin Andre <martichou.andre@gmail.com>
 */
object HashUtils {

    fun sha1(input: String) = hashString(input)

    private fun hashString(input: String): String {
        val s = "0123456789ABCDEF"
        val bytes = MessageDigest
                .getInstance("SHA-1")
                .digest(input.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(s[i shr 4 and 0x0f])
            result.append(s[i and 0x0f])
        }

        return result.toString()
    }
}