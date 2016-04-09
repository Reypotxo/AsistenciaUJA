package com.uja.telematica.GUI;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uja.telematica.BO.AsyncTasker;
import com.uja.telematica.DAO.Alumno;
import com.uja.telematica.DAO.Asignatura;
import com.uja.telematica.DAO.BaseDatos;
import com.uja.telematica.DAO.BaseDatosHelper;
import com.uja.telematica.DAO.Comunicador;
import com.uja.telematica.DAO.CsvFile;
import com.uja.telematica.DAO.GenericTypes;
import com.uja.telematica.DAO.Ilias;
import com.uja.telematica.R;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    String usuarioId;
    String clave;
    Ilias ilias;
    EditText usuarioTextView, claveTextView;
    Button buttonAceptarLogin;
    BaseDatos baseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        buttonAceptarLogin = (Button)findViewById(R.id.buttonAceptarLogin);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void OnButtonAceptarLoginClick(View view) {
        //Comprobar que se ha hecho click en el boton y no en cualquier otra parte de la pantalla
        if (view instanceof Button) {
            if (usuarioTextView == null) {
                usuarioTextView = (EditText) findViewById(R.id.editTextUsuarioLogin);
                claveTextView = (EditText) findViewById(R.id.editTextClaveLogin);
            }

            usuarioId = usuarioTextView.getText().toString();
            clave = claveTextView.getText().toString();

            ilias = new Ilias(usuarioId, clave, this);
            AsyncTasker asyncTaskerLogin = new AsyncTasker(ilias);
            AsyncTask<String, Float, GenericTypes.ExecutionResult> resultLoginTask = asyncTaskerLogin.execute(Ilias.LOGIN);

            try
            {
                //resultAsyncTask.wait(GenericTypes.WAIT_TIME); //wait 5s for login
                Toast.makeText(this, resultLoginTask.get().executionMessage, Toast.LENGTH_LONG).show();
                if(resultLoginTask.get().executionResult == GenericTypes.EXECUTION_RESULT.OK)
                {
                    Comunicador.setIlias(ilias);
                    //Obtiene el id de usuario que hace login
                    AsyncTasker asyncTaskerGetIdUsuario = new AsyncTasker(ilias);
                    AsyncTask<String, Float, GenericTypes.ExecutionResult> resultGetIdUsuarioTask = asyncTaskerGetIdUsuario.execute(Ilias.GET_IDUSUARIO);

                    if(resultGetIdUsuarioTask.get().executionResult == GenericTypes.EXECUTION_RESULT.OK)
                    {
                        File folder = new File(Comunicador.baseDatosDir);

                        //Crear directorio de la app si no existe
                        boolean folderCreated = false;
                        if (!folder.exists())
                        {
                            folderCreated = folder.mkdir();
                        }

                        //Si ha tenido que crear el directorio, hay que crear tambien la base de datos con la informacion de Illias
                        if (folderCreated)
                        {
                            baseDatos = new BaseDatos(ilias, this.getApplicationContext());

                            if(baseDatos != null)
                            {
                                baseDatos.AbrirBBDD();
                                //Crear directorio por cada asignatura
                                boolean hasErrors = false;
                                for(Asignatura asignatura : baseDatos.asignaturas.values())
                                {
                                    asignatura.setRutaFicherosLocal(Comunicador.baseDatosDir + "/" + asignatura.getIliasId());
                                    File asignaturaFolder = new File(asignatura.getRutaFicherosLocal());
                                    boolean folderOk = true;
                                    if (!asignaturaFolder.exists())
                                    {
                                        folderOk = asignaturaFolder.mkdir();
                                    }

                                    if(folderOk)
                                    {
                                        //Salvar BBDD
                                        baseDatos.SalvarBBDD();
                                    }
                                    else
                                    {
                                        hasErrors = true;
                                        Toast.makeText(MainActivity.this, R.string.errorCrearDirectoriosApp, Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                            }
                        }

                        //Cargar la base de datos desde local, tanto si existia como si la acaba de generar
                        baseDatos = new BaseDatos(this.getApplicationContext());
                        baseDatos.AbrirBBDD();
                        baseDatos.CargarBBDD();

                        if(baseDatos.asignaturas.size() > 0) {
                            //Lanzar activity asignaturas
                            Comunicador.setBaseDatos(baseDatos);
                            Intent intentAsignaturas = new Intent(this, AsignaturasActivity.class);
                            //intentAsignaturas.putExtra("BASEDATOS", (Serializable)baseDatos);
                            startActivity(intentAsignaturas);
                        }
                        else{
                            Toast.makeText(MainActivity.this, R.string.noAsignaturas, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            catch(Exception exception)
            {
                //Do nothing now
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();;
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
