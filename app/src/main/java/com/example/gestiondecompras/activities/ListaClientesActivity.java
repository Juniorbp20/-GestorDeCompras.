package com.example.gestiondecompras.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.gestiondecompras.R;
import com.example.gestiondecompras.models.Cliente;
import com.example.gestiondecompras.adapters.ClientesAdapter;
import com.example.gestiondecompras.database.DatabaseHelper;
import com.example.gestiondecompras.databinding.ActivityListaClientesBinding;
import com.example.gestiondecompras.models.Pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListaClientesActivity extends AppCompatActivity implements ClientesAdapter.OnClienteClickListener {

    private ActivityListaClientesBinding binding;
    private DatabaseHelper db;
    private ClientesAdapter adapter;

    private final List<Cliente> fuente = new ArrayList<>();
    private final List<Cliente> visibles = new ArrayList<>();
    private String filtroTodos;
    private String filtroTelefono;
    private String filtroEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaClientesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setTitle(R.string.clientes_title);

        db = new DatabaseHelper(this);

        // Recycler
        binding.rvClientes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClientesAdapter(visibles, this);
        binding.rvClientes.setAdapter(adapter);

        // Spinner filtros
        filtroTodos = getString(R.string.clientes_filter_all);
        filtroTelefono = getString(R.string.clientes_filter_phone);
        filtroEmail = getString(R.string.clientes_filter_email);

        String[] opciones = new String[]{filtroTodos, filtroTelefono, filtroEmail};
        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFiltro.setAdapter(spAdapter);

        // Eventos
        binding.fabNuevo.setOnClickListener(v ->
                startActivity(new Intent(this, NuevoClienteActivity.class)));

        binding.spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { aplicarFiltros(); }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { aplicarFiltros(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarClientes();
    }

    private void cargarClientes() {
        fuente.clear();
        fuente.addAll(db.getAllClientes());
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String q = binding.etBuscar.getText().toString().toLowerCase(Locale.getDefault()).trim();
        String filtro = (String) binding.spinnerFiltro.getSelectedItem();

        visibles.clear();
        for (Cliente c : fuente) {
            boolean porTexto = q.isEmpty()
                    || (c.getNombre() != null && c.getNombre().toLowerCase(Locale.getDefault()).contains(q))
                    || (c.getTelefono() != null && c.getTelefono().toLowerCase(Locale.getDefault()).contains(q))
                    || (c.getEmail() != null && c.getEmail().toLowerCase(Locale.getDefault()).contains(q));

            boolean porFiltro = true;
            if (filtroTelefono.equals(filtro)) porFiltro = c.getTelefono() != null && !c.getTelefono().isEmpty();
            else if (filtroEmail.equals(filtro)) porFiltro = c.getEmail() != null && !c.getEmail().isEmpty();

            if (porTexto && porFiltro) visibles.add(c);
        }

        adapter.notifyDataSetChanged();
        binding.tvVacio.setVisibility(visibles.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // Clicks del adapter
    @Override
    public void onClienteClick(Cliente cliente) {
        Intent i = new Intent(this, ListaPedidosActivity.class);
        i.putExtra("cliente_id", cliente.getId());
        i.putExtra("cliente_nombre", cliente.getNombre());
        startActivity(i);
    }

    @Override
    public void onClienteLongClick(Cliente cliente) {
        CharSequence[] opciones = {
                getString(R.string.clientes_ver_pedidos),
                getString(R.string.accion_eliminar)
        };

        new AlertDialog.Builder(this)
                .setTitle(cliente.getNombre())
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        onClienteClick(cliente);
                    } else if (which == 1) {
                        confirmarEliminarCliente(cliente);
                    }
                })
                .setNegativeButton(R.string.accion_cancelar, null)
                .show();
    }

    private void confirmarEliminarCliente(Cliente cliente) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_cliente_title)
                .setMessage(getString(R.string.dialog_delete_cliente_message, cliente.getNombre()))
                .setPositiveButton(R.string.accion_eliminar, (d, w) -> eliminarCliente(cliente))
                .setNegativeButton(R.string.accion_cancelar, null)
                .show();
    }

    private void eliminarCliente(Cliente cliente) {
        List<Pedido> pedidosCliente = db.getPedidosFiltrados("Todos", null, cliente.getId());
        if (pedidosCliente != null) {
            for (Pedido pedido : pedidosCliente) {
                db.eliminarPedido(pedido.getId());
            }
        }
        db.eliminarCliente(cliente.getId());
        Toast.makeText(this, R.string.toast_cliente_eliminado, Toast.LENGTH_SHORT).show();
        cargarClientes();
    }
}
