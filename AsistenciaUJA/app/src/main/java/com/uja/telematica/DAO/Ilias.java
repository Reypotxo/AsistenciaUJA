/**
 *
 */
package com.uja.telematica.DAO;

import android.content.Context;
import android.util.Base64;

import com.uja.telematica.R;

import org.apache.http.protocol.HTTP;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.uja.telematica.DAO.GenericTypes.*;

/**
 * Implementa toda la funcionalidad necesaria para que un dispositivo Android
 * pueda consumir un WebService de Ilias.
 * <p/>
 * En esta clase se implementan los metodos necesarios para que la aplicacion pueda utilizar los WebServices
 * de la Universidad de Jaen y almacenar el fichero de registro en la carpeta adecuada.
 * <p/>
 * Las comunicaciones con WebServices desde un terminal Android se consiguen gracias a la libreria ksoap2
 * <p/>
 * Los metodos que se implementan en esta clase son:
 * -Login: Iniciar sesion en la plataforma virtual
 * -getUserByID: Obtener el numero de identificador de usuario
 * -getObjectsByTittle: Buscar la carpeta donde guardar el fichero
 * -addFile: Guardar el fichero
 * -getCourseXML: Obtener toda la informacion de un curso
 *
 * @author Alfonso Troyano Montes
 * @author Jose Manuel Jimenez Bravo
 * @version 1.0 , Marzo 2015
 */
public class Ilias{

    public static final String LOGIN = "LOGIN";
    public static final String GET_ASIGNATURAS = "GET_ASIGNATURAS";
    public static final String GET_IDUSUARIO = "GET_IDUSUARIO";
    public static final String GET_ALUMNO = "GET_ALUMNO";
    public static final String GET_FICHERO = "GET_FICHERO";
    public static final String SUBIR_FICHERO = "SUBIR_FICHERO";
    public static final String EDITAR_FICHERO = "EDITAR_FICHERO";

    String sid;        //SID que nos devuelve el LOGIN
    int idUser;                //Identificador de usuario que nos devuelve GETUSERBYID
    String VarXMLenvio = null;    //XML que se carga en Docencia virtual

    String usuario;
    String contrasena;
    String error;
    Context activityContext;

    /**
     *
     * @param usuario, es el identificador de usuario que tenemos en docencia virtual
     * @param contrasena, el la contrasena que tiene nuestra cuenta de docencia virtual
     * @param activityContext permite acceder a los strings definidos como recurso
     */
    public Ilias(String usuario, String contrasena, Context activityContext)
    {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.activityContext = activityContext;
        error = "";
        sid = null;
        idUser = Integer.MIN_VALUE;
    }

    /**
     * Funcion que nos permite realizar el LOGIN e iniciar una conexion
     *
     * @return Identificador de sesion (sid), de tipo String
     * @throws Exception Error al realizar la conexion al Werservice
     */
    public GenericTypes.ExecutionResult Login() {
        //La siguiente informacion puede obtenerse del WSDL de la Universidad de Jaen
        //http://dv.ujaen.es/docencia/webservice/soap/server.php
        //En dicha pagina se puede consultar todos los servicios que se ofrecen y como trabajar
        //con ellos

        final String SOAP_ACTION = "urn:ilUserAdministration#login";
        final String METHOD = "login";
        final String NAMESPACE = "urn:ilUserAdministration";
        final String ENDPOINTWS = "http://dv.ujaen.es/docencia/webservice/soap/server.php";

        //Respuesta que devolvera el webservice
        GenericTypes.ExecutionResult res = new GenericTypes.ExecutionResult();

        //Objeto para hacer la llamada al WS
        SoapObject userRequest = new SoapObject(NAMESPACE, METHOD);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        //Pasar los parametros
        userRequest.addProperty("client", "docencia");
        userRequest.addProperty("username", usuario);
        userRequest.addProperty("password", contrasena);

        //Se empaquetan los parametros
        envelope.setOutputSoapObject(userRequest);

        //Se establece la comunicacion con la direccion que ofrece los WebServices
        HttpTransportSE androidHttpTransport = null;
        androidHttpTransport = new HttpTransportSE(ENDPOINTWS);

        try {
            //Se solicicta el WebService y se envian los parametros
            androidHttpTransport.call(METHOD, envelope);

            //Se obtiene la respuesta por parte del WebService, en esta caso el identificador de sesion
            sid = envelope.getResponse().toString();
            res.executionResult = EXECUTION_RESULT.OK;
            res.executionMessage = activityContext.getString(R.string.loginCorrecto);

        } catch (Exception e) {
            //System.out.println(e.getMessage());
            //Si el Login no se ha podido realizar correctamente se debe mandar un mensaje de error
            //para que el hilo que realiza el registro se detenga y no continue con el proceso
            res.executionResult = EXECUTION_RESULT.ERROR;
            res.executionMessage = e.getMessage();
        }

        //Se devuelve el identificador de sesion obtenido al hacer el Login
        return res;
    }

