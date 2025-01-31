package adaptadorNuevasComidas

import android.text.Editable
import android.text.TextWatcher
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

        // Remover TextWatcher previo si existe para evitar bucles infinitos
        holder.etCantidad.tag?.let {
            (it as? TextWatcher)?.let { watcher -> holder.etCantidad.removeTextChangedListener(watcher) }
        }

        // Crear un nuevo TextWatcher para capturar cambios en tiempo real
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val currentPosition = holder.adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onCantidadCambiada(currentPosition, s.toString()) // Usamos la posición actualizada
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        // Asignar el TextWatcher y almacenar la referencia en el tag para evitar conflictos
        holder.etCantidad.addTextChangedListener(textWatcher)
        holder.etCantidad.tag = textWatcher
    }

    override fun getItemCount() = ingredientes.size
}
