package com.uja.telematica.DAO;

/**
 * Created by Alfonso Troyano on 14/06/2015.
 * Permite procesar el fichero CSV
 */
public class CsvFile {
    /**
     * Fichero CSV
     */
    String fichero;
    /**
     * Numero de alumno a buscar en el fichero
     */
    int numAlumno;

    /**
     * Inicializa una instancia de la clase
     * @param fichero contenido del fichero descargado
     */
    public CsvFile(String fichero)
    {
        this.fichero = fichero;
        numAlumno = 1;
    }

    /**
     * Permite ir leyendo las lineas de los alumnos
     * @return Siguiente linea de alumno
     */
    public String GetLineaAlumno()
    {
        String res = GenericTypes.UNDEFINED_STRING;
        String cadenaAlumno = "; " + numAlumno + ";;";
        int indiceInicial = fichero.indexOf(cadenaAlumno);

        if(indiceInicial != GenericTypes.UNDEFINED_NUM)
        {
            indiceInicial += cadenaAlumno.length();
            numAlumno++;
            String cadenaSiguienteAlumno = "; " + numAlumno + ";;";
            int indiceFinal = fichero.indexOf(cadenaSiguienteAlumno);

            if(indiceFinal != GenericTypes.UNDEFINED_NUM)
            {
                res = fichero.substring(indiceInicial, indiceFinal);
            }
            else
            {
                res = fichero.substring(indiceInicial);
            }
        }

        return res;
    }
}
