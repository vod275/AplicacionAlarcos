package com.example.aplicacionalarcos

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacionalarcos.databinding.ActivityMenuBinding
import com.google.firebase.auth.FirebaseAuth

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var auth: FirebaseAuth // Declarar FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.IMCButton.setOnClickListener {
            val intent = Intent(this, ImcActivity::class.java)
            startActivity(intent)
        }

        binding.IMCButton.setOnClickListener {
            val intent = Intent(this, ImcActivity::class.java)
            startActivity(intent)
            // Agregar animación
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        //binding.InfoNutriButton.setOnClickListener {
        //  val intent = Intent(this, InfoNutriActivity::class.java)
        //    startActivity(intent)
            // Agregar animación
        //    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        //}

        //binding.UltimasComidasButton.setOnClickListener {
        //    val intent = Intent(this, UltimasComidasActivity::class.java)
        //    startActivity(intent)
            // Agregar animación
        //    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        //}

        binding.obAtras.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            auth.signOut()
            startActivity(intent)
            // Agregar animación de regreso
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }



    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Agregar animación de regreso
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}