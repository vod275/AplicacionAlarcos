package com.example.aplicacionalarcos

import android.content.Intent
import android.net.Uri
import java.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicacionalarcos.databinding.ActivityDatosUsuarioBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.storage.FirebaseStorage
import objetos.UserSession
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class DatosUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatosUsuarioBinding

    // Registrar el ActivityResultLauncher para la selección de imagen
    private val selectImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                handleImageSelection(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar el email del usuario
        binding.tvCorreo.text = UserSession.nombre ?: getString(R.string.usuario_no_disponible)

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

        binding.btnAceptar.setOnClickListener {
            val nombre = binding.tvNombreAjustes.editText?.text.toString().takeIf { it.isNotEmpty() } ?: getString(R.string.nombre_no_disponible)
            val apellidos = binding.tvApellidos.editText?.text.toString().takeIf { it.isNotEmpty() } ?: getString(R.string.apellidos_no_disponibles)
            val fechaNacimiento = binding.etFechaNacimientoEditText.text.toString().takeIf { it.isNotEmpty() } ?: getString(R.string.fecha_no_disponible)

            if (nombre.isNotEmpty() && apellidos.isNotEmpty() && fechaNacimiento.isNotEmpty()) {
                // Aquí puedes pasar los datos junto con la ruta de la imagen
                val intent = Intent(this, AjustesActivity::class.java)
                intent.putExtra("nombre", nombre)
                intent.putExtra("apellidos", apellidos)
                intent.putExtra("fechaNacimiento", fechaNacimiento)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.error_campos_vacios), Toast.LENGTH_SHORT).show()
            }
        }

        binding.ibFotoPerfil.setOnClickListener {
            selectImageResultLauncher.launch("image/*")
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

    // Función para la fecha
    private fun showDatePickerDialog() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.seleccionar_fecha))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTheme(R.style.ThemeOverlay_App_DatePicker) // Aplica el tema personalizado
            .build()

        // Mostrar el selector de fecha
        datePicker.show(supportFragmentManager, "datePicker")

        // Obtener la fecha y ponerla en el EditText
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = it?.let { date ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = date
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH) + 1
                val year = calendar.get(Calendar.YEAR)
                // Formato estándar para la fecha
                "$year-$month-$day"
            }

            // Actualizar el TextInputEditText con la fecha seleccionada
            binding.etFechaNacimiento.editText?.setText(selectedDate)
        }
    }

    private fun getFileSize(uri: Uri): Long {
        var size: Long = 0
        try {
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileInputStream = FileInputStream(fileDescriptor?.fileDescriptor)
            size = fileInputStream.available().toLong()
            fileInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return size
    }

    private fun saveImageToInternalStorage(uri: Uri) {
        try {
            // Obtener el InputStream del archivo seleccionado
            val inputStream = contentResolver.openInputStream(uri)
            // Crear un archivo en el almacenamiento interno para guardar la imagen
            val file = File(filesDir, "profile_image.jpg")
            val outputStream = FileOutputStream(file)

            // Copiar la imagen desde el InputStream al OutputStream
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            // Ahora puedes almacenar la ruta del archivo para usarlo más tarde
            val imagePath = file.absolutePath
            // Usar SharedPreferences o base de datos para guardar la ruta si es necesario
            Toast.makeText(this, "Imagen guardada correctamente.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar la imagen.", Toast.LENGTH_SHORT).show()
        }
    }

    
    private fun uploadImageToFirebase(uri: Uri) {
        // Obtener una referencia a Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference

        // Crear una referencia para la carpeta 'FotosUsers' y asignar un nombre único para la imagen
        val imagesRef = storageRef.child("FotosUsers/${System.currentTimeMillis()}.jpg")

        // Subir la imagen
        imagesRef.putFile(uri)
            .addOnSuccessListener {
                // Subida exitosa
                Toast.makeText(this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show()

                // Obtener la URL de la imagen subida
                imagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Esta es la URL de la imagen subida en Firebase Storage
                    val imageUrl = downloadUri.toString()
                    // Puedes guardar la URL donde la necesites, por ejemplo en la base de datos o en SharedPreferences
                }
            }
            .addOnFailureListener { e ->
                // Error al subir la imagen
                Toast.makeText(this, "Error al subir la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
