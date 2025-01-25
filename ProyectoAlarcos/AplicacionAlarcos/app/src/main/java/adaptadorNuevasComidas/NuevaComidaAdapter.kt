package adaptadorNuevasComidas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionalarcos.R
import modelosNuevasComidas.Ingrediente

class NuevaComidaAdapter(
    private val ingredientes: List<Ingrediente>,
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
        // Inflar el layout del item (item_ingrediente.xml)
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
}
