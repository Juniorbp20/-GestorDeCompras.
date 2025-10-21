package com.example.gestiondecompras.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestiondecompras.R;
import com.example.gestiondecompras.models.Pedido;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.PedidoViewHolder> {

    public interface OnPedidoClickListener {
        void onPedidoClick(Pedido pedido);
        void onPedidoLongClick(Pedido pedido);

        void onCreate(Bundle savedInstanceState);
    }

    private List<Pedido> pedidos;
    private final OnPedidoClickListener listener;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public PedidosAdapter(List<Pedido> pedidos, OnPedidoClickListener listener) {
        this.pedidos = pedidos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        holder.bind(pedido);
        holder.itemView.setOnClickListener(v -> { if (listener != null) listener.onPedidoClick(pedido); });
        holder.itemView.setOnLongClickListener(v -> { if (listener != null) listener.onPedidoLongClick(pedido); return true; });
    }

    @Override
    public int getItemCount() { return pedidos != null ? pedidos.size() : 0; }

    public void actualizarLista(List<Pedido> nuevosPedidos) {
        this.pedidos = nuevosPedidos;
        notifyDataSetChanged();
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvCliente, tvTienda, tvTotal, tvFechaEntrega, tvEstado, tvGanancia, tvMontoCompra;

        @SuppressLint("WrongViewCast")
        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvTienda = itemView.findViewById(R.id.tvTienda);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvFechaEntrega = itemView.findViewById(R.id.tvFechaEntrega);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvGanancia = itemView.findViewById(R.id.tvGanancia);
            tvMontoCompra = itemView.findViewById(R.id.tvMontoCompra);
        }

        void bind(Pedido p) {
            tvCliente.setText(p.getClienteNombre());
            tvTienda.setText(p.getTienda());
            tvTotal.setText(String.format(Locale.getDefault(), "RD$ %.2f", p.getTotalGeneral()));
            tvMontoCompra.setText(String.format(Locale.getDefault(), "Compra: RD$ %.2f", p.getMontoCompra()));
            tvGanancia.setText(String.format(Locale.getDefault(), "Ganancia: RD$ %.2f", p.getGanancia()));
            if (p.getFechaEntrega() != null) tvFechaEntrega.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(p.getFechaEntrega()));
            else tvFechaEntrega.setText("Sin fecha");

            tvEstado.setText(p.getEstado());
            aplicarEstilosPorEstado(p);
        }

        void aplicarEstilosPorEstado(Pedido p) {
            int colorFondo = Color.WHITE;
            int colorEstado = Color.GRAY;
            switch (p.getEstado()) {
                case Pedido.ESTADO_PENDIENTE:
                    if (p.estaAtrasado()) { colorEstado = Color.parseColor("#F44336"); colorFondo = Color.parseColor("#FFF5F5"); }
                    else { colorEstado = Color.parseColor("#FF9800"); colorFondo = Color.parseColor("#FFF3E0"); }
                    break;
                case Pedido.ESTADO_ENTREGADO:
                    colorEstado = Color.parseColor("#2196F3"); colorFondo = Color.parseColor("#E3F2FD");
                    break;
                case Pedido.ESTADO_PAGADO:
                    colorEstado = Color.parseColor("#4CAF50"); colorFondo = Color.parseColor("#E8F5E9");
                    break;
                case Pedido.ESTADO_CANCELADO:
                    colorEstado = Color.GRAY; colorFondo = Color.parseColor("#F5F5F5");
                    break;
            }
            cardView.setCardBackgroundColor(colorFondo);
            tvEstado.setTextColor(colorEstado);
            cardView.setCardElevation(p.estaAtrasado() ? 8f : 2f);
        }
    }
}