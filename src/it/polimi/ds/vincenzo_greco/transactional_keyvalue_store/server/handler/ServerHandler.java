package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.handler;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.Server;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.ServerRequest;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.ServerResponse;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * The class responsible for the communication between two servers
 * Once the connection is established a request can be sent to the other server that will collect it
 */
public class ServerHandler extends Thread {
    public final Socket socket;
    public Integer id;
    public final Server server;
    final ObjectOutputStream objectOutputStream;
    final ObjectInputStream objectInputStream;
    final Map<Integer, Object> lockMap = new HashMap<>();
    final Map<Integer, ServerResponse> responseMap = new HashMap<>();

    public ServerHandler(Socket socket, Server server) throws IOException, ClassNotFoundException {
        this.socket = socket;
        this.server = server;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        objectOutputStream.writeObject(server.serverId);
        id = (Integer) objectInputStream.readObject();
    }

    @Override
    public void run() {
        try {
            listener();

        } catch (IOException e) {
            interrupt();
            e.printStackTrace();
        }
    }

    public void listener() throws IOException {

        while (true) {
            try {
                Object object = objectInputStream.readObject();

                if (object instanceof ServerResponse) {
                    addResponse((ServerResponse) object);
                } else if (object instanceof ServerRequest) {
                    new ServerRequestHandler((ServerRequest) object, server).start();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addResponse(ServerResponse serverResponse) {
        synchronized (responseMap) {
            responseMap.put(serverResponse.schedulerTransactionId, serverResponse);
        }

        synchronized (lockMap) {
            synchronized (lockMap.get(serverResponse.schedulerTransactionId)) {
                lockMap.get(serverResponse.schedulerTransactionId).notifyAll();
            }
        }
    }

    public ServerResponse addRequest(ServerRequest serverRequest) throws InterruptedException, IOException {

        if (serverRequest.synchronous) {
            synchronized (lockMap) {
                lockMap.put(serverRequest.schedulerTransactionHandlerId, new Object());
            }
        }

        sendRequest(serverRequest);

        if (serverRequest.synchronous) {
            synchronized (lockMap.get(serverRequest.schedulerTransactionHandlerId)) {
                ServerResponse serverResponse;

                synchronized (responseMap) {
                    serverResponse = responseMap.remove(serverRequest.schedulerTransactionHandlerId);
                }

                while (serverResponse == null) {
                    lockMap.get(serverRequest.schedulerTransactionHandlerId).wait();

                    synchronized (responseMap) {
                        serverResponse = responseMap.get(serverRequest.schedulerTransactionHandlerId);
                    }
                }
                return serverResponse;
            }
        } else return null;
    }

    public void sendRequest(ServerRequest serverRequest) throws IOException {
        synchronized (objectOutputStream) {
            objectOutputStream.writeObject(serverRequest);
            objectOutputStream.flush();
        }
    }

    public void sendResponse(ServerResponse serverResponse) {
        try {
            synchronized (objectOutputStream) {
                objectOutputStream.writeObject(serverResponse);
                objectOutputStream.flush();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interrupt() {
        try {
            objectInputStream.close();
            objectOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.interrupt();
    }
}