    /**
     *Funcion que nos permite obtener toda la informacion sobre una asignatura
     *
     *@param asignaturaId es el identificador numerico del curso del cual se obtendra la informacion
     *
     *@return XML con toda la informacion del curso.
     *
     *@throws Exception Error al realizar la conexion al Werservice
     */

    public GenericTypes.ExecutionResult GetAsignatura(int asignaturaId){

        //La siguiente informacion puede obtenerse del WSDL de la Universidad de Jaen
        //http://dv.ujaen.es/docencia/webservice/soap/server.php
        //En dicha pagina se puede consultar todos los servicios que se ofrecen y como trabajar
        //con ellos
        final String SOAP_ACTION = "urn:ilUserAdministration#getCourseXML";
        final String METHOD = "getCourseXML";
        final String NAMESPACE = "urn:ilUserAdministration";
        final String ENDPOINTWS = "http://dv.ujaen.es/docencia/webservice/soap/server.php";

        //Respuesta que devolvera el webservice
        GenericTypes.ExecutionResult res = new GenericTypes.ExecutionResult();


        //Objeto para hacer la llamada al WS
        SoapObject userRequest=new SoapObject(NAMESPACE, METHOD);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);


        //Pasar los parametros
        userRequest.addProperty("sid",sid);
        userRequest.addProperty("course_id",asignaturaId);

        //Se empaquetan los parametros
        envelope.setOutputSoapObject(userRequest);

        //Se establece la comunicacion con la direccion que ofrece los WebServices
        HttpTransportSE androidHttpTransport = null;
        androidHttpTransport = new HttpTransportSE(ENDPOINTWS);

        try
        {
            //Se solicita el WebService y se envian los parametros
            androidHttpTransport.call(METHOD, envelope);

            //Se obtiene la respuesta por parte del WebService, en esta caso el XML de la asignatura
            res.executionResult = EXECUTION_RESULT.OK;
            res.executionMessage = envelope.getResponse().toString();
        }
        catch (Exception e){
            res.executionResult = EXECUTION_RESULT.ERROR;
            res.executionMessage = e.getMessage();
        }

