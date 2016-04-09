package com.uja.telematica.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

/**
 * Created by Alfonso Troyano on 20/06/2015.
 * Proporciona metodos de asistencia para escribir en la Base de datos
 */
public class BaseDatosHelper extends SQLiteOpenHelper implements Serializable {

    //Anadir asignatura a la tabla de grupos para saber a que asignatura pertenece cada grupo como clave foranea

    private final String crearTablaAlumnos = "CREATE TABLE Alumnos (\n" +
            "    idAlumno INTEGER PRIMARY KEY\n" +
            "                     UNIQUE,\n" +
            "    dni      STRING,\n" +
            "    nombre   STRING,\n" +
            "    ape1     STRING,\n" +
            "    ape2     STRING\n" +
            ");";

    private final String crearTablaGrupoPracticas = "CREATE TABLE GrupoPracticas (\n" +
            "    idGrupo      INTEGER PRIMARY KEY\n" +
            "                         UNIQUE,\n" +
            "    nombre       STRING,\n" +
            "    descripcion  STRING,\n" +
            "    editable     BOOLEAN,\n" +
            "    borrado      BOOLEAN,\n" +
            "    idAsignatura INTEGER REFERENCES Asignaturas (idAsignatura) ON DELETE CASCADE\n" +
            "                                                               ON UPDATE CASCADE,\n" +
            "    dia          STRING,\n" +
            "    hora         STRING\n" +
            ");";

    private final String crearTablaAsignaturas = "CREATE TABLE Asignaturas (\n" +
            "    idAsignatura  INTEGER PRIMARY KEY\n" +
            "                          UNIQUE,\n" +
            "    nombre        STRING,\n" +
            "    refAsistencia STRING,\n" +
            "    rutaFicheros  STRING\n" +
            ");";

    private final String crearTablaSesionPracticas = "CREATE TABLE SesionPracticas (\n" +
            "    idSesion               INTEGER,\n" +
            "    idGrupo                INTEGER REFERENCES GrupoPracticas (idGrupo) ON DELETE CASCADE,\n" +
            "    fechaCreacion          STRING,\n" +
            "    fechaSesion            STRING,\n" +
            "    horaSesion             STRING,\n" +
            "    rutaLocalFicheroSesion STRING,\n" +
            "    idIlliasFicheroSesion  STRING,\n" +
            "    estado                 STRING,\n" +
            "    duracion               INTEGER,\n" +
            "    PRIMARY KEY (\n" +
            "        idSesion,\n" +
            "        idGrupo\n" +
            "    )\n" +
            ");";

    private final String crearTablaAlumnoAsignaturaGrupo = "CREATE TABLE AlumnoAsignaturaGrupo (\n" +
            "    idAlumno     INTEGER REFERENCES Alumnos (idAlumno) ON DELETE CASCADE,\n" +
            "    idAsignatura INTEGER REFERENCES Asignaturas (idAsignatura) ON DELETE CASCADE,\n" +
            "    idGrupo      INTEGER REFERENCES GrupoPracticas (idGrupo) ON DELETE CASCADE\n" +
            ");";

    private final String crearTablaIllias = "CREATE TABLE Illias (\n" +
            "    Nombre       STRING,\n" +
            "    idIllias     STRING,\n" +
            "    idAsignatura STRING REFERENCES Asignaturas (idAsignatura) ON DELETE CASCADE,\n" +
            "    PRIMARY KEY (\n" +
            "        Nombre,\n" +
            "        idAsignatura\n" +
            "    )\n" +
            ");";
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "AsistenciaUJA";

    public BaseDatosHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(crearTablaAlumnoAsignaturaGrupo);
        db.execSQL(crearTablaAlumnos);
        db.execSQL(crearTablaAsignaturas);
        db.execSQL(crearTablaGrupoPracticas);
        db.execSQL(crearTablaSesionPracticas);
        db.execSQL(crearTablaIllias);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS Alumnos");
        db.execSQL("DROP TABLE IF EXISTS AlumnoAsignaturaGrupo");
        db.execSQL("DROP TABLE IF EXISTS Asignaturas");
        db.execSQL("DROP TABLE IF EXISTS GrupoPracticas");
        db.execSQL("DROP TABLE IF EXISTS SesionPracticas");
        db.execSQL("DROP TABLE IF EXISTS Illias");

