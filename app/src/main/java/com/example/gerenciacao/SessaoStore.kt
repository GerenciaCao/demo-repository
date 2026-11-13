package com.example.gerenciacao.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Guarda o token de login CRIPTOGRAFADO (AES-256-GCM). A chave fica no Android Keystore e nunca sai do aparelho;
 * o que vai para o disco é só o texto cifrado. Se algo falhar ao decifrar, o token é descartado (exige novo login).
 */
class SessaoStore(contexto: Context) {
    private val prefs = contexto.applicationContext.getSharedPreferences("sessao_segura", Context.MODE_PRIVATE)

    /** Cifra e grava o token. */
    fun salvarToken(token: String) {
        val cifra = Cipher.getInstance(TRANSFORMACAO).apply { init(Cipher.ENCRYPT_MODE, obterChave()) }
        val cifrado = cifra.doFinal(token.toByteArray(Charsets.UTF_8))
        val texto = Base64.encodeToString(cifra.iv, Base64.NO_WRAP) + ":" + Base64.encodeToString(cifrado, Base64.NO_WRAP)
        prefs.edit().putString(CHAVE_PREFS, texto).apply()
    }

    /** Lê e decifra o token; devolve null se não existir ou se não puder ser decifrado. */
    fun lerToken(): String? {
        val texto = prefs.getString(CHAVE_PREFS, null) ?: return null
        return try {
            val (iv, cifrado) = texto.split(":").let { Base64.decode(it[0], Base64.NO_WRAP) to Base64.decode(it[1], Base64.NO_WRAP) }
            val cifra = Cipher.getInstance(TRANSFORMACAO).apply {
                init(Cipher.DECRYPT_MODE, obterChave(), GCMParameterSpec(128, iv))
            }
            String(cifra.doFinal(cifrado), Charsets.UTF_8)
        } catch (e: Exception) {
            limpar()
            null
        }
    }

    /** Apaga o token guardado. */
    fun limpar() {
        prefs.edit().remove(CHAVE_PREFS).apply()
    }

    /** Pega (ou cria na primeira vez) a chave AES do Keystore. */
    private fun obterChave(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(ALIAS, null) as? SecretKey)?.let { return it }
        val gerador = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        gerador.init(
            KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build(),
        )
        return gerador.generateKey()
    }

    private companion object {
        const val ALIAS = "gerenciacao_token"
        const val CHAVE_PREFS = "token_cifrado"
        const val TRANSFORMACAO = "AES/GCM/NoPadding"
    }
}
