package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.client.ClientSocket;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore.Scheduler;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.handler.ClientHandler;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.handler.ServerHandler;

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

    public final Scheduler scheduler = new Scheduler(this);
    final List<ServerHandler> serverHandlers = new ArrayList<>();
    public final int serverId;
    public final Map<Integer, ServerHandler> serverHandlerMap = new HashMap<>();
    private static final int serverPort = 4040;
    public final int numberOfServers;
    public final int numberOfReplicas;

    public Server(List<String> addresses, int numberOfServers, int numberOfReplicas) {

        this.numberOfServers = numberOfServers;
        this.numberOfReplicas = numberOfReplicas;

        List<InetAddress> inetAddressList = new ArrayList<>();

        for (String address : addresses) {
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
                Socket socket = new Socket(inetAddress, serverPort);
                serverHandler = new ServerHandler(socket, this);
                serverHandler.start();
                serverHandlers.add(serverHandler);
            }
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while (serverHandlers.size() < numberOfServers - 1) {
                serverHandler = new ServerHandler(serverSocket.accept(), this);
                serverHandler.start();
                serverHandlers.add(serverHandler);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(ClientSocket.clientPort);
            while (true) {
                new ClientHandler(serverSocket.accept(), scheduler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerResponse sendRequest(ServerRequest serverRequest) throws InterruptedException {
        ServerHandler serverHandler = serverHandlerMap.get(serverRequest.destinationId);
        return serverHandler.sendRequest(serverRequest);
    }

    public void sendResponse(ServerResponse serverResponse, int destinationId) {
        serverHandlerMap.get(destinationId).sendResponse(serverResponse);
    }
}