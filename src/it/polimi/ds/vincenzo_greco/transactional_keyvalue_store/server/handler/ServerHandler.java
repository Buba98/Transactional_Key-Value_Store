package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.handler;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.Server;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.ServerRequest;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.ServerResponse;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * The class responsible for the communication between two servers
 * Once the connection is established a request can be sent to the other server that will collect it
 */
public class ServerHandler extends Thread {
    public final Socket socket;
    public int id;
    public final Server server;
    final List<ServerResponse> serverResponseList = new ArrayList<>();
    final ObjectOutputStream objectOutputStream;
    final ObjectInputStream objectInputStream;

    public ServerHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            objectOutputStream.writeInt(server.serverId);
            id = objectInputStream.readInt();
            listener();

        } catch (IOException e) {
            interrupt();
            e.printStackTrace();
        }
    }

    public void listener() throws IOException {

        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

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
        synchronized (serverResponseList) {
            serverResponseList.add(serverResponse);
            notifyAll();
        }
    }

    public ServerResponse sendRequest(ServerRequest serverRequest) throws InterruptedException {

        try {
            synchronized (objectOutputStream) {
                objectOutputStream.writeObject(serverRequest);
                objectOutputStream.flush();
            }

            int i;

            synchronized (serverResponseList) {

                while ((i = containResponse(serverRequest.schedulerTransactionHandlerId)) == -1) {
                    wait();
                }

                return serverResponseList.remove(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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

    public int containResponse(int schedulerTransactionId) {
        for (int i = 0; i <= serverResponseList.size(); i++) {
            if (serverResponseList.get(i).schedulerTransactionId == schedulerTransactionId) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void interrupt() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.interrupt();
    }
}