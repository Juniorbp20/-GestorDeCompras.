package com.example.gestiondecompras.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestiondecompras.R;
import com.example.gestiondecompras.adapters.TarjetasAdapter;
import com.example.gestiondecompras.database.DatabaseHelper;
import com.example.gestiondecompras.models.Tarjeta;

import java.util.List;

public class NuevaTarjetaActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private EditText etBanco, etAlias, etLimite, etCorte, etVence, etNotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_tarjeta);

        db = new DatabaseHelper(this);
        etBanco = findViewById(R.id.etBanco);
        etAlias = findViewById(R.id.etAlias);
        etLimite = findViewById(R.id.etLimite);
        etCorte = findViewById(R.id.etCorte);
        etVence = findViewById(R.id.etVence);
        etNotas = findViewById(R.id.etNotas);
        Button btnGuardar = findViewById(R.id.btnGuardarTarjeta);

        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void guardar() {
        if (TextUtils.isEmpty(etBanco.getText())) { Toast.makeText(this, "Banco requerido", Toast.LENGTH_SHORT).show(); return; }
        double limite = 0; try { limite = Double.parseDouble(etLimite.getText().toString()); } catch (Exception ignored) {}

        Tarjeta t = new Tarjeta();
        t.setBanco(etBanco.getText().toString());
        t.setAlias(etAlias.getText().toString());
        t.setLimiteCredito(limite);
        try { t.setDiaCorte(Integer.parseInt(etCorte.getText().toString())); } catch (Exception ignored) {}
        try { t.setDiaVencimiento(Integer.parseInt(etVence.getText().toString())); } catch (Exception ignored) {}
        t.setNotas(etNotas.getText().toString());

        db.agregarTarjeta(t);
        Toast.makeText(this, "Tarjeta guardada", Toast.LENGTH_SHORT).show();
        finish();
    }
}
