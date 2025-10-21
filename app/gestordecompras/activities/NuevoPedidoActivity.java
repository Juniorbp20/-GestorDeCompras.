package com.example.gestordecompras.activities;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestordecompras.R;
import com.example.gestordecompras.database.DatabaseHelper;
import com.example.gestordecompras.models.Cliente;
import com.example.gestordecompras.models.Pedido;
import com.example.gestordecompras.models.Tienda;
import com.example.gestordecompras.utils.CalculadoraGanancias;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NuevoPedidoActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private Spinner spinnerClientes, spinnerTiendas;
    private EditText etMontoCompra, etGanancia, etNotas;
    private TextView tvTotalGeneral, tvFechaEntrega;
    private Button btnGuardar, btnSeleccionarFecha;
    private RadioGroup rgTipoGanancia;
    private RadioButton rbMontoFijo, rbPorcentaje;

    private Calendar calendar;
    private SimpleDateFormat dateFormatter;
    private int pedidoId = -1; // -1 = nuevo pedido, >0 = edición

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        initViews();
        cargarSpinners();
        setupListeners();
        checkEdicion();
    }

    private void initViews() {
        spinnerClientes = findViewById(R.id.spinnerClientes);
        spinnerTiendas = findViewById(R.id.spinnerTiendas);
        etMontoCompra = findViewById(R.id.etMontoCompra);
        etGanancia = findViewById(R.id.etGanancia);
        etNotas = findViewById(R.id.etNotas);
        tvTotalGeneral = findViewById(R.id.tvTotalGeneral);
        tvFechaEntrega = findViewById(R.id.tvFechaEntrega);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);
        rgTipoGanancia = findViewById(R.id.rgTipoGanancia);
        rbMontoFijo = findViewById(R.id.rbMontoFijo);
        rbPorcentaje = findViewById(R.id.rbPorcentaje);

        // Configurar fecha por defecto (mañana)
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        tvFechaEntrega.setText(dateFormatter.format(calendar.getTime()));
    }

    private void cargarSpinners() {
        // Cargar clientes
        List<Cliente> clientes = dbHelper.getAllClientes();
        ArrayAdapter<Cliente> clienteAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, clientes);
        clienteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClientes.setAdapter(clienteAdapter);

        // Cargar tiendas predefinidas
        ArrayAdapter<String> tiendaAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, Tienda.getTiendasPredefinidas());
        tiendaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTiendas.setAdapter(tiendaAdapter);
    }

    private void setupListeners() {
        btnSeleccionarFecha.setOnClickListener(v -> mostrarDatePicker());

        rgTipoGanancia.setOnCheckedChangeListener((group, checkedId) -> calcularTotalGeneral());

        etMontoCompra.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calcularTotalGeneral();
            }
        });

        etGanancia.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calcularTotalGeneral();
            }
        });

        btnGuardar.setOnClickListener(v -> guardarPedido());
    }

    private void calcularTotalGeneral() {
        try {
            double montoCompra = TextUtils.isEmpty(etMontoCompra.getText()) ?
                    0 : Double.parseDouble(etMontoCompra.getText().toString());

            double ganancia = TextUtils.isEmpty(etGanancia.getText()) ?
                    0 : Double.parseDouble(etGanancia.getText().toString());

            if (rbPorcentaje.isChecked() && montoCompra > 0) {
                ganancia = CalculadoraGanancias.calcularGananciaDesdePorcentaje(montoCompra, ganancia);
            }

            double totalGeneral = CalculadoraGanancias.calcularTotal(montoCompra, ganancia);
            tvTotalGeneral.setText(String.format("RD$ %.2f", totalGeneral));

        } catch (NumberFormatException e) {
            tvTotalGeneral.setText("RD$ 0.00");
        }
    }

    private void mostrarDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    tvFechaEntrega.setText(dateFormatter.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    private void guardarPedido() {
        if (!validarFormulario()) {
            return;
        }

        try {
            Cliente clienteSeleccionado = (Cliente) spinnerClientes.getSelectedItem();
            String tienda = spinnerTiendas.getSelectedItem().toString();
            double montoCompra = Double.parseDouble(etMontoCompra.getText().toString());
            double ganancia = Double.parseDouble(etGanancia.getText().toString());
            String notas = etNotas.getText().toString();

            if (rbPorcentaje.isChecked()) {
                ganancia = CalculadoraGanancias.calcularGananciaDesdePorcentaje(montoCompra, ganancia);
            }

            Pedido pedido = new Pedido(
                    clienteSeleccionado.getId(),
                    clienteSeleccionado.getNombre(),
                    tienda,
                    montoCompra,
                    ganancia,
                    calendar.getTime()
            );
            pedido.setNotas(notas);

            if (pedidoId > 0) {
                pedido.setId(pedidoId);
                dbHelper.actualizarPedido(pedido);
                Toast.makeText(this, "Pedido actualizado", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.agregarPedido(pedido);
                Toast.makeText(this, "Pedido guardado", Toast.LENGTH_SHORT).show();
            }

            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar pedido", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarFormulario() {
        if (spinnerClientes.getSelectedItem() == null) {
            Toast.makeText(this, "Selecciona un cliente", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(etMontoCompra.getText())) {
            Toast.makeText(this, "Ingresa el monto de compra", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(etGanancia.getText())) {
            Toast.makeText(this, "Ingresa la ganancia", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void checkEdicion() {
        pedidoId = getIntent().getIntExtra("pedido_id", -1);
        if (pedidoId > 0) {
            cargarPedidoParaEdicion(pedidoId);
        }
    }

    private void cargarPedidoParaEdicion(int id) {
        Pedido pedido = dbHelper.getPedidoById(id);
        if (pedido != null) {
            // Llenar los campos con los datos del pedido
            // (Implementar según necesidad)
        }
    }

    // Clase interna simple para text watcher
    private abstract class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}