package adaptadorNuevasComidas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionalarcos.R
import modelosNuevasComidas.Ingrediente

class CantidadesIngredientesAdapter(
    private val ingredientes: List<Ingrediente>,
    private val onCantidadCambiada: (Int, String) -> Unit // Callback para manejar el cambio de cantidad
) : RecyclerView.Adapter<CantidadesIngredientesAdapter.CantidadViewHolder>() {

    inner class CantidadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreIngrediente: TextView = itemView.findViewById(R.id.TVIngrediente)
        val etCantidad: EditText = itemView.findViewById(R.id.etCantidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CantidadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cantidades_ingredientes_item, parent, false)
        return CantidadViewHolder(view)
    }

    override fun onBindViewHolder(holder: CantidadViewHolder, position: Int) {
        val ingrediente = ingredientes[position]
        holder.tvNombreIngrediente.text = ingrediente.nombre

        // Manejar cambios en la cantidad ingresada
        holder.etCantidad.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // Solo cuando pierde el foco
                val cantidad = holder.etCantidad.text.toString()
                onCantidadCambiada(position, cantidad) // Llamar al callback con la posición y la cantidad ingresada
            }
        }
    }

    override fun getItemCount() = ingredientes.size
}
