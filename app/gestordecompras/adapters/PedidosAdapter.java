package com.example.gestordecompras.adapters;



import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestordecompras.R;
import com.example.gestordecompras.models.Pedido;
import com.example.gestordecompras.utils.DateUtils;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.PedidoViewHolder> {

    private List<Pedido> pedidos;
    private OnPedidoClickListener listener;
    private SimpleDateFormat dateFormatter;

    public interface OnPedidoClickListener {
        void onPedidoClick(Pedido pedido);
        void onPedidoLongClick(Pedido pedido);
    }

    public PedidosAdapter(List<Pedido> pedidos, OnPedidoClickListener listener) {
        this.pedidos = pedidos;
        this.listener = listener;
        this.dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        holder.bind(pedido);

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPedidoClick(pedido);
            }
        });

        // Long click listener
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onPedidoLongClick(pedido);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return pedidos != null ? pedidos.size() : 0;
    }

    public void actualizarLista(List<Pedido> nuevosPedidos) {
        this.pedidos = nuevosPedidos;
        notifyDataSetChanged();
    }

    public class PedidoViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView tvCliente, tvTienda, tvTotal, tvFechaEntrega;
        private TextView tvEstado, tvGanancia, tvMontoCompra;

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

        public void bind(Pedido pedido) {
            // Datos básicos
            tvCliente.setText(pedido.getClienteNombre());
            tvTienda.setText(pedido.getTienda());
            tvTotal.setText(String.format("RD$ %.2f", pedido.getTotalGeneral()));
            tvMontoCompra.setText(String.format("Compra: RD$ %.2f", pedido.getMontoCompra()));
            tvGanancia.setText(String.format("Ganancia: RD$ %.2f", pedido.getGanancia()));

            // Fecha de entrega
            if (pedido.getFechaEntrega() != null) {
                String fechaStr = dateFormatter.format(pedido.getFechaEntrega());
                tvFechaEntrega.setText(fechaStr);
            } else {
                tvFechaEntrega.setText("Sin fecha");
            }

            // Estado con colores
            tvEstado.setText(pedido.getEstado());
            aplicarEstilosPorEstado(pedido);
        }

        private void aplicarEstilosPorEstado(Pedido pedido) {
            int colorFondo = Color.WHITE;
            int colorTexto = Color.BLACK;
            int colorEstado = Color.GRAY;

            switch (pedido.getEstado()) {
                case Pedido.ESTADO_PENDIENTE:
                    if (pedido.estaAtrasado()) {
                        // Pedido atrasado - ROJO
                        colorEstado = Color.RED;
                        colorFondo = Color.parseColor("#FFF5F5");
                    } else {
                        // Pedido pendiente normal - NARANJA
                        colorEstado = Color.parseColor("#FF9800");
                        colorFondo = Color.parseColor("#FFF3E0");
                    }
                    break;

                case Pedido.ESTADO_ENTREGADO:
                    // Entregado pero no pagado - AZUL
                    colorEstado = Color.parseColor("#2196F3");
                    colorFondo = Color.parseColor("#E3F2FD");
                    break;

                case Pedido.ESTADO_PAGADO:
                    // Pagado completo - VERDE
                    colorEstado = Color.parseColor("#4CAF50");
                    colorFondo = Color.parseColor("#E8F5E8");
                    break;

                case Pedido.ESTADO_CANCELADO:
                    // Cancelado - GRIS
                    colorEstado = Color.GRAY;
                    colorFondo = Color.parseColor("#F5F5F5");
                    break;
            }

            cardView.setCardBackgroundColor(colorFondo);
            tvEstado.setTextColor(colorEstado);

            // Opcional: agregar borde según estado
            cardView.setCardElevation(pedido.estaAtrasado() ? 8f : 2f);
        }
    }
}