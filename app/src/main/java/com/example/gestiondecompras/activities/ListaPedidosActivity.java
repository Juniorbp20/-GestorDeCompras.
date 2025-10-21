package com.example.gestiondecompras.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestiondecompras.R;
import com.example.gestiondecompras.adapters.PedidosAdapter;
import com.example.gestiondecompras.database.DatabaseHelper;
import com.example.gestiondecompras.models.Pedido;

import java.util.List;

public class ListaPedidosActivity extends AppCompatActivity implements PedidosAdapter.OnPedidoClickListener {
    private DatabaseHelper db;
    private RecyclerView rv;
    private Spinner spEstado;
    private EditText etBuscar;
    private PedidosAdapter adapter;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pedidos);

        db = new DatabaseHelper(this);
        rv = findViewById(R.id.rvProximosPedidos);
        spEstado = findViewById(R.id.tvEstado);
        etBuscar = findViewById(R.id.etBuscar);
        Button btnFiltrar = findViewById(R.id.btnFiltrar);

        rv.setLayoutManager(new LinearLayoutManager(this));

        btnFiltrar.setOnClickListener(v -> cargar());
        etBuscar.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) { cargar(); }
            public void afterTextChanged(Editable s) {}
        });

        cargar();
    }

    private void cargar() {
        String estado = spEstado.getSelectedItem() == null ? "Todos" : spEstado.getSelectedItem().toString();
        String q = etBuscar.getText().toString();
        List<Pedido> data = db.getPedidosFiltrados(estado, q);
        if (adapter == null) { adapter = new PedidosAdapter(data, this); rv.setAdapter(adapter); }
        else adapter.actualizarLista(data);
    }

    @Override
    public void onPedidoClick(Pedido p) {
        Intent i = new Intent(this, NuevoPedidoActivity.class);
        i.putExtra("pedido_id", p.getId());
        startActivity(i);
    }

    @Override
    public void onPedidoLongClick(Pedido p) { onPedidoClick(p); }

    @Override
    protected void onResume() { super.onResume(); cargar(); }
}