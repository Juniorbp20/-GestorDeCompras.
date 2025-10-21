package com.example.gestiondecompras.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestiondecompras.R;
import com.example.gestiondecompras.models.Pedido;
import com.example.gestiondecompras.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarioActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private ListView list;
    private TextView tvFecha, tvCantidad;
    private final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        db = new DatabaseHelper(this);
        CalendarView calendar = findViewById(R.id.calendarView);
        list = findViewById(R.id.lvPedidosDelDia);
        tvFecha = findViewById(R.id.tvFechaSeleccionada);
        tvCantidad = findViewById(R.id.tvCantidadPedidos);

        tvFecha.setText("Hoy: " + df.format(new Date()));
        cargarParaFecha(new Date());

        calendar.setOnDateChangeListener((v, y, m, d) -> {
            Calendar c = Calendar.getInstance();
            c.set(y, m, d, 0, 0, 0);
            Date sel = c.getTime();
            tvFecha.setText(df.format(sel));
            cargarParaFecha(sel);
        });
    }

    @SuppressLint("SetTextI18n")
    private void cargarParaFecha(Date fecha) {
        List<Pedido> data = db.getPedidosPorFecha(fecha);
        tvCantidad.setText("(" + data.size() + " pedidos)");
        list.setAdapter(new SimpleAdapterList(data));
    }

    class SimpleAdapterList extends BaseAdapter {
        List<Pedido> data;
        SimpleAdapterList(List<Pedido> d) { data = d; }
        public int getCount() { return data.size(); }
        public Object getItem(int p) { return data.get(p); }
        public long getItemId(int p) { return p; }
        public android.view.View getView(int pos, android.view.View convertView, android.view.ViewGroup parent) {
            @SuppressLint("ViewHolder") android.view.View v = getLayoutInflater().inflate(R.layout.item_pedido, parent, false);
            Pedido p = data.get(pos);
            ((TextView) v.findViewById(R.id.tvCliente)).setText(p.getClienteNombre());
            ((TextView) v.findViewById(R.id.tvTienda)).setText(p.getTienda());
            ((TextView) v.findViewById(R.id.tvTotal)).setText(String.format(Locale.getDefault(), "RD$ %,.2f", p.getTotalGeneral()));
            ((TextView) v.findViewById(R.id.tvFechaEntrega)).setText(p.getFechaEntrega() == null ? "" : new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(p.getFechaEntrega()));
            ((TextView) v.findViewById(R.id.tvEstado)).setText(p.getEstado());
            v.setOnClickListener(v1 -> {
                Intent i = new Intent(CalendarioActivity.this, NuevoPedidoActivity.class);
                i.putExtra("pedido_id", p.getId());
                startActivity(i);
            });
            return v;
        }
    }
}
