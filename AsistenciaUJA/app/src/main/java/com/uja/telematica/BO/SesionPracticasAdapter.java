package com.uja.telematica.BO;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uja.telematica.DAO.BaseDatos;
import com.uja.telematica.DAO.Comunicador;
import com.uja.telematica.DAO.CsvFile;
import com.uja.telematica.DAO.GenericTypes;
import com.uja.telematica.DAO.GrupoPracticas;
import com.uja.telematica.DAO.Ilias;
import com.uja.telematica.DAO.SesionPracticas;
import com.uja.telematica.DAO.SesionRegister;
import com.uja.telematica.GUI.AlumnosGrupoActivity;
import com.uja.telematica.GUI.AlumnosSesionActivity;
import com.uja.telematica.GUI.CrearSesionActivity;
import com.uja.telematica.GUI.SesionesActivity;
import com.uja.telematica.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Alfonso on 3/02/16.
 */
public class SesionPracticasAdapter extends BaseExpandableListAdapter {
    public ArrayList<String> groupItem;
    public ArrayList<String> Childtem;
    public String tempChild, tempGroup;
    public LayoutInflater minflater;
    public Activity activity;
    public BaseDatos baseDatos;
    private Ilias ilias;
    //private ListView sesionesGrupoListView;
    private Hashtable<String, Integer> fechaHoraToIdSesion;
    private static ExpandableListView sesionesGrupoListView;

    public SesionPracticasAdapter(ArrayList<String> grList, ArrayList<String> childItem) {
        groupItem = grList;
        this.Childtem = childItem;
        baseDatos = Comunicador.getBaseDatos();
        ilias = Comunicador.getIlias();
        fechaHoraToIdSesion = new Hashtable<String, Integer>();
        for (SesionPracticas sesionPracticas :
                baseDatos.sesionesPracticas.values()) {
            fechaHoraToIdSesion.put(sesionPracticas.getFechaSesion() + ":" + sesionPracticas.horaSesion, sesionPracticas.getIdSesion());
        }
        //sesionesGrupoTexto = Comunicador.getSesionesGrupo();
    }

    public void setInflater(LayoutInflater mInflater, Activity act) {
        this.minflater = mInflater;
        activity = act;

        sesionesGrupoListView = (ExpandableListView)activity.findViewById(R.id.sesionesGrupoListView);
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
                             boolean isLastChild, View convertView, final ViewGroup parent) {
        tempGroup = groupItem.get(groupPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = minflater.inflate(R.layout.opciones_lista_layout, null);
        }
        convertView.setPadding(140, 0, 0, 10);
        text = (TextView) convertView.findViewById(R.id.opcionesTextView);
        text.setText(Childtem.get(childPosition));
        //String sesionTexto = sesionesGrupoTexto.get(groupPosition);
        int indiceInicio = tempGroup.indexOf(" - ");
        int indiceFin = tempGroup.lastIndexOf(" - ");


        Comunicador.setSesionSeleccionada(fechaHoraToIdSesion.get(tempGroup.substring(indiceInicio + 3, indiceFin)));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (childPosition) {
                    case 0: //Ver Sesion
                        Intent intentAlumnosSesion = new Intent(activity, AlumnosSesionActivity.class);
                        //intentAsignaturas.putExtra("BASEDATOS", (Serializable)baseDatos);
                        activity.startActivity(intentAlumnosSesion);
                        break;

                    case 1: // Subir Sesion

                        SesionPracticas sesionSeleccionada = baseDatos.sesionesPracticas.get(Integer.toString(Comunicador.getSesionSeleccionada()));
                        if (sesionSeleccionada.estado != SesionPracticas.ESTADO.SUBIDA) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity.getWindow().getContext());   //Se coge el contexto de la ventana actual
                            builder = builder.setMessage(activity.getString(R.string.WarningSubirSesion));
                            builder = builder.setPositiveButton(activity.getString(R.string.si), subirSesionClickListener);
                            builder = builder.setNegativeButton(activity.getString(R.string.no), subirSesionClickListener);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            //El proceso continua en el click listener del alertDialog
                        } else {
                            Toast.makeText(activity.getBaseContext(), activity.getString(R.string.ErrorSesionSubida), Toast.LENGTH_SHORT).show();
                        }
                        break;


