package com.uja.telematica.BO;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.uja.telematica.DAO.Alumno;
import com.uja.telematica.DAO.BaseDatos;
import com.uja.telematica.DAO.Comunicador;
import com.uja.telematica.DAO.GenericTypes;
import com.uja.telematica.DAO.SesionPracticas;
import com.uja.telematica.DAO.SesionRegister;
import com.uja.telematica.R;

import java.util.List;

/**
 * Created by Alfonso Troyano on 24/07/2015.
 *
 * http://www.hermosaprogramacion.com/2014/11/android-navigation-drawer-tutorial/
 */
public class AlumnoSesionListAdapter extends BaseAdapter
{
    private Context context;
    private List<Alumno> items;
    private List<Integer> alumnosGrupo;
    private BaseDatos baseDatos;
    private SesionPracticas sesionSeleccionada;


    public AlumnoSesionListAdapter(Context context, List<Alumno> items, List<Integer> alumnosGrupo)
    {
        this.context = context;
        this.setItems(items);
        this.alumnosGrupo = alumnosGrupo;
        baseDatos = Comunicador.getBaseDatos();
        sesionSeleccionada = baseDatos.sesionesPracticas.get(new Integer(Comunicador.getSesionSeleccionada()).toString());
    }

    @Override
    public int getCount() {
        return this.getItems().size();
    }

    @Override
    public Object getItem(int position) {
        return this.getItems().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)parent.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.elemento_lista_sesion, null);
        }


        ImageView foto = (ImageView) convertView.findViewById(R.id.fotoSesionImageView);
        TextView nombre = (TextView) convertView.findViewById(R.id.nombreAlumnoSesionTextView);
        TextView grupo = (TextView) convertView.findViewById(R.id.grupoAlumnoSesionTextView);
        TextView dniAlumno = (TextView) convertView.findViewById(R.id.dniAlumnoGrupoTextView);
        TextView idAlumnoRegistro = (TextView) convertView.findViewById(R.id.idAlumnoRegistroTextView);
        CheckBox alumnoEntradaCheckBox = (CheckBox)convertView.findViewById(R.id.alumnoSesionEntradaCheckBox);
        CheckBox alumnoSalidaCheckBox = (CheckBox)convertView.findViewById(R.id.alumnoSesionSalidaCheckBox);

        Alumno alumno = (Alumno) getItem(position);
        foto.setImageResource(R.drawable.no_photo_xsmall);
        dniAlumno.setText(alumno.getDni());
        idAlumnoRegistro.setText(alumno.getIdAlumno());
        nombre.setText(alumno.getNombre() + " " + alumno.getApe1() + " " + alumno.getApe2());
        int grupoAlumno = baseDatos.alumnosAsignaturaGrupo.get(alumno.getIdAlumno()).getIdGrupoPracticas();
        String grupoAlumnoTexto = "";
        if(baseDatos.gruposPracticas.containsKey(Integer.toString(grupoAlumno)))
        {
            grupoAlumnoTexto = baseDatos.gruposPracticas.get(Integer.toString(grupoAlumno)).getId();
        }
        else
        {
            grupoAlumnoTexto = context.getString(R.string.sin_grupo);
        }
        grupo.setText(grupoAlumnoTexto);
        //grupo.setText(alumno.getDni());
        //alumnoCheckBox.setChecked(alumnosGrupo.contains(new Integer(alumno.getIdAlumno())));    //Pone el alumno a TRUE si está ya en el grupo

        int grupoId = baseDatos.GetGrupoAlumnoAsignatura(Integer.parseInt(alumno.getIdAlumno()), Comunicador.getAsignaturaSeleccionada());

        //Leer el fichero de la sesion y ponerlo checked aqui
        //boolean asistencia = false;
        if(sesionSeleccionada.getFicheroSesion().registrosSesion.containsKey(new Integer(alumno.getIdAlumno())))
        {
            SesionRegister registroSesion =(SesionRegister)sesionSeleccionada.getFicheroSesion().registrosSesion.get(new Integer(alumno.getIdAlumno()));
            if(!registroSesion.getHoraEntrada().equals(""))
            {
                alumnoEntradaCheckBox.setChecked(true);
                alumnoSalidaCheckBox.setEnabled(alumnoEntradaCheckBox.isChecked());

                if(!registroSesion.getHoraSalida().equals(""))
                {
                    alumnoSalidaCheckBox.setChecked(true);
                }
            }
        }

        if(grupoId == GenericTypes.UNDEFINED_NUM)
        {
            convertView.setBackgroundColor(Color.YELLOW);   //Alumnos sin grupo apareceran en amarillo en la vista de la sesion
        }
        else
        {
            if(grupoId != Comunicador.getGrupoSeleccionado())
            {
                convertView.setBackgroundColor(Color.RED);
            }
        }

        //La sesion no se puede editar si ha sido subida
        if(sesionSeleccionada.getEstado() == SesionPracticas.ESTADO.SUBIDA)
        {
            convertView.setEnabled(false);
            convertView.setBackgroundColor(Color.LTGRAY);
            alumnoEntradaCheckBox.setEnabled(false);
            alumnoSalidaCheckBox.setEnabled(false);
        }

        return convertView;
    }

    public List<Alumno> getItems() {
        return items;
    }

    public void setItems(List<Alumno> items) {
        this.items = items;
    }
}
