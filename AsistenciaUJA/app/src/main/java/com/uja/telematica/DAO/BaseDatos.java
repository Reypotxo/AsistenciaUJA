package com.uja.telematica.DAO;

import android.content.Context;
import android.os.AsyncTask;

import com.uja.telematica.BO.AsyncTasker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Alfonso Troyano on 16/05/2015.
 */
public class BaseDatos implements Serializable {

    public Hashtable<String, Asignatura> asignaturas;
    public Hashtable<String, Alumno> alumnos;
    public Hashtable<String, ArrayList<Alumno>> alumnosAsignatura;
    public Hashtable<String, GrupoPracticas> gruposPracticas;
    public Hashtable<String, SesionPracticas> sesionesPracticas;
    //public ArrayList<AlumnoAsignaturaGrupo> alumnosAsignaturaGrupo;
    public Hashtable<String, AlumnoAsignaturaGrupo> alumnosAsignaturaGrupo;
    //public Hashtable<String, ArrayList<Integer>> sesionesGrupo;
    public Hashtable<String, String> datosIllias;

    private Context context;
    BaseDatosHelper baseDatosHelper;

    /**
     * Inicializa una instancia de la clase.
     */
    public BaseDatos()
    {
        context = null;
        baseDatosHelper = null;

        asignaturas = new Hashtable<String, Asignatura>();
        alumnos = new Hashtable<String, Alumno>();
        alumnosAsignatura = new Hashtable<String, ArrayList<Alumno>>();
        alumnosAsignaturaGrupo = new Hashtable<String, AlumnoAsignaturaGrupo>();
        gruposPracticas = new Hashtable<String, GrupoPracticas>();
        sesionesPracticas = new Hashtable<String, SesionPracticas>();
        datosIllias = new Hashtable<String, String>();
        //sesionesGrupo = new Hashtable<String, ArrayList<Integer>>();
    }

    public BaseDatos(Context context)
    {
        this();
        this.context = context;
    }
    /**
     * Inicializa/Actualiza la base de datos a partir de los datos de ilias
     * @param ilias Instancia de ilias con login hecho
     * @return Resultado de la ejecucion y la base de datos cargada
     */
    public BaseDatos(Ilias ilias, Context context)
    {
        this(context);

        try
        {
            //Obtiene todas las asignaturas con un fichero alumnos.csv
            AsyncTasker asyncTaskerGetAsignaturas = new AsyncTasker(ilias);
            AsyncTask<String, Float, GenericTypes.ExecutionResult> resultGetAsignaturasTask = asyncTaskerGetAsignaturas.execute(Ilias.GET_ASIGNATURAS);
            //Toast.makeText(this, resultGetAsignaturasTask.get().executionMessage, Toast.LENGTH_LONG).show();

            if(resultGetAsignaturasTask.get().executionResult == GenericTypes.EXECUTION_RESULT.OK)
            {
                String[] asignaturaInfos = resultGetAsignaturasTask.get().executionMessage.split(";");
                for(String asignaturaInfo : asignaturaInfos)
                {
                    //[Ref_id fichero, Ref_id dir asistencia asignatura, Ref_id curso, nombre curso]
                    String[] asignaturaData = asignaturaInfo.split(",");
                    String ficheroRef = asignaturaData[0];
                    String asistenciaRef = asignaturaData[1];
                    String cursoRef = asignaturaData[2];
                    String cursoId = asignaturaData[3];

                    Asignatura asignatura = new Asignatura(cursoRef, cursoId, "", asistenciaRef);
                    asignaturas.put(asignatura.getIliasId(), asignatura);
                    //alumnosAsignatura.put(asignatura.getIliasId(), new ArrayList<Alumno>());

                    AsyncTasker asyncTaskerGetFichero = new AsyncTasker(ilias);
                    AsyncTask<String, Float, GenericTypes.ExecutionResult> resultGetFicheroTask = asyncTaskerGetFichero.execute(Ilias.GET_FICHERO, ficheroRef);
                    if(resultGetFicheroTask.get().executionResult == GenericTypes.EXECUTION_RESULT.OK)
                    {
                        String fichero = resultGetFicheroTask.get().executionMessage;
                        CsvFile csvFile = new CsvFile(fichero);
                        String lineaAlumno = csvFile.GetLineaAlumno();
                        while(lineaAlumno != GenericTypes.UNDEFINED_STRING)
                        {
                            Alumno alumno = new Alumno(lineaAlumno);
                            alumno.setIdAlumno((new Integer(alumnos.size())).toString());
                            alumnos.put(alumno.getIdAlumno(), alumno);
                            //alumnosAsignatura.get(asignatura.getIliasId()).add(alumno);
                            AlumnoAsignaturaGrupo alumnoAsignaturaGrupo = new AlumnoAsignaturaGrupo();
                            alumnoAsignaturaGrupo.setIdAlumno(Integer.parseInt(alumno.getIdAlumno()));
                            alumnoAsignaturaGrupo.setIdAsignatura(Integer.parseInt(asignatura.getIliasId()));
                            alumnosAsignaturaGrupo.put(new Integer(alumnoAsignaturaGrupo.getIdAlumno()).toString(), alumnoAsignaturaGrupo);
                            lineaAlumno = csvFile.GetLineaAlumno(); //Obtiene el siguiente alumno del csv
                        }
                    }
                }

            }
        }
        catch (Exception e)
        {
            //do nothing
        }
    }

