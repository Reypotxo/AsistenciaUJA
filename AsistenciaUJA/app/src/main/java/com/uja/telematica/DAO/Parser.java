package com.uja.telematica.DAO;

//Import's para analizar el el documento XML
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * La clase Parser.Java se implementa la funcionalidad para analizar un
 * documento XML Su funci—n ser‡ la de analizar el documento XML que se obtiene
 * al llamar a getObjectsByTittle. El metodo Parsear debe buscar los ref_id y
 * quedarse unicamente con el que hace referencia a la carpeta "asistencia".
 *
 * El ref_id de la carpeta asistencia es el que debe devolverse.
 *
 * @version 1.0 , Junio 2014
 * @author Jose Manuel JimŽnez Bravo
 */

public class Parser {

    /**
     * Contructor por defecto
     *
     */
    public Parser() {
    }
}


