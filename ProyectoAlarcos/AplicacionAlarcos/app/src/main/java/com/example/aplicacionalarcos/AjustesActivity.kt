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
    private var currentLanguage: String = "es" // Idioma predeterminado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restaurar el idioma del Bundle si existe
        savedInstanceState?.getString("currentLanguage")?.let {
            currentLanguage = it
            applyLanguage(currentLanguage)
        }

        enableEdgeToEdge()
        binding = ActivityAjustesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de margenes del sistema
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
        setupLanguageSelection()

        // Configuración del Switch de tema
        setupThemeSwitch()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Guardar el idioma actual en el Bundle
        outState.putString("currentLanguage", currentLanguage)
    }

    private fun setupLanguageSelection() {
        // Selección de idioma español
        binding.RbSpanish.setOnClickListener {
            binding.RBIngles.isChecked = false
            changeLanguage("es")
        }

        // Selección de idioma inglés
        binding.RBIngles.setOnClickListener {
            binding.RbSpanish.isChecked = false
            changeLanguage("en")
        }
    }

    private fun setupThemeSwitch() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            binding.SWTheme.isChecked = true
            binding.SWTheme.text = getString(R.string.Oscuro)
        } else {
            binding.SWTheme.isChecked = false
            binding.SWTheme.text = getString(R.string.Claro)
        }

        // Cambiar el tema al activar el Switch
        binding.SWTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setDarkMode()
            } else {
                setLightMode()
            }
        }
    }

    private fun changeLanguage(languageCode: String) {
        currentLanguage = languageCode
        applyLanguage(languageCode)
        recreate()
    }

    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun setLightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
