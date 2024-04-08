package com.example.tdmpa_3p_ex_nhba_issc_512

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {
    private val splashTimeOut: Long = 3000 // Duración del splash screen en milisegundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        Handler().postDelayed({
            // Inicia tu actividad principal
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            // Cierra esta actividad
            finish()
        }, splashTimeOut)
    }
}
