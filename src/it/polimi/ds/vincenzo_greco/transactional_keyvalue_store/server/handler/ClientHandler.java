package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.handler;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.Transaction;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore.Scheduler;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore.SchedulerTransactionHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

            SchedulerTransactionHandler schedulerTransactionHandler = scheduler.addTransaction(transaction);

            schedulerTransactionHandler.run();

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            outputStream.writeObject(transaction);
            outputStream.flush();
            outputStream.close();
            in.close();
            socket.close();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}