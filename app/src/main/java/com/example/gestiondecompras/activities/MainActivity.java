package com.example.gestiondecompras.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.gestiondecompras.adapters.PedidosAdapter;
import com.example.gestiondecompras.database.DatabaseHelper;
import com.example.gestiondecompras.databinding.ActivityMainBinding;
import com.example.gestiondecompras.models.Pedido;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements PedidosAdapter.OnPedidoClickListener {

    private ActivityMainBinding binding;
    private DatabaseHelper db;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding del layout activity_main.xml
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHelper(this);

        // Recycler
        binding.rvProximosPedidos.setLayoutManager(new LinearLayoutManager(this));

        // Listeners
        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnNuevoPedido.setOnClickListener(v ->
                startActivity(new Intent(this, NuevoPedidoActivity.class)));

        binding.btnListaPedidos.setOnClickListener(v ->
                startActivity(new Intent(this, ListaPedidosActivity.class)));

        binding.btnCalendario.setOnClickListener(v ->
                startActivity(new Intent(this, CalendarioActivity.class)));

        // ⚠️ Evita crash si aún no tienes ClientesActivity:
        binding.btnClientes.setOnClickListener(v ->
                Toast.makeText(this, "Clientes: pantalla en construcción", Toast.LENGTH_SHORT).show());

        // Si ya tienes TarjetasActivity y botón en el layout (btnTarjetas), puedes usar:
        // binding.btnTarjetas.setOnClickListener(v ->
        //         startActivity(new Intent(this, TarjetasActivity.class)));
    }

    @SuppressLint("DefaultLocale")
    private void cargarDashboard() {
        executor.execute(() -> {
            // Hilo en background
            double totalPendiente   = db.getTotalPendiente();
            double gananciaEsperada = db.getGananciaEsperada();
            int pedidosHoy          = db.getPedidosParaHoy();
            List<Pedido> proximos   = db.getProximosPedidos(5);

            // Si tienes contador de clientes
            int clientesActivos = 0;
            try {
                clientesActivos = db.getAllClientes().size();
            } catch (Exception ignore) {}

            int finalClientesActivos = clientesActivos;

            handler.post(() -> {
                // UI thread
                binding.tvTotalPendiente.setText(String.format("RD$ %,.2f", totalPendiente));
                binding.tvGananciaEsperada.setText(String.format("RD$ %,.2f", gananciaEsperada));
                binding.tvPedidosHoy.setText(String.valueOf(pedidosHoy));

                // Solo si existe en tu layout:
                binding.tvClientesActivos.setText(String.valueOf(finalClientesActivos));

                PedidosAdapter adapter = new PedidosAdapter(proximos, MainActivity.this);
                binding.rvProximosPedidos.setAdapter(adapter);
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDashboard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    // Callbacks del adapter
    @Override
    public void onPedidoClick(Pedido pedido) {
        Intent intent = new Intent(this, NuevoPedidoActivity.class);
        intent.putExtra("pedido_id", pedido.getId());
        startActivity(intent);
    }

    @Override
    public void onPedidoLongClick(Pedido pedido) {
        onPedidoClick(pedido);
    }
}
