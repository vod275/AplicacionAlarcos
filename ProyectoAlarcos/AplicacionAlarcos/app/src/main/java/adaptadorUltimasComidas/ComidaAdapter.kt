package adaptadorUltimasComidas

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionalarcos.R
import com.google.firebase.firestore.FirebaseFirestore
import modelosNuevasComidas.Plato

class ComidaAdapter(private val comidas: MutableList<Pair<String, Plato>>) : RecyclerView.Adapter<ComidaAdapter.ComidaViewHolder>() {

    private val selectedItems = mutableSetOf<Int>()

    inner class ComidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.comidaNombre)
        val ingredientesTextView: TextView = itemView.findViewById(R.id.comidaIngredientes)
        val linearLayout: ConstraintLayout = itemView.findViewById(R.id.linearLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComidaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.comida_item, parent, false)
        return ComidaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ComidaViewHolder, position: Int) {
        val (nombreDocumento, comida) = comidas[position]

        holder.nombreTextView.text = comida.nombre

        val ingredientesTexto = comida.ingredientes.zip(comida.cantidad)
            .joinToString(", ") { (ingrediente, cantidad) -> "${ingrediente.nombre}: $cantidad" }
        holder.ingredientesTextView.text = "Ingredientes: $ingredientesTexto"

        val color = if (selectedItems.contains(position))
            ContextCompat.getColor(holder.itemView.context, R.color.swicth)
        else
            ContextCompat.getColor(holder.itemView.context, R.color.VerdeFont)

        holder.itemView.setBackgroundColor(color)
        holder.linearLayout.setBackgroundColor(color)

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            notifyItemChanged(position)
        }

        holder.itemView.setOnLongClickListener {
            val context = holder.itemView.context

            val dialog = AlertDialog.Builder(context)
                .setTitle("Opciones para ${comida.nombre}")
                .setMessage("¿Qué acción quieres realizar con este elemento?")
                .setNegativeButton("Eliminar") { _, _ ->
                    eliminarPlatoPorNombreDocumento(nombreDocumento, position, context)
                }
                .setNeutralButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .create()

            dialog.show()
            true
        }
    }

    override fun getItemCount(): Int = comidas.size

    private fun eliminarPlatoPorNombreDocumento(nombreDocumento: String, position: Int, context: Context) {
        val db = FirebaseFirestore.getInstance()

        db.collection("platos").document(nombreDocumento)
            .delete()
            .addOnSuccessListener {
                if (position in comidas.indices) {
                    comidas.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, comidas.size)
                }
                Toast.makeText(context, "Plato eliminado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al eliminar: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun eliminarSeleccionados() {
        val db = FirebaseFirestore.getInstance()
        val indicesParaEliminar = selectedItems.sortedDescending()

        for (index in indicesParaEliminar) {
            if (index in comidas.indices) {
                val nombreDocumento = comidas[index].first

                db.collection("platos").document(nombreDocumento)
                    .delete()
                    .addOnSuccessListener {
                        println("Plato eliminado correctamente: $nombreDocumento")
                    }
                    .addOnFailureListener { exception ->
                        println("Error al eliminar: ${exception.message}")
                    }

                comidas.removeAt(index)
            }
        }

        selectedItems.clear()
        notifyDataSetChanged()
    }
}
