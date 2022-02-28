package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.client.Client;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Allow to start the application both as a client or as a sever
 * All the input are meant to be the argument at the start
 * <p>
 * In order to start a client the first argument has to be -c, for a server -s
 * <p>
 * If you are a client the second argument has to be one of the servers, the third the transaction itself.
 * The transaction can be composed only by reads (r(x) - read variable x) and writes (w(x)a - write "a" on variable x)
 * <p>
 * If you are a server for the second argument on you have to enter ALL the servers already connected to the network as
 * distinct arguments as addresses
 */
public class KeyValueStore {

    public static void main(String[] args) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {

        assert GlobalVariables.numberOfServers >= GlobalVariables.numberOfReplica;

        localIp();

        if (args.length == 0) {
            help();
        } else {
            switch (args[0]) {
                case "-s" -> {

                    List<String> addresses = new ArrayList<>(Arrays.asList(args));
                    addresses.remove(0);
                    Server server = new Server(addresses);
                    server.run();
                }
                case "-c" -> new Client(args[1], args[2]).run();
                default -> help();
            }
        }
    }

    public static void help() {
        System.out.println("""
                Usage: key_value_store [-s || -c]

                    s   start a server
                    c   start a client
                """);
    }

    public static void localIp() {
        InetAddress ip;
        String hostname;
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            System.out.println("Your current IP address : " + ip);
            System.out.println("Your current Hostname : " + hostname);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
