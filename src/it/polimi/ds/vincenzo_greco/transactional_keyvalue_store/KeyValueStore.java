package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.client.Client;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.Server;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
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

        localIp();

        if (args.length == 0) {
            help();
        } else {
            switch (args[0]) {
                case "-s" -> {

                    List<String> addresses = new ArrayList<>(Arrays.asList(args));
                    addresses.remove(0);

                    int numberOfServers = Integer.parseInt(addresses.remove(0));
                    int numberOfReplicas = Integer.parseInt(addresses.remove(0));

                    Server server = new Server(addresses, numberOfServers, numberOfReplicas);
                    server.run();
                }
                case "-c" -> {
                    try {
                        new Client(args[1], args[2]).run();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
                default -> help();
            }
        }
    }

    public static void help() {
        System.out.println("""
                Usage: key_value_store [-s || -c]

                    -s   start a server [number of servers] [number of replicas] [addresses]
                    -c   start a client [address] [transaction]
                """);
    }

    public static void localIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    System.out.printf("NetInterface: name [%s], ip [%s]%n",
                            networkInterface.getDisplayName(), address.getHostAddress());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
