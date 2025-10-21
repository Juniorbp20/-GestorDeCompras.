package com.example.gestiondecompras.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.example.gestiondecompras.models.Tienda;
import com.example.gestiondecompras.utils.CalculadoraGanancias;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NuevoPedidoActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private Spinner spClientes, spTiendas;
    private EditText etMonto, etGanancia, etNotas;
    private TextView tvTotal, tvFecha;
    private RadioButton rbPorcentaje;
    private Calendar cal;
    private SimpleDateFormat df;
    private int pedidoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

        db = new DatabaseHelper(this);
        cal = Calendar.getInstance();
        df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        spClientes = findViewById(R.id.spinnerClientes);
        spTiendas = findViewById(R.id.spinnerTiendas);
        etMonto = findViewById(R.id.etMontoCompra);
        etGanancia = findViewById(R.id.etGanancia);
        etNotas = findViewById(R.id.etNotas);
        tvTotal = findViewById(R.id.tvTotalGeneral);
        tvFecha = findViewById(R.id.tvFechaEntrega);
        RadioButton rbFijo = findViewById(R.id.rbMontoFijo);
        rbPorcentaje = findViewById(R.id.rbPorcentaje);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnFecha = findViewById(R.id.btnSeleccionarFecha);

        cargarSpinners();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        tvFecha.setText(df.format(cal.getTime()));

        btnFecha.setOnClickListener(v -> mostrarDatePicker());
        rbFijo.setOnCheckedChangeListener((g, b) -> calcular());
        rbPorcentaje.setOnCheckedChangeListener((g, b) -> calcular());

        TextWatcher tw = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) { calcular(); }
            public void afterTextChanged(Editable s) {}
        };
        etMonto.addTextChangedListener(tw);
        etGanancia.addTextChangedListener(tw);

        btnGuardar.setOnClickListener(v -> guardar());

        pedidoId = getIntent().getIntExtra("pedido_id", -1);
        if (pedidoId > 0) cargarPedido(pedidoId);
    }

    private void cargarSpinners() {
        List<Cliente> clientes = db.getAllClientes();
        ArrayAdapter<Cliente> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, clientes);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClientes.setAdapter(a);

        ArrayAdapter<String> t = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Tienda.getTiendasPredefinidas());
        t.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTiendas.setAdapter(t);
    }

    private void mostrarDatePicker() {
        DatePickerDialog d = new DatePickerDialog(this, (view, y, m, day) -> {
            cal.set(y, m, day);
            tvFecha.setText(df.format(cal.getTime()));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        d.show();
    }

    @SuppressLint("SetTextI18n")
    private void calcular() {
        try {
            double monto = TextUtils.isEmpty(etMonto.getText()) ? 0 : Double.parseDouble(etMonto.getText().toString());
            double g = TextUtils.isEmpty(etGanancia.getText()) ? 0 : Double.parseDouble(etGanancia.getText().toString());
            if (rbPorcentaje.isChecked() && monto > 0) g = CalculadoraGanancias.calcularGananciaDesdePorcentaje(monto, g);
            double total = CalculadoraGanancias.calcularTotal(monto, g);
            tvTotal.setText(String.format(Locale.getDefault(), "RD$ %,.2f", total));
        } catch (Exception e) {
            tvTotal.setText("RD$ 0.00");
        }
    }

    private boolean validar() {
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
            Cliente c = (Cliente) spClientes.getSelectedItem();
            String tienda = spTiendas.getSelectedItem().toString();
            double monto = Double.parseDouble(etMonto.getText().toString());
            double gan = Double.parseDouble(etGanancia.getText().toString());
            if (rbPorcentaje.isChecked()) gan = CalculadoraGanancias.calcularGananciaDesdePorcentaje(monto, gan);

            Pedido p = new Pedido(c.getId(), c.getNombre(), tienda, monto, gan, cal.getTime());
            p.setNotas(etNotas.getText().toString());

            if (pedidoId > 0) { p.setId(pedidoId); db.actualizarPedido(p); Toast.makeText(this, "Pedido actualizado", Toast.LENGTH_SHORT).show(); }
            else { db.agregarPedido(p); Toast.makeText(this, "Pedido guardado", Toast.LENGTH_SHORT).show(); }
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarPedido(int id) {
        Pedido p = db.getPedidoById(id);
        if (p == null) return;
        etMonto.setText(String.valueOf(p.getMontoCompra()));
        etGanancia.setText(String.valueOf(p.getGanancia()));
        tvTotal.setText(String.format(Locale.getDefault(), "RD$ %,.2f", p.getTotalGeneral()));
        if (p.getFechaEntrega() != null) { cal.setTime(p.getFechaEntrega()); tvFecha.setText(df.format(p.getFechaEntrega())); }
        etNotas.setText(p.getNotas());
        // TODO: seleccionar cliente/tienda correctos en los spinners si lo necesitas.
    }
}