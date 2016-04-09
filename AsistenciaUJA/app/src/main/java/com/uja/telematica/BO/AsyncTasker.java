package com.uja.telematica.BO;

import android.os.AsyncTask;

import com.uja.telematica.DAO.GenericTypes;
import com.uja.telematica.DAO.Ilias;
import com.uja.telematica.R;

/**
 * Created by Alfonso Troyano on 17/05/2015.
 *
 * Hay que heredar de AsyncTask para hacer las operaciones con el webService en otro hilo y evitar excepciones
 */
public class AsyncTasker extends AsyncTask<String, Float, GenericTypes.ExecutionResult> {

    /**
     * Instancia de ilias para poder realizar las operaciones
     */
    private Ilias ilias;

    /**
     * Inicializa una instancia de la clase
     * @param ilias instancia de ilias para poder llamar a los webservices.
     */
    public AsyncTasker(Ilias ilias)
    {
        this.ilias = ilias;
    }

    @Override
    protected GenericTypes.ExecutionResult doInBackground(String... params) {
        GenericTypes.ExecutionResult res = new GenericTypes.ExecutionResult();

        switch(params[0])
        {
            case Ilias.LOGIN:
                res = ilias.Login();
                break;

            case Ilias.GET_IDUSUARIO:
                res = ilias.GetUserBySid();
                break;

            case Ilias.GET_ASIGNATURAS:
                res = ilias.GetAsignaturas();
                break;

            case Ilias.GET_ALUMNO:
                res = ilias.GetUser(Integer.parseInt(params[1]));
                break;

            case Ilias.GET_FICHERO:
                res = ilias.GetFichero(params[1]);
                break;

            case Ilias.SUBIR_FICHERO:
                res = ilias.AddFile(params[1], params[2], params[3], params[4]);
                break;

            case Ilias.EDITAR_FICHERO:
                res = ilias.UpdateFile(params[1], params[2], params[3]);
                break;
        }
        return res;
    }//android.os.AsyncTask<Params, Progress, Result>

}
