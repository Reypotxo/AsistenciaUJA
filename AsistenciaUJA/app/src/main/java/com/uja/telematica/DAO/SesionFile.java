package com.uja.telematica.DAO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

/**
 * Created by Alfonso on 8/02/16.
 */
public class SesionFile {
    /**
     * Registros del fichero de sesion
     */
    public Hashtable<Integer, SesionRegister> registrosSesion;
    private String uri;

    /**
     * Inicializa una instancia de la clase
     */
    public SesionFile()
    {
        registrosSesion = new Hashtable<Integer, SesionRegister>();
        setUri(GenericTypes.UNDEFINED_STRING);
    }

    /**
     * Inicializa una instancia de la clase
     * @param uri URI de la que se leera el fichero de sesion
     */
    public SesionFile(String uri)
    {
        this();
        this.setUri(uri);
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(uri))));
            String line = bufferedReader.readLine();
            while(line != null )
            {
                    SesionRegister registro = new SesionRegister(line);
                    registrosSesion.put(new Integer(registro.getIdAlumno()), registro);
                    line = bufferedReader.readLine();
            }

            bufferedReader.close();
        }
        catch (IOException ioException)
        {
        }
    }

    /**
     * Crea un nuevo fichero para la sesion
     * @param baseDatos base de datos con informacion de la asignatura y grupo actual
     * @param grupoId Grupo para el que se crea el fichero
     * @param uri Nombre completo del fichero en el sistema de archivos
     * @param hora hora de la sesion
     * @param fecha fecha de la sesion
     * @param sesionId identificador de la sesion
     * @return True si la operacion se completo correctamente. False en caso contrario.
     */
    public static SesionFile CrearFicheroSesion(BaseDatos baseDatos, String grupoId, String uri, String fecha, String hora, String sesionId)
    {
        SesionFile res = new SesionFile();
        res.setUri(uri);
        Integer grupo = new Integer(grupoId);
        for (AlumnoAsignaturaGrupo alumnoAsignaturaGrupo : baseDatos.alumnosAsignaturaGrupo.values()) {
            if(alumnoAsignaturaGrupo.getIdGrupoPracticas() == grupo)
            {
                SesionRegister registroSesion = new SesionRegister(new Integer(alumnoAsignaturaGrupo.getIdAlumno()).toString(), false, fecha, hora, grupoId, sesionId, baseDatos.alumnos.get(new Integer(alumnoAsignaturaGrupo.getIdAlumno()).toString()).getDni(), grupoId);
                res.registrosSesion.put(new Integer(registroSesion.getIdAlumno()), registroSesion);
            }
        }

        res.EscribirFichero();

        return res;
    }

    /**
     * Escribe el fichero en su ruta
     * @return True si la operacion se completo. False en caso contrario.
     */
    public boolean EscribirFichero()
    {
        boolean res = true;

        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(getUri()));

            for (SesionRegister registroSesion :
                    registrosSesion.values()) {
                writer.write(registroSesion.toString());
                writer.newLine();
            }

            writer.flush();
            writer.close();
        }
        catch (IOException ioException)
        {
            res = false;
        }
        
        return res;
    }

    @Override
    public String toString()
    {
        String res = "";

        for (SesionRegister registroSesion :
                registrosSesion.values()) {
            res += (registroSesion.toString()) + "\n";
        }
        return res;
    }

    /**
     * Vacia el contenido de un fichero
     */
    public void Clear()
    {
        registrosSesion.clear();
        EscribirFichero();
    }

    /**
     * Ubicacion del fichero de sesion en el sistema
     */
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
