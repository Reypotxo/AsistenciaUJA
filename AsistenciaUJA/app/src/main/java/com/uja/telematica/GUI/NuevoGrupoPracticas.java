package com.uja.telematica.GUI;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.uja.telematica.DAO.BaseDatos;
import com.uja.telematica.DAO.Comunicador;
import com.uja.telematica.DAO.GenericTypes;
import com.uja.telematica.DAO.GrupoPracticas;
import com.uja.telematica.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NuevoGrupoPracticas extends ActionBarActivity implements View.OnClickListener{

    private boolean nuevoGrupo;
    GrupoPracticas grupoPracticas;
    BaseDatos baseDatos;
    private SimpleDateFormat timeFormatter;
    private Date horaGrupo;
    private EditText horaEditText;
    private TimePickerDialog horaTimePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timeFormatter = new SimpleDateFormat("HH:mm");
        baseDatos = Comunicador.getBaseDatos();
        nuevoGrupo = Boolean.parseBoolean((String) this.getIntent().getExtras().get("NUEVOGRUPO"));

        setContentView(R.layout.activity_nuevo_grupo_practicas);

        Spinner spinner = (Spinner) findViewById(R.id.diaGrupoSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.dias_practicas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        horaEditText = (EditText) findViewById(R.id.horaGrupoEditText);

        if(!nuevoGrupo)
        {
            grupoPracticas = baseDatos.gruposPracticas.get(new Integer(Comunicador.getGrupoSeleccionado()).toString());
            EditText idEditText = (EditText) findViewById(R.id.grupoIdEditText);
            EditText descripcionEditText = (EditText) findViewById(R.id.grupoDescripcionEditText);
            try
            {
                horaGrupo = timeFormatter.parse(grupoPracticas.getHora());
            }
            catch (java.text.ParseException parseEx)
            {
                Toast.makeText(this, R.string.errorDiaHoraGrupo, Toast.LENGTH_SHORT).show();
            }

            idEditText.setText(grupoPracticas.getId());
            descripcionEditText.setText(grupoPracticas.getDescripcion());
            horaEditText.setText(grupoPracticas.getHora());
            spinner.setSelection(((ArrayAdapter<String>)spinner.getAdapter()).getPosition(grupoPracticas.getDia()));
        }

        setDateTimeField();
    }

    private void setDateTimeField() {
        horaEditText.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();

        horaTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            public void onTimeSet(TimePicker view, int hour, int minute) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(1,1,1, hour, minute);
                horaGrupo = newDate.getTime();
                horaEditText.setText(timeFormatter.format(horaGrupo));
            }

        },newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nuevo_grupo_practicas, menu);
        return true;
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

    public void OnButtonAceptarCrearGrupoClick(View view) {
        EditText idEditText = (EditText) findViewById(R.id.grupoIdEditText);

        if(idEditText.getText() != null)
        {
            String grupoId = idEditText.getText().toString();

            if(grupoId.isEmpty())
            {
                Toast.makeText(this, R.string.errorIdVacio, Toast.LENGTH_LONG).show();;
            }
            else
            {
                if(!baseDatos.gruposPracticas.containsKey(grupoId))
                {
                    Spinner spinner = (Spinner) findViewById(R.id.diaGrupoSpinner);
                    EditText horaGrupoEditText = (EditText) findViewById(R.id.horaGrupoEditText);
                    if(!horaGrupoEditText.getText().toString().equals(""))
                    {
                        String dia = (String)spinner.getSelectedItem();
                        String hora = horaGrupoEditText.getText().toString();

                        EditText descripcionEditText = (EditText) findViewById(R.id.grupoDescripcionEditText);

                        String descripcion = null;
                        if(descripcionEditText.getText() != null)
                        {
                            descripcion = descripcionEditText.getText().toString();
                        }

                        if(nuevoGrupo)
                        {
                            int idAsignatura = Integer.parseInt((String) this.getIntent().getExtras().get("IDASIGNATURA"));

                            GrupoPracticas grupoPracticas = new GrupoPracticas(baseDatos.gruposPracticas.size(), grupoId, descripcion, true, false, null, idAsignatura, dia, hora);
                            baseDatos.CrearGrupoPracticas(grupoPracticas);

                            String grupoDirNombre = Comunicador.baseDatosDir + "/" + idAsignatura + "/" + grupoPracticas.getGrupoId();
                            File grupoDir = new File(grupoDirNombre);
                            if(grupoDir.exists())
                            {
                                grupoDir.delete();
                            }

                            grupoDir.mkdir();
                        }
                        else
                        {
                            grupoPracticas.setId(grupoId);
                            grupoPracticas.setDescripcion(descripcion);
                            grupoPracticas.setDia(dia);
                            grupoPracticas.setHora(hora);
                            baseDatos.ActualizaGrupoPracticas(grupoPracticas);
                        }
                        this.finish();
                    }
                    else
                    {
                        Toast.makeText(this, R.string.errorDiaHoraGrupo, Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(this, R.string.errorIdRepetido, Toast.LENGTH_LONG).show();
                }
            }
        }
        else
        {
            Toast.makeText(this, R.string.errorIdVacio, Toast.LENGTH_LONG).show();;
        }
    }

    @Override
    public void onClick(View view) {
        if(view == horaEditText) {
            horaTimePickerDialog.show();
        }
    }
}
