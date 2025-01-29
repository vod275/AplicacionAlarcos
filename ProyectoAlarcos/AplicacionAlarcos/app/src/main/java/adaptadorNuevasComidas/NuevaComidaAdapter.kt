package adaptadorNuevasComidas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionalarcos.R
import com.example.aplicacionalarcos.databinding.ActivityNuevasComidasBinding
import com.google.firebase.firestore.FirebaseFirestore
import modelosNuevasComidas.Ingrediente

class NuevaComidaAdapter(
    private val binding: ActivityNuevasComidasBinding,
    private val ingredientes: MutableList<Ingrediente>, // Cambiado a MutableList para eliminar dinámicamente
    private val onItemClick: (Ingrediente) -> Unit
) : RecyclerView.Adapter<NuevaComidaAdapter.IngredienteViewHolder>() {

    // Lista para rastrear los elementos seleccionados
    private val selectedItems = mutableSetOf<Int>()

    inner class IngredienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvValorEnergetico: TextView = itemView.findViewById(R.id.tvValorEnergetico)
        val tvGrasas: TextView = itemView.findViewById(R.id.tvGrasas)
        val tvCarbohidratos: TextView = itemView.findViewById(R.id.tvCarbohidratos)
        val tvProteinas: TextView = itemView.findViewById(R.id.tvProteinas)
        val tvSal: TextView = itemView.findViewById(R.id.tvSal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredienteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.nueva_comida_item, parent, false)
        return IngredienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredienteViewHolder, position: Int) {
        val ingrediente = ingredientes[position]

        // Configurar los datos
        holder.tvNombre.text = ingrediente.nombre
        holder.tvValorEnergetico.text = "Valor energético: ${ingrediente.valorEnergeticoPor100g}"
        holder.tvGrasas.text = "Grasas: ${ingrediente.grasasPor100g}"
        holder.tvCarbohidratos.text = "Carbohidratos: ${ingrediente.carbohidratosPor100g}"
        holder.tvProteinas.text = "Proteínas: ${ingrediente.proteinasPor100g}"
        holder.tvSal.text = "Sal: ${ingrediente.salPor100g}"

        // Cambiar el fondo del LinearLayout según el estado de selección
        holder.linearLayout.setBackgroundColor(
            if (selectedItems.contains(position))
                ContextCompat.getColor(holder.itemView.context, R.color.swicth) // Color seleccionado
            else
                ContextCompat.getColor(holder.itemView.context, R.color.VerdeFont) // Color no seleccionado
        )

        // Evento de clic corto para seleccionar/desmarcar
        holder.itemView.setOnClickListener {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            notifyItemChanged(position) // Actualizar visualmente el ítem
        }

        // Evento adicional: Pasar el ingrediente al callback
        holder.itemView.setOnLongClickListener {
            onItemClick(ingrediente)
            true
        }
    }

    override fun getItemCount() = ingredientes.size

    // Método para obtener los ingredientes seleccionados
    fun getSeleccionados(): List<Ingrediente> {
        return selectedItems.map { ingredientes[it] }
    }

    // Eliminar ingredientes seleccionados de Firestore
    fun eliminarIngredientesSeleccionados() {
        val db = FirebaseFirestore.getInstance()
        val ingredientesAEliminar = getSeleccionados()

        for (ingrediente in ingredientesAEliminar) {
            db.collection("ingredientes")
                .document(ingrediente.id) // Usamos el ID único del documento
                .delete()
                .addOnSuccessListener {
                    println("Ingrediente ${ingrediente.nombre} eliminado correctamente")
                }
                .addOnFailureListener { e ->
                    println("Error al eliminar ${ingrediente.nombre}: ${e.message}")
                }
        }

        // Eliminar de la lista seleccionada y actualizar la vista
        ingredientes.removeAll(ingredientesAEliminar) // Eliminar los elementos de la lista
        selectedItems.clear() // Limpiar la selección
        notifyDataSetChanged() // Refrescar la interfaz
    }

    // Configurar la acción del botón eliminar
    fun setupDeleteButton() {
        binding.obBorrar.setOnClickListener {
            eliminarIngredientesSeleccionados()
        }
    }
}
