package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store;

/**
 * Set of global variables
 */
public class GlobalVariables {
    public static int numberOfServers = 2;
    public static int numberOfReplica = 2;
    public static String ipRegex = "^((([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}|localhost)$";
    public static String operationRegex = "((r\\(\\w\\)|w\\(\\w\\)\\w),)*(r\\(\\w\\)|w\\(\\w\\)\\w)$";
    public static int serverPort = 4040;
    public static int clientPort = 8080;
}
