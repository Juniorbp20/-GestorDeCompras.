package com.example.gestordecompras.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestordecompras.R;
import com.example.gestordecompras.adapters.PedidosAdapter;
import com.example.gestordecompras.database.DatabaseHelper;
import com.example.gestordecompras.models.Pedido;
import java.util.List;

public class ListaPedidosActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvPedidos;
    private PedidosAdapter adapter;
    private Spinner spinnerFiltroEstado;
    private EditText etBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pedidos);

        dbHelper = new DatabaseHelper(this);
        initViews();
        cargarPedidos();
    }

    private void initViews() {
        rvPedidos = findViewById(R.id.rvPedidos);
        spinnerFiltroEstado = findViewById(R.id.spinnerFiltroEstado);
        etBuscar = findViewById(R.id.etBuscar);

        rvPedidos.setLayoutManager(new LinearLayoutManager(this));

        // Configurar filtros
        setupFiltros();
    }

    private void setupFiltros() {
        String[] estados = {"Todos", "Pendientes", "Entregados", "Pagados", "Atrasados"};
        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, estados);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroEstado.setAdapter(estadoAdapter);

        spinnerFiltroEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarPedidos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarPedidos() {
        String filtroEstado = spinnerFiltroEstado.getSelectedItem().toString();
        String busqueda = etBuscar.getText().toString();

        List<Pedido> pedidos = dbHelper.getPedidosFiltrados(filtroEstado, busqueda);
        adapter = new PedidosAdapter(pedidos, this::onPedidoClick);
        rvPedidos.setAdapter(adapter);
    }

    private void onPedidoClick(Pedido pedido) {
        // Abrir edición del pedido
        Intent intent = new Intent(this, NuevoPedidoActivity.class);
        intent.putExtra("pedido_id", pedido.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPedidos(); // Refrescar al volver
    }
}