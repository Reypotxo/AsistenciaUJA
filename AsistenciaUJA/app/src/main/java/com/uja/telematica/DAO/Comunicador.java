package com.uja.telematica.DAO;

import android.os.Environment;

import java.util.ArrayList;

/**
 * Created by Alfonso Troyano on 09/07/2015.
 * Es la clase que se utilizara para intercambiar la base de datos, utilizando el patron Singleton
 */
public class Comunicador {
    private static BaseDatos objeto = null;
    private static int asignaturaSeleccionada = GenericTypes.UNDEFINED_NUM;
    private static int grupoSeleccionado = GenericTypes.UNDEFINED_NUM;
    private static int sesionSeleccionada = GenericTypes.UNDEFINED_NUM;
    private static ArrayList<Integer> gruposAsignatura;
    private static ArrayList<Integer> alumnosGrupo;
    private static ArrayList<String> sesionesGrupo;
    private static Ilias ilias;

    public static void setBaseDatos(BaseDatos newObjeto) {
        objeto = newObjeto;
    }

    public static BaseDatos getBaseDatos() {
        return objeto;
    }

    public static int getAsignaturaSeleccionada() {
        return asignaturaSeleccionada;
    }

    public static void setAsignaturaSeleccionada(int asignaturaSeleccionada) {
        Comunicador.asignaturaSeleccionada = asignaturaSeleccionada;
    }

    public static final String baseDatosDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AsistenciaUJA";

    public static int getGrupoSeleccionado() {
        return grupoSeleccionado;
    }

    public static void setGrupoSeleccionado(int grupoSeleccionado) {
        Comunicador.grupoSeleccionado = grupoSeleccionado;
    }

    public static ArrayList<Integer> getGruposAsignatura() {
        return gruposAsignatura;
    }

    public static void setGruposAsignatura(ArrayList<Integer> gruposAsignatura) {
        Comunicador.gruposAsignatura = gruposAsignatura;
    }

    public static ArrayList<Integer> getAlumnosGrupo() {
        return alumnosGrupo;
    }

    public static void setAlumnosGrupo(ArrayList<Integer> alumnosGrupo) {
        Comunicador.alumnosGrupo = alumnosGrupo;
    }

    public static void setSesionSeleccionada(int sesionSeleccionada) {
        Comunicador.sesionSeleccionada = sesionSeleccionada;

        //Se carga el fichero cada vez que se selecciona una sesion
        getBaseDatos().sesionesPracticas.get(new Integer(sesionSeleccionada).toString()).CargarFicheroSesion();
    }

    public static int getSesionSeleccionada() {
        return sesionSeleccionada;
    }

    public static ArrayList<String> getSesionesGrupo() {
        return sesionesGrupo;
    }

    public static void setSesionesGrupo(ArrayList<String> sesionesGrupo) {
        Comunicador.sesionesGrupo = sesionesGrupo;
    }

    public static Ilias getIlias() {
        return ilias;
    }

    public static void setIlias(Ilias ilias) {
        Comunicador.ilias = ilias;
    }
}
