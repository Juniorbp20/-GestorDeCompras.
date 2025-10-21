package com.example.gestiondecompras.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestiondecompras.database.DatabaseHelper;
import com.example.gestiondecompras.databinding.ActivityNuevoClienteBinding;
import com.example.gestiondecompras.models.Cliente;

public class NuevoClienteActivity extends AppCompatActivity {

    private ActivityNuevoClienteBinding binding;
    private DatabaseHelper db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNuevoClienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHelper(this);

        binding.btnCancelar.setOnClickListener(v -> finish());

        binding.btnGuardar.setOnClickListener(v -> {
            String nombre   = binding.etNombre.getText().toString().trim();
            String telefono = binding.etTelefono.getText().toString().trim();
            String email    = binding.etEmail.getText().toString().trim();
            String dir      = binding.etDireccion.getText().toString().trim();
            boolean activo  = binding.chkActivo.isChecked();

            if (TextUtils.isEmpty(nombre)) {
                binding.etNombre.setError("Requerido");
                binding.etNombre.requestFocus();
                return;
            }

            Cliente c = new Cliente();
            c.setNombre(nombre);
            c.setTelefono(telefono);
            c.setEmail(email);
            c.setDireccion(dir);
            c.setActivo(activo);

            try {
                long id = db.insertarCliente(c); // ← ver punto 3
                if (id > 0) {
                    Toast.makeText(this, "Cliente guardado", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "No se pudo guardar", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
