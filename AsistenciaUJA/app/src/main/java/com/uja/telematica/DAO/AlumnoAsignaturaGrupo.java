package com.uja.telematica.DAO;

import java.io.Serializable;

/**
 * Created by Alfonso Troyano on 13/06/2015.
 * Establece una relacion entre alumno y grupo de practicas
 */
public class AlumnoAsignaturaGrupo implements Serializable {
    /**
     * Identificador de la asignatura
     */
    public int idAsignatura;
    /**
     * Identificador de alumno
     */
    public int idAlumno;
    /**
     * Identificador del grupo de practicas
     */
    public int idGrupoPracticas;

    public int getIdAsignatura() {
        return idAsignatura;
    }

    public void setIdAsignatura(int idAsignatura) {
        this.idAsignatura = idAsignatura;
    }

    public int getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }

    public int getIdGrupoPracticas() {
        return idGrupoPracticas;
    }

    public void setIdGrupoPracticas(int idGrupoPracticas) {
        this.idGrupoPracticas = idGrupoPracticas;
    }

    /**
     * Inicializa una instancia de la clase
     */
    public AlumnoAsignaturaGrupo()
    {
        idAsignatura = GenericTypes.UNDEFINED_NUM;
        idAlumno = GenericTypes.UNDEFINED_NUM;
        idGrupoPracticas = GenericTypes.UNDEFINED_NUM;
    }

    /**
     * Inicializa una instancia de la clase
     * @param idAsignatura Identificador de la asignatura
     * @param idAlumno Identificador del alumno
     * @param idGrupoPracticas Identificador del grupo de practicas al que pertenece el alumno
     */
    public AlumnoAsignaturaGrupo(int idAsignatura, int idAlumno, int idGrupoPracticas)
    {
        this.idAsignatura = idAsignatura;
        this.idAlumno = idAlumno;
        this.idGrupoPracticas = idGrupoPracticas;
    }
}
