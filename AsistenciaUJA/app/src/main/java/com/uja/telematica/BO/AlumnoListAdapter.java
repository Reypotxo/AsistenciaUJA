package com.uja.telematica.BO;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.uja.telematica.DAO.Alumno;
import com.uja.telematica.DAO.AlumnoAsignaturaGrupo;
import com.uja.telematica.DAO.BaseDatos;
import com.uja.telematica.DAO.Comunicador;
import com.uja.telematica.DAO.GenericTypes;
import com.uja.telematica.R;

import java.util.List;

/**
 * Created by Alfonso Troyano on 24/07/2015.
 *
 * http://www.hermosaprogramacion.com/2014/11/android-navigation-drawer-tutorial/
 */
public class AlumnoListAdapter extends BaseAdapter
{
    private Context context;
    private List<Alumno> items;
    private List<Integer> alumnosGrupo;
    private BaseDatos baseDatos;

    public AlumnoListAdapter(Context context, List<Alumno> items, List<Integer> alumnosGrupo)
    {
        this.context = context;
        this.items = items;
        this.alumnosGrupo = alumnosGrupo;
        baseDatos = Comunicador.getBaseDatos();
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
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
            convertView = inflater.inflate(R.layout.elemento_lista_grupo, null);
        }


        ImageView foto = (ImageView) convertView.findViewById(R.id.fotoImageView);
        TextView nombre = (TextView) convertView.findViewById(R.id.nombreAlumnoTextView);
        TextView grupo = (TextView) convertView.findViewById(R.id.grupoAlumnoTextView);
        final CheckBox alumnoCheckBox = (CheckBox)convertView.findViewById(R.id.alumnoGrupoCheckBox);

        final Alumno alumno = (Alumno) getItem(position);
        foto.setImageResource(R.drawable.no_photo_xsmall);
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
        //alumnoCheckBox.setChecked(alumnosGrupo.contains(new Integer(alumno.getIdAlumno())));    //Pone el alumno a TRUE si está ya en el grupo

        int grupoId = baseDatos.GetGrupoAlumnoAsignatura(Integer.parseInt(alumno.getIdAlumno()), Comunicador.getAsignaturaSeleccionada());
        alumnoCheckBox.setChecked(grupoId != GenericTypes.UNDEFINED_NUM);

        if(grupoId != GenericTypes.UNDEFINED_NUM && grupoId != Comunicador.getGrupoSeleccionado())
        {
            convertView.setEnabled(false);
            convertView.setBackgroundColor(Color.RED);
            alumnoCheckBox.setEnabled(false);
        }

        alumnoCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = alumno.getIdAlumno();
                int grupoId = GenericTypes.UNDEFINED_NUM;
                if(alumnoCheckBox.isChecked())
                {
                    grupoId = Comunicador.getGrupoSeleccionado();
                }

                AlumnoAsignaturaGrupo alumnoAsignaturaGrupo = baseDatos.alumnosAsignaturaGrupo.get(id);
                alumnoAsignaturaGrupo.setIdGrupoPracticas(grupoId);
                baseDatos.ActualizaGrupoPracticasAlumnoAsignatura(alumnoAsignaturaGrupo);
            }
        });

        return convertView;
    }
}
