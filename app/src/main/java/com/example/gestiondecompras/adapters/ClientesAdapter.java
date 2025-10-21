package com.example.gestiondecompras.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestiondecompras.R;
import com.example.gestiondecompras.models.Cliente;

import java.util.List;

public class ClientesAdapter extends RecyclerView.Adapter<ClientesAdapter.ClienteViewHolder> {

    public interface OnClienteClickListener {
        void onClienteClick(Cliente cliente);
        void onClienteLongClick(Cliente cliente);
    }

    private List<Cliente> clientes;
    private final OnClienteClickListener listener;

    public ClientesAdapter(List<Cliente> clientes, OnClienteClickListener listener) {
        this.clientes = clientes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
        return new ClienteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente c = clientes.get(position);
        holder.bind(c);
        holder.itemView.setOnClickListener(v -> { if (listener != null) listener.onClienteClick(c); });
        holder.itemView.setOnLongClickListener(v -> { if (listener != null) listener.onClienteLongClick(c); return true; });
    }

    @Override
    public int getItemCount() { return clientes != null ? clientes.size() : 0; }

    public void actualizarLista(List<Cliente> nuevos) {
        this.clientes = nuevos;
        notifyDataSetChanged();
    }

    static class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTelefono, tvEmail, tvCantidadPedidos, tvTotalCompras;
        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvCantidadPedidos = itemView.findViewById(R.id.tvCantidadPedidos);
            tvTotalCompras = itemView.findViewById(R.id.tvTotalCompras);
        }
        void bind(Cliente c) {
            tvNombre.setText(c.getNombre());
            if (c.getTelefono() != null && !c.getTelefono().isEmpty()) { tvTelefono.setText(c.getTelefono()); tvTelefono.setVisibility(View.VISIBLE);} else tvTelefono.setVisibility(View.GONE);
            if (c.getEmail() != null && !c.getEmail().isEmpty()) { tvEmail.setText(c.getEmail()); tvEmail.setVisibility(View.VISIBLE);} else tvEmail.setVisibility(View.GONE);
            // Placeholder de métricas; se pueden llenar desde la activity con consultas reales
            tvCantidadPedidos.setText("0 pedidos");
            tvTotalCompras.setText("RD$ 0.00");
        }
    }
}