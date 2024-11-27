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
import modelosUltimasComidas.Comida

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

        // Evento de clic largo para mostrar diálogo con opciones
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
                        .create()

                    // Personalización del fondo y color del botón
                    detallesDialog.setOnShowListener {
                        val positiveButton = detallesDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        val buttonColor = ContextCompat.getColor(context, R.color.VerdeFont)
                        positiveButton.setTextColor(buttonColor)
                        val backgroundColor = ContextCompat.getColor(context, R.color.swicth)
                        detallesDialog.window?.setBackgroundDrawable(ColorDrawable(backgroundColor))
                    }

                    detallesDialog.show()
                }
                .setNegativeButton("Eliminar elemento") { _, _ ->
                    // Eliminar elemento
                    comidas.removeAt(position)

                    // Actualizar índices en `selectedItems`
                    val updatedSelectedItems = mutableSetOf<Int>()
                    for (index in selectedItems) {
                        if (index < position) {
                            updatedSelectedItems.add(index) // Índices antes de la posición eliminada no cambian
                        } else if (index > position) {
                            updatedSelectedItems.add(index - 1) // Ajustar índices después de la posición eliminada
                        }
                    }
                    selectedItems.clear()
                    selectedItems.addAll(updatedSelectedItems)

                    // Notificar al RecyclerView del cambio
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, comidas.size)

                    Toast.makeText(
                        context,
                        "${comida.Nombre} eliminado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNeutralButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .create()

            // Personalización al mostrar el diálogo
            dialog.setOnShowListener {
                val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                val neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                val buttonColor = ContextCompat.getColor(context, R.color.VerdeFont)
                positiveButton.setTextColor(buttonColor)
                negativeButton.setTextColor(buttonColor)
                neutralButton.setTextColor(buttonColor)
                val backgroundColor = ContextCompat.getColor(context, R.color.swicth)
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
        val indicesParaEliminar = selectedItems.sortedDescending() // Ordenar de mayor a menor
        for (i in indicesParaEliminar) {
            comidas.removeAt(i)
        }
        selectedItems.clear() // Limpiar selección tras eliminar
        notifyDataSetChanged() // Actualizar la lista
    }
}
