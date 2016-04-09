package com.uja.telematica.DAO;

/**
 * Created by Alfonso on 8/02/16.
 * Contiene un único registro del fichero de sesion
 */
public class SesionRegister {
    private String fecha;
    private String hora;
    private String grupoSesionId;
    private String sesionId;
    private String idAlumno;
    private boolean asistencia;
    private String alumnoDni;
    private String grupoAlumnoId;
    private String horaEntrada;
    private String horaSalida;

    public static String cabecera = "GrupoSesion;Sesion;Fecha;Hora;GrupoAlumno;IdAlumno;DniAlumno;Asistencia;HoraEntrada;HoraSalida\n";    //Columnas del fichero csv

    /**
     * Inicializa un registro de sesion
     */
    public SesionRegister()
    {
        setIdAlumno(GenericTypes.UNDEFINED_STRING);
        setAsistencia(false);
        setFecha(GenericTypes.UNDEFINED_STRING);
        setHora(GenericTypes.UNDEFINED_STRING);
        setGrupoId(GenericTypes.UNDEFINED_STRING);
        setSesionId(GenericTypes.UNDEFINED_STRING);
        setGrupoAlumnoId(GenericTypes.UNDEFINED_STRING);
        setHoraEntrada("");
        setHoraSalida("");
    }

    /**
     * Inicializa un registro de sesion
     * @param idAlumno Identificador del alumno
     * @param asistencia Indica si el alumno ha asistido
     * @param fecha fecha de la sesion
     * @param grupoSesionId grupo de la sesion
     * @param hora hora de la sesion
     * @param sesionId id de la sesion
     * @param alumnoDni dni del alumno
     * @param grupoAlumnoId Id del grupo de practicas al que pertenece el alumno
     */
    public SesionRegister(String idAlumno, boolean asistencia, String fecha, String hora, String grupoSesionId, String sesionId, String alumnoDni, String grupoAlumnoId)
    {
        this();
        this.setIdAlumno(idAlumno);
        this.setAsistencia(asistencia);
        this.setFecha(fecha);
        this.setHora(hora);
        this.setGrupoId(grupoSesionId);
        this.setSesionId(sesionId);
        this.setAlumnoDni(alumnoDni);
        this.setGrupoAlumnoId(grupoAlumnoId);
    }

    /**
     * Inicializa un registro de sesion
     * @param idAlumno Identificador del alumno
     * @param asistencia Indica si el alumno ha asistido
     * @param fecha fecha de la sesion
     * @param grupoSesionId grupo de la sesion
     * @param hora hora de la sesion
     * @param sesionId id de la sesion
     * @param alumnoDni dni del alumno
     * @param grupoAlumnoId Id del grupo de practicas al que pertenece el alumno
     * @param horaEntrada hora de entrada a la sesion
     * @param horaSalida hora de salida de la sesion
     */
    public SesionRegister(String idAlumno, boolean asistencia, String fecha, String hora, String grupoSesionId, String sesionId, String alumnoDni, String grupoAlumnoId, String horaEntrada, String horaSalida)
    {
        this(idAlumno, asistencia, fecha, hora, grupoSesionId, sesionId, alumnoDni, grupoAlumnoId);
        this.setHoraEntrada(horaEntrada);
        this.setHoraSalida(horaSalida);
    }

    /**
     * Inicializa un registro de sesion
     * @param sesionLine
     */
    public SesionRegister(String sesionLine)
    {
        this();
        if(sesionLine.contains(";"))
        {
            String[] sesionLineTokens = sesionLine.split(";");
            setGrupoId(sesionLineTokens[0]);
            setSesionId(sesionLineTokens[1]);
            setFecha(sesionLineTokens[2]);
            setHora(sesionLineTokens[3]);
            setGrupoAlumnoId(sesionLineTokens[4]);
            setIdAlumno(sesionLineTokens[5]);
            setAlumnoDni(sesionLineTokens[6]);
            setAsistencia(Boolean.parseBoolean(sesionLineTokens[7]));
            if(sesionLineTokens.length > 8)
            {
                setHoraEntrada(sesionLineTokens[8]);
            }
            if(sesionLineTokens.length > 9)
            {
                setHoraSalida(sesionLineTokens[9]);
            }
        }
    }

    @Override
    public String toString()
    {
        //La estructura del registro es: grupoId;sesionId;fechaSesion;horaSesion;idAlumno;Asistencia
        return getGrupoId() + ";" + getSesionId() + ";" + getFecha() + ";" + getHora() + ";" + getGrupoAlumnoId() + ";" + getIdAlumno() + ";" + getAlumnoDni() + ";" + isAsistencia() + ";" + getHoraEntrada() + ";" + getHoraSalida();
    }

    /**
     * Identificador del alumno
     */
    public String getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(String idAlumno) {
        this.idAlumno = idAlumno;
    }

    /**
     * Indica si el alumno ha asistido
     */
    public boolean isAsistencia() {
        return asistencia;
    }

    public void setAsistencia(boolean asistencia) {
        this.asistencia = asistencia;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getGrupoId() {
        return grupoSesionId;
    }

    public void setGrupoId(String grupoSesionId) {
        this.grupoSesionId = grupoSesionId;
    }

    public String getSesionId() {
        return sesionId;
    }

    public void setSesionId(String sesionId) {
        this.sesionId = sesionId;
    }

    /**
     * Dni del alumno
     */
    public String getAlumnoDni() {
        return alumnoDni;
    }

    public void setAlumnoDni(String alumnoDni) {
        this.alumnoDni = alumnoDni;
    }

    public String getGrupoAlumnoId() {
        return grupoAlumnoId;
    }

    public void setGrupoAlumnoId(String grupoAlumnoId) {
        this.grupoAlumnoId = grupoAlumnoId;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(String horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }
}
