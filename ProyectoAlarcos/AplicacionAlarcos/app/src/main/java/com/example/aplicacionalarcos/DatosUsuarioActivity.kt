package com.example.aplicacionalarcos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.aplicacionalarcos.databinding.ActivityDatosUsuarioBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.storage.FirebaseStorage
import objetos.UserSession
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_CODE_CAMERA = 1001

class DatosUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatosUsuarioBinding
    private lateinit var currentPhotoPath: String

    // Selector de imagen desde galería
    private val selectImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { handleImageSelection(it) }
        }

    // Toma de foto con la cámara
    private val takePictureResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val file = File(currentPhotoPath)
                val uri = Uri.fromFile(file)
                handleImageSelection(uri)
            } else {
                Toast.makeText(this, "No se tomó ninguna foto.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar el email del usuario o su nombre
        binding.tvCorreo.text = UserSession.nombre ?: UserSession.email

        // Icono del calendario para seleccionar la fecha
        binding.etFechaNacimiento.setStartIconOnClickListener {
            showDatePickerDialog()
        }

        // Botón para ir atrás
        binding.obAtras.setOnClickListener {
            val intent = Intent(this, AjustesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        // Botón para seleccionar o tomar una foto
        binding.ibFotoPerfil.setOnClickListener {
            showImageOptions()
        }

        // Botón de aceptar datos
        binding.btnAceptar.setOnClickListener {
            val nombre = binding.tvNombreAjustes.editText?.text.toString().takeIf { it.isNotEmpty() }
                ?: getString(R.string.nombre_no_disponible)
            val apellidos = binding.tvApellidos.editText?.text.toString().takeIf { it.isNotEmpty() }
                ?: getString(R.string.apellidos_no_disponibles)
            val fechaNacimiento =
                binding.etFechaNacimientoEditText.text.toString().takeIf { it.isNotEmpty() }
                    ?: getString(R.string.fecha_no_disponible)

            if (nombre.isNotEmpty() && apellidos.isNotEmpty() && fechaNacimiento.isNotEmpty()) {
                val intent = Intent(this, AjustesActivity::class.java)
                intent.putExtra("nombre", nombre)
                intent.putExtra("apellidos", apellidos)
                intent.putExtra("fechaNacimiento", fechaNacimiento)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.error_campos_vacios), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showImageOptions() {
        val options = arrayOf("Seleccionar desde galería", "Tomar una foto")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Elige una opción")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> selectImageResultLauncher.launch("image/*")
                1 -> checkCameraPermissionAndLaunch()
            }
        }
        builder.show()
    }

    private fun checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
        } else {
            launchCamera()
        }
    }

    private fun launchCamera() {
        val photoFile = createImageFile()
        val photoURI = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            photoFile
        )
        takePictureResultLauncher.launch(photoURI)
    }

    private fun createImageFile(): File {
        val storageDir: File? = getExternalFilesDir(null)

        // 🔹 Usar email o nombre como nombre de archivo único y fijo
        val userIdentifier = UserSession.email ?: UserSession.nombre ?: "usuario"
        val fileName = "Perfil_${userIdentifier}.jpg"

        val photoFile = File(storageDir, fileName)

        // 🔹 Asignar la ruta del archivo para su uso posterior
        currentPhotoPath = photoFile.absolutePath

        return photoFile
    }

    private fun handleImageSelection(uri: Uri) {
        binding.ibFotoPerfil.setImageURI(uri) // Mostrar la imagen en el botón
        uploadImageToFirebase(uri)
    }

    private fun showDatePickerDialog() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.seleccionar_fecha))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .build()

        datePicker.show(supportFragmentManager, "datePicker")
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = it?.let { date ->
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                format.format(Date(date))
            }
            binding.etFechaNacimiento.editText?.setText(selectedDate)
        }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val userIdentifier = UserSession.email ?: UserSession.nombre ?: "usuario"

        // 🔹 Guardar la imagen con un nombre FIJO para sobreescribirla siempre
        val imagesRef = storageRef.child("FotosUsers/$userIdentifier.jpg")

        imagesRef.putFile(uri)
            .addOnSuccessListener {
                Toast.makeText(this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