        // create new tables
        onCreate(db);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * Crear alumno en la BBDD
     * @param alumno Alumno que sera creado en la BBDD
     */
    public void CrearAlumno(Alumno alumno) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idAlumno", alumno.getIdAlumno());
        values.put("dni", alumno.getDni());
        values.put("nombre", alumno.getNombre());
        values.put("ape1", alumno.getApe1());
        values.put("ape2", alumno.getApe2());

        db.insert("Alumnos", null, values);
        db.close();
    }

    /**
     * Recupera un alumno de la BBDD
     * @param idAlumno id del alumno a recuperar
     * @return Alumno recuperado
     */
    public Alumno GetAlumno(String idAlumno) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM Alumnos WHERE idAlumno = " + idAlumno;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Alumno alumno = new Alumno();
        alumno.setIdAlumno(idAlumno);
        alumno.setDni(c.getString(c.getColumnIndex("dni")));
        alumno.setNombre(c.getString(c.getColumnIndex("nombre")));
        alumno.setApe1(c.getString(c.getColumnIndex("ape1")));
        alumno.setApe2(c.getString(c.getColumnIndex("ape2")));

        db.close();

        return alumno;
    }

    /**
     * Recupera todos los alumnos de la BBDD
     * @return Alumnos recuperados
     */
    public Hashtable<String, Alumno> GetAlumnos() {
        Hashtable<String, Alumno> res = new Hashtable<String, Alumno>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT idAlumno FROM Alumnos";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            for(int i = 0 ; i < c.getCount() ; i++)
            {
                c.moveToPosition(i);
                String idAlumno = c.getString(c.getColumnIndex("idAlumno"));
                Alumno alumno = GetAlumno(idAlumno);
                res.put(idAlumno, alumno);
            }
        }

        db.close();

        return res;
    }

    /**
     * Actualiza alumno en la BBDD
     * @param alumno Alumno que sera actualizado en la BBDD
     * @return total de filas actualizada
     */
    public int ActualizaAlumno(Alumno alumno) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("dni", alumno.getDni());
        values.put("nombre", alumno.getNombre());
        values.put("ape1", alumno.getApe1());
        values.put("ape2", alumno.getApe2());

        int res = db.update("Alumnos", values, "idAlumno=" + alumno.getIdAlumno(), null);

        db.close();

        return res;
    }

    /**
     * Borra alumno de la BBDD
     * @param alumno Alumno que sera borrado en la BBDD
     * @return total de filas borradas
     */
    public int BorraAlumno(Alumno alumno) {
        SQLiteDatabase db = this.getWritableDatabase();

        int res = db.delete("Alumnos", "idAlumno=" + alumno.getIdAlumno(), null);

        db.close();

        return res;
    }

    /**
     * Crear asignatura en la BBDD
     * @param asignatura Asignatura que sera creado en la BBDD
     */
    public void CrearAsignatura(Asignatura asignatura) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idAsignatura", asignatura.getIliasId());
        values.put("nombre", asignatura.getId());
        values.put("refAsistencia", asignatura.getRefAsistenciaServidor());
        values.put("rutaFicheros", asignatura.getRutaFicherosLocal());

        db.insert("Asignaturas", null, values);
        db.close();
    }

    /**
     * Recupera una asignatura de la BBDD
     * @param idAsignatura id de la asignatura a recuperar
     * @return Asignatura recuperada
     */
    public Asignatura GetAsignatura(String idAsignatura) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM Asignaturas WHERE idAsignatura = " + idAsignatura;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Asignatura asignatura = new Asignatura();
        asignatura.setIliasId(idAsignatura);
        asignatura.setId(c.getString(c.getColumnIndex("nombre")));
        asignatura.setRefAsistenciaServidor(c.getString(c.getColumnIndex("refAsistencia")));
        asignatura.setRutaFicherosLocal(c.getString(c.getColumnIndex("rutaFicheros")));

        db.close();
        return asignatura;
    }

    /**
     * Recupera todas las asignaturas de la BBDD
     * @return Asignaturas recuperados
     */
    public Hashtable<String, Asignatura> GetAsignaturas() {
        Hashtable<String, Asignatura> res = new Hashtable<String, Asignatura>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT idAsignatura FROM Asignaturas";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            for(int i = 0 ; i < c.getCount() ; i++)
            {
                c.moveToPosition(i);
                String idAsignatura = c.getString(c.getColumnIndex("idAsignatura"));
                Asignatura asignatura = GetAsignatura(idAsignatura);
                res.put(idAsignatura, asignatura);
            }
        }

        db.close();

        return res;
    }

    /**
     * Actualiza asignatura en la BBDD
     * @param asignatura Asignatura que sera actualizado en la BBDD
     * @return total de filas actualizada
     */
    public int ActualizaAsignatura(Asignatura asignatura) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", asignatura.getId());
        values.put("refAsistencia", asignatura.getRefAsistenciaServidor());
        values.put("rutaFicheros", asignatura.getRutaFicherosLocal());

        int res = db.update("Asignaturas", values, "idAsignatura=" + asignatura.getIliasId(), null);

        db.close();
        return res;
    }

    /**
     * Borra asignatura de la BBDD
     * @param asignatura Asignatura que sera borrado en la BBDD
     * @return total de filas borradas
     */
    public int BorraAsignatura(Asignatura asignatura) {
        SQLiteDatabase db = this.getWritableDatabase();

        int res = db.delete("Asignaturas", "idAsignatura=" + asignatura.getIliasId(), null);
        db.close();

        return res;
    }

    /**
     * Crear grupoPracticas en la BBDD
     * @param grupoPracticas GrupoPracticas que sera creado en la BBDD
     */
    public void CrearGrupoPracticas(GrupoPracticas grupoPracticas) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idGrupo", grupoPracticas.getGrupoId());
        values.put("nombre", grupoPracticas.getId());
        values.put("descripcion", grupoPracticas.getDescripcion());
        values.put("editable", grupoPracticas.isEditable());
        values.put("borrado", grupoPracticas.isBorrado());
        values.put("idAsignatura", grupoPracticas.getIdAsignatura());
        values.put("dia", grupoPracticas.getDia());
        values.put("hora", grupoPracticas.getHora());

        db.insert("GrupoPracticas", null, values);

        db.close();
    }

    /**
     * Recupera una grupoPracticas de la BBDD
     * @param idGrupoPracticas id de la grupoPracticas a recuperar
     * @return GrupoPracticas recuperada
     */
    public GrupoPracticas GetGrupoPracticas(String idGrupoPracticas) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM GrupoPracticas WHERE idGrupo = " + idGrupoPracticas;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        GrupoPracticas grupoPracticas = new GrupoPracticas();
        grupoPracticas.setGrupoId(Integer.parseInt(idGrupoPracticas));
        grupoPracticas.setId(c.getString(c.getColumnIndex("nombre")));
        grupoPracticas.setDescripcion(c.getString(c.getColumnIndex("descripcion")));
        grupoPracticas.setEditable(c.getInt(c.getColumnIndex("editable")) > 0);
        grupoPracticas.setBorrado(c.getInt(c.getColumnIndex("borrado")) > 0);
        grupoPracticas.setIdAsignatura(Integer.parseInt(c.getString(c.getColumnIndex("idAsignatura"))));
        grupoPracticas.setDia(c.getString(c.getColumnIndex("dia")));
        grupoPracticas.setHora(c.getString(c.getColumnIndex("hora")));

        db.close();

        return grupoPracticas;
    }

    /**
     * Recupera todos los grupos de practicas de la BBDD
     * @return Grupos de practicas recuperados
     */
    public Hashtable<String, GrupoPracticas> GetGruposPracticas() {
        Hashtable<String, GrupoPracticas> res = new Hashtable<String, GrupoPracticas>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT idGrupo FROM GrupoPracticas";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            for(int i = 0 ; i < c.getCount() ; i++)
            {
                c.moveToPosition(i);
                String idGrupo = c.getString(c.getColumnIndex("idGrupo"));
                GrupoPracticas grupoPracticas = GetGrupoPracticas(idGrupo);
                res.put(idGrupo, grupoPracticas);
            }
        }

        db.close();

        return res;
    }

    /**
     * Actualiza grupoPracticas en la BBDD
     * @param grupoPracticas GrupoPracticas que sera actualizado en la BBDD
     * @return total de filas actualizada
     */
    public int ActualizaGrupoPracticas(GrupoPracticas grupoPracticas) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", grupoPracticas.getId());
        values.put("descripcion", grupoPracticas.getDescripcion());
        values.put("editable", grupoPracticas.isEditable());
        values.put("borrado", grupoPracticas.isBorrado());
        values.put("idAsignatura", grupoPracticas.getIdAsignatura());
        values.put("dia", grupoPracticas.getDia());
        values.put("hora", grupoPracticas.getHora());
        int res = db.update("GrupoPracticas", values, "idGrupo=" + grupoPracticas.getGrupoId(), null);

        db.close();

        return res;
    }

    /**
     * Borra grupoPracticas de la BBDD
     * @param grupoPracticas GrupoPracticas que sera borrado en la BBDD
     * @return total de filas borradas
     */
    public int BorraGrupoPracticas(GrupoPracticas grupoPracticas) {
        SQLiteDatabase db = this.getWritableDatabase();

        int res = db.delete("GrupoPracticas", "idGrupo=" + grupoPracticas.getGrupoId(), null);

        db.close();

        return res;
    }

    public int GetMaxSesionPracticas()
    {
        int res = GenericTypes.UNDEFINED_NUM;

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT Count(idSesion) as COUNT FROM SesionPracticas";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        res = c.getInt(c.getColumnIndex("COUNT"));

        db.close();

        return res;
    }

    /**
     * Crear sesionPracticas en la BBDD
     * @param sesionPracticas SesionPracticas que sera creado en la BBDD
     */
    public void CrearSesionPracticas(SesionPracticas sesionPracticas) {
        SQLiteDatabase db = this.getWritableDatabase();

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = df.format(c.getTime());

        ContentValues values = new ContentValues();
        values.put("idSesion", sesionPracticas.getIdSesion());
        values.put("idGrupo", sesionPracticas.getIdGrupo());
        values.put("fechaCreacion", formattedDate);
        values.put("fechaSesion", sesionPracticas.getFechaSesion());
        values.put("horaSesion", sesionPracticas.getHoraSesion());
        values.put("rutaLocalFicheroSesion", sesionPracticas.getRutaFicheroSesionLocal());
        values.put("idIlliasFicheroSesion", sesionPracticas.getIdIlliasFicheroSesion());
        values.put("estado", sesionPracticas.getEstado().toString());
        values.put("duracion", Integer.toString(sesionPracticas.getDuracion()));

        db.insert("SesionPracticas", null, values);

        db.close();
    }

    public Hashtable<String, SesionPracticas> GetSesionesPracticasGrupo(int idGrupo)
    {
        Hashtable<String, SesionPracticas> res = new Hashtable<String, SesionPracticas>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT idSesion FROM SesionPracticas WHERE idGrupo = " + idGrupo;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            for(int i = 0 ; i < c.getCount() ; i++)
            {
                c.moveToPosition(i);
                String idSesion = c.getString(c.getColumnIndex("idSesion"));
                SesionPracticas sesionPracticas = getSesionPracticas(idSesion, new Integer(idGrupo).toString());
                res.put(idSesion, sesionPracticas);
            }
        }

        db.close();

        return res;
    }

    /**
     * Recupera una sesionPracticas de la BBDD
     * @param idSesionPracticas id de la sesionPracticas a recuperar
     * @return SesionPracticas recuperada
     */
    public SesionPracticas getSesionPracticas(String idSesionPracticas, String idGrupo) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM SesionPracticas WHERE idSesion = " + idSesionPracticas + " AND idGrupo = " + idGrupo+ " AND idGrupo = " + idGrupo;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        SesionPracticas sesionPracticas = new SesionPracticas();
        sesionPracticas.setIdSesion(Integer.parseInt(idSesionPracticas));
        sesionPracticas.setIdGrupo(Integer.parseInt(c.getString(c.getColumnIndex("idGrupo"))));
        sesionPracticas.setFechaCreacion(c.getString(c.getColumnIndex("fechaCreacion")));
        sesionPracticas.setFechaSesion(c.getString(c.getColumnIndex("fechaSesion")));
        sesionPracticas.setHoraSesion(c.getString(c.getColumnIndex("horaSesion")));
        sesionPracticas.setRutaFicheroSesionLocal(c.getString(c.getColumnIndex("rutaLocalFicheroSesion")));
        sesionPracticas.setIdIlliasFicheroSesion(c.getString(c.getColumnIndex("idIlliasFicheroSesion")));
        sesionPracticas.setEstado(SesionPracticas.ESTADO.valueOf(c.getString(c.getColumnIndex("estado"))));
        sesionPracticas.setDuracion(c.getInt(c.getColumnIndex("duracion")));

        db.close();

        return sesionPracticas;
    }

    /**
     * Actualiza sesionPracticas en la BBDD
     * @param sesionPracticas SesionPracticas que sera actualizado en la BBDD
     * @return total de filas actualizada
     */
    public int ActualizaSesionPracticas(SesionPracticas sesionPracticas) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idGrupo", sesionPracticas.getIdGrupo());
        values.put("fechaCreacion", sesionPracticas.getFechaCreacion());
        values.put("fechaSesion", sesionPracticas.getFechaSesion());
        values.put("horaSesion", sesionPracticas.getHoraSesion());
        values.put("rutaLocalFicheroSesion", sesionPracticas.getRutaFicheroSesionLocal());
        values.put("idIlliasFicheroSesion", sesionPracticas.getIdIlliasFicheroSesion());
        values.put("estado", sesionPracticas.getEstado().toString());
        values.put("duracion", Integer.toString(sesionPracticas.getDuracion()));

        int res = db.update("SesionPracticas", values, "idSesion=" + sesionPracticas.getIdSesion()+ " AND idGrupo = " + sesionPracticas.getIdGrupo(), null);

        db.close();

        return res;
    }

    /**
     * Borra sesionPracticas de la BBDD
     * @param sesionPracticas SesionPracticas que sera borrado en la BBDD
     * @return total de filas borradas
     */
    public int BorraSesionPracticas(SesionPracticas sesionPracticas) {
        SQLiteDatabase db = this.getWritableDatabase();

        int res = db.delete("SesionPracticas", "idSesion=" + sesionPracticas.getIdSesion(), null);

        db.close();

        return res;
    }

    /**
     * Crear alumnoAsignaturaGrupo en la BBDD
     * @param alumnoAsignaturaGrupo AlumnoAsignaturaGrupo que sera creado en la BBDD
     */
    public void CrearAlumnoAsignaturaGrupo(AlumnoAsignaturaGrupo alumnoAsignaturaGrupo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idAlumno", alumnoAsignaturaGrupo.getIdAlumno());
        values.put("idAsignatura", alumnoAsignaturaGrupo.getIdAsignatura());
        values.put("idGrupo", alumnoAsignaturaGrupo.getIdGrupoPracticas());

        db.insert("AlumnoAsignaturaGrupo", null, values);

        db.close();
    }

    /**
     * Recupera una alumnoAsignaturaGrupo de la BBDD
     * @param idAlumnoAsignaturaGrupo id de la alumnoAsignaturaGrupo a recuperar
     * @return AlumnoAsignaturaGrupo recuperada
     */
    public AlumnoAsignaturaGrupo GetAlumnoAsignaturaGrupo(String idAlumnoAsignaturaGrupo, String idAsignatura, String idGrupo) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM AlumnoAsignaturaGrupo WHERE idAlumno = "+ idAlumnoAsignaturaGrupo + " and idAsignatura = " + idAsignatura + " and idGrupo = " + idGrupo;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        AlumnoAsignaturaGrupo alumnoAsignaturaGrupo = new AlumnoAsignaturaGrupo(Integer.parseInt(idAsignatura), Integer.parseInt(idAlumnoAsignaturaGrupo), Integer.parseInt(idGrupo));

        db.close();

        return alumnoAsignaturaGrupo;
    }

    /**
     * Recupera todos los alumnos, asignaturas y grupos de practicas de la BBDD
     * @return alumnos, asignaturas y grupos de practicas recuperados
     */
    public Hashtable<String, AlumnoAsignaturaGrupo>  GetAlumnosAsignaturaGrupo() {
        Hashtable<String, AlumnoAsignaturaGrupo>  res = new Hashtable<String, AlumnoAsignaturaGrupo> ();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM AlumnoAsignaturaGrupo";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            for(int i = 0 ; i < c.getCount() ; i++)
            {
                c.moveToPosition(i);
                String idAlumno = c.getString(c.getColumnIndex("idAlumno"));
                String idAsignatura = c.getString(c.getColumnIndex("idAsignatura"));
                String idGrupo = c.getString(c.getColumnIndex("idGrupo"));
                AlumnoAsignaturaGrupo alumnoAsignaturaGrupo = GetAlumnoAsignaturaGrupo(idAlumno, idAsignatura, idGrupo);
                res.put(new Integer(alumnoAsignaturaGrupo.getIdAlumno()).toString(), alumnoAsignaturaGrupo);
            }
        }

        db.close();

        return res;
    }

    /**
     * Borra alumnoAsignaturaGrupo de la BBDD
     * @param alumnoAsignaturaGrupo AlumnoAsignaturaGrupo que sera borrado en la BBDD
     * @return total de filas borradas
     */
    public int BorraAlumnoAsignaturaGrupo(AlumnoAsignaturaGrupo alumnoAsignaturaGrupo) {
        SQLiteDatabase db = this.getWritableDatabase();

        int res = db.delete("AlumnoAsignaturaGrupo", "idAlumno=" + alumnoAsignaturaGrupo.getIdAlumno() + " and idAsignatura = " + alumnoAsignaturaGrupo.getIdAsignatura() + " and idGrupo = " + alumnoAsignaturaGrupo.getIdGrupoPracticas(), null);

        db.close();

        return res;
    }

    /**
     * Actualiza la terna AlumnoAsignaturaGrupo en la BBDD
     * @param alumnoAsignaturaGrupo AlumnoAsignaturaGrupo que sera actualizado en la BBDD
     * @return total de filas actualizada
     */
    public int ActualizaAlumnoAsignaturaGrupo(AlumnoAsignaturaGrupo alumnoAsignaturaGrupo)
    {
        int res = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idGrupo", alumnoAsignaturaGrupo.getIdGrupoPracticas());
        res = db.update("AlumnoAsignaturaGrupo", values, "idAlumno=" + alumnoAsignaturaGrupo.getIdAlumno() + " and idAsignatura=" + alumnoAsignaturaGrupo.getIdAsignatura(), null);

        db.close();

        return res;
    }

    public Hashtable<String, String> CargarDatosIlliasAsignatura(String idAsignatura)
    {
        Hashtable<String, String> res = new Hashtable<String, String>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM Illias WHERE idAsignatura = " + idAsignatura;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            c.moveToFirst();

            for(int i = 0 ; i < c.getCount() ; i++)
            {
                c.moveToPosition(i);
                String clave = c.getString(c.getColumnIndex("Nombre"));
                String valor = c.getString(c.getColumnIndex("idIllias"));
                res.put(clave, valor);
            }
        }

        db.close();

        return res;
    }

    /**
     * Actualiza la terna AlumnoAsignaturaGrupo en la BBDD
     * @param idAsignatura identificador de la asignatura
     * @param idIllias identificador en ilias
     * @param nombre nombre de la variable
     */
    public void CrearDatoIliasAsignatura(String idAsignatura, String nombre, String idIllias)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Nombre", nombre);
        values.put("idIllias", idIllias);
        values.put("idAsignatura", idAsignatura);

        db.insert("Illias", null, values);

        db.close();
    }
}
