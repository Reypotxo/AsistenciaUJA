package com.uja.telematica.GUI;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TimeUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.uja.telematica.DAO.BaseDatos;
import com.uja.telematica.DAO.Comunicador;
import com.uja.telematica.DAO.GenericTypes;
import com.uja.telematica.DAO.GrupoPracticas;
import com.uja.telematica.DAO.SesionFile;
import com.uja.telematica.DAO.SesionPracticas;
import com.uja.telematica.R;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class CrearSesionActivity extends ActionBarActivity implements View.OnClickListener {

    private EditText fechaEditText;
    private EditText horaEditText;
    private EditText duracionEditText;
    private CheckBox bateriaSesionesCheckBox;
    private TextView bateriaTextView;
    private EditText bateriaFechaFinEditText;

    private boolean nuevaSesion;

    private DatePickerDialog fechaDatePickerDialog, fechaFinSesionesDatePickerDialog;
    private TimePickerDialog horaTimePickerDialog;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;
    SimpleDateFormat timeFormatterFichero;
    private Date fechaSesion;
    private Date fechaFinSesion;
    private Date horaSesion;

    private HashSet<String> sesionesCreadas;

    private SesionPracticas sesion;
    private BaseDatos baseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_sesion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InicializarDatos();
    }

    private void InicializarDatos() {
        fechaEditText = (EditText) findViewById(R.id.fechaSesionEditText);
        horaEditText = (EditText) findViewById(R.id.horaSesionEditText);
        duracionEditText = (EditText) findViewById(R.id.duracionSesionEditText);
        bateriaSesionesCheckBox = (CheckBox) findViewById(R.id.bateriaSesionesCheckBox);
        bateriaTextView = (TextView)findViewById(R.id.fechaFinTextView);
        bateriaFechaFinEditText = (EditText) findViewById(R.id.fechaFinSesionEditText);
        dateFormatter = new SimpleDateFormat("yyyyMMdd");
        timeFormatter = new SimpleDateFormat("HH:mm");
        timeFormatterFichero = new SimpleDateFormat("HHmm");

        baseDatos = Comunicador.getBaseDatos();
        GrupoPracticas grupoPracticas = baseDatos.gruposPracticas.get(Integer.toString(Comunicador.getGrupoSeleccionado()));
        horaEditText.setText(grupoPracticas.getHora());
        try {
            horaSesion = timeFormatter.parse(grupoPracticas.getHora());
        } catch (ParseException e) {
            //
        }

        sesionesCreadas = new HashSet<String>();
        for(SesionPracticas sesionPracticas : baseDatos.sesionesPracticas.values())
        {
            sesionesCreadas.add(sesionPracticas.GetFechaHoraString());
        }

        nuevaSesion = Boolean.parseBoolean ((String) this.getIntent().getExtras().get("NUEVASESION"));
        if(!nuevaSesion)
        {
            sesion = baseDatos.sesionesPracticas.get(new Integer(Comunicador.getSesionSeleccionada()).toString());
            fechaEditText.setText(sesion.getFechaSesion());
            horaEditText.setText(sesion.getHoraSesion());
            duracionEditText.setText(Integer.toString(sesion.getDuracion()));

            try
            {
                fechaSesion = dateFormatter.parse(sesion.getFechaSesion());
                horaSesion = timeFormatter.parse(sesion.getHoraSesion());
            }
            catch (ParseException parseEx)
            {
                Toast.makeText(CrearSesionActivity.this, R.string.errorFechaHoraSesion, Toast.LENGTH_SHORT).show();
            }

            this.setTitle(getString(R.string.editar_sesion));
            TextView nuevaSesionTextView = (TextView) findViewById(R.id.nuevaSesionTextView);
            nuevaSesionTextView.setText(getString(R.string.editar_sesion));
            bateriaSesionesCheckBox.setEnabled(false);  //No se pueden crear batería de sesiones durante la edicion
            bateriaSesionesCheckBox.setVisibility(View.INVISIBLE);
            EditText fechaFinEditText = (EditText) findViewById(R.id.fechaFinSesionEditText);
            fechaFinEditText.setVisibility(View.INVISIBLE);
            TextView fechaFinTextView = (TextView) findViewById(R.id.fechaFinTextView);
            fechaFinTextView.setVisibility(View.INVISIBLE);
        }
        bateriaSesionesCheckBox.setOnClickListener(this);
        setDateTimeField();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        InicializarDatos();
    }

    private void setDateTimeField() {
        fechaEditText.setOnClickListener(this);
        horaEditText.setOnClickListener(this);
        bateriaFechaFinEditText.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();

        fechaDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fechaSesion = newDate.getTime();
                fechaEditText.setText(dateFormatter.format(fechaSesion));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        horaTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            public void onTimeSet(TimePicker view, int hour, int minute) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(1,1,1, hour, minute);
                horaSesion = newDate.getTime();
                horaEditText.setText(timeFormatter.format(horaSesion));
            }

        },newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE), true);

        fechaFinSesionesDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fechaFinSesion = newDate.getTime();
                bateriaFechaFinEditText.setText(dateFormatter.format(fechaFinSesion));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void OnButtonAceptarCrearSesionClick(View view)
    {
        int totalSesiones = 1;
        SesionPracticas primeraSesion = null;

        if(!(fechaSesion == null || horaSesion == null))
        {
            if(bateriaSesionesCheckBox.isChecked())
            {
                String fechaFinTexto = bateriaFechaFinEditText.getText().toString();
                if(!fechaFinTexto.equals(""))
                {
                    Calendar initialCalendar = GregorianCalendar.getInstance();
                    initialCalendar.setTime(fechaSesion);
                    Calendar endingCalendar = GregorianCalendar.getInstance();
                    endingCalendar.setTime(fechaFinSesion);

                    //Se comprueba que la fecha de fin es posterior a la de inicio
                    if(initialCalendar.before(endingCalendar))
                    {
                        DateTime dateTimeFechaSesion = new DateTime(fechaSesion);
                        DateTime dateTimeFechaFinSesion = new DateTime(fechaFinSesion);

                        totalSesiones = Weeks.weeksBetween(dateTimeFechaSesion, dateTimeFechaFinSesion).getWeeks() + 1;
                    }
                    else {
                        totalSesiones = GenericTypes.UNDEFINED_NUM;
                    }
/*
                    int deltaYears = 0;
                    for(int i = 0;i < diffYears;i++){
                        deltaYears += initialCalendar.getMaximum(Calendar.WEEK_OF_YEAR);
                        initialCalendar.add(Calendar.YEAR, 1);
                    }
                    totalSesiones = (endingWeek + deltaYears) - initialWeek;*/
                }
            }

            boolean primerError = true;
            if(totalSesiones >= 1)
            {
                for(int i = 0; i < totalSesiones; i++)
                {
                    long millisASumar = i * 7 * 24 * 60 * GenericTypes.ONE_MINUTE_IN_MILLIS;    //Milisegundos en una semana, para ir creando sesiones posteriores
                    Date fechaSesionBateria = new Date(fechaSesion.getTime() + millisASumar);
                    String fechaSesionTexto = dateFormatter.format(fechaSesionBateria);
                    String horaSesionTexto = timeFormatter.format(horaSesion);
                    String horaSesionTextoFichero = timeFormatterFichero.format(horaSesion);
                    String fechaCreacion = dateFormatter.format(Calendar.getInstance().getTime());
                    int duracionSesion = Integer.parseInt(duracionEditText.getText().toString());
                    Integer asignaturaSeleccionada = new Integer (Comunicador.getAsignaturaSeleccionada());
                    Integer grupoSeleccionado = new Integer (Comunicador.getGrupoSeleccionado());
                    Integer idSesion = baseDatos.GetMaxSesionPracticas();

                    String fechaHora = fechaSesionTexto + ":" + horaSesionTexto;
                    if(!sesionesCreadas.contains(fechaHora))
                    {
                        String nombreFichero = grupoSeleccionado + "_" + fechaSesionTexto + "_" + horaSesionTextoFichero;    //El nombre local del fichero sera la fecha y hora de la sesion
                        String rutaFichero = Comunicador.baseDatosDir + "/" + asignaturaSeleccionada + "/" + grupoSeleccionado.toString() + "/" + nombreFichero + ".csv";
                        File ficheroSesion = new File(rutaFichero);

                        //Actualiza la sesion, renombrando fichero si lo hubiera
                        if(sesion != null && !nuevaSesion)
                        {
                            //No se puede modificar una sesion cerrada o subida.
                            if(sesion.getEstado() != SesionPracticas.ESTADO.CERRADA && sesion.getEstado() != SesionPracticas.ESTADO.SUBIDA)
                            {
                                sesion.setFechaSesion(fechaEditText.getText().toString());
                                sesion.setHoraSesion(horaEditText.getText().toString());

                                sesionesCreadas.add(fechaHora);

                                //Se actualiza el nombre del fichero de sesion actual.
                                File ficheroActualSesion = new File(sesion.getRutaFicheroSesionLocal());
                                ficheroActualSesion.renameTo(ficheroSesion);
                                sesion.setRutaFicheroSesionLocal(rutaFichero);

                                baseDatos.ActualizaSesionPracticas(sesion);
                            }
                            else{
                                //No se debería haber entrado aquí, hay que evitarlo en la ventana previa
                            }
                        }
                        else
                        {
                            if(ficheroSesion.exists())
                            {
                                Toast.makeText(CrearSesionActivity.this, R.string.errorSesionRepetida, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                SesionFile nuevoFicheroSesion = SesionFile.CrearFicheroSesion(baseDatos, grupoSeleccionado.toString(), rutaFichero, fechaEditText.getText().toString(), horaEditText.getText().toString(), idSesion.toString());

                                if(nuevoFicheroSesion != null)
                                {
                                    //if(sesion == null)
                                    //{
                                   sesion = new SesionPracticas(idSesion, grupoSeleccionado, fechaCreacion, dateFormatter.format(fechaSesionBateria), horaEditText.getText().toString(), rutaFichero, SesionPracticas.ESTADO.CREADA, "", duracionSesion);
                                   if(!baseDatos.CrearSesionPracticas(sesion))
                                   {
                                       sesion = null;  //Error durante la creacion de la sesion en la BBDD
                                       Toast.makeText(CrearSesionActivity.this, R.string.errorSesionBBDD, Toast.LENGTH_SHORT).show();;
                                   }
                                   else
                                   {
                                       sesionesCreadas.add(fechaHora);
                                   }
                                    //}
                                }
                                else
                                {
                                    Toast.makeText(CrearSesionActivity.this, R.string.errorFicheroSesion, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        if(i==0)
                        {
                            primeraSesion = sesion;
                        }
                    }
                    else
                    {
                        if(primerError)
                        {
                            Toast.makeText(CrearSesionActivity.this, R.string.warningFechaHoraSesionRepetidas, Toast.LENGTH_SHORT).show();
                            primerError = false;
                        }
                    }
                }
            }
            else
            {
                Toast.makeText(CrearSesionActivity.this, R.string.errorFechaFinSesion, Toast.LENGTH_SHORT).show();;
            }
        }
        else
        {
            Toast.makeText(CrearSesionActivity.this, R.string.errorFechaHoraSesion, Toast.LENGTH_SHORT).show();;
        }

        if(primeraSesion != null)
        {
            Comunicador.setSesionSeleccionada(primeraSesion.getIdSesion());
            this.finish();
        }
    }
/*
    public void OnSeleccionarFechaSesionClick(View view) {
        EditText fechaEditText = (EditText) findViewById(R.id.fechaSesionEditText);

    }

    public void OnSeleccionarHoraSesionClick(View view) {
        EditText horaEditText = (EditText) findViewById(R.id.horaSesionEditText);
    }*/

    @Override
    public void onClick(View view) {
        if(view == fechaEditText)
        {
            fechaDatePickerDialog.show();
        }
        else if(view == horaEditText)
        {
            horaTimePickerDialog.show();
        }
        else if(view == bateriaSesionesCheckBox)
        {
            bateriaTextView.setEnabled(bateriaSesionesCheckBox.isChecked());
            bateriaFechaFinEditText.setEnabled(bateriaSesionesCheckBox.isChecked());
        }
        else if(view == bateriaFechaFinEditText)
        {
            fechaFinSesionesDatePickerDialog.show();
        }
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
        switch(id)
        {
            case R.id.action_ayuda:

                View ayudaView = getLayoutInflater().inflate(R.layout.ayuda, null, false);

                WebView webView = (WebView)ayudaView.findViewById(R.id.webViewTexto);

                try
                {
                    webView.loadUrl("file:///android_asset/AyudaCrearSesion.htm");
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
