package com.uja.telematica.GUI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.uja.telematica.BO.AlumnoListAdapter;
import com.uja.telematica.BO.GrupoPracticasAdapter;
import com.uja.telematica.DAO.Alumno;
import com.uja.telematica.DAO.AlumnoAsignaturaGrupo;
import com.uja.telematica.DAO.BaseDatos;
import com.uja.telematica.DAO.Comunicador;
import com.uja.telematica.DAO.GenericTypes;
import com.uja.telematica.DAO.GrupoPracticas;
import com.uja.telematica.R;

import java.util.ArrayList;

public class AlumnosGrupoActivity extends ActionBarActivity {

    BaseDatos baseDatos;
    ArrayList<Alumno> alumnos;
    ArrayList<AlumnoAsignaturaGrupo> alumnosAsignaturaGrupoSeleccionados;    //Contiene todos los alumnos de la asignatura, para asignarles su grupo
    ListView alumnosGrupoListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseDatos = Comunicador.getBaseDatos();
        alumnos = new ArrayList<Alumno>();
        setContentView(R.layout.activity_alumnos_grupo);
        alumnosGrupoListView = (ListView)findViewById(R.id.alumnosGrupoListView);
        //alumnosGrupoListView.setOnItemClickListener(this);
        alumnosAsignaturaGrupoSeleccionados = new ArrayList<AlumnoAsignaturaGrupo>();

        for(AlumnoAsignaturaGrupo alumnoAsignaturaGrupo : baseDatos.alumnosAsignaturaGrupo.values())
        {
            if(alumnoAsignaturaGrupo.getIdAsignatura() == Comunicador.getAsignaturaSeleccionada())
            {
                alumnosAsignaturaGrupoSeleccionados.add(alumnoAsignaturaGrupo);
                alumnos.add(baseDatos.alumnos.get(Integer.toString(alumnoAsignaturaGrupo.getIdAlumno())));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alumnos_grupo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id)
        {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_ayuda:

                View ayudaView = getLayoutInflater().inflate(R.layout.ayuda, null, false);

                WebView webView = (WebView)ayudaView.findViewById(R.id.webViewTexto);

                try
                {
                    webView.loadUrl("file:///android_asset/AyudaAlumnosGrupo.htm");
                }
                catch (Exception ex)
                {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
                }

                AlertDialog.Builder ayudaBuilder = new AlertDialog.Builder(this);
                ayudaBuilder.setIcon(R.mipmap.icono_launcher);
                ayudaBuilder.setTitle(R.string.ayuda);
                ayudaBuilder.setView(ayudaView);
                ayudaBuilder.create();
                ayudaBuilder.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();


        Comunicador.setAlumnosGrupo(baseDatos.GetAlumnosGrupo(Comunicador.getGrupoSeleccionado()));
        AlumnoListAdapter alumnoListAdapter = new AlumnoListAdapter(getBaseContext(), alumnos, Comunicador.getAlumnosGrupo());
        alumnosGrupoListView = (ListView) findViewById(R.id.alumnosGrupoListView);
        alumnosGrupoListView.setAdapter(alumnoListAdapter);
        //alumnosGrupoListView.setOnItemClickListener(this);
    }
/*
    @Override
    public void onBackPressed() {
        GuardarDatos();

        super.onBackPressed();
    }

    @Override
    public void onPause()
    {
        GuardarDatos();

        super.onPause();
    }*/

    /*
    private void GuardarDatos() {
        //Recuperar los elementos marcados
        if(alumnosGrupoListView.getCount() > 0)
        {
            //Resetear todos los alumnos del grupo y asignatura actual
            for(AlumnoAsignaturaGrupo alumnoAsignaturaGrupo : alumnosAsignaturaGrupoSeleccionados)
            {
                if(alumnoAsignaturaGrupo.getIdGrupoPracticas() == Comunicador.getGrupoSeleccionado()) {
                    alumnoAsignaturaGrupo.setIdGrupoPracticas(GenericTypes.UNDEFINED_NUM);
                }
            }

            ArrayList<Integer> alumnosSeleccionados = new ArrayList<Integer>();
            for (int i = 0; i < alumnosGrupoListView.getCount(); i++)
            {
                View v = alumnosGrupoListView.getAdapter().getView(i, null, alumnosGrupoListView);
                Alumno alumno = alumnos.get(i);
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.alumnoGrupoCheckBox);

                //Recupera el alumnoAsignaturaGrupo correspondiente
                AlumnoAsignaturaGrupo alumnoAsignaturaGrupo = baseDatos.GetAlumnoAsignaturaGrupo(Comunicador.getAsignaturaSeleccionada(), Integer.parseInt(alumno.getIdAlumno()));

                if(v.isEnabled() && checkBox.isChecked())
                {
                    alumnoAsignaturaGrupo.setIdGrupoPracticas(Comunicador.getGrupoSeleccionado());
                }

                baseDatos.ActualizaGrupoPracticasAlumnoAsignatura(alumnoAsignaturaGrupo);
            }
        }
    }*/
/*
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(view.isEnabled())
        {
            if(view.getId() == R.id.alumnoGrupoCheckBox)
            {
                //Alumno alumno = view.getParent().get
            }
        }
    }*/
}
