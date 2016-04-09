package com.uja.telematica.DAO;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Alfonso Troyano on 28/04/2015.
 * Contiene los datos básicos de una asignatura
 */
public class Asignatura implements Serializable {
    private String iliasId;
    private String id;
    private String rutaFicherosLocal;
    private String refAsistenciaServidor;
    private String notas;

    /**
     * Inicializa una instancia de la clase
     */
    Asignatura()
    {
        setIliasId(GenericTypes.UNDEFINED_STRING);
        setId(GenericTypes.UNDEFINED_STRING);
        setRutaFicherosLocal(GenericTypes.UNDEFINED_STRING);
        setRefAsistenciaServidor(GenericTypes.UNDEFINED_STRING);
    }

    /**
     * Inicializa una instancia de la clase
     * @param iliasId Identificador de la asignatura dentro de Ilias
     * @param id Nombre de la asignatura
     * @param rutaFicherosLocal Ruta de los ficheros de sesion dentro del equipo local
     * @param refAsistenciaServidor Ruta de los ficheros de sesión dentro de Ilias
     */
    Asignatura(String iliasId, String id, String rutaFicherosLocal, String refAsistenciaServidor)
    {
        this();
        this.setIliasId(iliasId);
        this.setId(id);
        this.setRutaFicherosLocal(rutaFicherosLocal);
        this.setRefAsistenciaServidor(refAsistenciaServidor);
    }

    /**
     * Identificador de la asignatura dentro de Ilias
     */
    public String getIliasId() {
        return iliasId;
    }

    public void setIliasId(String iliasId) {
        this.iliasId = iliasId;
    }

    /**
     * Nombre de la asignatura
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Ruta donde se guardaran los ficheros en local
     */
    public String getRutaFicherosLocal() {
        return rutaFicherosLocal;
    }

    public void setRutaFicherosLocal(String rutaFicherosLocal) {
        this.rutaFicherosLocal = rutaFicherosLocal;
    }

    /**
     * RefId donde se guardaran los ficheros en el servidor
     */
    public String getRefAsistenciaServidor() {
        return refAsistenciaServidor;
    }

    public void setRefAsistenciaServidor(String rutaFicherosServidor) {
        this.refAsistenciaServidor = rutaFicherosServidor;
    }

    /**
     * Notas asociadas al alumno
     */
    public String getNotas(){
        return notas;
    }

    public void setNotas(String notas)
    {
        this.notas = notas;
    }


    /**
     * Obtiene los ids de los alumnos de una asignatura
     * @param fileContent Contenido del fichero alumnos.csv de la asignatura
     * @return Lista con los ids de los alumnos de la asignatura
     */
    public static ArrayList<String> GetAlumnosFromFile(String fileContent)
    {
        ArrayList<String> alumnoIds = new ArrayList<>();
/*
        try
        {
            //BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent));
            try {
                String linea;
                while ((linea = reader.readLine()) != null) {
                }
            }
            catch (IOException ex) {
                // handle exception
            }
        }
        catch(Exception e)
        {

        }*/

        return alumnoIds;
    }
}
