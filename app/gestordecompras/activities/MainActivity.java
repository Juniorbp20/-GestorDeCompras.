package com.example.gestordecompras.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestordecompras.R;
import com.example.gestordecompras.adapters.PedidosAdapter;
import com.example.gestordecompras.database.DatabaseHelper;
import com.example.gestordecompras.models.Pedido;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvTotalPendiente, tvGananciaEsperada, tvPedidosHoy;
    private RecyclerView rvProximosPedidos;
    private PedidosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupButtons();
        cargarDatosDashboard();
    }

    private void initViews() {
        tvTotalPendiente = findViewById(R.id.tvTotalPendiente);
        tvGananciaEsperada = findViewById(R.id.tvGananciaEsperada);
        tvPedidosHoy = findViewById(R.id.tvPedidosHoy);
        rvProximosPedidos = findViewById(R.id.rvProximosPedidos);

        rvProximosPedidos.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupButtons() {
        Button btnNuevoPedido = findViewById(R.id.btnNuevoPedido);
        Button btnListaPedidos = findViewById(R.id.btnListaPedidos);
        Button btnCalendario = findViewById(R.id.btnCalendario);

        btnNuevoPedido.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NuevoPedidoActivity.class);
            startActivity(intent);
        });

        btnListaPedidos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListaPedidosActivity.class);
            startActivity(intent);
        });

        btnCalendario.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarioActivity.class);
            startActivity(intent);
        });
    }

    private void cargarDatosDashboard() {
        // Cargar resumen numérico
        double totalPendiente = dbHelper.getTotalPendiente();
        double gananciaEsperada = dbHelper.getGananciaEsperada();
        int pedidosHoy = dbHelper.getPedidosParaHoy();

        tvTotalPendiente.setText(String.format("RD$ %.2f", totalPendiente));
        tvGananciaEsperada.setText(String.format("RD$ %.2f", gananciaEsperada));
        tvPedidosHoy.setText(String.valueOf(pedidosHoy));

        // Cargar próximos pedidos
        List<Pedido> proximosPedidos = dbHelper.getProximosPedidos(5);
        adapter = new PedidosAdapter(proximosPedidos, this::onPedidoClick);
        rvProximosPedidos.setAdapter(adapter);
    }

    private void onPedidoClick(Pedido pedido) {
        // Abrir edición o detalles del pedido
        Intent intent = new Intent(this, NuevoPedidoActivity.class);
        intent.putExtra("pedido_id", pedido.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosDashboard(); // Refrescar datos al volver
    }
}