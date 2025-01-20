package com.example.aplicacionalarcos

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacionalarcos.databinding.ActivityNuevasComidasBinding

class NuevasComidas : AppCompatActivity() {
    private lateinit var binding: ActivityNuevasComidasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Vincular la vista con ViewBinding
        binding = ActivityNuevasComidasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajustar insets para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }




}