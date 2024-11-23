package com.example.aplicacionalarcos

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacionalarcos.databinding.ActivityAjustesBinding
import java.util.Locale

class AjustesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAjustesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAjustesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuración del botón "Atrás"
        binding.obAtras.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        // Configuración del idioma
        binding.RBIngles.setOnClickListener {
            binding.RbSpanish.isChecked = false
            changeLanguage("en")
        }

        binding.RbSpanish.setOnClickListener {
            binding.RBIngles.isChecked = false
            changeLanguage("es")
        }

        // Configuración del Switch de tema
        setupSwitch()

        binding.SWTheme.setOnClickListener {
            if (binding.SWTheme.isChecked) {
                setDarkMode()
            } else {
                setLightMode()
            }
        }
    }

    private fun setupSwitch() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            binding.SWTheme.isChecked = true
            binding.SWTheme.text = getString(R.string.Oscuro) // Texto dinámico según el idioma
        } else {
            binding.SWTheme.isChecked = false
            binding.SWTheme.text = getString(R.string.Claro) // Texto dinámico según el idioma
        }
    }

    private fun changeLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Recargar la actividad para aplicar el cambio de idioma
        recreate()
    }

    private fun setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        binding.SWTheme.text = getString(R.string.Oscuro) // Actualizar texto después del cambio
    }

    private fun setLightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding.SWTheme.text = getString(R.string.Claro) // Actualizar texto después del cambio
    }
}
