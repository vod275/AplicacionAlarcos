package com.example.aplicacionalarcos

import android.content.Intent
import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacionalarcos.databinding.ActivityAjustesBinding
import java.util.Date
import java.util.Locale

class AjustesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAjustesBinding
    private var currentLanguage: String = "it" // Idioma por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargar el idioma guardado
        currentLanguage = loadLanguage()
        applyLanguage(currentLanguage)

        enableEdgeToEdge()
        binding = ActivityAjustesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombre = intent.getStringExtra("nombre") ?: getString(R.string.nombre_no_disponible)
        val apellidos = intent.getStringExtra("apellidos") ?: getString(R.string.nombre_no_disponible)
        val fechaNacimiento = intent.getStringExtra("fechaNacimiento") ?: getString(R.string.nombre_no_disponible)

        binding.tvNombreAjustes.text = nombre
        binding.tvApellidosAjustes.text = apellidos

        if (fechaNacimiento != getString(R.string.nombre_no_disponible)) {
            val fechaNacimientoDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(fechaNacimiento)
            val edad = calcularEdad(fechaNacimientoDate)
            binding.tvEdad.text = getString(R.string.edad_con_años, edad)
        } else {
            binding.tvEdad.text = getString(R.string.edad_no_disponible)
        }


        // Configuración de márgenes del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.obAtras.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        binding.obCambiarDatosDelPerfil.setOnClickListener {
            val intent = Intent(this, DatosUsuarioActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }

        // Configuración del idioma
        setupLanguageSelection()

        // Configurar el estado del RadioButton según el idioma actual
        when (currentLanguage) {
            "es" -> binding.RbSpanish.isChecked = true
            "en" -> binding.RBIngles.isChecked = true
            "ja" -> binding.RBJapones.isChecked = true
        }

        // Configuración del Switch de tema
        setupThemeSwitch()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Guardar el idioma actual en el Bundle
        outState.putString("currentLanguage", currentLanguage)
    }

    private fun setupLanguageSelection() {
        binding.RbSpanish.setOnClickListener {
            changeLanguage("es")
        }
        binding.RBIngles.setOnClickListener {
            changeLanguage("en")
        }
        binding.RBJapones.setOnClickListener {
            changeLanguage("ja")
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
        if (currentLanguage != languageCode) {
            currentLanguage = languageCode
            saveLanguage(languageCode) // Guardar el idioma seleccionado
            applyLanguage(languageCode)
            recreate() // Reinicia la actividad para aplicar el idioma
        }
    }

    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        createConfigurationContext(config)  // Esto asegura que el idioma se aplique correctamente
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun setLightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    // Guardar el idioma seleccionado en SharedPreferences
    private fun saveLanguage(languageCode: String) {
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("language", languageCode)
            apply()
        }
    }

    // Cargar el idioma guardado desde SharedPreferences
    private fun loadLanguage(): String {
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        return sharedPreferences.getString("language", "it") ?: "it" // Por defecto "it"
    }

    // Función para calcular la edad
    private fun calcularEdad(fechaNacimiento: Date): Int {
        val today = Calendar.getInstance()
        val birthDate = Calendar.getInstance()
        birthDate.time = fechaNacimiento

        var edad = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

        // Si no ha pasado el cumpleaños de este año, restamos 1
        if (today.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) ||
            (today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
            edad--
        }

        return edad
    }
}
