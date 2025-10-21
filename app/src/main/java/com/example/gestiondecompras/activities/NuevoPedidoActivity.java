package com.example.gestiondecompras.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestiondecompras.R;
import com.example.gestiondecompras.database.DatabaseHelper;
import com.example.gestiondecompras.models.Cliente;
import com.example.gestiondecompras.models.Pedido;
import com.example.gestiondecompras.models.Tarjeta;
import com.example.gestiondecompras.models.Tienda;
import com.example.gestiondecompras.utils.CalculadoraGanancias;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NuevoPedidoActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private Spinner spClientes;
    private Spinner spTiendas;
    private Spinner spTarjetas;
    private EditText etMonto;
    private EditText etGanancia;
    private EditText etNotas;
    private TextView tvTotal;
    private TextView tvFecha;
    private RadioButton rbPorcentaje;
    private Calendar cal;
    private SimpleDateFormat df;
    private int pedidoId = -1;
    private Pedido pedidoEnEdicion;
    private Integer tarjetaSeleccionadaId = null;
    private String tarjetaSeleccionadaAlias = null;
    private Integer clienteSeleccionadoId = null;
    private String tiendaSeleccionada = null;

    private final List<Cliente> clientesDisponibles = new ArrayList<>();
    private final List<String> tiendasDisponibles = new ArrayList<>();
    private final List<Tarjeta> tarjetasDisponibles = new ArrayList<>();
    private final List<String> tarjetasLabels = new ArrayList<>();

    private ArrayAdapter<String> tarjetasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        db = new DatabaseHelper(this);
        cal = Calendar.getInstance();
        df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        spClientes = findViewById(R.id.spinnerClientes);
        spTiendas = findViewById(R.id.spinnerTiendas);
        spTarjetas = findViewById(R.id.spinnerTarjetas);
        etMonto = findViewById(R.id.etMontoCompra);
        etGanancia = findViewById(R.id.etGanancia);
        etNotas = findViewById(R.id.etNotas);
        tvTotal = findViewById(R.id.tvTotalGeneral);
        tvFecha = findViewById(R.id.tvFechaEntrega);
        RadioButton rbFijo = findViewById(R.id.rbMontoFijo);
        rbPorcentaje = findViewById(R.id.rbPorcentaje);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnFecha = findViewById(R.id.btnSeleccionarFecha);
        MaterialButton btnAgregarTarjeta = findViewById(R.id.btnAgregarTarjeta);

        tvFecha.setText(df.format(cal.getTime()));

        pedidoId = getIntent().getIntExtra("pedido_id", -1);
        if (pedidoId > 0) {
            pedidoEnEdicion = db.getPedidoById(pedidoId);
            if (pedidoEnEdicion == null) {
                Toast.makeText(this, R.string.no_hay_pedidos_proximos, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            if (pedidoEnEdicion.getFechaEntrega() != null) {
                cal.setTime(pedidoEnEdicion.getFechaEntrega());
            }
            tarjetaSeleccionadaId = pedidoEnEdicion.getTarjetaId();
            tarjetaSeleccionadaAlias = pedidoEnEdicion.getTarjetaAlias();
        } else if (getIntent().hasExtra("cliente_id")) {
            int clienteExtra = getIntent().getIntExtra("cliente_id", -1);
            if (clienteExtra > 0) {
                clienteSeleccionadoId = clienteExtra;
            }
        }

        btnFecha.setOnClickListener(v -> mostrarDatePicker());
        rbFijo.setOnCheckedChangeListener((g, b) -> calcular());
        rbPorcentaje.setOnCheckedChangeListener((g, b) -> calcular());

        TextWatcher tw = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            public void onTextChanged(CharSequence s, int a, int b, int c) { calcular(); }
            public void afterTextChanged(Editable s) { }
        };
        etMonto.addTextChangedListener(tw);
        etGanancia.addTextChangedListener(tw);

        btnGuardar.setOnClickListener(v -> guardar());
        btnAgregarTarjeta.setOnClickListener(v ->
                startActivity(new Intent(this, NuevaTarjetaActivity.class)));

        cargarSpinners();
        if (pedidoEnEdicion != null) {
            poblarCampos(pedidoEnEdicion);
        } else {
            tvFecha.setText(df.format(cal.getTime()));
        }
        calcular();

        spClientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof Cliente) {
                    clienteSeleccionadoId = ((Cliente) item).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spTiendas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                if (item != null) {
                    tiendaSeleccionada = item.toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void cargarSpinners() {
        Integer clienteActualId = clienteSeleccionadoId;
        if (spClientes.getAdapter() != null && spClientes.getSelectedItem() instanceof Cliente) {
            clienteActualId = ((Cliente) spClientes.getSelectedItem()).getId();
        }
        String tiendaActual = tiendaSeleccionada;
        if (spTiendas.getAdapter() != null && spTiendas.getSelectedItem() != null) {
            tiendaActual = spTiendas.getSelectedItem().toString();
        }

        clientesDisponibles.clear();
        clientesDisponibles.addAll(db.getAllClientes());
        ArrayAdapter<Cliente> clientesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, clientesDisponibles);
        clientesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClientes.setAdapter(clientesAdapter);

        tiendasDisponibles.clear();
        tiendasDisponibles.addAll(Arrays.asList(Tienda.getTiendasPredefinidas()));
        ArrayAdapter<String> tiendasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiendasDisponibles);
        tiendasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTiendas.setAdapter(tiendasAdapter);

        tarjetasDisponibles.clear();
        tarjetasDisponibles.addAll(db.getTarjetas());
        tarjetasLabels.clear();
        tarjetasLabels.add(getString(R.string.tarjeta_sin_asignar));
        for (Tarjeta tarjeta : tarjetasDisponibles) {
            tarjetasLabels.add(formatearTarjeta(tarjeta));
        }
        tarjetasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tarjetasLabels);
        tarjetasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTarjetas.setAdapter(tarjetasAdapter);

        Integer tarjetaTargetId = tarjetaSeleccionadaId;
        String tarjetaTargetAlias = tarjetaSeleccionadaAlias;
        if (pedidoEnEdicion != null && tarjetaTargetId == null && tarjetaTargetAlias == null) {
            tarjetaTargetId = pedidoEnEdicion.getTarjetaId();
            tarjetaTargetAlias = pedidoEnEdicion.getTarjetaAlias();
        }

        if (clienteActualId == null && pedidoEnEdicion != null) {
            clienteActualId = pedidoEnEdicion.getClienteId();
        }
        if (tiendaActual == null && pedidoEnEdicion != null) {
            tiendaActual = pedidoEnEdicion.getTienda();
        }
        clienteSeleccionadoId = clienteActualId;
        tiendaSeleccionada = tiendaActual;
        seleccionarCliente(clienteSeleccionadoId != null ? clienteSeleccionadoId : -1);
        seleccionarTienda(tiendaSeleccionada);
        seleccionarTarjeta(tarjetaTargetId, tarjetaTargetAlias);
    }

    private void mostrarDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    cal.set(year, month, dayOfMonth);
                    tvFecha.setText(df.format(cal.getTime()));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void calcular() {
        try {
            double monto = TextUtils.isEmpty(etMonto.getText()) ? 0 : Double.parseDouble(etMonto.getText().toString());
            double ganancia = TextUtils.isEmpty(etGanancia.getText()) ? 0 : Double.parseDouble(etGanancia.getText().toString());
            if (rbPorcentaje.isChecked() && monto > 0) {
                ganancia = CalculadoraGanancias.calcularGananciaDesdePorcentaje(monto, ganancia);
            }
            double total = CalculadoraGanancias.calcularTotal(monto, ganancia);
            tvTotal.setText(String.format(Locale.getDefault(), "RD$ %,.2f", total));
        } catch (Exception e) {
            tvTotal.setText("RD$ 0.00");
        }
    }

    private boolean validar() {
        if (clientesDisponibles.isEmpty()) {
            Toast.makeText(this, R.string.error_sin_clientes, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spClientes.getSelectedItem() == null) {
            Toast.makeText(this, "Selecciona un cliente", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etMonto.getText())) {
            Toast.makeText(this, "Ingresa el monto de compra", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etGanancia.getText())) {
            Toast.makeText(this, "Ingresa la ganancia", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void guardar() {
        if (!validar()) return;
        try {
            Cliente cliente = (Cliente) spClientes.getSelectedItem();
            Object tiendaSeleccion = spTiendas.getSelectedItem();
            if (cliente == null || tiendaSeleccion == null && tiendaSeleccionada == null) {
                Toast.makeText(this, R.string.error_guardar_pedido, Toast.LENGTH_SHORT).show();
                return;
            }
            String tienda = tiendaSeleccion != null ? tiendaSeleccion.toString() : tiendaSeleccionada;

            double monto = Double.parseDouble(etMonto.getText().toString());
            double ganancia = Double.parseDouble(etGanancia.getText().toString());
            if (rbPorcentaje.isChecked()) {
                ganancia = CalculadoraGanancias.calcularGananciaDesdePorcentaje(monto, ganancia);
            }

            Integer tarjetaAnteriorId = pedidoEnEdicion != null ? pedidoEnEdicion.getTarjetaId() : null;
            double montoAnterior = pedidoEnEdicion != null ? pedidoEnEdicion.getMontoCompra() : 0d;

            Tarjeta tarjetaSeleccionada = obtenerTarjetaSeleccionada();
            if (tarjetaSeleccionada != null) {
                tarjetaSeleccionadaId = tarjetaSeleccionada.getId();
                tarjetaSeleccionadaAlias = formatearTarjeta(tarjetaSeleccionada);

                double disponible = tarjetaSeleccionada.getLimiteCredito() - tarjetaSeleccionada.getDeudaActual();
                if (tarjetaAnteriorId != null && tarjetaAnteriorId.equals(tarjetaSeleccionada.getId())) {
                    disponible += montoAnterior;
                }
                if (monto > disponible + 0.01) {
                    Toast.makeText(this, R.string.error_tarjeta_saldo_insuficiente, Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                tarjetaSeleccionadaId = null;
                tarjetaSeleccionadaAlias = obtenerEtiquetaTarjetaSeleccionada();
            }

            Pedido pedido = new Pedido(cliente.getId(), cliente.getNombre(), tienda, monto, ganancia, cal.getTime());
            pedido.setNotas(etNotas.getText().toString());

            if (tarjetaSeleccionada != null) {
                pedido.setTarjetaId(tarjetaSeleccionada.getId());
                pedido.setTarjetaAlias(formatearTarjeta(tarjetaSeleccionada));
            } else {
                pedido.setTarjetaId(null);
                pedido.setTarjetaAlias(tarjetaSeleccionadaAlias);
            }

            if (pedidoEnEdicion != null) {
                pedido.setId(pedidoId);
                pedido.setEstado(pedidoEnEdicion.getEstado());
                db.actualizarPedido(pedido);
                Toast.makeText(this, R.string.pedido_actualizado, Toast.LENGTH_SHORT).show();
            } else {
                long nuevoId = db.agregarPedido(pedido);
                pedido.setId((int) nuevoId);
                Toast.makeText(this, R.string.pedido_guardado, Toast.LENGTH_SHORT).show();
            }

            if (tarjetaAnteriorId != null) {
                db.ajustarDeudaTarjeta(tarjetaAnteriorId, -montoAnterior);
            }
            if (tarjetaSeleccionada != null) {
                db.ajustarDeudaTarjeta(tarjetaSeleccionada.getId(), monto);
            }

            finish();
        } catch (Exception ex) {
            Toast.makeText(this, R.string.error_guardar_pedido, Toast.LENGTH_SHORT).show();
        }
    }

    private void poblarCampos(Pedido pedido) {
        etMonto.setText(String.valueOf(pedido.getMontoCompra()));
        etGanancia.setText(String.valueOf(pedido.getGanancia()));
        tvTotal.setText(String.format(Locale.getDefault(), "RD$ %,.2f", pedido.getTotalGeneral()));
        if (pedido.getFechaEntrega() != null) {
            cal.setTime(pedido.getFechaEntrega());
            tvFecha.setText(df.format(pedido.getFechaEntrega()));
        } else {
            cal.setTime(Calendar.getInstance().getTime());
            tvFecha.setText(df.format(cal.getTime()));
        }
        etNotas.setText(pedido.getNotas());
        seleccionarCliente(pedido.getClienteId());
        seleccionarTienda(pedido.getTienda());
        seleccionarTarjeta(pedido.getTarjetaId(), pedido.getTarjetaAlias());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tarjeta tarjeta = obtenerTarjetaSeleccionada();
        if (tarjeta != null) {
            tarjetaSeleccionadaId = tarjeta.getId();
            tarjetaSeleccionadaAlias = formatearTarjeta(tarjeta);
        } else {
            tarjetaSeleccionadaId = null;
            tarjetaSeleccionadaAlias = obtenerEtiquetaTarjetaSeleccionada();
        }
        cargarSpinners();
    }

    private void seleccionarCliente(int clienteId) {
        for (int i = 0; i < clientesDisponibles.size(); i++) {
            if (clientesDisponibles.get(i).getId() == clienteId) {
                spClientes.setSelection(i);
                clienteSeleccionadoId = clienteId;
                return;
            }
        }
    }

    private void seleccionarTienda(String tienda) {
        if (tienda == null) return;
        for (int i = 0; i < tiendasDisponibles.size(); i++) {
            if (tienda.equalsIgnoreCase(tiendasDisponibles.get(i))) {
                spTiendas.setSelection(i);
                tiendaSeleccionada = tiendasDisponibles.get(i);
                return;
            }
        }
    }

    private void seleccionarTarjeta(Integer tarjetaId, String tarjetaAlias) {
        int seleccion = 0;
        if (tarjetaId != null) {
            for (int i = 0; i < tarjetasDisponibles.size(); i++) {
                if (tarjetasDisponibles.get(i).getId() == tarjetaId) {
                    seleccion = i + 1;
                    break;
                }
            }
            if (seleccion == 0 && tarjetaAlias != null && !tarjetaAlias.isEmpty()) {
                tarjetasLabels.add(tarjetaAlias);
                tarjetasAdapter.notifyDataSetChanged();
                seleccion = tarjetasLabels.size() - 1;
            }
        } else if (tarjetaAlias != null && !tarjetaAlias.isEmpty()) {
            tarjetasLabels.add(tarjetaAlias);
            tarjetasAdapter.notifyDataSetChanged();
            seleccion = tarjetasLabels.size() - 1;
        }
        spTarjetas.setSelection(seleccion);
    }

    private Tarjeta obtenerTarjetaSeleccionada() {
        int posicion = spTarjetas.getSelectedItemPosition();
        if (posicion <= 0) {
            return null;
        }
        int indexLista = posicion - 1;
        if (indexLista >= 0 && indexLista < tarjetasDisponibles.size()) {
            return tarjetasDisponibles.get(indexLista);
        }
        return null;
    }

    private String obtenerEtiquetaTarjetaSeleccionada() {
        int posicion = spTarjetas.getSelectedItemPosition();
        if (posicion <= 0 || posicion >= tarjetasLabels.size()) {
            return null;
        }
        return tarjetasLabels.get(posicion);
    }

    private String formatearTarjeta(Tarjeta tarjeta) {
        String alias = tarjeta.getAlias();
        if (alias == null || alias.trim().isEmpty()) {
            return tarjeta.getBanco();
        }
        return tarjeta.getBanco() + " - " + alias;
    }
}