    /**
     * Abre la base de datos en el directorio predeterminado por Android. Si no existe, la crea.
     * @return True si la base de datos se creo correctamente. False en caso contrario
     */
    public boolean AbrirBBDD()
    {
        boolean res = true;

        baseDatosHelper = new BaseDatosHelper(context, "AsistenciaUJA", null, 1);
        baseDatosHelper.getWritableDatabase();

        return  res;
    }

    public boolean SalvarBBDD()
    {
        boolean res = true;

        for(Alumno alumno : alumnos.values())
        {
            if(baseDatosHelper.ActualizaAlumno(alumno) == 0)
            {
                //No existe alumno, hay que crearlo
                baseDatosHelper.CrearAlumno(alumno);
            }
        }

        for(Asignatura asignatura : asignaturas.values())
        {
            if(baseDatosHelper.ActualizaAsignatura(asignatura) == 0)
            {
                baseDatosHelper.CrearAsignatura(asignatura);
            }
        }

        for(GrupoPracticas grupoPracticas : gruposPracticas.values())
        {
            if(baseDatosHelper.ActualizaGrupoPracticas(grupoPracticas) == 0)
            {
                baseDatosHelper.CrearGrupoPracticas(grupoPracticas);
            }
        }

        for(SesionPracticas sesionPracticas : sesionesPracticas.values())
        {
            if(baseDatosHelper.ActualizaSesionPracticas(sesionPracticas) == 0)
            {
                baseDatosHelper.CrearSesionPracticas(sesionPracticas);
            }
        }

        for(AlumnoAsignaturaGrupo alumnoAsignaturaGrupo : alumnosAsignaturaGrupo.values())
        {
            if(baseDatosHelper.ActualizaAlumnoAsignaturaGrupo(alumnoAsignaturaGrupo) == 0)
            {
                baseDatosHelper.CrearAlumnoAsignaturaGrupo(alumnoAsignaturaGrupo);
            }
        }

        return res;
    }

    public boolean CargarBBDD()
    {
        boolean res = true;

        alumnos = baseDatosHelper.GetAlumnos();
        alumnosAsignaturaGrupo = baseDatosHelper.GetAlumnosAsignaturaGrupo();
        asignaturas = baseDatosHelper.GetAsignaturas();
        //datosIllias = baseDatosHelper.GetDatosIllias();
        //gruposPracticas = baseDatosHelper.GetGruposPracticas();

        return res;
    }

    public boolean CrearGrupoPracticas(GrupoPracticas grupoPracticas)
    {
        boolean res = true;

        try
        {
            baseDatosHelper.CrearGrupoPracticas(grupoPracticas);
            //gruposPracticas.AddGrupoPracticas(grupoPracticas);
            gruposPracticas.put(new Integer(grupoPracticas.getGrupoId()).toString(), grupoPracticas);
        }
        catch (Exception e)
        {

        }


        return res;
    }

    public boolean CrearSesionPracticas(SesionPracticas sesionPracticas)
    {
        boolean res = true;

        try
        {
            baseDatosHelper.CrearSesionPracticas(sesionPracticas);

            sesionesPracticas.put(new Integer(sesionPracticas.getIdSesion()).toString(), sesionPracticas);
        }
        catch (Exception e)
        {

        }


        return res;
    }

    public boolean ActualizaSesionPracticas(SesionPracticas sesionPracticas)
    {
        boolean res = true;

        try
        {
            baseDatosHelper.ActualizaSesionPracticas(sesionPracticas);

            sesionesPracticas.remove(new Integer(sesionPracticas.getIdSesion()).toString());

            sesionesPracticas.put(new Integer(sesionPracticas.getIdSesion()).toString(), sesionPracticas);
        }
        catch (Exception e)
        {

        }

        return res;
    }

    /**
     * Obtiene una lista de los nombres de las asignaturas
     * @return Lista con los nombres de las asignaturas
     */
    public ArrayList<String> GetListaAsignaturas()
    {
        ArrayList<String> res = new ArrayList<String>();

        if(asignaturas != null) {
            for (Asignatura asignatura : asignaturas.values()) {
                res.add(asignatura.getId());
            }
        }

        return res;
    }

    public Asignatura GetAsignaturaSeleccionada(String nombreAsignatura)
    {
        Asignatura res = null;

        if(asignaturas != null) {
            for (Asignatura asignatura : asignaturas.values()) {
                if(asignatura.getId() == nombreAsignatura)
                {
                    res = asignatura;
                    break;
                }
            }
        }

        return res;
    }

