package com.uja.telematica.DAO;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Alfonso Troyano on 13/06/2015.
 * Contiene la informacion de una sesion de practicas
 */
public class SesionPracticas implements Serializable {

    private SesionFile ficheroSesion;
    /**
     * Identificador en illias del fichero de sesion
     */
    public String getIdIlliasFicheroSesion() {
        return idIlliasFicheroSesion;
    }

    public void setIdIlliasFicheroSesion(String idIlliasFicheroSesion) {
        this.idIlliasFicheroSesion = idIlliasFicheroSesion;
    }

    public String getHoraSesion() {
        return horaSesion;
    }

    public void setHoraSesion(String horaSesion) {
        this.horaSesion = horaSesion;
    }

    /**
     * Fichero asociado a la sesion actual
     */
    public SesionFile getFicheroSesion() {
        return ficheroSesion;
    }

    public void setFicheroSesion(SesionFile ficheroSesion) {
        this.ficheroSesion = ficheroSesion;
    }

    /**
     * Duracion de la sesion
     */
    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    /**
     * Estado de la sesion
     */
    public enum ESTADO
    {
        /**
         * Sesion creada
         */
        CREADA,
        /**
         * Sesion abierta
         */
        ABIERTA,
        /**
         * Sesion cerrada
         */
        CERRADA,
        /**
         * Sesion subida a ilias
         */
        SUBIDA,
        /**
         * Estado indefinido
         */
        INDEFINIDA
    }

    /**
     * Id de la sesion
     */
    public int idSesion;
    /**
     * Id del grupo al que pertenece la sesion
     */
    public int idGrupo;
    /**
     * Fecha en la que se ha creado la sesion
     */
    public String fechaCreacion;
    /**
     * Fecha y hora en la que se establece la sesion como ABIERTA
     */
    public String fechaSesion;
    public String horaSesion;
    /**
     * Ruta de los ficheros de la sesion en el equipo local
     */
    public String rutaFicheroSesionLocal;
    private String idIlliasFicheroSesion;
    /**
     * Estado de la sesion
     */
    public ESTADO estado;
    /**
     * Notas asociadas a la sesion
     */
    public String notas;
    private int duracion;

    /**
     * Inicializa una instancia de la clase
     */
    public SesionPracticas()
    {
        idSesion = GenericTypes.UNDEFINED_NUM;
        idGrupo = GenericTypes.UNDEFINED_NUM;
        fechaCreacion = GenericTypes.UNDEFINED_STRING;
        fechaSesion = GenericTypes.UNDEFINED_STRING;
        rutaFicheroSesionLocal = GenericTypes.UNDEFINED_STRING;
        setIdIlliasFicheroSesion(GenericTypes.UNDEFINED_STRING);
        estado = ESTADO.INDEFINIDA;
        notas = GenericTypes.UNDEFINED_STRING;
        ficheroSesion = null;
        setDuracion(GenericTypes.UNDEFINED_NUM);
    }

    public int getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(int idSesion) {
        this.idSesion = idSesion;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaSesion() {
        return fechaSesion;
    }

    public void setFechaSesion(String fechaSesion) {
        this.fechaSesion = fechaSesion;
    }

    public String getRutaFicheroSesionLocal() {
        return rutaFicheroSesionLocal;
    }

    public void setRutaFicheroSesionLocal(String rutaFicheroSesionLocal) {
        this.rutaFicheroSesionLocal = rutaFicheroSesionLocal;
    }

    public ESTADO getEstado() {
        return estado;
    }

    public void setEstado(ESTADO estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    /**
     * Inicializa una instancia de la clase
     * @param idSesion identificador de sesion
     * @param idGrupo identificador del grupo
     * @param fechaCreacion fecha en la que se creo la sesion
     * @param fechaSesion fecha en la que la sesion se establece como ABIERTA
     * @param horaSesion hora en la que se establece la sesion como ABIERTA
     * @param rutaFicheroSesionLocal ruta en la que se almacenara el fichero de la sesion local
     * @param estado estado de la sesion
     * @param notas notas asociadas a la sesion
     * @param duracion duracion de la sesion
     */
    public SesionPracticas(int idSesion, int idGrupo, String fechaCreacion, String fechaSesion, String horaSesion, String rutaFicheroSesionLocal, ESTADO estado, String notas, int duracion)
    {
        this();
        this.idSesion = idSesion;
        this.idGrupo = idGrupo;
        this.fechaCreacion = fechaCreacion;
        this.fechaSesion = fechaSesion;
        this.horaSesion = horaSesion;
        this.rutaFicheroSesionLocal = rutaFicheroSesionLocal;
        this.estado = estado;
        this.notas = notas;
        this.setDuracion(duracion);

        this.ficheroSesion = new SesionFile(rutaFicheroSesionLocal);
    }

    public void CargarFicheroSesion()
    {
        ficheroSesion = new SesionFile(rutaFicheroSesionLocal);
    }

    /**
     * Inicializa una instancia de la clase
     * @param idSesion identificador de sesion
     * @param idGrupo identificador del grupo
     * @param fechaCreacion fecha en la que se creo la sesion
     * @param fechaSesion fecha en la que la sesion se establece como ABIERTA
     * @param horaSesion hora en la que la sesion se establece como ABIERTA
     * @param rutaFicheroSesionLocal ruta en la que se almacenara el fichero de la sesion local
     * @param idIlliasFicheroSesion identificador en illias del fichero de la sesion
     * @param estado estado de la sesion
     * @param notas notas asociadas a la sesion
     * @param duracion duracion de la sesion
     */
    public SesionPracticas(int idSesion, int idGrupo, String fechaCreacion, String fechaSesion, String horaSesion, String rutaFicheroSesionLocal, ESTADO estado, String notas, String idIlliasFicheroSesion, int duracion)
    {
        this(idSesion, idGrupo, fechaCreacion, fechaSesion, horaSesion, rutaFicheroSesionLocal,estado,notas, duracion);
        this.idIlliasFicheroSesion = idIlliasFicheroSesion;
    }

    /**
     * Pone la hora de fin a todos los registros, siempre que la sesion ya haya pasado
     */
    public void PonerHoraFin()
    {

        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        //Actualizar la hora de salida de los alumnos, si no está escrita, solo si hemos sobrepasado la hora de fin de la sesion
        for(SesionRegister registroSesion : ficheroSesion.registrosSesion.values())
        {
            if(!registroSesion.getHoraEntrada().equals("") && registroSesion.getHoraSalida().equals(""))
            {
                String horaInicioSesionTxt = getFechaSesion()+" "+getHoraSesion();
                SimpleDateFormat stringToDateFormatter = new SimpleDateFormat("yyyyMMdd HH:mm");
                try
                {
                    Date horaInicioSesion = stringToDateFormatter.parse(horaInicioSesionTxt);
                    long horaInicioSesionMillis = horaInicioSesion.getTime();
                    Date horaFinSesion = new Date(horaInicioSesionMillis + (getDuracion() * GenericTypes.ONE_MINUTE_IN_MILLIS));

                    if(Calendar.getInstance().getTime().after(horaFinSesion))
                    {
                        registroSesion.setHoraSalida(timeFormatter.format(horaFinSesion));
                    }
                }
                catch (Exception e)
                {

                }
            }
        }
    }

    public String GetFechaHoraString()
    {
        String res = GenericTypes.UNDEFINED_STRING;
        if(!fechaSesion.equals(GenericTypes.UNDEFINED_STRING) && !horaSesion.equals(GenericTypes.UNDEFINED_STRING))
        {
            res = getFechaSesion() + ":" + getHoraSesion();
        }

        return res;
    }
}
