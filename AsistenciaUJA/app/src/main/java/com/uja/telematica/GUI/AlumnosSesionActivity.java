package com.uja.telematica.GUI;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uja.telematica.BO.AlumnoSesionListAdapter;
import com.uja.telematica.DAO.GenericTypes;
import com.uja.telematica.DAO.NfcTextRecord;
import com.uja.telematica.DAO.Alumno;
import com.uja.telematica.DAO.AlumnoAsignaturaGrupo;
import com.uja.telematica.DAO.BaseDatos;
import com.uja.telematica.DAO.Comunicador;
import com.uja.telematica.DAO.SesionFile;
import com.uja.telematica.DAO.SesionPracticas;
import com.uja.telematica.DAO.SesionRegister;
import com.uja.telematica.R;

import java.nio.charset.Charset;
import java.security.Timestamp;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public class AlumnosSesionActivity extends ActionBarActivity {

    BaseDatos baseDatos;
    ArrayList<Alumno> alumnos;
    ListView alumnosGrupoListView;
    AlumnoSesionListAdapter alumnoSesionListAdapter;

    Hashtable<String, String> alumnoIdDni;
    SesionPracticas sesionSeleccionada;
    SesionFile ficheroSesion;

    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;
    private SimpleDateFormat timeFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseDatos = Comunicador.getBaseDatos();
        sesionSeleccionada = baseDatos.sesionesPracticas.get(Integer.toString(Comunicador.getSesionSeleccionada()));
        alumnos = new ArrayList<Alumno>();
        setContentView(R.layout.activity_alumnos_grupo);
        alumnosGrupoListView = (ListView)findViewById(R.id.alumnosGrupoListView);
        timeFormatter = new SimpleDateFormat("HH:mm");
        ficheroSesion = sesionSeleccionada.getFicheroSesion();
        for(AlumnoAsignaturaGrupo alumnoAsignaturaGrupo : baseDatos.alumnosAsignaturaGrupo.values())
        {
            if(alumnoAsignaturaGrupo.getIdAsignatura() == Comunicador.getAsignaturaSeleccionada())
            {
                alumnos.add(baseDatos.alumnos.get(Integer.toString(alumnoAsignaturaGrupo.getIdAlumno())));
            }
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, getString(R.string.NoNfc), Toast.LENGTH_LONG).show();
        }

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[] { CrearRegistroInicio(
                getString(R.string.MsgInicioNfc), Locale.ENGLISH, true) });

        alumnoIdDni = new Hashtable<String, String >();
        for(Alumno alumno : alumnos)
        {
            alumnoIdDni.put(alumno.getDni(), alumno.getIdAlumno());
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
                    webView.loadUrl("file:///android_asset/AyudaAlumnosSesion.htm");
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
        alumnoSesionListAdapter = new AlumnoSesionListAdapter(getBaseContext(), alumnos, Comunicador.getAlumnosGrupo());
        alumnosGrupoListView = (ListView) findViewById(R.id.alumnosGrupoListView);
        alumnosGrupoListView.setAdapter(alumnoSesionListAdapter);
        //alumnosGrupoListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) {
                Toast.makeText(this, getString(R.string.NoNfc), Toast.LENGTH_LONG).show();
            }
            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            nfcAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
        }
    }

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

        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
            nfcAdapter.disableForegroundNdefPush(this);
        }
    }

    private void GuardarDatos() {
        //Recuperar los elementos marcados
        if(alumnosGrupoListView.getCount() > 0)
        {
            //Resetear el fichero de sesion
            SesionPracticas sesionSeleccionada = baseDatos.sesionesPracticas.get(new Integer(Comunicador.getSesionSeleccionada()).toString());
            SesionFile ficheroSesion = sesionSeleccionada.getFicheroSesion();

            sesionSeleccionada.PonerHoraFin();//Se pone la hora de fin en los registros, si no existe

            ficheroSesion.EscribirFichero();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = VolcarDatosTarjeta(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
                msgs = new NdefMessage[] { msg };
            }
            // Poner asistencia
            PonerAsistencia(msgs[0]);
        }
    }

    private void PonerAsistencia(NdefMessage ndefMsg)
    {
        if(sesionSeleccionada.getEstado() != SesionPracticas.ESTADO.SUBIDA && sesionSeleccionada.getEstado() != SesionPracticas.ESTADO.CERRADA)
        {
            if (ndefMsg != null)
            {
                NfcTextRecord element = NfcTextRecord.Parse(ndefMsg.getRecords()[0]);
                String dniAlumno = element.getTexto().split(",")[0];

                for (int i = 0; i < alumnosGrupoListView.getCount(); i++)
                {
                    View v = alumnosGrupoListView.getAdapter().getView(i, null, alumnosGrupoListView);
                    TextView tx = (TextView) v.findViewById(R.id.dniAlumnoGrupoTextView);

                    if(tx.getText().toString().equals(dniAlumno))
                    {
                        CheckBox checkBoxEntrada = (CheckBox) v.findViewById(R.id.alumnoSesionEntradaCheckBox);
                        CheckBox checkBoxSalida = (CheckBox) v.findViewById(R.id.alumnoSesionSalidaCheckBox);
                        String idAlumno = alumnoIdDni.get(dniAlumno);
                        alumnosGrupoListView.setSelection(i);
                        /*
                        alumnosGrupoListView.smoothScrollToPosition(i);
                        alumnosGrupoListView.invalidate();
                        alumnosGrupoListView.postInvalidate();
                        ((BaseAdapter)alumnosGrupoListView.getAdapter()).notifyDataSetChanged();*/

                        if(!checkBoxEntrada.isChecked())
                        {
                            //Si la tarjeta se pasa, es porque el alumno está presente y no tiene sentido deschequear los checkboxes
                            checkBoxEntrada.setChecked(true);
                            ProcesarClickCheckBoxEntrada(checkBoxEntrada, checkBoxSalida, idAlumno);
                        }
                        else
                        {
                            checkBoxSalida.setChecked(true);
                            ProcesarClickCheckBoxSalida(checkBoxSalida, idAlumno);
                        }

                        break;
                    }
                }
            }
        }
        else
        {
            Toast.makeText(this, getString(R.string.ErrorSesionSubida), Toast.LENGTH_LONG).show();
        }
    }


    private String VolcarDatosTarjeta(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
        sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
        sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                MifareClassic mifareTag = MifareClassic.get(tag);
                String type = "Unknown";
                switch (mifareTag.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        type = "Classic";
                        break;
                    case MifareClassic.TYPE_PLUS:
                        type = "Plus";
                        break;
                    case MifareClassic.TYPE_PRO:
                        type = "Pro";
                        break;
                }
                sb.append("Mifare Classic type: ");
                sb.append(type);
                sb.append('\n');

                sb.append("Mifare size: ");
                sb.append(mifareTag.getSize() + " bytes");
                sb.append('\n');

                sb.append("Mifare sectors: ");
                sb.append(mifareTag.getSectorCount());
                sb.append('\n');

                sb.append("Mifare blocks: ");
                sb.append(mifareTag.getBlockCount());
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private NdefRecord CrearRegistroInicio(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    public void OnAlumnoSesionEntradaCheckBoxClick(View v)
    {
        CheckBox checkBoxEntrada = (CheckBox)v;
        CheckBox checkBoxSalida = (CheckBox)((LinearLayout)v.getParent()).findViewById(R.id.alumnoSesionSalidaCheckBox);
        String alumnoId = ((TextView)((LinearLayout)v.getParent()).findViewById(R.id.idAlumnoRegistroTextView)).getText().toString();

        ProcesarClickCheckBoxEntrada(checkBoxEntrada, checkBoxSalida, alumnoId);
    }

    private void ProcesarClickCheckBoxEntrada(CheckBox checkBoxEntrada, CheckBox checkBoxSalida, String alumnoId) {
        Alumno alumno = baseDatos.alumnos.get(alumnoId);
        AlumnoAsignaturaGrupo alumnoAsignaturaGrupo = baseDatos.alumnosAsignaturaGrupo.get(new Integer(alumno.getIdAlumno()).toString());

        if(checkBoxEntrada.isChecked())
        {
            String horaEntrada = timeFormatter.format(Calendar.getInstance().getTime());

            //Si el alumno pertenece al grupo de la sesion, debe aparecer en el fichero, tanto si asiste como si no
            //Si no pertenece al grupo, solo aparecera cuando asista a la sesion
            if(ficheroSesion.registrosSesion.containsKey(new Integer(alumnoId)))
            {
                SesionRegister registroSesion = ficheroSesion.registrosSesion.get(new Integer(alumnoId));
                registroSesion.setAsistencia(true);
                registroSesion.setHoraEntrada(horaEntrada);
            }
            else
            {
                SesionRegister registroSesion = new SesionRegister(new Integer(alumno.getIdAlumno()).toString(), true, sesionSeleccionada.getFechaSesion(), sesionSeleccionada.getHoraSesion(), new Integer(sesionSeleccionada.getIdGrupo()).toString(), new Integer(sesionSeleccionada.getIdSesion()).toString(), alumno.getDni(),Integer.toString(alumnoAsignaturaGrupo.getIdGrupoPracticas()), horaEntrada, "");
                ficheroSesion.registrosSesion.put(new Integer(alumno.getIdAlumno()), registroSesion);
            }
        }
        else
        {
            checkBoxSalida.setChecked(false);
            if(alumnoAsignaturaGrupo.getIdGrupoPracticas() == sesionSeleccionada.getIdGrupo())
            {
                SesionRegister registroSesion = ficheroSesion.registrosSesion.get(new Integer(alumno.getIdAlumno()));
                registroSesion.setHoraEntrada("");
                registroSesion.setHoraSalida("");
                registroSesion.setAsistencia(false);
            }
            else
            {
                ficheroSesion.registrosSesion.remove(new Integer(alumnoId));
            }
        }

        checkBoxSalida.setEnabled(checkBoxEntrada.isChecked());
    }

    public void OnAlumnoSesionSalidaCheckBoxClick(View v)
    {
        CheckBox checkBoxSalida = (CheckBox)v;
        String alumnoId = ((TextView)((LinearLayout)v.getParent()).findViewById(R.id.idAlumnoRegistroTextView)).getText().toString();

        ProcesarClickCheckBoxSalida(checkBoxSalida, alumnoId);
    }

    private void ProcesarClickCheckBoxSalida(CheckBox checkBoxSalida, String alumnoId) {
        Alumno alumno = baseDatos.alumnos.get(alumnoId);
        SesionRegister registroSesion = ficheroSesion.registrosSesion.get(new Integer(alumno.getIdAlumno()));
        String horaSalida = "";
        if(checkBoxSalida.isChecked())
        {
            horaSalida = timeFormatter.format(Calendar.getInstance().getTime());
        }
        registroSesion.setHoraSalida(horaSalida);
    }
}
