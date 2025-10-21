package com.example.gestordecompras.adapters;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestordecompras.R;
import com.example.gestordecompras.models.Cliente;
import java.util.List;

public class ClientesAdapter extends RecyclerView.Adapter<ClientesAdapter.ClienteViewHolder> {

    private List<Cliente> clientes;
    private OnClienteClickListener listener;

    public interface OnClienteClickListener {
        void onClienteClick(Cliente cliente);
        void onClienteLongClick(Cliente cliente);
    }

    public ClientesAdapter(List<Cliente> clientes, OnClienteClickListener listener) {
        this.clientes = clientes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cliente, parent, false);
        return new ClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente cliente = clientes.get(position);
        holder.bind(cliente);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClienteClick(cliente);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onClienteLongClick(cliente);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return clientes != null ? clientes.size() : 0;
    }

    public void actualizarLista(List<Cliente> nuevosClientes) {
        this.clientes = nuevosClientes;
        notifyDataSetChanged();
    }

    public static class ClienteViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNombre, tvTelefono, tvEmail;
        private TextView tvCantidadPedidos, tvTotalCompras;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvCantidadPedidos = itemView.findViewById(R.id.tvCantidadPedidos);
            tvTotalCompras = itemView.findViewById(R.id.tvTotalCompras);
        }

        public void bind(Cliente cliente) {
            // Información básica del cliente
            tvNombre.setText(cliente.getNombre());

            if (cliente.getTelefono() != null && !cliente.getTelefono().isEmpty()) {
                tvTelefono.setText(cliente.getTelefono());
                tvTelefono.setVisibility(View.VISIBLE);
            } else {
                tvTelefono.setVisibility(View.GONE);
            }

            if (cliente.getEmail() != null && !cliente.getEmail().isEmpty()) {
                tvEmail.setText(cliente.getEmail());
                tvEmail.setVisibility(View.VISIBLE);
            } else {
                tvEmail.setVisibility(View.GONE);
            }

            // Estadísticas (se pueden calcular después)
            // Por ahora placeholder
            tvCantidadPedidos.setText("0 pedidos");
            tvTotalCompras.setText("RD$ 0.00");
        }
    }

    // Método para filtrar clientes
    public void filtrar(String texto) {
        // Implementar filtrado si es necesario
    }
}