package com.uja.telematica.DAO;

/**
 * Created by Alfonso Troyano on 16/04/2015.
 */
public class GenericTypes {

    /**
     * Milisegundos en un minuto
     */
    public static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs
    /**
     * Valor indefinido para texto
     */
    public static final String UNDEFINED_STRING = "UNDEFINED";
    /**
     * Valor indefinido para numérico
     */
    public static final int UNDEFINED_NUM = -1;
    /**
     * Contiene los resultados posibles de la ejecucion de una operacion
     */
    public enum EXECUTION_RESULT
    {
        OK,
        ERROR,
        WARNING,
        UNDEFINED
    }


    /**
     * Implementa el resultado de la ejecución de una tarea con el webservice
     */
    public static class ExecutionResult
    {
        public GenericTypes.EXECUTION_RESULT executionResult;
        public String executionMessage;

        /**
         * Inicializa la instancia
         */
        public ExecutionResult()
        {
            executionResult = GenericTypes.EXECUTION_RESULT.UNDEFINED;
            executionMessage = "";
        }
    }

    /**
     * Seconds to wait for webservice operation
     */
    public static final int WAIT_TIME = 5000;
}
