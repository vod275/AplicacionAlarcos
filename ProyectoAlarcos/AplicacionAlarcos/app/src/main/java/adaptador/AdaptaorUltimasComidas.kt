import adaptador.Comida
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionalarcos.R


class ComidaAdapter(private val comidas: MutableList<Comida>) : RecyclerView.Adapter<ComidaAdapter.ComidaViewHolder>() {

    private val selectedItems = mutableSetOf<Int>() // Índices de elementos seleccionados

    inner class ComidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.comidaNombre)
        val ingredientesTextView: TextView = itemView.findViewById(R.id.comidaIngredientes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComidaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.comida_item, parent, false)
        return ComidaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ComidaViewHolder, position: Int) {
        val comida = comidas[position]

        holder.nombreTextView.text = comida.Nombre
        val ingredientesTexto = comida.ingredientes.joinToString(", ") { it.nombre }
        holder.ingredientesTextView.text = "Ingredientes: $ingredientesTexto"

        // Cambiar el fondo del item según si está seleccionado
        holder.itemView.setBackgroundColor(
            if (selectedItems.contains(position))
                holder.itemView.context.getColor(R.color.swicth) // Color para seleccionado
            else
                holder.itemView.context.getColor(R.color.VerdeFont) // Color para no seleccionado
        )

        // Evento de clic corto para seleccionar/desmarcar
        holder.itemView.setOnClickListener {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            notifyItemChanged(position) // Actualizar visualmente el item
        }

        // Evento de clic largo para mostrar diálogo con dos botones
        holder.itemView.setOnLongClickListener {
            val context = holder.itemView.context

            val dialog = AlertDialog.Builder(context)
                .setTitle("Opciones para ${comida.Nombre}")
                .setMessage("¿Qué acción quieres realizar con este elemento?")
                .setPositiveButton("Ver detalles") { _, _ ->
                    // Mostrar detalles
                    val detalles = comida.ingredientes.joinToString("\n") {
                        "${it.nombre}: ${it.cantidad}"
                    }

                    val detallesDialog = AlertDialog.Builder(context)
                        .setTitle("Detalles de ${comida.Nombre}")
                        .setMessage(detalles)
                        .setPositiveButton("Cerrar") { d, _ -> d.dismiss() }
                        .create() // Crear el diálogo para personalizar

                    // Personalización del fondo y color del botón
                    detallesDialog.setOnShowListener {
                        val positiveButton = detallesDialog.getButton(AlertDialog.BUTTON_POSITIVE)

                        // Cambiar el color de texto del botón
                        val buttonColor = ContextCompat.getColor(context, R.color.VerdeFont)
                        positiveButton.setTextColor(buttonColor)

                        // Cambiar el fondo del diálogo
                        val backgroundColor = ContextCompat.getColor(context, R.color.swicth)
                        detallesDialog.window?.setBackgroundDrawable(ColorDrawable(backgroundColor))
                    }

                    detallesDialog.show()
                }
                .setNegativeButton("Eliminar elemento") { _, _ ->
                    // Eliminar elemento
                    comidas.removeAt(position)
                    notifyItemRemoved(position)
                    Toast.makeText(
                        context,
                        "${comida.Nombre} eliminado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNeutralButton("Cancelar") { dialog, _ ->
                    // Cerrar diálogo sin hacer nada
                    dialog.dismiss()
                }
                .create() // Usar create() para personalizar después

            // Personalización al mostrar el diálogo
            dialog.setOnShowListener {
                // Obtener botones del diálogo
                val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                val neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)

                // Cambiar el color de texto de los botones
                val buttonColor = ContextCompat.getColor(context, R.color.VerdeFont) // Color definido
                positiveButton.setTextColor(buttonColor)
                negativeButton.setTextColor(buttonColor)
                neutralButton.setTextColor(buttonColor)

                // Cambiar el fondo del diálogo
                val backgroundColor = ContextCompat.getColor(context, R.color.swicth) // Color de fondo
                dialog.window?.setBackgroundDrawable(ColorDrawable(backgroundColor))
            }

            dialog.show()

            true // Indicar que el evento ha sido consumido
        }
    }


    override fun getItemCount(): Int {
        return comidas.size
    }

    // Método para eliminar los elementos seleccionados
    fun eliminarSeleccionados() {
        val iterator = comidas.iterator()
        for (i in comidas.size - 1 downTo 0) {
            if (selectedItems.contains(i)) {
                comidas.removeAt(i)
            }
        }
        selectedItems.clear() // Limpiar selección
        notifyDataSetChanged() // Actualizar la lista
    }
}


