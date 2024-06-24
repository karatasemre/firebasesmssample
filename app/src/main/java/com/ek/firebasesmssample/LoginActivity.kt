package com.ek.firebasesmssample

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var phoneNumberInput: EditText
    private lateinit var loginButton: Button
    private lateinit var phoneNumberLayout: TextInputLayout

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        phoneNumberInput = findViewById(R.id.phone_number_input)
        loginButton = findViewById(R.id.login_button)
        phoneNumberLayout = findViewById(R.id.phone_number_layout)

        auth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        phoneNumberInput.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    phoneNumberInput.removeTextChangedListener(this)

                    val cleanString = s.toString().replace(" ", "").replace("+90", "")
                    val formatted = cleanString.chunked(3).joinToString(" ")

                    if (cleanString.length > 6) {
                        val part1 = cleanString.substring(0, 3)
                        val part2 = cleanString.substring(3, 6)
                        val part3 = cleanString.substring(6)
                        current = "+90 $part1 $part2$part3"
                    } else {
                        current = "+90 $formatted"
                    }

                    phoneNumberInput.setText(current)
                    phoneNumberInput.setSelection(current.length)

                    phoneNumberInput.addTextChangedListener(this)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        loginButton.setOnClickListener {
            val phoneNumber = phoneNumberInput.text.toString()
            if (isValidPhoneNumber(phoneNumber)) {
                startPhoneNumberVerification("+905492025695") // Telefon numarasını buraya girin
            } else {
                Toast.makeText(this, "Geçerli bir telefon numarası girin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val pattern = Pattern.compile("^\\+?[1-9]\\d{1,14}\$")
        return pattern.matcher(phoneNumber.replace(" ", "")).matches()
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Bu callback, doğrulama başarılı olduğunda tetiklenir.
                // Burada oturum açma işlemini gerçekleştirebilirsiniz.
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // Bu callback, doğrulama başarısız olduğunda tetiklenir.
                // Hata mesajını göster.
                Log.e("onVerificationFailed", e.message ?: "Hata oluştu")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.e("onCodeSent", "verificationId: $verificationId")
                // Bu callback, doğrulama kodu gönderildiğinde tetiklenir.
                // Kullanıcıdan alınan kod ile credential oluşturulabilir.
                val credential = PhoneAuthProvider.getCredential(verificationId, "123456")
                signInWithPhoneAuthCredential(credential)
            }
        }

        // Telefon numarası ve callbacks ile doğrulama başlatılır.
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Telefon numarası
            60, // Zaman aşımı süresi
            TimeUnit.SECONDS, // Zaman aşımı birimi
            this, // Activity (veya Fragment)
            callbacks
        ) // Callbacks
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val user = task.result?.user
                    val uid = user?.uid // Kullanıcı ID
                    val phoneNumber = user?.phoneNumber // Telefon numarası
                    val providerId = user?.providerId // Sağlayıcı ID

                    sendUserToBackend(uid)
                    // ...
                } else {
                    Log.e("signInWithCredential", "signInWithCredential:failure", task.exception)
                    // Sign in failed
                    // ...
                }
            }
    }

    private fun sendUserToBackend(uid: String?) {
        Log.e("sendUserToBackend", "UID: $uid")
        // Bu fonksiyon, kullanıcının uid değerini backend'e gönderir.
        // Bu işlem, genellikle bir HTTP isteği ile gerçekleştirilir.
        // Bu isteğin nasıl yapılacağı, backend'inizdeki API'ya bağlıdır.
    }
}