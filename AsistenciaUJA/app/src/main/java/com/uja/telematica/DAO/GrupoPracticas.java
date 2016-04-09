package com.uja.telematica.DAO;

import java.io.Serializable;

/**
 * Created by Alfonso Troyano on 28/04/2015.
 * Identifica un grupo de prácticas.
 */
public class GrupoPracticas implements Serializable {
    private int grupoId;
    private String id;
    private String descripcion;
    private boolean editable;
    private boolean borrado;
    private String notas;
    private int idAsignatura;
    private String dia;
    private String hora;

    /**
     * Inicializa una instancia de la clase
     */
    public GrupoPracticas()
    {
        setGrupoId(GenericTypes.UNDEFINED_NUM);
        setId(GenericTypes.UNDEFINED_STRING);
        setDescripcion(GenericTypes.UNDEFINED_STRING);
        setEditable(true);
        setBorrado(false);
        setNotas(GenericTypes.UNDEFINED_STRING);
        setDia(GenericTypes.UNDEFINED_STRING);
        setHora(GenericTypes.UNDEFINED_STRING);
    }

    /**
     * Inicializa una instancia de la clase
     * @param grupoId Identificador del grupo en BBDD
     * @param id Identificador del grupo
     * @param descripcion Descripcion del grupo
     * @param editable Indica si el grupo es editable
     * @param borrado Indica si el grupo esta borrado
     * @param notas Notas asociadas al grupo
     * @param idAsignatura asignatura a la que pertenece el grupo
     * @param dia dia en que el grupo hace las practicas
     * @param hora hora a la que el grupo hace las practicas
     */
    public GrupoPracticas(int grupoId, String id, String descripcion, boolean editable, boolean borrado, String notas, int idAsignatura, String dia, String hora)
    {
        this.setGrupoId(grupoId);
        this.setId(id);
        this.setDescripcion(descripcion);
        this.setEditable(editable);
        this.setBorrado(borrado);
        this.setNotas(notas);
        this.setIdAsignatura(idAsignatura);
        this.setDia(dia);
        this.setHora(hora);
    }

    /**
     * Identificador del grupo
     */
    public int getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(int grupoId) {
        this.grupoId = grupoId;
    }

    /**
     * Nombre del grupo
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Descripcion del grupo
     */
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Indica si el grupo es editable.
     */
    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * Indica si el grupo ha sido borrado en base de datos
     */
    public boolean isBorrado() {
        return borrado;
    }

    public void setBorrado(boolean borrado) {
        this.borrado = borrado;
    }

    /**
     * Notas asociadas al grupo
     */
    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public int getIdAsignatura() {
        return idAsignatura;
    }

    public void setIdAsignatura(int idAsignatura) {
        this.idAsignatura = idAsignatura;
    }

    /**
     * Dia que el grupo hace practicas
     */
    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    /**
     * Hora a la que el grupo hace practicas
     */
    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