                    case 2: //Editar Sesion
                        SesionPracticas sesionSeleccionadaEditar = baseDatos.sesionesPracticas.get(Integer.toString(Comunicador.getSesionSeleccionada()));
                        if (sesionSeleccionadaEditar.estado != SesionPracticas.ESTADO.SUBIDA) {
                            Intent intentNuevaSesion = new Intent(activity.getApplication().getApplicationContext(), CrearSesionActivity.class);
                            intentNuevaSesion.putExtra("NUEVASESION", "false");
                            activity.startActivity(intentNuevaSesion);
                        } else {
                            Toast.makeText(activity.getBaseContext(), activity.getString(R.string.ErrorSesionSubida), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case 3: //Borrar Sesion
                        SesionPracticas sesionSeleccionadaBorrar = baseDatos.sesionesPracticas.get(Integer.toString(Comunicador.getSesionSeleccionada()));
                        if(sesionSeleccionadaBorrar.estado != SesionPracticas.ESTADO.SUBIDA)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity.getWindow().getContext());   //Se coge el contexto de la ventana actual
                            builder = builder.setMessage(activity.getString(R.string.WarningBorrar));
                            builder = builder.setPositiveButton(activity.getString(R.string.si), borrarSesionClickListener);
                            builder = builder.setNegativeButton(activity.getString(R.string.no), borrarSesionClickListener);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            //El proceso continua en el click listener
                        }
                        else
                        {
                            Toast.makeText(activity.getBaseContext(), activity.getString(R.string.ErrorSesionSubida), Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                ExpandableListView listView = (ExpandableListView)activity.findViewById(R.id.sesionesGrupoListView);
                listView.invalidate();
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


    DialogInterface.OnClickListener subirSesionClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    String idDirectorio = baseDatos.asignaturas.get(new Integer(Comunicador.getAsignaturaSeleccionada()).toString()).getRefAsistenciaServidor();
                    SesionPracticas sesionSeleccionada = baseDatos.sesionesPracticas.get(Integer.toString(Comunicador.getSesionSeleccionada()));

                    //Crear fichero de sesiones, si no existe
                    if(!baseDatos.datosIllias.containsKey(activity.getString(R.string.FicheroSesionesGlobal)))
                    {
                        try
                        {
                            GenericTypes.ExecutionResult resultSubirFichero = FicheroNuevoAIlias(idDirectorio, activity.getString(R.string.FicheroSesionesGlobal), "");
                            if(resultSubirFichero.executionResult == GenericTypes.EXECUTION_RESULT.OK)
                            {
                                baseDatos.datosIllias.put(activity.getString(R.string.FicheroSesionesGlobal), resultSubirFichero.executionMessage);
                                baseDatos.CrearDatoIliasAsignatura(new Integer(Comunicador.getAsignaturaSeleccionada()).toString(), activity.getString(R.string.FicheroSesionesGlobal), resultSubirFichero.executionMessage);
                            }
                        }
                        catch(Exception exception)
                        {
                            //Do nothing now
                            Toast.makeText(activity.getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    //Coger el fichero de sesiones global y añadirle los datos de la actual
                    try
                    {
                        String ficheroGlobalRef = baseDatos.datosIllias.get(activity.getString(R.string.FicheroSesionesGlobal));
                        AsyncTasker asyncTaskerGetFichero = new AsyncTasker(ilias);
                        AsyncTask<String, Float, GenericTypes.ExecutionResult> resultGetFicheroTask = asyncTaskerGetFichero.execute(Ilias.GET_FICHERO, ficheroGlobalRef);
                        if(resultGetFicheroTask.get().executionResult == GenericTypes.EXECUTION_RESULT.OK)
                        {
                            String datosASubir = resultGetFicheroTask.get().executionMessage;
                            datosASubir += sesionSeleccionada.getFicheroSesion().toString();    //Se añaden los datos de la sesion actual

                            GenericTypes.ExecutionResult resultEditarFicheroGlobal = FicheroEditadoAIlias(activity.getString(R.string.FicheroSesionesGlobal), ficheroGlobalRef, datosASubir);
                            if(resultEditarFicheroGlobal.executionResult == GenericTypes.EXECUTION_RESULT.OK)
                            {
                                sesionSeleccionada.setEstado(SesionPracticas.ESTADO.SUBIDA);
                                baseDatos.ActualizaSesionPracticas(sesionSeleccionada);
                                GrupoPracticas grupoSesion = baseDatos.gruposPracticas.get(Integer.toString(Comunicador.getGrupoSeleccionado()));
                                if(grupoSesion.isEditable())
                                {
                                    grupoSesion.setEditable(false); //Si se ha subido una sesion, el grupo ya no se puede borrar
                                    baseDatos.ActualizaGrupoPracticas(grupoSesion);
                                }
                                //sesionesGrupoListView.getAdapter().notifyAll();
                                //thisAdapter.notifyDataSetChanged();
                                sesionesGrupoListView.postInvalidate();

                            }
                        }
                    }
                    catch(Exception exception)
                    {
                        //Do nothing now
                        Toast.makeText(activity.getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    ((SesionesActivity)activity).listener.onListChanged();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No hacer nada
                    break;
            }
        }
    };

    private GenericTypes.ExecutionResult FicheroNuevoAIlias(String idDirectorio, String nombreFichero, String datosASubir) throws InterruptedException, java.util.concurrent.ExecutionException {
        AsyncTasker asyncTaskerSubirFichero = new AsyncTasker(ilias);
        AsyncTask<String, Float, GenericTypes.ExecutionResult> resultSubirFicheroTask = asyncTaskerSubirFichero.execute(Ilias.SUBIR_FICHERO, nombreFichero, idDirectorio, datosASubir, SesionRegister.cabecera);
        GenericTypes.ExecutionResult resultSubirFichero = resultSubirFicheroTask.get();

        return resultSubirFichero;
    }

    private GenericTypes.ExecutionResult FicheroEditadoAIlias(String nombreFichero, String idFichero, String datosASubir) throws InterruptedException, java.util.concurrent.ExecutionException {
        AsyncTasker asyncTaskerSubirFichero = new AsyncTasker(ilias);
        AsyncTask<String, Float, GenericTypes.ExecutionResult> resultSubirFicheroTask = asyncTaskerSubirFichero.execute(Ilias.EDITAR_FICHERO, nombreFichero, idFichero, datosASubir, "");   //Sin cabecera, puesto que ya la llevan los datos
        GenericTypes.ExecutionResult resultSubirFichero = resultSubirFicheroTask.get();

        return resultSubirFichero;
    }

    DialogInterface.OnClickListener borrarSesionClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    SesionPracticas sesionSeleccionadaBorrar = baseDatos.sesionesPracticas.get(Integer.toString(Comunicador.getSesionSeleccionada()));
                    boolean operacionOk = true;
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
                        Toast.makeText(activity.getBaseContext(), activity.getString(R.string.ErrorBorrado), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        ((SesionesActivity)activity).listener.onListChanged();
                    }

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No hacer nada
                    break;
            }
        }
    };
}
