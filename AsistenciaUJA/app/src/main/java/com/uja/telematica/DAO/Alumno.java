package com.uja.telematica.DAO;

import java.io.Serializable;

/**
 * Created by Alfonso Troyano on 13/06/2015.
 * Contiene los datos necesarios para identificar a un alumno
 */
public class Alumno implements Serializable {
    /**
     * Id del alumno en BBDD
     */
    public String idAlumno;
    /**
     * Nombre de alumno
     */
    public String nombre;
    /**
     * Primer apellido del alumno
     */
    public String ape1;

    public String getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(String idAlumno) {
        this.idAlumno = idAlumno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApe1() {
        return ape1;
    }

    public void setApe1(String ape1) {
        this.ape1 = ape1;
    }

    public String getApe2() {
        return ape2;
    }

    public void setApe2(String ape2) {
        this.ape2 = ape2;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    /**
     * Segundo apellido del alumno
     */

    public String ape2;
    /**
     * DNI del alumno
     */
    public String dni;
    /**
     * Informacion extra sobre el alumno.
     */
    public String notas;

    /**
     * Inicializa una instancia de la clase
     */
    public Alumno()
    {
        idAlumno = GenericTypes.UNDEFINED_STRING;
        nombre = GenericTypes.UNDEFINED_STRING;
        ape1 = GenericTypes.UNDEFINED_STRING;
        ape2 = GenericTypes.UNDEFINED_STRING;
        dni = GenericTypes.UNDEFINED_STRING;
        notas = GenericTypes.UNDEFINED_STRING;
    }

    /**
     * Inicializa una instancia de esta clase
     * @param nombre Nombre de alumno
     * @param ape1 Primer apellido del alumno
     * @param ape2 Segundo apellido del alumno
     * @param dni DNI del alumno
     */
    public Alumno(String nombre, String ape1, String ape2, String dni)
    {
        this();
        this.nombre = nombre;
        this.ape1 = ape1;
        this.ape2 = ape2;
        this.dni = dni;
    }

    /**
     * Inicializa una instancia de esta clase
     * @param idAlumno Id del alumno en BBDD
     * @param nombre Nombre de alumno
     * @param ape1 Primer apellido del alumno
     * @param ape2 Segundo apellido del alumno
     * @param dni DNI del alumno
     * @param notas Informacion extra sobre el alumno.
     */
    public Alumno(String idAlumno, String nombre, String ape1, String ape2, String dni, String notas)
    {
        this(nombre, ape1, ape2, dni);
        this.idAlumno = idAlumno;
        this.notas = notas;
    }

    /**
     * Inicializa una instancia de la clase
     * @param linea Contiene los datos de un alumno, en formato csv
     */
    public Alumno(String linea)
    {
        String [] datos = linea.split(";");

        this.nombre = datos[3].trim();
        this.ape1 = datos[2].trim();
        this.ape2 = datos[1].trim();
        this.dni = datos[0].substring(datos[0].indexOf("-") + 1).trim();
    }
}
