package com.uja.telematica.BO;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.uja.telematica.DAO.AlumnoAsignaturaGrupo;
import com.uja.telematica.DAO.BaseDatos;
import com.uja.telematica.DAO.Comunicador;
import com.uja.telematica.DAO.GenericTypes;
import com.uja.telematica.DAO.GrupoPracticas;
import com.uja.telematica.DAO.SesionPracticas;
import com.uja.telematica.GUI.AlumnosGrupoActivity;
import com.uja.telematica.GUI.CrearSesionActivity;
import com.uja.telematica.GUI.NuevoGrupoPracticas;
import com.uja.telematica.GUI.SesionesActivity;
import com.uja.telematica.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Alfonso Troyano on 05/08/2015.
 */
public class GrupoPracticasAdapter extends BaseExpandableListAdapter {

    public ArrayList<String> groupItem;
    public ArrayList<String> Childtem;
    public String tempChild, tempGroup;
    public LayoutInflater minflater;
    public Activity activity;
    public BaseDatos baseDatos;
    int previousGroup = -1;

    public GrupoPracticasAdapter(ArrayList<String> grList, ArrayList<String> childItem) {
        groupItem = grList;
        this.Childtem = childItem;
        baseDatos = Comunicador.getBaseDatos();
    }

    public void setInflater(LayoutInflater mInflater, Activity act) {
        this.minflater = mInflater;
        activity = act;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        tempGroup = groupItem.get(groupPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = minflater.inflate(R.layout.opciones_lista_layout, null);
        }
        convertView.setPadding(140, 0, 0, 10);
        text = (TextView) convertView.findViewById(R.id.opcionesTextView);
        text.setText(Childtem.get(childPosition));

        int indiceInicio = tempGroup.indexOf('(');
        int indiceFin = tempGroup.indexOf(") - ");

        Comunicador.setGrupoSeleccionado(Integer.parseInt(tempGroup.substring(indiceInicio + 1, indiceFin)));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (childPosition)
                {

                    case 0: //Editar Grupo
                        Intent intentNuevoGrupo = new Intent(activity.getApplication().getApplicationContext(), NuevoGrupoPracticas.class);
                        intentNuevoGrupo.putExtra("NUEVOGRUPO", "false");
                        activity.startActivity(intentNuevoGrupo);
                        break;

                    case 1: //Ver Alumnos
                        Intent intentAlumnosGrupo = new Intent(activity, AlumnosGrupoActivity.class);
                        //intentAsignaturas.putExtra("BASEDATOS", (Serializable)baseDatos);
                        activity.startActivity(intentAlumnosGrupo);
                        break;

                    case 2: // Ver Sesiones

                        Intent intentSesionesGrupo = new Intent(activity, SesionesActivity.class);
                        activity.startActivity(intentSesionesGrupo);
                        break;

/*
                    case 3: //Borrar Grupo
                        GrupoPracticas grupoSeleccionado = baseDatos.gruposPracticas.get(Integer.toString(Comunicador.getGrupoSeleccionado()));
                        if(grupoSeleccionado.isEditable())
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity.getWindow().getContext());   //Se coge el contexto de la ventana actual
                            builder = builder.setMessage(activity.getString(R.string.WarningBorrar));
                            builder = builder.setPositiveButton(activity.getString(R.string.si), borrarGrupoClickListener);
                            builder = builder.setNegativeButton(activity.getString(R.string.no), borrarGrupoClickListener);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        else
                        {
                            Toast.makeText(activity.getBaseContext(), activity.getString(R.string.ErrorGrupoSesionSubida), Toast.LENGTH_SHORT).show();
                        }
                        break;*/
                }
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Childtem.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return groupItem.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = minflater.inflate(R.layout.grupo_list_layout, null);
        }
        ((CheckedTextView) convertView).setText(groupItem.get(groupPosition));
        ((CheckedTextView) convertView).setChecked(isExpanded);
        convertView.setPadding(100, 0, 0, 10);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

/*
    DialogInterface.OnClickListener borrarGrupoClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    GrupoPracticas grupoSeleccionado = baseDatos.gruposPracticas.get(Integer.toString(Comunicador.getGrupoSeleccionado()));
                    boolean operacionOk = true;
                    baseDatos.CargarSesionesPracticasGrupo(grupoSeleccionado.getGrupoId());
                    for (SesionPracticas sesionSeleccionadaBorrar: baseDatos.sesionesPracticas.values()
                            ) {

                        //Borrar fichero de sesion
                        File ficheroSesion = new File(sesionSeleccionadaBorrar.getRutaFicheroSesionLocal());
                        if(ficheroSesion.delete())
                        {
                            //Borrar sesion en base de datos
                            if(!baseDatos.BorrarSesionPracticas(sesionSeleccionadaBorrar))
                            {
                                operacionOk = false;
                            }
                        }
                        else
                        {
                            operacionOk = false;
                        }

                        if(!operacionOk)
                        {
                            break;//para el bucle si hay algun error
                        }
                    }

                    for (AlumnoAsignaturaGrupo alumnoAsignaturaGrupo:baseDatos.alumnosAsignaturaGrupo.values()
                            ) {
                        if(alumnoAsignaturaGrupo.getIdGrupoPracticas() == grupoSeleccionado.getGrupoId())
                        {
                            alumnoAsignaturaGrupo.setIdGrupoPracticas(GenericTypes.UNDEFINED_NUM);
                            baseDatos.ActualizaGrupoPracticasAlumnoAsignatura(alumnoAsignaturaGrupo);
                        }
                    }

                    if(operacionOk)
                    {
                        baseDatos.BorrarGrupoPracticas(grupoSeleccionado);
                        notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(activity.getBaseContext(), activity.getString(R.string.ErrorBorrado), Toast.LENGTH_SHORT).show();
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No hacer nada
                    break;
            }
        }
    };*/
}

