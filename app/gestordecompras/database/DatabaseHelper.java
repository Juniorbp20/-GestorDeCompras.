package com.example.gestordecompras.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.gestordecompras.models.Cliente;
import com.example.gestordecompras.models.Pedido;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Información de la base de datos
    private static final String DATABASE_NAME = "GestionCompras.db";
    private static final int DATABASE_VERSION = 1;

    // Nombres de tablas
    private static final String TABLE_CLIENTES = "clientes";
    private static final String TABLE_PEDIDOS = "pedidos";

    // Columnas comunes
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // Columnas tabla CLIENTES
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_TELEFONO = "telefono";
    private static final String KEY_EMAIL = "email";

    // Columnas tabla PEDIDOS
    private static final String KEY_CLIENTE_ID = "cliente_id";
    private static final String KEY_CLIENTE_NOMBRE = "cliente_nombre";
    private static final String KEY_TIENDA = "tienda";
    private static final String KEY_MONTO_COMPRA = "monto_compra";
    private static final String KEY_GANANCIA = "ganancia";
    private static final String KEY_TOTAL_GENERAL = "total_general";
    private static final String KEY_FECHA_ENTREGA = "fecha_entrega";
    private static final String KEY_ESTADO = "estado";
    private static final String KEY_NOTAS = "notas";

    // Sentencias CREATE TABLE
    private static final String CREATE_TABLE_CLIENTES = "CREATE TABLE " + TABLE_CLIENTES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NOMBRE + " TEXT NOT NULL,"
            + KEY_TELEFONO + " TEXT,"
            + KEY_EMAIL + " TEXT,"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    private static final String CREATE_TABLE_PEDIDOS = "CREATE TABLE " + TABLE_PEDIDOS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CLIENTE_ID + " INTEGER NOT NULL,"
            + KEY_CLIENTE_NOMBRE + " TEXT NOT NULL,"
            + KEY_TIENDA + " TEXT NOT NULL,"
            + KEY_MONTO_COMPRA + " REAL NOT NULL,"
            + KEY_GANANCIA + " REAL NOT NULL,"
            + KEY_TOTAL_GENERAL + " REAL NOT NULL,"
            + KEY_FECHA_ENTREGA + " DATETIME,"
            + KEY_ESTADO + " TEXT DEFAULT '" + Pedido.ESTADO_PENDIENTE + "',"
            + KEY_NOTAS + " TEXT,"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + KEY_CLIENTE_ID + ") REFERENCES " + TABLE_CLIENTES + "(" + KEY_ID + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLIENTES);
        db.execSQL(CREATE_TABLE_PEDIDOS);

        // Insertar clientes de ejemplo
        insertarClientesEjemplo(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEDIDOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTES);
        onCreate(db);
    }

    // ==================== OPERACIONES CLIENTES ====================

    public long agregarCliente(Cliente cliente) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOMBRE, cliente.getNombre());
        values.put(KEY_TELEFONO, cliente.getTelefono());
        values.put(KEY_EMAIL, cliente.getEmail());

        long id = db.insert(TABLE_CLIENTES, null, values);
        db.close();
        return id;
    }

    public Cliente getCliente(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLIENTES,
                new String[]{KEY_ID, KEY_NOMBRE, KEY_TELEFONO, KEY_EMAIL, KEY_CREATED_AT},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        cursor.moveToFirst();

        Cliente cliente = new Cliente();
        cliente.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        cliente.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOMBRE)));
        cliente.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TELEFONO)));
        cliente.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));
        // cliente.setFechaRegistro(...); // Puedes parsear la fecha si es necesario

        cursor.close();
        return cliente;
    }

    public List<Cliente> getAllClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CLIENTES + " ORDER BY " + KEY_NOMBRE + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Cliente cliente = new Cliente();
                cliente.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                cliente.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOMBRE)));
                cliente.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TELEFONO)));
                cliente.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));

                clientes.add(cliente);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return clientes;
    }

    public int actualizarCliente(Cliente cliente) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOMBRE, cliente.getNombre());
        values.put(KEY_TELEFONO, cliente.getTelefono());
        values.put(KEY_EMAIL, cliente.getEmail());

        return db.update(TABLE_CLIENTES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(cliente.getId())});
    }

    public void eliminarCliente(Cliente cliente) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLIENTES, KEY_ID + " = ?",
                new String[]{String.valueOf(cliente.getId())});
        db.close();
    }

    // ==================== OPERACIONES PEDIDOS ====================

    public void agregarPedido(Pedido pedido) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CLIENTE_ID, pedido.getClienteId());
        values.put(KEY_CLIENTE_NOMBRE, pedido.getClienteNombre());
        values.put(KEY_TIENDA, pedido.getTienda());
        values.put(KEY_MONTO_COMPRA, pedido.getMontoCompra());
        values.put(KEY_GANANCIA, pedido.getGanancia());
        values.put(KEY_TOTAL_GENERAL, pedido.getTotalGeneral());
        values.put(KEY_FECHA_ENTREGA, pedido.getFechaEntrega().getTime());
        values.put(KEY_ESTADO, pedido.getEstado());
        values.put(KEY_NOTAS, pedido.getNotas());

        long id = db.insert(TABLE_PEDIDOS, null, values);
        db.close();
    }

    public Pedido getPedidoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PEDIDOS,
                new String[]{
                        KEY_ID, KEY_CLIENTE_ID, KEY_CLIENTE_NOMBRE, KEY_TIENDA,
                        KEY_MONTO_COMPRA, KEY_GANANCIA, KEY_TOTAL_GENERAL,
                        KEY_FECHA_ENTREGA, KEY_ESTADO, KEY_NOTAS, KEY_CREATED_AT
                },
                KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        cursor.moveToFirst();

        Pedido pedido = new Pedido();
        pedido.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        pedido.setClienteId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CLIENTE_ID)));
        pedido.setClienteNombre(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLIENTE_NOMBRE)));
        pedido.setTienda(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIENDA)));
        pedido.setMontoCompra(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_MONTO_COMPRA)));
        pedido.setGanancia(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_GANANCIA)));
        pedido.setTotalGeneral(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_TOTAL_GENERAL)));

        // Fecha de entrega
        long fechaEntregaMillis = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_FECHA_ENTREGA));
        pedido.setFechaEntrega(new Date(fechaEntregaMillis));

        pedido.setEstado(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ESTADO)));
        pedido.setNotas(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTAS)));

        cursor.close();
        return pedido;
    }

    public List<Pedido> getAllPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PEDIDOS + " ORDER BY " + KEY_FECHA_ENTREGA + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Pedido pedido = cursorAPedido(cursor);
                pedidos.add(pedido);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return pedidos;
    }

    public List<Pedido> getProximosPedidos(int limite) {
        List<Pedido> pedidos = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PEDIDOS
                + " WHERE " + KEY_ESTADO + " = '" + Pedido.ESTADO_PENDIENTE + "'"
                + " ORDER BY " + KEY_FECHA_ENTREGA + " ASC"
                + " LIMIT " + limite;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Pedido pedido = cursorAPedido(cursor);
                pedidos.add(pedido);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return pedidos;
    }

    public List<Pedido> getPedidosFiltrados(String filtroEstado, String busqueda) {
        List<Pedido> pedidos = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM " + TABLE_PEDIDOS + " WHERE 1=1");

        // Aplicar filtro de estado
        if (!filtroEstado.equals("Todos")) {
            switch (filtroEstado) {
                case "Pendientes":
                    query.append(" AND ").append(KEY_ESTADO).append(" = '").append(Pedido.ESTADO_PENDIENTE).append("'");
                    break;
                case "Entregados":
                    query.append(" AND ").append(KEY_ESTADO).append(" = '").append(Pedido.ESTADO_ENTREGADO).append("'");
                    break;
                case "Pagados":
                    query.append(" AND ").append(KEY_ESTADO).append(" = '").append(Pedido.ESTADO_PAGADO).append("'");
                    break;
                case "Atrasados":
                    query.append(" AND ").append(KEY_ESTADO).append(" = '").append(Pedido.ESTADO_PENDIENTE).append("'");
                    query.append(" AND ").append(KEY_FECHA_ENTREGA).append(" < ").append(System.currentTimeMillis());
                    break;
            }
        }

        // Aplicar búsqueda
        if (busqueda != null && !busqueda.isEmpty()) {
            query.append(" AND (").append(KEY_CLIENTE_NOMBRE).append(" LIKE '%").append(busqueda).append("%'")
                    .append(" OR ").append(KEY_TIENDA).append(" LIKE '%").append(busqueda).append("%')");
        }

        query.append(" ORDER BY ").append(KEY_FECHA_ENTREGA).append(" ASC");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query.toString(), null);

        if (cursor.moveToFirst()) {
            do {
                Pedido pedido = cursorAPedido(cursor);
                pedidos.add(pedido);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return pedidos;
    }

    public List<Pedido> getPedidosPorFecha(Date fecha) {
        List<Pedido> pedidos = new ArrayList<>();

        // Convertir fecha a inicio y fin del día
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long inicioDia = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        long finDia = cal.getTimeInMillis();

        String selectQuery = "SELECT * FROM " + TABLE_PEDIDOS
                + " WHERE " + KEY_FECHA_ENTREGA + " BETWEEN " + inicioDia + " AND " + finDia
                + " ORDER BY " + KEY_FECHA_ENTREGA + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Pedido pedido = cursorAPedido(cursor);
                pedidos.add(pedido);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return pedidos;
    }

    public void actualizarPedido(Pedido pedido) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CLIENTE_ID, pedido.getClienteId());
        values.put(KEY_CLIENTE_NOMBRE, pedido.getClienteNombre());
        values.put(KEY_TIENDA, pedido.getTienda());
        values.put(KEY_MONTO_COMPRA, pedido.getMontoCompra());
        values.put(KEY_GANANCIA, pedido.getGanancia());
        values.put(KEY_TOTAL_GENERAL, pedido.getTotalGeneral());
        values.put(KEY_FECHA_ENTREGA, pedido.getFechaEntrega().getTime());
        values.put(KEY_ESTADO, pedido.getEstado());
        values.put(KEY_NOTAS, pedido.getNotas());

        db.update(TABLE_PEDIDOS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(pedido.getId())});
    }

    public void eliminarPedido(Pedido pedido) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PEDIDOS, KEY_ID + " = ?",
                new String[]{String.valueOf(pedido.getId())});
        db.close();
    }

    // ==================== MÉTODOS DE DASHBOARD ====================

    public double getTotalPendiente() {
        String query = "SELECT SUM(" + KEY_TOTAL_GENERAL + ") FROM " + TABLE_PEDIDOS
                + " WHERE " + KEY_ESTADO + " IN ('" + Pedido.ESTADO_PENDIENTE + "', '" + Pedido.ESTADO_ENTREGADO + "')";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        return total;
    }

    public double getGananciaEsperada() {
        String query = "SELECT SUM(" + KEY_GANANCIA + ") FROM " + TABLE_PEDIDOS
                + " WHERE " + KEY_ESTADO + " IN ('" + Pedido.ESTADO_PENDIENTE + "', '" + Pedido.ESTADO_ENTREGADO + "')";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        double ganancia = 0;
        if (cursor.moveToFirst()) {
            ganancia = cursor.getDouble(0);
        }

        cursor.close();
        return ganancia;
    }

    public int getPedidosParaHoy() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long inicioDia = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        long finDia = cal.getTimeInMillis();

        String query = "SELECT COUNT(*) FROM " + TABLE_PEDIDOS
                + " WHERE " + KEY_FECHA_ENTREGA + " BETWEEN " + inicioDia + " AND " + finDia
                + " AND " + KEY_ESTADO + " = '" + Pedido.ESTADO_PENDIENTE + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private Pedido cursorAPedido(Cursor cursor) {
        Pedido pedido = new Pedido();
        pedido.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        pedido.setClienteId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CLIENTE_ID)));
        pedido.setClienteNombre(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLIENTE_NOMBRE)));
        pedido.setTienda(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIENDA)));
        pedido.setMontoCompra(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_MONTO_COMPRA)));
        pedido.setGanancia(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_GANANCIA)));
        pedido.setTotalGeneral(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_TOTAL_GENERAL)));

        long fechaEntregaMillis = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_FECHA_ENTREGA));
        pedido.setFechaEntrega(new Date(fechaEntregaMillis));

        pedido.setEstado(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ESTADO)));
        pedido.setNotas(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTAS)));

        return pedido;
    }

    private void insertarClientesEjemplo(SQLiteDatabase db) {
        // Insertar algunos clientes de ejemplo
        String[][] clientesEjemplo = {
                {"Juan Pérez", "809-123-4567", "juan@email.com"},
                {"María García", "809-234-5678", "maria@email.com"},
                {"Carlos Rodríguez", "809-345-6789", "carlos@email.com"}
        };

        for (String[] cliente : clientesEjemplo) {
            ContentValues values = new ContentValues();
            values.put(KEY_NOMBRE, cliente[0]);
            values.put(KEY_TELEFONO, cliente[1]);
            values.put(KEY_EMAIL, cliente[2]);
            db.insert(TABLE_CLIENTES, null, values);
        }
    }
}