package com.example.gestordecompras.activities;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gestordecompras.R;
import com.example.gestordecompras.adapters.PedidosAdapter;
import com.example.gestordecompras.database.DatabaseHelper;
import com.example.gestordecompras.models.Pedido;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarioActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private CalendarView calendarView;
    private ListView lvPedidosDelDia;
    private TextView tvFechaSeleccionada;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        dbHelper = new DatabaseHelper(this);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        initViews();
        setupCalendar();
    }

    private void initViews() {
        calendarView = findViewById(R.id.calendarView);
        lvPedidosDelDia = findViewById(R.id.lvPedidosDelDia);
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada);

        // Fecha inicial (hoy)
        tvFechaSeleccionada.setText("Hoy: " + dateFormatter.format(new Date()));
        cargarPedidosParaFecha(new Date().getTime());
    }

    private void setupCalendar() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Nota: month empieza en 0 (Enero = 0)
            String fechaStr = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
            tvFechaSeleccionada.setText(fechaStr);

            // Convertir a timestamp
            Date selectedDate = new Date(year - 1900, month, dayOfMonth);
            cargarPedidosParaFecha(selectedDate.getTime());
        });
    }

    private void cargarPedidosParaFecha(long fechaTimestamp) {
        List<Pedido> pedidos = dbHelper.getPedidosPorFecha(new Date(fechaTimestamp));

        PedidosAdapter adapter = new PedidosAdapter(pedidos, this::onPedidoClick);
        lvPedidosDelDia.setAdapter(adapter);

        // Actualizar contador
        TextView tvCantidad = findViewById(R.id.tvCantidadPedidos);
        tvCantidad.setText(String.format("(%d pedidos)", pedidos.size()));
    }

    private void onPedidoClick(Pedido pedido) {
        Intent intent = new Intent(this, NuevoPedidoActivity.class);
        intent.putExtra("pedido_id", pedido.getId());
        startActivity(intent);
    }
}