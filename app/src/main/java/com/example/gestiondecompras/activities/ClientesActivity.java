package com.example.gestiondecompras.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.gestiondecompras.adapters.ClientesAdapter;
import com.example.gestiondecompras.database.DatabaseHelper;
import com.example.gestiondecompras.databinding.ActivityClientesBinding;
import com.example.gestiondecompras.models.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClientesActivity extends AppCompatActivity implements ClientesAdapter.OnClienteClickListener {

    private ActivityClientesBinding binding;
    private DatabaseHelper db;
    private ClientesAdapter adapter;

    private final List<Cliente> todos = new ArrayList<>();   // fuente completa
    private final List<Cliente> visibles = new ArrayList<>(); // lista filtrada que muestra el adapter

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClientesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHelper(this);

        // Recycler
        binding.rvClientes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClientesAdapter(visibles, this);
        binding.rvClientes.setAdapter(adapter);

        // Spinner (filtros simples)
        String[] opciones = new String[]{"Todos", "Con teléfono", "Con email"};
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

        cargarClientes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarClientes(); // por si vuelves después de crear/editar
    }

    private void cargarClientes() {
        todos.clear();
        todos.addAll(db.getAllClientes());  // ya la tienes implementada
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String q = binding.etBuscar.getText().toString().toLowerCase(Locale.getDefault()).trim();
        String filtro = (String) binding.spinnerFiltro.getSelectedItem();

        visibles.clear();
        for (Cliente c : todos) {
            boolean pasaTexto = q.isEmpty()
                    || (c.getNombre() != null && c.getNombre().toLowerCase(Locale.getDefault()).contains(q))
                    || (c.getTelefono() != null && c.getTelefono().toLowerCase(Locale.getDefault()).contains(q))
                    || (c.getEmail() != null && c.getEmail().toLowerCase(Locale.getDefault()).contains(q));

            boolean pasaFiltro = true;
            if ("Con teléfono".equals(filtro))      pasaFiltro = c.getTelefono() != null && !c.getTelefono().isEmpty();
            else if ("Con email".equals(filtro))    pasaFiltro = c.getEmail() != null && !c.getEmail().isEmpty();

            if (pasaTexto && pasaFiltro) visibles.add(c);
        }

        adapter.notifyDataSetChanged();
        binding.tvVacio.setVisibility(visibles.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // Clicks del adapter
    @Override
    public void onClienteClick(Cliente cliente) {
        Intent i = new Intent(this, ListaPedidosActivity.class);
        i.putExtra("cliente_id", cliente.getId()); // en ListaPedidosActivity filtra por este id
        startActivity(i);
    }

    @Override
    public void onClienteLongClick(Cliente cliente) {
        // Aquí podrías abrir una pantalla de edición, por ahora igual que click
        onClienteClick(cliente);
    }
}
