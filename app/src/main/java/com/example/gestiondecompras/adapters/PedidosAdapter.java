package com.example.gestiondecompras.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.google.android.material.card.MaterialCardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestiondecompras.R;
import com.example.gestiondecompras.models.Pedido;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.PedidoViewHolder> {

    public interface OnPedidoClickListener {
        void onPedidoClick(Pedido pedido);
        void onPedidoLongClick(Pedido pedido);
    }

    private List<Pedido> pedidos;
    private final OnPedidoClickListener listener;

    public PedidosAdapter(List<Pedido> pedidos, OnPedidoClickListener listener) {
        this.pedidos = pedidos;
        this.listener = listener;
        setHasStableIds(true); // <- IDs estables evitan Ã¢â‚¬Å“saltosÃ¢â‚¬Â
    }

    @Override public long getItemId(int position) {
        // AsegÃƒÂºrate que getId() sea ÃƒÂºnico y > 0; si puede ser 0, usa -position
        return pedidos.get(position).getId();
    }

    @NonNull @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        holder.bind(pedidos.get(position));
    }

    @Override public int getItemCount() { return pedidos == null ? 0 : pedidos.size(); }

    public void actualizarLista(List<Pedido> nuevos) {
        this.pedidos = nuevos;
        notifyDataSetChanged();
    }

    public Pedido getPedidoAt(int position) {
        if (pedidos == null || position < 0 || position >= pedidos.size()) return null;
        return pedidos.get(position);
    }

    class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvCliente, tvTienda, tvTotal, tvFechaEntrega, tvGanancia, tvMontoCompra;
        Chip chipEstado;
        MaterialCardView card;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardView);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvTienda = itemView.findViewById(R.id.tvTienda);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvFechaEntrega = itemView.findViewById(R.id.tvFechaEntrega);
            tvGanancia = itemView.findViewById(R.id.tvGanancia);
            tvMontoCompra = itemView.findViewById(R.id.tvMontoCompra);
            chipEstado = itemView.findViewById(R.id.tvEstado);

            // Click SIEMPRE toma el item por posiciÃƒÂ³n actual:
            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPedidoClick(pedidos.get(pos));
                }
            });
            itemView.setOnLongClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPedidoLongClick(pedidos.get(pos));
                }
                return true;
            });

            // Click en estado (cambio rÃƒÂ¡pido de estado)
            chipEstado.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPedidoLongClick(pedidos.get(pos));
                }
            });
        }

        void bind(Pedido p) {
            Context context = itemView.getContext();
            tvCliente.setText(p.getClienteNombre());
            tvTienda.setText(p.getTienda());
            tvTotal.setText(String.format(Locale.getDefault(), "RD$ %,.2f", p.getTotalGeneral()));
            tvMontoCompra.setText(String.format(Locale.getDefault(), context.getString(R.string.item_pedido_compra_format), p.getMontoCompra()));
            tvGanancia.setText(String.format(Locale.getDefault(), context.getString(R.string.item_pedido_ganancia_format), p.getGanancia()));
            String fechaEntrega = p.getFechaEntrega() == null
                    ? context.getString(R.string.item_pedido_sin_fecha)
                    : dateFormat.format(p.getFechaEntrega());
            tvFechaEntrega.setText(context.getString(R.string.item_pedido_entrega_format, fechaEntrega));

            chipEstado.setText(p.getEstado());
            chipEstado.setChipBackgroundColor(ColorStateList.valueOf(
                    ContextCompat.getColor(context, obtenerColorEstado(p.getEstado()))
            ));
            chipEstado.setTextColor(ContextCompat.getColor(context, R.color.white));
            card.setStrokeColor(ContextCompat.getColor(context, obtenerColorEstado(p.getEstado())));
        }

        private int obtenerColorEstado(String estado) {
            if (Pedido.ESTADO_PAGADO.equalsIgnoreCase(estado)) {
                return R.color.success_color;
            }
            if (Pedido.ESTADO_ENTREGADO.equalsIgnoreCase(estado)) {
                return R.color.dashboard_card_orders;
            }
            if (Pedido.ESTADO_CANCELADO.equalsIgnoreCase(estado)) {
                return R.color.error_color;
            }
            return R.color.dashboard_card_secondary;
        }
    }
}