        return res;
    }

    /**
     *Funcion que nos permite realizar GETUSERBYID para conocer el identificar de usuario. El usuario dispone
     *de un identificador de usuario en modo texto pero la plataforma virtual internamente asocia dicho
     *identificador con un identificador numerico.
     *
     *Este metodo nos permite conocer dicho identificador numerico
     *
     *@return Identificador de usuario (IDuser), de tipo Int
     *
     * @throws Exception Error al realizar la conexion al Webservice
     */
    public GenericTypes.ExecutionResult GetUserBySid(){

        //La siguiente informacion puede obtenerse del WSDL de la Universidad de Jaen
        //http://dv.ujaen.es/docencia/webservice/soap/server.php
        //En dicha pagina se puede consultar todos los servicios que se ofrecen y como trabajar
        //con ellos

        final String SOAP_ACTION = "urn:ilUserAdministration#getUserIdBySid";
        final String METHOD = "getUserIdBySid";
        final String NAMESPACE = "urn:ilUserAdministration";
        final String ENDPOINTWS = "http://dv.ujaen.es/docencia/webservice/soap/server.php";

        GenericTypes.ExecutionResult res = new GenericTypes.ExecutionResult();

        //Objeto para hacer la llamada al WS
        SoapObject userRequest=new SoapObject(NAMESPACE, METHOD);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        //Pasar los parametros
        userRequest.addProperty("sid",sid);

        //Se empaquetan los parametros
        envelope.setOutputSoapObject(userRequest);

        //Se establece la comunicacion con la direccion que ofrece los WebServices
        HttpTransportSE androidHttpTransport = null;
        androidHttpTransport = new HttpTransportSE(ENDPOINTWS);

        try
        {
            //Se solicicta el WebService y se envian los parametros
            androidHttpTransport.call(METHOD, envelope);

            //Se obtiene la respuesta por parte del WebService, en esta caso el identificador de usuario
            res.executionResult = EXECUTION_RESULT.OK;
            res.executionMessage = envelope.getResponse().toString();
            idUser = Integer.parseInt(res.executionMessage);
        }

        catch (Exception e){
            //Si el getUserByID no se ha podido realizar correctamente se debe mandar un mensaje de error
            //para que el hilo que realiza el registro se detenga y no continue con el proceso.
            res.executionResult = EXECUTION_RESULT.ERROR;
            res.executionMessage = e.getMessage();
        }

        return res;
    }

    /**
     *Funcion que nos permite realizar GETOBJECTBYTITTLE para obtener el identificador de la carpeta
     *sobre la que almacenaremos el fichero de asistencia
     *
     *Esta funcion busca aquellas elementos almacenados en la plataforma que contengan en su titulo
     *la palabra asistencia, el resultado obtenido es un XML con los identificadores de todos estos elementos
     *
     *Este XML debe ser tratato posteriormente para obtener concretamente el identificador que representa a
     *la carpeta sobre la que almacenaremos el fichero.
     *
     *@return Resultado de la operacion en una fila con el siguiente formato por cada fichero encontrado [Ref_id fichero, Ref_id dir asistencia asignatura, Ref_id curso, nombre curso]
     *
     *@throws Exception Error al realizar la conexion al Webservice
     */
    public GenericTypes.ExecutionResult GetAsignaturas()
    {
        //La siguiente informacion puede obtenerse del WSDL de la Universidad de Jaen
        //http://dv.ujaen.es/docencia/webservice/soap/server.php
        //En dicha pagina se puede consultar todos los servicios que se ofrecen y como trabajar
        //con ellos

        final String SOAP_ACTION = "urn:ilUserAdministration#getObjectsByTitle";
        final String METHOD = "getObjectsByTitle";
        final String NAMESPACE = "urn:ilUserAdministration";
        final String ENDPOINTWS = "http://dv.ujaen.es/docencia/webservice/soap/server.php";

        //Respuesta que devolvera el webservice
        GenericTypes.ExecutionResult res = new GenericTypes.ExecutionResult();

        //Objeto para hacer la llamada al WS
        SoapObject userRequest=new SoapObject(NAMESPACE, METHOD);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        //Pasar los parametros
        userRequest.addProperty("sid",sid);
        userRequest.addProperty("title","alumnos.csv");
        userRequest.addProperty("user_id",idUser);

        //Se empaquetan los parametros
        envelope.setOutputSoapObject(userRequest);

        //Se establece la comunicacion con la direccion que ofrece los WebServices
        HttpTransportSE androidHttpTransport = null;
        androidHttpTransport = new HttpTransportSE(ENDPOINTWS);

        try
        {
            //Se solicicta el WebService y se envian los parametros
            androidHttpTransport.call(METHOD, envelope);

            //Se obtiene la respuesta por parte del WebService
            String xmlAsistenciaResponse = envelope.getResponse().toString();

            //Se procesa la resupuesta para obtener los ids de las asignaturas
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource archivo = new InputSource();

            archivo.setCharacterStream(new StringReader(xmlAsistenciaResponse));

            Document documento = db.parse(archivo);
            documento.getDocumentElement().normalize();

            //Se busca en el XML los nodos "References"
            NodeList referencesNodeList = documento.getElementsByTagName("References");

            for (int s = 0; s < referencesNodeList.getLength(); s++)
            {
                String[] asignaturaRes = new String[4];
                Element referencesElement = (Element)referencesNodeList.item(s);
                //De cada nodo "Referencies" se obtiene el valor "ref_id" con la referencia para futura descarga del fichero
                asignaturaRes[0] = referencesElement.getAttributes().getNamedItem("ref_id").getNodeValue();

                NodeList elementNodeList = referencesElement.getElementsByTagName("Element");
                for (int i = 0; i < elementNodeList.getLength(); i++)
                {
                    Element elementTagElement = (Element)elementNodeList.item(i);

                    if(elementTagElement.hasAttribute("type") && elementTagElement.getAttributes().getNamedItem("type").getNodeValue().equals("fold"))
                    {
                        if(elementTagElement.getTextContent().equals("asistencia"))
                        {
                            //Se ha encontrado la referencia del directorio asistencia
                            asignaturaRes[1] = elementTagElement.getAttributes().getNamedItem("ref_id").getNodeValue();
                        }
                        else
                        {
                            res.executionResult = EXECUTION_RESULT.ERROR;
                            res.executionMessage = activityContext.getString(R.string.errorBuscarDirAsistencia);
                            break;
                        }
                    }

                    if(elementTagElement.hasAttribute("type") && elementTagElement.getAttributes().getNamedItem("type").getNodeValue().equals("crs"))
                    {
                        //Se ha encontrado la asignatura
                        asignaturaRes[2] = elementTagElement.getAttributes().getNamedItem("ref_id").getNodeValue();
                        asignaturaRes[3] = elementTagElement.getTextContent();
                    }
                }

                res.executionMessage += asignaturaRes[0] + "," + asignaturaRes[1] + "," + asignaturaRes[2] + "," + asignaturaRes[3] + ";";
            }

            res.executionResult = EXECUTION_RESULT.OK;
        }
        catch (Exception e)
        {
            //Si la operacion no se ha podido realizar correctamente se debe mandar un mensaje de error
            res.executionResult = EXECUTION_RESULT.ERROR;
            res.executionMessage = e.getMessage();
        }

        //Se devuelve el XML obtenido
        return res;
    }

    /**
     * Obtiene el XML de un usuario
     * @param alumnoId usuario
     * @return Informacion XML del usuario recuperada de Ilias
     */
    public GenericTypes.ExecutionResult GetUser(int alumnoId){

        //La siguiente informacion puede obtenerse del WSDL de la Universidad de Jaen
        //http://dv.ujaen.es/docencia/webservice/soap/server.php
        //En dicha pagina se puede consultar todos los servicios que se ofrecen y como trabajar
        //con ellos
        final String SOAP_ACTION = "urn:ilUserAdministration#resolveUsers";
        final String METHOD = "getUserXML";
        final String NAMESPACE = "urn:ilUserAdministration";
        final String ENDPOINTWS = "http://dv.ujaen.es/docencia/webservice/soap/server.php";

        //Respuesta que devolvera el webservice
        GenericTypes.ExecutionResult res = new GenericTypes.ExecutionResult();

        //Objeto para hacer la llamada al WS
        SoapObject userRequest=new SoapObject(NAMESPACE, METHOD);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);


        //Pasar los parametros
        userRequest.addProperty("sid",sid);
        userRequest.addProperty("user_id",alumnoId);

        //Se empaquetan los parametros
        envelope.setOutputSoapObject(userRequest);

        //Se establece la comunicacion con la direccion que ofrece los WebServices
        HttpTransportSE androidHttpTransport = null;
        androidHttpTransport = new HttpTransportSE(ENDPOINTWS);

        try
        {
            //Se solicicta el WebService y se envian los parametros
            androidHttpTransport.call(METHOD, envelope);

            //Se obtiene la respuesta por parte del WebService, en este caso los datos de usuario
            res.executionResult = EXECUTION_RESULT.OK;
            res.executionMessage = envelope.getResponse().toString();
        }

        catch (Exception e){
            res.executionResult = EXECUTION_RESULT.ERROR;
            res.executionMessage = e.getMessage();
        }

        return res;
    }

    /**
     * Obtiene el fichero de los alumnos .csv de Ilias
     * @param ficheroRef refId del fichero en Ilias
     * @return Fichero de ilias con la referencia solicitada
     */
    public GenericTypes.ExecutionResult GetFichero(String ficheroRef)
    {
        //La siguiente informacion puede obtenerse del WSDL de la Universidad de Jaen
        //http://dv.ujaen.es/docencia/webservice/soap/server.php
        //En dicha pagina se puede consultar todos los servicios que se ofrecen y como trabajar
        //con ellos

        final String SOAP_ACTION = "urn:ilUserAdministration#getFileXML";
        final String METHOD = "getFileXML";
        final String NAMESPACE = "urn:ilUserAdministration";
        final String ENDPOINTWS = "http://dv.ujaen.es/docencia/webservice/soap/server.php";

        //Respuesta que devolvera el webservice
        GenericTypes.ExecutionResult res = new GenericTypes.ExecutionResult();

        //Objeto para hacer la llamada al WS
        SoapObject userRequest=new SoapObject(NAMESPACE, METHOD);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        //Pasar los parametros
        userRequest.addProperty("sid",sid);
        userRequest.addProperty("ref_id",ficheroRef);
        userRequest.addProperty("attachment_mode", 1);

        //Se empaquetan los parametros
        envelope.setOutputSoapObject(userRequest);

        //Se establece la comunicacion con la direccion que ofrece los WebServices
        HttpTransportSE androidHttpTransport = null;
        androidHttpTransport = new HttpTransportSE(ENDPOINTWS);

        try
        {
            //Se solicicta el WebService y se envian los parametros
            androidHttpTransport.call(METHOD, envelope);

            //Se obtiene la respuesta por parte del WebService
            String xmlAsistenciaResponse = envelope.getResponse().toString();

            //Se procesa la resupuesta para obtener los ids de las asignaturas
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource archivo = new InputSource();

            archivo.setCharacterStream(new StringReader(xmlAsistenciaResponse));

            Document documento = db.parse(archivo);
            documento.getDocumentElement().normalize();

            //Se busca en el XML los nodos "References"
            Node contentNode = documento.getElementsByTagName("Content").item(0);
            res.executionMessage = new String(Base64.decode(contentNode.getTextContent().getBytes(HTTP.UTF_8),Base64.DEFAULT), HTTP.UTF_8);
            res.executionResult = EXECUTION_RESULT.OK;

        }
        catch (Exception e)
        {
            //Si la operacion no se ha podido realizar correctamente se debe mandar un mensaje de error
            res.executionResult = EXECUTION_RESULT.ERROR;
            res.executionMessage = e.getMessage();
        }

        //Se devuelve el XML obtenido
        return res;
    }



    /**
     * Esta funci—n se encarga de Parsear un documento XML en busca de un parametro
     *
     * @param XML es el documento XML donde se realizar‡ la busqueda del parametro
     * @param parametroBusqueda es el parametro a buscar en el XML
     *
     * @return Devuelve el vector con toda la informaci—n encontrada en el documento XML
     *
     */
    public int[] Parsear(String XML, String parametroBusqueda) {

        //Variable para almacenar los identificadores
        int[] resultado = null;
        int[] resp = {0,0,0};;

        try {

            ArrayList<String> refIds = new ArrayList<String>();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource archivo = new InputSource();

            archivo.setCharacterStream(new StringReader(XML));

            Document documento = db.parse(archivo);
            documento.getDocumentElement().normalize();

            //Se busca en el XML los nodos "References"
            NodeList nodeLista = documento.getElementsByTagName("References");

            for (int s = 0; s < nodeLista.getLength(); s++) {
                Node primerNodo = nodeLista.item(s);
                //De cada nodo "Referencies" se obtiene el valor "ref_id"
                refIds.add(primerNodo.getAttributes().getNamedItem(parametroBusqueda)
                        .getNodeValue());
            }

            resultado = new int[refIds.size()];
            for (int i = 0; i < refIds.size(); i++) {
                //Todos los "ref_id" seleccionados se almacenan en el vector resultado
                resultado[i] = new Integer(refIds.get(i)).intValue();
                System.out.println("resultado del parser numero " + i + " "
                        + resultado[i]);
            }

            return resultado;

        } catch (Exception e) {
            resp[0] = 0;
            return resp;
        }


    }

    /**
     * Esta funcion permite seleccionar concretamente el ref_id correspondiente a la carpeta
     * donde se almacenaran el ficheros
     *
     * @param XML es el documento XML donde se realizara la busqueda de los ref_id
     *
     * @return El identificador de la carpeta donde se debe almacenar el fichero
     *
     */
    public int ObtenerTargetXML(String XML, int identificadorAsignatura) {

        int[] resultado = null;
        int[] cursos  = null;

        //Se parsea el documento XML en busca de todos los ref_id
        try{

            resultado = Parsear(XML,"ref_id");
            cursos  = Parsear(XML,"parent_id");
            //De entre todos los cursos se busca aquel que coincida con el seleccionado
            for(int i = 0; i<cursos.length; i++){
                if(cursos[i] == identificadorAsignatura){
                    return resultado[i];
                }
            }


            return resultado[resultado.length - 1];
        }catch(Exception e){
            System.out.println("catch de obtenertarget");

            return 0;
        }


        // De todos los resultados asociados a "asistencia" nos quedaremos con
        // el ultimo de ellos pues este sera el que corresponda al identificador
        //de la carpeta asistencia.


    }

    /**
     *Funcion que nos permite realizar ADDFILE para almacenar un fichero en Docencia Virtual
     *La informaci—n del XML que hay que montar para mandarse como fichero se puede
     *encontrar buscando en el navegador lo siguiente: ilias_file_3_8.dtd
     *
     *@param nombreFichero nombre del fichero a subir
     *@param identificadorDirectorio es el identificador del directorio donde se guardaran los ficheros. El mismo que donde estaba el fichero de alumnos.
     *@param datosASubir datos a subir a la plataforma
     * @param cabecera fila de cabecera del fichero, si la tiene. Puede venir vacío
     *
     *@return confirmacion de fichero almacenado
     *
     * @throws Exception Error al realizar la conexion al Werservice
     */
    public GenericTypes.ExecutionResult AddFile(String nombreFichero, String identificadorDirectorio, String datosASubir, String cabecera){

        GenericTypes.ExecutionResult res = new GenericTypes.ExecutionResult();
        //La siguiente informacion puede obtenerse del WSDL de la Universidad de Jaen
        //http://dv.ujaen.es/docencia/webservice/soap/server.php
        //En dicha pagina se puede consultar todos los servicios que se ofrecen y como trabajar
        //con ellos
        final String SOAP_ACTION = "urn:ilUserAdministration#addFile";
        final String METHOD = "addFile";
        final String NAMESPACE = "urn:ilUserAdministration";
        final String ENDPOINTWS = "http://dv.ujaen.es/docencia/webservice/soap/server.php";

        //Variable para almacenar el XML que se enviar‡ a Docencia Virtual
        String XMLenvio;

        //Objeto para hacer la llamada al WS
        SoapObject userRequest=new SoapObject(NAMESPACE, METHOD);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        //Creamos el XML que se enviar‡ a docencia virtual
        XML objetoXML = new XML();
        XMLenvio = objetoXML.CrearXMLEnvio(nombreFichero, datosASubir, cabecera);

        //Pasar los parametros
        userRequest.addProperty("sid",sid);
        userRequest.addProperty("target_id", identificadorDirectorio);
        userRequest.addProperty("xml",XMLenvio);

        //Se empaquetan los parametros
        envelope.setOutputSoapObject(userRequest);

        //Se establece la comunicacion con la direccion que ofrece los WebServices
        HttpTransportSE androidHttpTransport = null;
        androidHttpTransport = new HttpTransportSE(ENDPOINTWS);

        try
        {
            //Se solicicta el WebService y se envian los parametros
            androidHttpTransport.call(METHOD, envelope);

            //Se obtiene la respuesta por parte del WebService, en este caso, el fichero subido
            res.executionResult = EXECUTION_RESULT.OK;
            res.executionMessage = envelope.getResponse().toString();
        }
        catch (Exception e){
            res.executionResult = EXECUTION_RESULT.ERROR;
            res.executionMessage = e.getMessage();
        }

        return res;
    }

    /**
     *Funcion que nos permite realizar UPDATEFILE para actualizar un fichero en Docencia Virtual
     *La informaci—n del XML que hay que montar para mandarse como fichero se puede
     *encontrar buscando en el navegador lo siguiente: ilias_file_3_8.dtd
     *
     *@param nombreFichero nombre del fichero a subir
     *@param idFichero es el identificador del fichero a actualizar.
     *@param datosASubir datos a subir a la plataforma
     *
     *@return confirmacion de fichero almacenado
     *
     * @throws Exception Error al realizar la conexion al Werservice
     */
    public GenericTypes.ExecutionResult UpdateFile(String nombreFichero, String idFichero, String datosASubir){

        GenericTypes.ExecutionResult res = new GenericTypes.ExecutionResult();
        //La siguiente informacion puede obtenerse del WSDL de la Universidad de Jaen
        //http://dv.ujaen.es/docencia/webservice/soap/server.php
        //En dicha pagina se puede consultar todos los servicios que se ofrecen y como trabajar
        //con ellos
        final String SOAP_ACTION = "urn:ilUserAdministration#updateFile";
        final String METHOD = "updateFile";
        final String NAMESPACE = "urn:ilUserAdministration";
        final String ENDPOINTWS = "http://dv.ujaen.es/docencia/webservice/soap/server.php";

        //Variable para almacenar el XML que se enviar‡ a Docencia Virtual
        String XMLenvio;

        //Objeto para hacer la llamada al WS
        SoapObject userRequest=new SoapObject(NAMESPACE, METHOD);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        //Creamos el XML que se enviar‡ a docencia virtual
        XML objetoXML = new XML();
        XMLenvio = objetoXML.CrearXMLEnvio(nombreFichero, datosASubir, "");

        //Pasar los parametros
        userRequest.addProperty("sid",sid);
        userRequest.addProperty("ref_id", idFichero);
        userRequest.addProperty("xml",XMLenvio);

        //Se empaquetan los parametros
        envelope.setOutputSoapObject(userRequest);

        //Se establece la comunicacion con la direccion que ofrece los WebServices
        HttpTransportSE androidHttpTransport = null;
        androidHttpTransport = new HttpTransportSE(ENDPOINTWS);

        try
        {
            //Se solicicta el WebService y se envian los parametros
            androidHttpTransport.call(METHOD, envelope);

            //Se obtiene la respuesta por parte del WebService, en este caso, el fichero subido
            res.executionResult = EXECUTION_RESULT.OK;
            res.executionMessage = envelope.getResponse().toString();
        }
        catch (Exception e){
            res.executionResult = EXECUTION_RESULT.ERROR;
            res.executionMessage = e.getMessage();
        }

        return res;
    }
}
