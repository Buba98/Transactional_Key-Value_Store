package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.GlobalVariables;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore.Scheduler;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Establish the connection with all the servers creating on thread for each server (view ServerHandler).
 * Once the setup is completed will start to listen for client's requests creating a thread for each client (view ClientHandler)
 */
public class Server {

    final Scheduler scheduler = new Scheduler(this);
    final List<ServerHandler> serverHandlers = new ArrayList<>();
    final int serverId;
    final Map<Integer, ServerHandler> serverHandlerMap = new HashMap<>();

    public Server(List<String> addresses) {

        List<InetAddress> inetAddressList = new ArrayList<>();

        for (String address : addresses) {
            if (address.matches(GlobalVariables.ipRegex))
                throw new IllegalArgumentException(address + " not a valid address");
            try {
                inetAddressList.add(InetAddress.getByName(address));
            } catch (UnknownHostException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(address + " not a valid address");
            }
        }

        serverId = addresses.size();

        setup(inetAddressList);

        serverHandlers.forEach(serverHandler -> serverHandlerMap.put(serverHandler.id, serverHandler));
    }

    public void setup(List<InetAddress> inetAddressList) {
        try {

            ServerHandler serverHandler;

            for (InetAddress inetAddress : inetAddressList) {
                Socket socket = new Socket(inetAddress, GlobalVariables.serverPort);
                serverHandler = new ServerHandler(socket, serverId);
                serverHandler.start();

                serverHandlers.add(serverHandler);
            }

            ServerSocket serverSocket = new ServerSocket(GlobalVariables.serverPort);
            while (serverHandlers.size() < GlobalVariables.numberOfServers) {
                serverHandler = new ServerHandler(serverSocket.accept(), serverId);
                serverHandler.start();
                serverHandlers.add(serverHandler);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try {
            ServerSocket serverSocket = new ServerSocket(GlobalVariables.clientPort);
            while (true) {
                new ClientHandler(serverSocket.accept(), scheduler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
