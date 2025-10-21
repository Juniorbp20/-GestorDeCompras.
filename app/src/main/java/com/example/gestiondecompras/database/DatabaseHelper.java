package com.example.gestiondecompras.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gestiondecompras.models.Cliente;
import com.example.gestiondecompras.models.MovimientoTarjeta;
import com.example.gestiondecompras.models.PagoCliente;
import com.example.gestiondecompras.models.Pedido;
import com.example.gestiondecompras.models.Tarjeta;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // ======= CONFIG =======
    private static final String DATABASE_NAME = "GestionCompras.db";
    private static final int DATABASE_VERSION = 1; // Si cambias tablas, sube la versión y agrega migración en onUpgrade

    // ======= TABLAS =======
    private static final String TABLE_CLIENTES = "clientes";
    private static final String TABLE_PEDIDOS = "pedidos";
    private static final String TABLE_TARJETAS = "tarjetas";
    private static final String TABLE_MOVIMIENTOS = "movimientos_tarjeta";
    private static final String TABLE_PAGOS = "pagos_cliente";

    // ======= COLUMNAS COMUNES =======
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // CLIENTES
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_TELEFONO = "telefono";
    private static final String KEY_EMAIL = "email";

    // PEDIDOS
    private static final String KEY_CLIENTE_ID = "cliente_id";
    private static final String KEY_CLIENTE_NOMBRE = "cliente_nombre";
    private static final String KEY_TIENDA = "tienda";
    private static final String KEY_MONTO_COMPRA = "monto_compra";
    private static final String KEY_GANANCIA = "ganancia";
    private static final String KEY_TOTAL_GENERAL = "total_general";
    private static final String KEY_FECHA_ENTREGA = "fecha_entrega"; // millis
    private static final String KEY_ESTADO = "estado";
    private static final String KEY_NOTAS = "notas";

    // TARJETAS
    private static final String KEY_BANCO = "banco";
    private static final String KEY_ALIAS = "alias";
    private static final String KEY_LIMITE = "limite";
    private static final String KEY_DEUDA = "deuda";
    private static final String KEY_DIA_CORTE = "dia_corte";
    private static final String KEY_DIA_VENCIMIENTO = "dia_vencimiento";
    private static final String KEY_NOTAS_TARJ = "notas"; // reuse name

    // MOVIMIENTOS TARJETA
    private static final String KEY_TARJETA_ID = "tarjeta_id";
    private static final String KEY_PEDIDO_ID_FK = "pedido_id";
    private static final String KEY_TIPO = "tipo"; // GASTO | PAGO
    private static final String KEY_MONTO = "monto";
    private static final String KEY_FECHA = "fecha"; // millis
    private static final String KEY_DESCRIPCION = "descripcion";

    // PAGOS CLIENTE
    private static final String KEY_PEDIDO_ID_PAGO = "pedido_id";
    private static final String KEY_MONTO_PAGO = "monto";
    private static final String KEY_METODO = "metodo";
    private static final String KEY_REFERENCIA = "referencia";
    private static final String KEY_FECHA_PAGO = "fecha"; // millis

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ======================= CREATE TABLES =======================
    private static final String CREATE_TABLE_CLIENTES =
            "CREATE TABLE " + TABLE_CLIENTES + "(" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_NOMBRE + " TEXT NOT NULL," +
                    KEY_TELEFONO + " TEXT," +
                    KEY_EMAIL + " TEXT," +
                    KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    private static final String CREATE_TABLE_PEDIDOS =
            "CREATE TABLE " + TABLE_PEDIDOS + "(" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_CLIENTE_ID + " INTEGER NOT NULL," +
                    KEY_CLIENTE_NOMBRE + " TEXT NOT NULL," +
                    KEY_TIENDA + " TEXT NOT NULL," +
                    KEY_MONTO_COMPRA + " REAL NOT NULL," +
                    KEY_GANANCIA + " REAL NOT NULL," +
                    KEY_TOTAL_GENERAL + " REAL NOT NULL," +
                    KEY_FECHA_ENTREGA + " INTEGER," + // millis
                    KEY_ESTADO + " TEXT DEFAULT '" + Pedido.ESTADO_PENDIENTE + "'," +
                    KEY_NOTAS + " TEXT," +
                    KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + KEY_CLIENTE_ID + ") REFERENCES " + TABLE_CLIENTES + "(" + KEY_ID + ")" +
                    ")";

    private static final String CREATE_TABLE_TARJETAS =
            "CREATE TABLE " + TABLE_TARJETAS + "(" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_BANCO + " TEXT NOT NULL," +
                    KEY_ALIAS + " TEXT," +
                    KEY_LIMITE + " REAL NOT NULL," +
                    KEY_DEUDA + " REAL NOT NULL DEFAULT 0," +
                    KEY_DIA_CORTE + " INTEGER," +
                    KEY_DIA_VENCIMIENTO + " INTEGER," +
                    KEY_NOTAS_TARJ + " TEXT," +
                    KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    private static final String CREATE_TABLE_MOVIMIENTOS =
            "CREATE TABLE " + TABLE_MOVIMIENTOS + "(" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_TARJETA_ID + " INTEGER NOT NULL," +
                    KEY_PEDIDO_ID_FK + " INTEGER," +
                    KEY_TIPO + " TEXT NOT NULL," +
                    KEY_MONTO + " REAL NOT NULL," +
                    KEY_FECHA + " INTEGER NOT NULL," +
                    KEY_DESCRIPCION + " TEXT," +
                    "FOREIGN KEY(" + KEY_TARJETA_ID + ") REFERENCES " + TABLE_TARJETAS + "(" + KEY_ID + ")," +
                    "FOREIGN KEY(" + KEY_PEDIDO_ID_FK + ") REFERENCES " + TABLE_PEDIDOS + "(" + KEY_ID + ")" +
                    ")";

    private static final String CREATE_TABLE_PAGOS =
            "CREATE TABLE " + TABLE_PAGOS + "(" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_PEDIDO_ID_PAGO + " INTEGER NOT NULL," +
                    KEY_MONTO_PAGO + " REAL NOT NULL," +
                    KEY_METODO + " TEXT," +
                    KEY_REFERENCIA + " TEXT," +
                    KEY_FECHA_PAGO + " INTEGER NOT NULL," +
                    KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + KEY_PEDIDO_ID_PAGO + ") REFERENCES " + TABLE_PEDIDOS + "(" + KEY_ID + ")" +
                    ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLIENTES);
        db.execSQL(CREATE_TABLE_PEDIDOS);
        db.execSQL(CREATE_TABLE_TARJETAS);
        db.execSQL(CREATE_TABLE_MOVIMIENTOS);
        db.execSQL(CREATE_TABLE_PAGOS);
        insertarClientesEjemplo(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // MIGRACIONES: ejemplo básico (en producción, migra con ALTER TABLE)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAGOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIMIENTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TARJETAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEDIDOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTES);
        onCreate(db);
    }

    // ==================== CLIENTES ====================
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
        Cursor c = db.query(TABLE_CLIENTES,
                new String[]{KEY_ID, KEY_NOMBRE, KEY_TELEFONO, KEY_EMAIL, KEY_CREATED_AT},
                KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Cliente cliente = null;
        if (c != null && c.moveToFirst()) {
            cliente = new Cliente();
            cliente.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
            cliente.setNombre(c.getString(c.getColumnIndexOrThrow(KEY_NOMBRE)));
            cliente.setTelefono(c.getString(c.getColumnIndexOrThrow(KEY_TELEFONO)));
            cliente.setEmail(c.getString(c.getColumnIndexOrThrow(KEY_EMAIL)));
            c.close();
        }
        return cliente;
    }

    public List<Cliente> getAllClientes() {
        List<Cliente> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CLIENTES + " ORDER BY " + KEY_NOMBRE + " ASC", null);
        if (c.moveToFirst()) {
            do {
                Cliente cl = new Cliente();
                cl.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
                cl.setNombre(c.getString(c.getColumnIndexOrThrow(KEY_NOMBRE)));
                cl.setTelefono(c.getString(c.getColumnIndexOrThrow(KEY_TELEFONO)));
                cl.setEmail(c.getString(c.getColumnIndexOrThrow(KEY_EMAIL)));
                list.add(cl);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public int actualizarCliente(Cliente cliente) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOMBRE, cliente.getNombre());
        values.put(KEY_TELEFONO, cliente.getTelefono());
        values.put(KEY_EMAIL, cliente.getEmail());
        return db.update(TABLE_CLIENTES, values, KEY_ID + "=?", new String[]{String.valueOf(cliente.getId())});
    }

    public void eliminarCliente(int clienteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLIENTES, KEY_ID + "=?", new String[]{String.valueOf(clienteId)});
        db.close();
    }

    // ==================== PEDIDOS ====================
    public long agregarPedido(Pedido p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_CLIENTE_ID, p.getClienteId());
        v.put(KEY_CLIENTE_NOMBRE, p.getClienteNombre());
        v.put(KEY_TIENDA, p.getTienda());
        v.put(KEY_MONTO_COMPRA, p.getMontoCompra());
        v.put(KEY_GANANCIA, p.getGanancia());
        v.put(KEY_TOTAL_GENERAL, p.getTotalGeneral());
        v.put(KEY_FECHA_ENTREGA, p.getFechaEntrega() != null ? p.getFechaEntrega().getTime() : null);
        v.put(KEY_ESTADO, p.getEstado());
        v.put(KEY_NOTAS, p.getNotas());
        long id = db.insert(TABLE_PEDIDOS, null, v);
        db.close();
        return id;
    }

    public Pedido getPedidoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_PEDIDOS,
                new String[]{KEY_ID, KEY_CLIENTE_ID, KEY_CLIENTE_NOMBRE, KEY_TIENDA, KEY_MONTO_COMPRA, KEY_GANANCIA, KEY_TOTAL_GENERAL, KEY_FECHA_ENTREGA, KEY_ESTADO, KEY_NOTAS, KEY_CREATED_AT},
                KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (c != null && c.moveToFirst()) {
            Pedido p = cursorAPedido(c);
            c.close();
            return p;
        }
        if (c != null) c.close();
        return null;
    }

    public List<Pedido> getAllPedidos() {
        List<Pedido> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PEDIDOS + " ORDER BY " + KEY_FECHA_ENTREGA + " ASC", null);
        if (c.moveToFirst()) {
            do { list.add(cursorAPedido(c)); } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<Pedido> getProximosPedidos(int limite) {
        List<Pedido> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_PEDIDOS +
                " WHERE " + KEY_ESTADO + "='" + Pedido.ESTADO_PENDIENTE + "'" +
                " ORDER BY " + KEY_FECHA_ENTREGA + " ASC LIMIT " + limite;
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do { list.add(cursorAPedido(c)); } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<Pedido> getPedidosFiltrados(String filtroEstado, String busqueda) {
        List<Pedido> list = new ArrayList<>();
        StringBuilder q = new StringBuilder("SELECT * FROM ").append(TABLE_PEDIDOS).append(" WHERE 1=1");
        if (filtroEstado != null && !"Todos".equalsIgnoreCase(filtroEstado)) {
            switch (filtroEstado) {
                case "Pendientes":
                    q.append(" AND ").append(KEY_ESTADO).append("='" + Pedido.ESTADO_PENDIENTE + "'");
                    break;
                case "Entregados":
                    q.append(" AND ").append(KEY_ESTADO).append("='" + Pedido.ESTADO_ENTREGADO + "'");
                    break;
                case "Pagados":
                    q.append(" AND ").append(KEY_ESTADO).append("='" + Pedido.ESTADO_PAGADO + "'");
                    break;
                case "Atrasados":
                    q.append(" AND ").append(KEY_ESTADO).append("='" + Pedido.ESTADO_PENDIENTE + "'");
                    q.append(" AND ").append(KEY_FECHA_ENTREGA).append(" < ").append(System.currentTimeMillis());
                    break;
            }
        }
        if (busqueda != null && !busqueda.isEmpty()) {
            q.append(" AND (").append(KEY_CLIENTE_NOMBRE).append(" LIKE '%").append(busqueda).append("%'")
                    .append(" OR ").append(KEY_TIENDA).append(" LIKE '%").append(busqueda).append("%')");
        }
        q.append(" ORDER BY ").append(KEY_FECHA_ENTREGA).append(" ASC");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(q.toString(), null);
        if (c.moveToFirst()) {
            do { list.add(cursorAPedido(c)); } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<Pedido> getPedidosPorFecha(Date fecha) {
        List<Pedido> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0);
        long inicio = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59);
        long fin = cal.getTimeInMillis();
        String sql = "SELECT * FROM " + TABLE_PEDIDOS +
                " WHERE " + KEY_FECHA_ENTREGA + " BETWEEN " + inicio + " AND " + fin +
                " ORDER BY " + KEY_FECHA_ENTREGA + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do { list.add(cursorAPedido(c)); } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public int actualizarPedido(Pedido p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_CLIENTE_ID, p.getClienteId());
        v.put(KEY_CLIENTE_NOMBRE, p.getClienteNombre());
        v.put(KEY_TIENDA, p.getTienda());
        v.put(KEY_MONTO_COMPRA, p.getMontoCompra());
        v.put(KEY_GANANCIA, p.getGanancia());
        v.put(KEY_TOTAL_GENERAL, p.getTotalGeneral());
        v.put(KEY_FECHA_ENTREGA, p.getFechaEntrega() != null ? p.getFechaEntrega().getTime() : null);
        v.put(KEY_ESTADO, p.getEstado());
        v.put(KEY_NOTAS, p.getNotas());
        return db.update(TABLE_PEDIDOS, v, KEY_ID + "=?", new String[]{String.valueOf(p.getId())});
    }

    public void eliminarPedido(int pedidoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PEDIDOS, KEY_ID + "=?", new String[]{String.valueOf(pedidoId)});
        db.close();
    }

    // =============== DASHBOARD / RESÚMENES ===============
    public double getSaldoPendienteGlobal() {
        // total_general - pagos, solo estados PENDIENTE/ENTREGADO
        String q = "SELECT SUM(p." + KEY_TOTAL_GENERAL + " - IFNULL(pg.pagado,0)) " +
                "FROM " + TABLE_PEDIDOS + " p " +
                "LEFT JOIN (SELECT " + KEY_PEDIDO_ID_PAGO + " pid, SUM(" + KEY_MONTO_PAGO + ") pagado FROM " + TABLE_PAGOS + " GROUP BY " + KEY_PEDIDO_ID_PAGO + ") pg " +
                "ON pg.pid = p." + KEY_ID + " " +
                "WHERE p." + KEY_ESTADO + " IN ('" + Pedido.ESTADO_PENDIENTE + "','" + Pedido.ESTADO_ENTREGADO + "')";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(q, null);
        double total = 0; if (c.moveToFirst()) total = c.getDouble(0); c.close();
        return round2(Math.max(0, total));
    }

    public double getGananciaEsperada() {
        String q = "SELECT SUM(" + KEY_GANANCIA + ") FROM " + TABLE_PEDIDOS +
                " WHERE " + KEY_ESTADO + " IN ('" + Pedido.ESTADO_PENDIENTE + "','" + Pedido.ESTADO_ENTREGADO + "')";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(q, null);
        double g = 0; if (c.moveToFirst()) g = c.getDouble(0); c.close();
        return round2(Math.max(0, g));
    }

    public int getPedidosParaHoy() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0);
        long inicio = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59);
        long fin = cal.getTimeInMillis();
        String q = "SELECT COUNT(*) FROM " + TABLE_PEDIDOS +
                " WHERE " + KEY_FECHA_ENTREGA + " BETWEEN " + inicio + " AND " + fin +
                " AND " + KEY_ESTADO + "='" + Pedido.ESTADO_PENDIENTE + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(q, null);
        int count = 0; if (c.moveToFirst()) count = c.getInt(0); c.close();
        return count;
    }

    public double getSaldoPendientePorPedido(int pedidoId) {
        Pedido p = getPedidoById(pedidoId);
        if (p == null) return 0;
        double pagado = getTotalPagadoPorPedido(pedidoId);
        return round2(Math.max(0, p.getTotalGeneral() - pagado));
    }

    // ==================== TARJETAS ====================
    public long agregarTarjeta(Tarjeta t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_BANCO, t.getBanco());
        v.put(KEY_ALIAS, t.getAlias());
        v.put(KEY_LIMITE, t.getLimiteCredito());
        v.put(KEY_DEUDA, t.getDeudaActual());
        v.put(KEY_DIA_CORTE, t.getDiaCorte());
        v.put(KEY_DIA_VENCIMIENTO, t.getDiaVencimiento());
        v.put(KEY_NOTAS_TARJ, t.getNotas());
        long id = db.insert(TABLE_TARJETAS, null, v);
        db.close();
        return id;
    }

    public int actualizarTarjeta(Tarjeta t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_BANCO, t.getBanco());
        v.put(KEY_ALIAS, t.getAlias());
        v.put(KEY_LIMITE, t.getLimiteCredito());
        v.put(KEY_DEUDA, t.getDeudaActual());
        v.put(KEY_DIA_CORTE, t.getDiaCorte());
        v.put(KEY_DIA_VENCIMIENTO, t.getDiaVencimiento());
        v.put(KEY_NOTAS_TARJ, t.getNotas());
        return db.update(TABLE_TARJETAS, v, KEY_ID + "=?", new String[]{String.valueOf(t.getId())});
    }

    public Tarjeta getTarjetaById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_TARJETAS, null, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Tarjeta t = null;
        if (c != null && c.moveToFirst()) {
            t = new Tarjeta();
            t.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
            t.setBanco(c.getString(c.getColumnIndexOrThrow(KEY_BANCO)));
            t.setAlias(c.getString(c.getColumnIndexOrThrow(KEY_ALIAS)));
            t.setLimiteCredito(c.getDouble(c.getColumnIndexOrThrow(KEY_LIMITE)));
            t.setDeudaActual(c.getDouble(c.getColumnIndexOrThrow(KEY_DEUDA)));
            t.setDiaCorte(c.getInt(c.getColumnIndexOrThrow(KEY_DIA_CORTE)));
            t.setDiaVencimiento(c.getInt(c.getColumnIndexOrThrow(KEY_DIA_VENCIMIENTO)));
            t.setNotas(c.getString(c.getColumnIndexOrThrow(KEY_NOTAS_TARJ)));
            c.close();
        }
        return t;
    }

    public List<Tarjeta> getTarjetas() {
        List<Tarjeta> out = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_TARJETAS + " ORDER BY " + KEY_BANCO + " ASC", null);
        if (c.moveToFirst()) {
            do {
                Tarjeta t = new Tarjeta();
                t.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
                t.setBanco(c.getString(c.getColumnIndexOrThrow(KEY_BANCO)));
                t.setAlias(c.getString(c.getColumnIndexOrThrow(KEY_ALIAS)));
                t.setLimiteCredito(c.getDouble(c.getColumnIndexOrThrow(KEY_LIMITE)));
                t.setDeudaActual(c.getDouble(c.getColumnIndexOrThrow(KEY_DEUDA)));
                t.setDiaCorte(c.getInt(c.getColumnIndexOrThrow(KEY_DIA_CORTE)));
                t.setDiaVencimiento(c.getInt(c.getColumnIndexOrThrow(KEY_DIA_VENCIMIENTO)));
                t.setNotas(c.getString(c.getColumnIndexOrThrow(KEY_NOTAS_TARJ)));
                out.add(t);
            } while (c.moveToNext());
        }
        c.close();
        return out;
    }

    // ==================== MOVIMIENTOS DE TARJETA ====================
    public long registrarMovimientoTarjeta(MovimientoTarjeta m) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_TARJETA_ID, m.getTarjetaId());
        if (m.getPedidoId() != null) v.put(KEY_PEDIDO_ID_FK, m.getPedidoId());
        v.put(KEY_TIPO, m.getTipo());
        v.put(KEY_MONTO, m.getMonto());
        v.put(KEY_FECHA, m.getFecha() != null ? m.getFecha().getTime() : System.currentTimeMillis());
        v.put(KEY_DESCRIPCION, m.getDescripcion());
        long id = db.insert(TABLE_MOVIMIENTOS, null, v);

        // actualizar deuda de la tarjeta
        Tarjeta t = getTarjetaById(m.getTarjetaId());
        if (t != null) {
            double nueva = t.getDeudaActual();
            if (MovimientoTarjeta.TIPO_GASTO.equals(m.getTipo())) {
                nueva += m.getMonto();
            } else if (MovimientoTarjeta.TIPO_PAGO.equals(m.getTipo())) {
                nueva -= m.getMonto();
            }
            if (nueva < 0) nueva = 0;
            t.setDeudaActual(round2(nueva));
            actualizarTarjeta(t);
        }
        db.close();
        return id;
    }

    // ==================== PAGOS DE CLIENTE ====================
    public long agregarPagoCliente(PagoCliente p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_PEDIDO_ID_PAGO, p.getPedidoId());
        v.put(KEY_MONTO_PAGO, p.getMonto());
        v.put(KEY_METODO, p.getMetodo());
        v.put(KEY_REFERENCIA, p.getReferencia());
        v.put(KEY_FECHA_PAGO, p.getFecha() != null ? p.getFecha().getTime() : System.currentTimeMillis());
        long id = db.insert(TABLE_PAGOS, null, v);
        db.close();
        return id;
    }

    public double getTotalPagadoPorPedido(int pedidoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COALESCE(SUM(" + KEY_MONTO_PAGO + "),0) FROM " + TABLE_PAGOS +
                " WHERE " + KEY_PEDIDO_ID_PAGO + "=?", new String[]{String.valueOf(pedidoId)});
        double total = 0; if (c.moveToFirst()) total = c.getDouble(0); c.close();
        return round2(total);
    }

    // ==================== HELPERS PRIVADOS ====================
    private Pedido cursorAPedido(Cursor cursor) {
        Pedido p = new Pedido();
        p.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        p.setClienteId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CLIENTE_ID)));
        p.setClienteNombre(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLIENTE_NOMBRE)));
        p.setTienda(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIENDA)));
        p.setMontoCompra(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_MONTO_COMPRA)));
        p.setGanancia(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_GANANCIA)));
        p.setTotalGeneral(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_TOTAL_GENERAL)));
        if (!cursor.isNull(cursor.getColumnIndexOrThrow(KEY_FECHA_ENTREGA))) {
            long ms = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_FECHA_ENTREGA));
            p.setFechaEntrega(new Date(ms));
        }
        p.setEstado(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ESTADO)));
        p.setNotas(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTAS)));
        return p;
    }

    private void insertarClientesEjemplo(SQLiteDatabase db) {
        String[][] seed = {
                {"Juan Pérez", "809-123-4567", "juan@email.com"},
                {"María García", "809-234-5678", "maria@email.com"},
                {"Carlos Rodríguez", "809-345-6789", "carlos@email.com"}
        };
        for (String[] s : seed) {
            ContentValues v = new ContentValues();
            v.put(KEY_NOMBRE, s[0]); v.put(KEY_TELEFONO, s[1]); v.put(KEY_EMAIL, s[2]);
            db.insert(TABLE_CLIENTES, null, v);
        }
    }

    private double round2(double v) { return Math.round(v * 100.0) / 100.0; }

    public double getTotalPendiente() {
        return 0;
    }
}