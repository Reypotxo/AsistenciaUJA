package com.uja.telematica.DAO;

//Import's necesarios para crear el XML
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.xmlpull.v1.XmlSerializer;
import android.content.Context;
import android.util.Base64;
import android.util.Xml;

/**
 * XML.Java es una clase que implementa toda la funcionalidad necesaria para crear tanto el XML que
 * madaremos a Docencia Virtual, como el XML interno que se almacenar‡
 *
 * Docencia virtual establece unas reglas sobre el formato que deben tener los documentos que se suben
 * a la plataforma. Estas reglas estan implementadas mediante un documento XML
 *
 * Posteriormente, la informacion de usuario que se almacena dentro de este documento tambien tendr‡
 * formato de XML para un mejor analisis posterior.
 *
 * @version 1.0 , Junio 2014
 * @author Jose Manuel JimŽnez Bravo
 */

public class XML{

    /**
     *Contructor por defecto
     */
    public XML(){}



    /**
     * Esta funci—n permite reconstruir el XML que se mandar‡ a la plataforma de Docencia virtual
     * incluyendo las descripciones, el titulo y el contenido
     *
     * Se debe recoger fecha y hora en la que se realiza el registro, comprobacion de si se trata de
     * un registro de entrada o de salida, comprobacion de si es necesario introducir PIN y
     * codificaci—n en base 64 de la informaci—n que se almacenar‡ en el fichero
     *
     * @param nombreFichero nombre del fichero a subir
     *
     * @return Devuelve un String con el XML que debe enviar a Docencia Virtual mediante el WebService addFile
     */
    public String CrearXMLEnvio (String nombreFichero, String datosASubir, String cabecera){

        //Variable que recoger‡ el XML que vamos a crear
        String XMLenvio = null;
        //Variable que recoger‡ un String con el XML de informaci—n de usuario
        //String TextoDocumento = null;

        //Leve descripcion del fichero, se puede acceder a ella desde la opcion "descricion" en Docencia virtual
        String descripcionfichero = "Archivo generado mediante la aplicacion Asistencia UJA desde un terminal Android";

        //Llamamos a la funcion CrearXMLinterior para generar el XML con informaci—n de usuario que va dentro
        //del documento XML que se manda a Docencia Virtual
        //TextoDocumento = CrearXMLinterior(usuario,accion,hora,dia,pin);

        //Realizamos la codificaci—n en base 64. Primero se pasa el String a Byte y luego se codifica
        String datos = cabecera + datosASubir;
        byte[] bytes = datos.getBytes();
        String contenido = Base64.encodeToString(bytes, Base64.DEFAULT);

        //Formamos el XML final que se enviar‡ a Docencia Virtual
        XMLenvio = "<?xml version=\"1.0\" encoding=\"utf-8\"?><!DOCTYPE File PUBLIC \"-//ILIAS//DTD FileAdministration//EN\" \"http://dv.ujaen.es:80/docencia/xml/ilias_file_3_8.dtd\"><!--Exercise Object--><File version=\"1\" size=\"38\" type=\"text/plain\"><Filename>"+nombreFichero+".csv</Filename><Title>"+nombreFichero+"</Title><Description>"+descripcionfichero+"</Description><Content mode=\"PLAIN\">"+contenido+"</Content><Versions><Version id=\"1\"/></Versions></File>";

        //Se devuelve el Documento XML formado
        return XMLenvio;
    }



    /**
     * Esta funcion nos permite creal un XML con la informaci—n de usuario. Este XML se devolver‡ en forma de
     * String para despues ser codificado en base 64 y ser almacenado en el campo informaci—n del documento
     * XML que se manda a Docencia Virtual
     *
     * @param usuario es el identificador de usuario (en modo texto)
     *
     * @param accion recoge la informacion de si se trata de una entrada o salida
     *
     * @param fecha recoge el dia, mes y a–o en que se realiza el registro
     *
     * @return Devuelve un String con el XML que debe incluir dentro del campo contenido del XML que se mande a Docencia Virtual
     */
    /*
    public String CrearXMLinterior(String usuario, String accion, String Hora, String fecha){

        String informacionInterior = null;

        //Se estrablece la cabecer‡ del documento XML
        String cabecera = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

        //Montamos el documento XML
        informacionInterior = cabecera+"<REGISTRO><Usuario>"+usuario+"</Usuario><Accion>"+accion+"</Accion><Fecha>"+fecha+"</Fecha><Hora>"+Hora+"</Hora><Pin>"+PIN+"</Pin></REGISTRO>";

        //Se devuelve el documento XML con la informacion de usuario
        return informacionInterior;

    }*/


}
