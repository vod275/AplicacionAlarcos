package adaptadorUltimasComidas

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionalarcos.R
import modelosNuevasComidas.Plato
import modelosNuevasComidas.Ingrediente

class ComidaAdapter(private val comidas: MutableList<Plato>) : RecyclerView.Adapter<ComidaAdapter.ComidaViewHolder>() {

    private val selectedItems = mutableSetOf<Int>() // Índices de elementos seleccionados

    inner class ComidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.comidaNombre)
        val ingredientesTextView: TextView = itemView.findViewById(R.id.comidaIngredientes)
        val linearLayout: ConstraintLayout = itemView.findViewById(R.id.linearLayout) // Asegúrate de que este ID exista en el XML
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComidaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.comida_item, parent, false)
        return ComidaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ComidaViewHolder, position: Int) {
        val comida = comidas[position]

        holder.nombreTextView.text = comida.nombre

        // Adaptar para usar el nuevo modelo
        val ingredientesTexto = comida.ingredientes.zip(comida.cantidad)
            .joinToString(", ") { (ingrediente, cantidad) -> "${ingrediente.nombre}: $cantidad" }
        holder.ingredientesTextView.text = "Ingredientes: $ingredientesTexto"

        val color = if (selectedItems.contains(position))
            ContextCompat.getColor(holder.itemView.context, R.color.swicth)
        else
            ContextCompat.getColor(holder.itemView.context, R.color.VerdeFont)

        holder.itemView.setBackgroundColor(color)
        holder.linearLayout.setBackgroundColor(color)

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
                .setTitle("Opciones para ${comida.nombre}")
                .setMessage("¿Qué acción quieres realizar con este elemento?")
                .setPositiveButton("Ver detalles") { _, _ ->
                    // Mostrar detalles
                    val detalles = comida.ingredientes.zip(comida.cantidad)
                        .joinToString("\n") { (ingrediente, cantidad) ->
                            "${ingrediente.nombre}: $cantidad"
                        }

                    val detallesDialog = AlertDialog.Builder(context)
                        .setTitle("Detalles de ${comida.nombre}")
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
                    if (position in comidas.indices) {
                        comidas.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, comidas.size)
                    }

                    // Actualizar índices en `selectedItems`
                    val updatedSelectedItems = mutableSetOf<Int>()
                    for (index in selectedItems) {
                        if (index < position) {
                            updatedSelectedItems.add(index) // Índices antes de la posición eliminada no cambian
                        } else if (index > position) {
                            updatedSelectedItems.add(index - 1) // Ajustar índices después de la posición eliminada
                        }
                    }

                    // Notificar al RecyclerView del cambio
                    selectedItems.clear() // Limpiar la selección tras eliminar
                    notifyDataSetChanged() // Actualizar la lista

                    Toast.makeText(
                        context,
                        "${comida.nombre} eliminado",
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
