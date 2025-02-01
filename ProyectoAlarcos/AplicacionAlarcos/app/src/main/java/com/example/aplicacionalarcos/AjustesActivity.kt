package com.example.aplicacionalarcos

import android.content.Intent
import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.aplicacionalarcos.databinding.ActivityAjustesBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import objetos.UserSession
import java.util.Date
import java.util.Locale

class AjustesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAjustesBinding
    private var currentLanguage: String = "it" // Idioma por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAjustesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cargar el idioma guardado
        currentLanguage = loadLanguage()
        applyLanguage(currentLanguage)

        enableEdgeToEdge()

        // Cargar la foto del usuario en el ImageView
        loadUserProfileImage()

        val nombre = intent.getStringExtra("nombre") ?: getString(R.string.nombre_no_disponible)
        val apellidos = intent.getStringExtra("apellidos") ?: getString(R.string.nombre_no_disponible)
        val fechaNacimiento = intent.getStringExtra("fechaNacimiento") ?: getString(R.string.nombre_no_disponible)

        binding.tvNombreAjustes.text = nombre
        binding.tvApellidosAjustes.text = apellidos

        if (fechaNacimiento != getString(R.string.nombre_no_disponible)) {
            try {
                val fechaNacimientoDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(fechaNacimiento)
                val edad = fechaNacimientoDate?.let { calcularEdad(it) } ?: 0
                binding.tvEdad.text = getString(R.string.edad_con_años, edad)
            } catch (e: Exception) {
                binding.tvEdad.text = getString(R.string.edad_no_disponible)
            }
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
        outState.putString("currentLanguage", currentLanguage)
    }

    private fun setupLanguageSelection() {
        binding.RbSpanish.setOnClickListener { changeLanguage("es") }
        binding.RBIngles.setOnClickListener { changeLanguage("en") }
        binding.RBJapones.setOnClickListener { changeLanguage("ja") }
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
            saveLanguage(languageCode)
            applyLanguage(languageCode)
            recreate()
        }
    }

    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun setLightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun saveLanguage(languageCode: String) {
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("language", languageCode)
            apply()
        }
    }

    private fun loadLanguage(): String {
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        return sharedPreferences.getString("language", "it") ?: "it"
    }

    private fun calcularEdad(fechaNacimiento: Date): Int {
        val today = Calendar.getInstance()
        val birthDate = Calendar.getInstance()
        birthDate.time = fechaNacimiento

        var edad = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

        if (today.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) ||
            (today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
            edad--
        }

        return edad
    }

    /**
     * Cargar la foto del usuario en el `ImageView FotoPrefil`
     */
    private fun loadUserProfileImage() {
        val userIdentifier = UserSession.id ?: "usuario"
        val fileName = "FotosUsers/$userIdentifier.jpg"

        Log.d("FirebaseStorage", "Intentando cargar: $fileName") // Verifica el nombre del archivo

        val storageRef = FirebaseStorage.getInstance().reference.child(fileName)

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri.toString())
                .into(binding.FotoPrefil)
        }.addOnFailureListener { e ->
            Log.e("FirebaseStorage", "Error al cargar la imagen", e)

            // Si no se encuentra la imagen, mostrar una por defecto
            Glide.with(this)
                .load(R.drawable.defecto)
                .into(binding.FotoPrefil)

            Toast.makeText(this,
                getString(R.string.no_se_encontr_imagen_de_perfil), Toast.LENGTH_SHORT).show()
        }
    }

}
