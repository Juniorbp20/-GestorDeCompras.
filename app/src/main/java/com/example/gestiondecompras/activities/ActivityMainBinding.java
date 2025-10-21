package com.example.gestiondecompras.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.gestiondecompras.R;
import java.lang.NullPointerException;
import java.lang.Override;

public final class ActivityMainBinding implements ViewBinding {
    @NonNull
    private final View rootView;

    @NonNull
    public final View btnCalendario;

    @NonNull
    public final View btnClientes;

    @NonNull
    public final View btnListaPedidos;

    @NonNull
    public final View btnNuevoPedido;

    @NonNull
    public final RecyclerView rvProximosPedidos;

    @NonNull
    public final EditText tvGananciaEsperada;

    @NonNull
    public final EditText tvPedidosHoy;

    @NonNull
    public final EditText tvTotalPendiente;

    public EditText tvClientesActivos;

    private ActivityMainBinding(@NonNull View rootView, @NonNull View btnCalendario,
                                @NonNull View btnClientes, @NonNull View btnListaPedidos, @NonNull View btnNuevoPedido,
                                @NonNull RecyclerView rvProximosPedidos, @NonNull EditText tvGananciaEsperada,
                                @NonNull EditText tvPedidosHoy, @NonNull EditText tvTotalPendiente) {
        this.rootView = rootView;
        this.btnCalendario = btnCalendario;
        this.btnClientes = btnClientes;
        this.btnListaPedidos = btnListaPedidos;
        this.btnNuevoPedido = btnNuevoPedido;
        this.rvProximosPedidos = rvProximosPedidos;
        this.tvGananciaEsperada = tvGananciaEsperada;
        this.tvPedidosHoy = tvPedidosHoy;
        this.tvTotalPendiente = tvTotalPendiente;
    }

    @Override
    @NonNull
    public View getRoot() {
        return rootView;
    }

    @NonNull
    public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    @NonNull
    public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
                                              @Nullable ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_main, parent, false);
        if (attachToParent) {
            assert parent != null;
            parent.addView(root);
        }
        return bind(root);
    }

    @NonNull
    public static ActivityMainBinding bind(@NonNull View rootView) {
        // La lógica para encontrar cada vista por su ID
        int id;
        id = R.id.calendarView;
        View btnCalendario = ViewBindings.findChildViewById(rootView, id);
        if (btnCalendario == null) {
            throw new NullPointerException("Missing required view with ID: ".concat(rootView.getResources().getResourceName(id)));
        }

        id = R.id.tvCliente;
        View btnClientes = ViewBindings.findChildViewById(rootView, id);
        if (btnClientes == null) {
            throw new NullPointerException("Missing required view with ID: ".concat(rootView.getResources().getResourceName(id)));
        }

        id = R.id.rvPedidos;
        View btnListaPedidos = ViewBindings.findChildViewById(rootView, id);
        if (btnListaPedidos == null) {
            throw new NullPointerException("Missing required view with ID: ".concat(rootView.getResources().getResourceName(id)));
        }

        id = R.id.rvPedidos;
        View btnNuevoPedido = ViewBindings.findChildViewById(rootView, id);
        if (btnNuevoPedido == null) {
            throw new NullPointerException("Missing required view with ID: ".concat(rootView.getResources().getResourceName(id)));
        }

        id = R.id.rvProximosPedidos;
        RecyclerView rvProximosPedidos = ViewBindings.findChildViewById(rootView, id);
        if (rvProximosPedidos == null) {
            throw new NullPointerException("Missing required view with ID: ".concat(rootView.getResources().getResourceName(id)));
        }

        id = R.id.tvGanancia;
        EditText tvGananciaEsperada = ViewBindings.findChildViewById(rootView, id);
        if (tvGananciaEsperada == null) {
            throw new NullPointerException("Missing required view with ID: ".concat(rootView.getResources().getResourceName(id)));
        }

        id = R.id.rvPedidos;
        EditText tvPedidosHoy = ViewBindings.findChildViewById(rootView, id);
        if (tvPedidosHoy == null) {
            throw new NullPointerException("Missing required view with ID: ".concat(rootView.getResources().getResourceName(id)));
        }

        id = R.id.tvTotalGeneral;
        EditText tvTotalPendiente = ViewBindings.findChildViewById(rootView, id);
        if (tvTotalPendiente == null) {
            throw new NullPointerException("Missing required view with ID: ".concat(rootView.getResources().getResourceName(id)));
        }

        return new ActivityMainBinding(rootView, btnCalendario, btnClientes, btnListaPedidos, btnNuevoPedido,
                rvProximosPedidos, tvGananciaEsperada, tvPedidosHoy, tvTotalPendiente);
    }
}
