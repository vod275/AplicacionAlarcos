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
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private val REQUEST_CODE_CAMERA = 1001

class DatosUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatosUsuarioBinding
    private lateinit var currentPhotoPath: String

    // Registrar el ActivityResultLauncher para la selección de imagen
    private val selectImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                handleImageSelection(it)
            }
        }

    // Registrar el ActivityResultLauncher para capturar una foto con la cámara
    private val takePictureResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val uri = Uri.fromFile(File(currentPhotoPath))
                handleImageSelection(uri)
            } else {
                Toast.makeText(this, "No se tomó ninguna foto.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Mostrar el email del usuario
        binding.tvCorreo.text = UserSession.email ?: getString(R.string.usuario_no_disponible)

        // El icono del calendario es un botón para mostrar la fecha
        binding.etFechaNacimiento.setStartIconOnClickListener {
            showDatePickerDialog()
        }

        // Botón atrás: cerrar sesión y volver al inicio
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

        binding.btnAceptar.setOnClickListener {
            val nombre =
                binding.tvNombreAjustes.editText?.text.toString().takeIf { it.isNotEmpty() }
                    ?: getString(R.string.nombre_no_disponible)
            val apellidos = binding.tvApellidos.editText?.text.toString().takeIf { it.isNotEmpty() }
                ?: getString(R.string.apellidos_no_disponibles)
            val fechaNacimiento =
                binding.etFechaNacimientoEditText.text.toString().takeIf { it.isNotEmpty() }
                    ?: getString(R.string.fecha_no_disponible)

            if (nombre.isNotEmpty() && apellidos.isNotEmpty() && fechaNacimiento.isNotEmpty()) {
                // Aquí puedes pasar los datos junto con la ruta de la imagen
                val intent = Intent(this, AjustesActivity::class.java)
                intent.putExtra("nombre", nombre)
                intent.putExtra("apellidos", apellidos)
                intent.putExtra("fechaNacimiento", fechaNacimiento)
                startActivity(intent)
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
            // Solicitar el permiso de la cámara si no está concedido
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
        } else {
            // Lanzar la cámara si el permiso ya está concedido
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
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun handleImageSelection(uri: Uri) {
        val imageSize = getFileSize(uri)
        if (imageSize <= 50 * 1024 * 1024) { // Limitar a 50 MB
            // Cargar la imagen en el ImageButton
            binding.ibFotoPerfil.setImageURI(uri)

            // Guardar la imagen en Firebase Storage
            uploadImageToFirebase(uri)
        } else {
            Toast.makeText(this, "La imagen excede el tamaño máximo permitido de 50MB", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.seleccionar_fecha))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTheme(R.style.ThemeOverlay_App_DatePicker) // Aplica el tema personalizado
            .build()

        datePicker.show(supportFragmentManager, "datePicker")
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = it?.let { date ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = date
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH) + 1
                val year = calendar.get(Calendar.YEAR)
                "$year-$month-$day"
            }
            binding.etFechaNacimiento.editText?.setText(selectedDate)
        }
    }

    private fun getFileSize(uri: Uri): Long {
        return try {
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileInputStream = FileInputStream(fileDescriptor?.fileDescriptor)
            val size = fileInputStream.available().toLong()
            fileInputStream.close()
            size
        } catch (e: IOException) {
            e.printStackTrace()
            0
        }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("FotosUsers/${System.currentTimeMillis()}.jpg")

        imagesRef.putFile(uri)
            .addOnSuccessListener {
                Toast.makeText(this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show()
                imagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Este método se llama cuando se solicita el permiso de la cámara y se otorga o deniega.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, lanzar la cámara
                launchCamera()
            } else {
                // Permiso denegado
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