    public void CargarGruposPracticas()
    {
        gruposPracticas = new Hashtable<String, GrupoPracticas>();
        gruposPracticas = baseDatosHelper.GetGruposPracticas();
    }

    public void CargarSesionesPracticasGrupo(int idGrupo)
    {
        sesionesPracticas = new Hashtable<String, SesionPracticas>();
        sesionesPracticas = baseDatosHelper.GetSesionesPracticasGrupo(idGrupo);
    }

    /**
     * Calcula los grupos de practicas asociados a una asignatura
     * @param asignaturaId identificador numerico de la asignatura
     * @return Grupos de practicas recuperados, asociados a la asignatura
     */
    public ArrayList<Integer> GetGruposAsignatura(int asignaturaId)
    {
        ArrayList<Integer> res = new ArrayList<Integer>();

        for(GrupoPracticas grupoPracticas : gruposPracticas.values())
        {
            if(grupoPracticas.getIdAsignatura() == asignaturaId)
            {
                res.add(new Integer(grupoPracticas.getGrupoId()));
            }
        }

        return res;
    }

    public ArrayList<Integer> GetAlumnosGrupo(int grupoId)
    {
        ArrayList<Integer> res = new ArrayList<>();
        for(AlumnoAsignaturaGrupo alumnoAsignaturaGrupo : alumnosAsignaturaGrupo.values())
        {
            if(alumnoAsignaturaGrupo.getIdGrupoPracticas() == grupoId)
            {
                res.add(alumnoAsignaturaGrupo.getIdAlumno());
            }
        }

        return res;
    }

    public AlumnoAsignaturaGrupo GetAlumnoAsignaturaGrupo(int idAsignatura, int idAlumno)
    {
        AlumnoAsignaturaGrupo res = null;
        for(AlumnoAsignaturaGrupo alumnoAsignaturaGrupo : alumnosAsignaturaGrupo.values())
        {
            if(idAlumno == alumnoAsignaturaGrupo.getIdAlumno() && idAsignatura == alumnoAsignaturaGrupo.getIdAsignatura())
            {
                res = alumnoAsignaturaGrupo;
            }
        }

        return res;
    }

    public void ActualizaGrupoPracticasAlumnoAsignatura(AlumnoAsignaturaGrupo alumnoAsignaturaGrupo)
    {
        baseDatosHelper.ActualizaAlumnoAsignaturaGrupo(alumnoAsignaturaGrupo);
    }

    public int GetGrupoAlumnoAsignatura(int idAlumno, int idAsignatura)
    {
        int grupoId = GenericTypes.UNDEFINED_NUM;
        for(AlumnoAsignaturaGrupo alumnoAsignaturaGrupo : alumnosAsignaturaGrupo.values())
        {
            if(alumnoAsignaturaGrupo.getIdAsignatura() == idAsignatura && alumnoAsignaturaGrupo.getIdAlumno() == idAlumno)
            {
                grupoId = alumnoAsignaturaGrupo.getIdGrupoPracticas();
                break;
            }
        }

        return grupoId;
    }

    public boolean ActualizaGrupoPracticas(GrupoPracticas grupoPracticas)
    {
        boolean res = true;

        try
        {
            baseDatosHelper.ActualizaGrupoPracticas(grupoPracticas);

            gruposPracticas.remove(new Integer(grupoPracticas.getGrupoId()).toString());

            gruposPracticas.put(new Integer(grupoPracticas.getGrupoId()).toString(), grupoPracticas);
        }
        catch (Exception e)
        {
            res = false;
        }

        return res;
    }

    public void CargarDatosIlliasAsignatura(String idAsignatura)
    {
        datosIllias = new Hashtable<String, String>();
        datosIllias = baseDatosHelper.CargarDatosIlliasAsignatura(idAsignatura);
    }

    public boolean CrearDatoIliasAsignatura(String idAsignatura, String nombre, String idIllias)
    {
        boolean res = true;

        try
        {
            baseDatosHelper.CrearDatoIliasAsignatura(idAsignatura, nombre, idIllias);
        }
        catch (Exception e)
        {
            res = false;
        }

        return res;
    }

    public boolean BorrarSesionPracticas(SesionPracticas sesionPracticas)
    {
        boolean res = true;

        try
        {
            baseDatosHelper.BorraSesionPracticas(sesionPracticas);
            sesionesPracticas.remove(Integer.toString(sesionPracticas.getIdSesion()));
        }
        catch (Exception e)
        {
            res = false;
        }

        return res;
    }

    public boolean BorrarGrupoPracticas(GrupoPracticas grupoPracticas)
    {
        boolean res = true;

        try
        {
            baseDatosHelper.BorraGrupoPracticas(grupoPracticas);
            gruposPracticas.remove(Integer.toString(grupoPracticas.getGrupoId()));
        }
        catch (Exception e)
        {
            res = false;
        }

        return res;
    }

    public int GetMaxSesionPracticas()
    {
        return baseDatosHelper.GetMaxSesionPracticas();
    }
}
