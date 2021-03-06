package com.uja.telematica.GUI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.uja.telematica.BO.GrupoPracticasAdapter;
import com.uja.telematica.BO.SesionPracticasAdapter;
import com.uja.telematica.DAO.Asignatura;
import com.uja.telematica.DAO.BaseDatos;
import com.uja.telematica.DAO.Comunicador;
import com.uja.telematica.DAO.GenericTypes;
import com.uja.telematica.DAO.SesionPracticas;
import com.uja.telematica.R;

import java.util.ArrayList;
import java.util.Collections;

public class SesionesActivity extends ActionBarActivity {
    /**
     * Manejador de eventos personalizado para solventar cuando la lista no escucha
     */
    public interface SesionesListListener {
        /**
         * Evento que se lanzara cuando se edite algun registro de la lista
         */
        public void onListChanged();
    }
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    public SesionesListListener listener;

    ArrayList opciones;
    BaseDatos baseDatos;
    ArrayList<String> sesionesGrupoTexto;

    public void setCustomObjectListener(SesionesListListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCustomObjectListener(new SesionesListListener() {
            @Override
            public void onListChanged(){
                onResume();
            }
        });

        CargarDatos();

        mTitle = getTitle();

        setContentView(R.layout.activity_sesiones);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        CargarDatos();

        super.onResume();

        SesionPracticasAdapter mNewAdapter = new SesionPracticasAdapter(sesionesGrupoTexto, opciones);
        ExpandableListView sesionesListView = (ExpandableListView) findViewById(R.id.sesionesGrupoListView);
            /*gruposListView.setAdapter(mNewAdapter);*/

        mNewAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
        sesionesListView.setAdapter(mNewAdapter);

        sesionesListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                ExpandableListView listView = (ExpandableListView) findViewById(R.id.sesionesGrupoListView);
                if (groupPosition != previousGroup)
                    listView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });
    }

    private void CargarDatos()
    {
        sesionesGrupoTexto = new ArrayList<String>();
        baseDatos = Comunicador.getBaseDatos();
        baseDatos.CargarSesionesPracticasGrupo(Comunicador.getGrupoSeleccionado());

        opciones = new ArrayList<String>();
        opciones.add(getString(R.string.ver_sesion));
        opciones.add(getString(R.string.subir_sesion));
        opciones.add(getString(R.string.editar_sesion));
        opciones.add(getString(R.string.borrar_sesion));

        for(String sesionId : Collections.list(baseDatos.sesionesPracticas.keys()))
        {
            SesionPracticas sesionPracticas = baseDatos.sesionesPracticas.get(sesionId);

            String sufijo = " - ";
            if(sesionPracticas.estado == SesionPracticas.ESTADO.SUBIDA)
            {
                sufijo += getString(R.string.sesion_online);
            }
            else
            {
                sufijo += getString(R.string.sesion_offline);
            }
            sesionesGrupoTexto.add(getString(R.string.sesion_titulo) + " - " + sesionPracticas.GetFechaHoraString() + sufijo);
        }

        Collections.sort(sesionesGrupoTexto, String.CASE_INSENSITIVE_ORDER);

        Comunicador.setSesionesGrupo(sesionesGrupoTexto);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sesiones, menu);
        restoreActionBar();

        //return true;
        return super.onCreateOptionsMenu(menu);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch(id)
        {
            case R.id.crear_sesion:
                //Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
                Intent intentNuevaSesion = new Intent(getApplicationContext(), CrearSesionActivity.class);
                intentNuevaSesion.putExtra("NUEVASESION", "true");
                startActivity(intentNuevaSesion);
                //return true;
                break;

            case R.id.action_ayuda:

                View ayudaView = getLayoutInflater().inflate(R.layout.ayuda, null, false);

                WebView webView = (WebView)ayudaView.findViewById(R.id.webViewTexto);

                try
                {
                    webView.loadUrl("file:///android_asset/AyudaSesiones.htm");
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
}
