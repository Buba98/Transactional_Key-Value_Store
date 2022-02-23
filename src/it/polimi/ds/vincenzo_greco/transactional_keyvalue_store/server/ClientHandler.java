package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.KeyValue;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation.Transaction;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore.Scheduler;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore.SchedulerThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


/**
 * This is a thread of a server and will handle all the client-server operations
 * Specifically once the thread is created the socket through which all the objects will be passed will be passed on
 * constructor as well as the scheduler.
 */
public class ClientHandler extends Thread {

    private final Socket socket;
    private final Scheduler scheduler;

    public ClientHandler(Socket socket, Scheduler scheduler) {
        this.socket = socket;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        try {

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Transaction transaction = (Transaction) in.readObject();
            SchedulerThread schedulerThread = scheduler.addTransaction(transaction);
            in.close();

            schedulerThread.start();
            schedulerThread.join();

            ArrayList<KeyValue> keyValues = schedulerThread.result;

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            outputStream.writeObject(keyValues);
            outputStream.flush();
            outputStream.close();
            socket.close();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}