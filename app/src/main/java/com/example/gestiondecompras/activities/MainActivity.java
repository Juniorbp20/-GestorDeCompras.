package com.example.gestiondecompras.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.gestiondecompras.R;
import com.example.gestiondecompras.adapters.PedidosAdapter;
import com.example.gestiondecompras.database.DatabaseHelper;
import com.example.gestiondecompras.databinding.ActivityMainBinding;
import com.example.gestiondecompras.models.Pedido;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements PedidosAdapter.OnPedidoClickListener {

    private static final String PREFS_DASHBOARD = "dashboard_prefs";
    private static final String PREF_KEY_LAST_PENDING = "last_pending_total";
    private static final String PREFS_SETTINGS = "settings_prefs";
    private static final String PREF_KEY_FORCE_DARK = "force_dark_mode";

    private ActivityMainBinding binding;
    private DatabaseHelper db;
    private SharedPreferences dashboardPrefs;
    private SharedPreferences settingsPrefs;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private PedidosAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsPrefs = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);
        boolean forceDark = settingsPrefs.getBoolean(PREF_KEY_FORCE_DARK, false);
        AppCompatDelegate.setDefaultNightMode(forceDark
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.topAppBar);
        binding.topAppBar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        db = new DatabaseHelper(this);
        dashboardPrefs = getSharedPreferences(PREFS_DASHBOARD, MODE_PRIVATE);

        binding.rvProximosPedidos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PedidosAdapter(new java.util.ArrayList<>(), this);
        binding.rvProximosPedidos.setAdapter(adapter);

        setupClickListeners();
        updateGreeting();
    }

    private void setupClickListeners() {
        binding.chipNuevoPedido.setOnClickListener(v ->
                startActivity(new Intent(this, NuevoPedidoActivity.class)));

        binding.chipListaPedidos.setOnClickListener(v ->
                startActivity(new Intent(this, ListaPedidosActivity.class)));

        binding.chipCalendario.setOnClickListener(v ->
                startActivity(new Intent(this, CalendarioActivity.class)));

        binding.chipClientes.setOnClickListener(v ->
                startActivity(new Intent(this, ListaClientesActivity.class)));

        binding.chipReportes.setOnClickListener(v ->
                startActivity(new Intent(this, ReportesActivity.class)));

        binding.chipTarjetas.setOnClickListener(v ->
                startActivity(new Intent(this, TarjetasActivity.class)));
    }

    private void updateGreeting() {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int greetingRes;
        if (hour < 12) {
            greetingRes = R.string.dashboard_greeting_morning;
        } else if (hour < 18) {
            greetingRes = R.string.dashboard_greeting_afternoon;
        } else {
            greetingRes = R.string.dashboard_greeting_evening;
        }
        binding.tvGreeting.setText(getString(R.string.dashboard_greeting_format, getString(greetingRes)));
    }

    @SuppressLint("DefaultLocale")
    private void cargarDashboard() {
        executor.execute(() -> {
            double totalPendiente = db.getSaldoPendienteGlobal();
            double gananciaEsperada = db.getGananciaEsperada();
            int pedidosHoy = db.getPedidosParaHoy();
            List<Pedido> proximos = db.getProximosPedidos(5);

            int clientesActivos = 0;
            try {
                clientesActivos = db.getAllClientes().size();
            } catch (Exception ignore) {
            }

            int pedidosAtrasados = 0;
            try {
                pedidosAtrasados = db.getPedidosFiltrados("Atrasados", null).size();
            } catch (Exception ignore) {
            }

            int finalClientesActivos = clientesActivos;
            int finalPedidosAtrasados = pedidosAtrasados;

            handler.post(() -> {
                binding.tvTotalPendiente.setText(String.format("RD$ %,.2f", totalPendiente));
                binding.tvGananciaEsperada.setText(String.format("RD$ %,.2f", gananciaEsperada));
                binding.tvPedidosHoy.setText(String.valueOf(pedidosHoy));
                binding.tvClientesActivos.setText(String.valueOf(finalClientesActivos));
                binding.tvSecondaryMetricValue.setText(String.valueOf(finalPedidosAtrasados));
                binding.tvSecondaryMetricLabel.setText(R.string.dashboard_pending_collections);

                updateTrendChip(totalPendiente);
                persistPendingSnapshot(totalPendiente);

                adapter.actualizarLista(proximos);

                if (proximos == null || proximos.isEmpty()) {
                    binding.emptyState.setVisibility(View.VISIBLE);
                    binding.rvProximosPedidos.setVisibility(View.GONE);
                } else {
                    binding.emptyState.setVisibility(View.GONE);
                    binding.rvProximosPedidos.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void updateTrendChip(double totalPendiente) {
        double lastSnapshot = getLastPendingSnapshot();
        ColorStateList iconTint = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white));
        binding.chipTrend.setChipIconTint(iconTint);

        if (Double.isNaN(lastSnapshot)) {
            binding.chipTrend.setText(getString(R.string.dashboard_trend_placeholder));
            binding.chipTrend.setChipBackgroundColor(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.dashboard_accent_positive)));
            binding.chipTrend.setChipIconResource(R.drawable.ic_trending_up);
            return;
        }

        double variation = totalPendiente - lastSnapshot;
        if (Math.abs(variation) < 0.01) {
            binding.chipTrend.setText(getString(R.string.dashboard_trend_neutral));
            binding.chipTrend.setChipBackgroundColor(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.dashboard_card_secondary)));
            binding.chipTrend.setChipIconResource(R.drawable.ic_trending_up);
            return;
        }

        if (variation > 0) {
            binding.chipTrend.setText(getString(R.string.dashboard_trend_up, variation));
            binding.chipTrend.setChipBackgroundColor(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.dashboard_accent_positive)));
            binding.chipTrend.setChipIconResource(R.drawable.ic_trending_up);
        } else {
            binding.chipTrend.setText(getString(R.string.dashboard_trend_down, Math.abs(variation)));
            binding.chipTrend.setChipBackgroundColor(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.dashboard_accent_negative)));
            binding.chipTrend.setChipIconResource(R.drawable.ic_trending_down);
        }
    }

    private double getLastPendingSnapshot() {
        if (dashboardPrefs == null) {
            return Double.NaN;
        }
        long raw = dashboardPrefs.getLong(PREF_KEY_LAST_PENDING,
                Double.doubleToRawLongBits(Double.NaN));
        return Double.longBitsToDouble(raw);
    }

    private void persistPendingSnapshot(double totalPendiente) {
        if (dashboardPrefs != null) {
            dashboardPrefs.edit()
                    .putLong(PREF_KEY_LAST_PENDING, Double.doubleToRawLongBits(totalPendiente))
                    .apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGreeting();
        cargarDashboard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    @Override
    public void onPedidoClick(Pedido pedido) {
        Intent intent = new Intent(this, NuevoPedidoActivity.class);
        intent.putExtra("pedido_id", pedido.getId());
        startActivity(intent);
    }

    @Override
    public void onPedidoLongClick(Pedido pedido) {
        mostrarDialogoCambioEstado(pedido);
    }

    private void mostrarDialogoCambioEstado(Pedido pedido) {
        java.util.ArrayList<String> opciones = new java.util.ArrayList<>();

        switch (pedido.getEstado()) {
            case Pedido.ESTADO_PENDIENTE:
                opciones.add("Marcar como ENTREGADO");
                opciones.add("Marcar como CANCELADO");
                break;
            case Pedido.ESTADO_ENTREGADO:
                opciones.add("Marcar como PAGADO");
                opciones.add("Volver a PENDIENTE");
                break;
            case Pedido.ESTADO_PAGADO:
                opciones.add("Volver a ENTREGADO");
                break;
            case Pedido.ESTADO_CANCELADO:
                opciones.add("Reactivar (PENDIENTE)");
                break;
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cambiar estado")
                .setItems(opciones.toArray(new String[0]), (dialog, which) -> {
                    String sel = opciones.get(which);
                    String nuevo = pedido.getEstado();

                    if (sel.contains("ENTREGADO")) nuevo = Pedido.ESTADO_ENTREGADO;
                    else if (sel.contains("PAGADO")) nuevo = Pedido.ESTADO_PAGADO;
                    else if (sel.contains("CANCELADO")) nuevo = Pedido.ESTADO_CANCELADO;
                    else if (sel.contains("PENDIENTE")) nuevo = Pedido.ESTADO_PENDIENTE;

                    int rows = db.actualizarEstadoPedido(pedido.getId(), nuevo);
                    if (rows > 0) {
                        pedido.setEstado(nuevo);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "Estado: " + nuevo, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_toggle_theme);
        boolean forceDark = settingsPrefs.getBoolean(PREF_KEY_FORCE_DARK, false);
        item.setChecked(forceDark);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_theme) {
            boolean enable = !item.isChecked();
            item.setChecked(enable);
            settingsPrefs.edit().putBoolean(PREF_KEY_FORCE_DARK, enable).apply();
            AppCompatDelegate.setDefaultNightMode(enable
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
