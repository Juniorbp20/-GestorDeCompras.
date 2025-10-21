package com.example.gestiondecompras.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestiondecompras.R;
import com.example.gestiondecompras.models.Tarjeta;

import java.util.List;
import java.util.Locale;

public class TarjetasAdapter extends RecyclerView.Adapter<TarjetasAdapter.TarjetaViewHolder> {

    public interface OnTarjetaClickListener {
        void onTarjetaClick(Tarjeta tarjeta);
        void onTarjetaLongClick(Tarjeta tarjeta);
    }

    private List<Tarjeta> tarjetas;
    private final OnTarjetaClickListener listener;

    public TarjetasAdapter(List<Tarjeta> tarjetas, OnTarjetaClickListener listener) {
        this.tarjetas = tarjetas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TarjetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarjeta, parent, false);
        return new TarjetaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TarjetaViewHolder holder, int position) {
        Tarjeta t = tarjetas.get(position);
        holder.bind(t);
        holder.itemView.setOnClickListener(v -> { if (listener != null) listener.onTarjetaClick(t); });
        holder.itemView.setOnLongClickListener(v -> { if (listener != null) listener.onTarjetaLongClick(t); return true; });
    }

    @Override
    public int getItemCount() { return tarjetas != null ? tarjetas.size() : 0; }

    public void actualizarLista(List<Tarjeta> nuevas) {
        this.tarjetas = nuevas;
        notifyDataSetChanged();
    }

    static class TarjetaViewHolder extends RecyclerView.ViewHolder {
        TextView tvBanco, tvAlias, tvLimite, tvDeuda, tvDisponible, tvCorte, tvVence;
        ProgressBar pbUso;
        public TarjetaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBanco = itemView.findViewById(R.id.tvBanco);
            tvAlias = itemView.findViewById(R.id.tvAlias);
            tvLimite = itemView.findViewById(R.id.tvLimite);
            tvDeuda = itemView.findViewById(R.id.tvDeuda);
            tvDisponible = itemView.findViewById(R.id.tvDisponible);
            tvCorte = itemView.findViewById(R.id.tvCorte);
            tvVence = itemView.findViewById(R.id.tvVence);
            pbUso = itemView.findViewById(R.id.pbUso);
        }
        void bind(Tarjeta t) {
            tvBanco.setText(t.getBanco());
            tvAlias.setText(t.getAlias());
            tvLimite.setText(String.format(Locale.getDefault(), "Límite: RD$ %,.2f", t.getLimiteCredito()));
            tvDeuda.setText(String.format(Locale.getDefault(), "Deuda: RD$ %,.2f", t.getDeudaActual()));
            double disponible = Math.max(0, t.getLimiteCredito() - t.getDeudaActual());
            tvDisponible.setText(String.format(Locale.getDefault(), "Disponible: RD$ %,.2f", disponible));
            tvCorte.setText("Corte: " + t.getDiaCorte());
            tvVence.setText("Vence: " + t.getDiaVencimiento());
            int progress = 0;
            if (t.getLimiteCredito() > 0) progress = (int) Math.min(100, Math.round((t.getDeudaActual() / t.getLimiteCredito()) * 100));
            pbUso.setProgress(progress);
        }
    }
}