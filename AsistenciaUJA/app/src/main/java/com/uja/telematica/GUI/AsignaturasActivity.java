package com.uja.telematica.GUI;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ExpandableListView;

import com.uja.telematica.BO.GrupoPracticasAdapter;
import com.uja.telematica.DAO.AlumnoAsignaturaGrupo;
import com.uja.telematica.DAO.Asignatura;
import com.uja.telematica.DAO.BaseDatos;
import com.uja.telematica.DAO.Comunicador;
import com.uja.telematica.DAO.GrupoPracticas;
import com.uja.telematica.R;

import java.util.ArrayList;

public class AsignaturasActivity extends ActionBarActivity
        implements AsignaturasNavigationDrawerFragment.NavigationDrawerCallbacks {

    static BaseDatos baseDatos;

    static ArrayList<String> asignaturas;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private AsignaturasNavigationDrawerFragment mAsignaturasNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseDatos = Comunicador.getBaseDatos();
        baseDatos.CargarGruposPracticas();  //Se cargan aqui, puesto que es donde se van a mostrar y se iran actualizando conforme se creen nuevos
        asignaturas = baseDatos.GetListaAsignaturas();
        Comunicador.setAsignaturaSeleccionada(Integer.parseInt(baseDatos.GetAsignaturaSeleccionada(asignaturas.get(0)).getIliasId()));  // Por defecto se selecciona la primera asignatura
        baseDatos.CargarDatosIlliasAsignatura(Integer.toString(Comunicador.getAsignaturaSeleccionada()));
        setContentView(R.layout.activity_asignaturas);

        mAsignaturasNavigationDrawerFragment = (AsignaturasNavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.asignaturas_navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mAsignaturasNavigationDrawerFragment.setUp(
                R.id.asignaturas_navigation_drawer,
                (DrawerLayout) findViewById(R.id.asignaturas_drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderAsignaturasFragment.newInstance(position, getBaseContext()))
                .commit();
    }

    public void onSectionAttached(int number) {
        String asignaturaSeleccionada = asignaturas.get(number);
        Asignatura asignatura = baseDatos.GetAsignaturaSeleccionada(asignaturaSeleccionada);
        Comunicador.setAsignaturaSeleccionada(Integer.parseInt(asignatura.getIliasId()));
        baseDatos.CargarDatosIlliasAsignatura(Integer.toString(Comunicador.getAsignaturaSeleccionada()));
        mTitle = asignatura.getId();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mAsignaturasNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.asignaturas, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderAsignaturasFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        static ArrayList<String> gruposAsignaturaTexto;
        static BaseDatos baseDatos;
        static int asignaturaSeleccionada;
        static ArrayList<String> opciones;
        static Context appContext;
        static View fragmentView;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderAsignaturasFragment newInstance(int sectionNumber, Context context) {
            PlaceholderAsignaturasFragment fragment = new PlaceholderAsignaturasFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            appContext = context;
            fragment.setArguments(args);

            baseDatos = Comunicador.getBaseDatos();
            asignaturaSeleccionada = Comunicador.getAsignaturaSeleccionada();

            opciones = new ArrayList<String>();
            opciones.add(context.getString(R.string.editar_grupo));
            opciones.add(context.getString(R.string.ver_alumnos));
            opciones.add(context.getString(R.string.ver_sesiones));
            //opciones.add(context.getString(R.string.borrar_grupo));

            return fragment;
        }

        public PlaceholderAsignaturasFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            fragmentView = inflater.inflate(R.layout.fragment_asignaturas, container, false);

            return fragmentView;
        }

        @Override
        public void onResume() {
            baseDatos = Comunicador.getBaseDatos();
            baseDatos.CargarGruposPracticas();  //Se cargan aqui, puesto que es donde se van a mostrar y se iran actualizando conforme se creen nuevos
            asignaturas = baseDatos.GetListaAsignaturas();
            Comunicador.setAsignaturaSeleccionada(Integer.parseInt(baseDatos.GetAsignaturaSeleccionada(asignaturas.get(0)).getIliasId()));  // Por defecto se selecciona la primera asignatura
            baseDatos.CargarDatosIlliasAsignatura(Integer.toString(Comunicador.getAsignaturaSeleccionada()));
            super.onResume();

            Comunicador.setGruposAsignatura(baseDatos.GetGruposAsignatura(asignaturaSeleccionada));
            gruposAsignaturaTexto = new ArrayList<String>();
            for(Integer grupoAsignatura : Comunicador.getGruposAsignatura())
            {
                GrupoPracticas grupoPracticas = baseDatos.gruposPracticas.get(grupoAsignatura.toString());
                int totalAlumnos = 0;
                for(AlumnoAsignaturaGrupo alumnoAsignaturaGrupo : baseDatos.alumnosAsignaturaGrupo.values())
                {
                    if(alumnoAsignaturaGrupo.getIdGrupoPracticas() == grupoAsignatura)
                    {
                        totalAlumnos++;
                    }
                }

                gruposAsignaturaTexto.add(grupoPracticas.getId() + " (" + grupoPracticas.getGrupoId() + ") - " + grupoPracticas.getDia() + " " + grupoPracticas.getHora() + " - " + totalAlumnos + " alumno(s)");
            }

            GrupoPracticasAdapter mNewAdapter = new GrupoPracticasAdapter(gruposAsignaturaTexto, opciones);
            ExpandableListView gruposListView = (ExpandableListView) getView().findViewById(R.id.gruposAsignaturaListView);

            mNewAdapter.setInflater((LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE), getActivity());
            gruposListView.setAdapter(mNewAdapter);

            gruposListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int previousGroup = -1;

                @Override
                public void onGroupExpand(int groupPosition) {
                    ExpandableListView listView = (ExpandableListView) getView().findViewById(R.id.gruposAsignaturaListView);
                    if (groupPosition != previousGroup)
                        listView.collapseGroup(previousGroup);
                    previousGroup = groupPosition;
                }
            });
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            ((AsignaturasActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
