package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.client;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.Transaction;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.TransactionResult;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientSocket {

    public static final int clientPort = 8080;
    Socket socket;

    public ClientSocket(InetAddress inetAddress) throws IOException {
        socket = new Socket(inetAddress, clientPort);
    }

    public TransactionResult sendTransaction(Transaction transaction) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            outputStream.writeObject(transaction);
            outputStream.flush();

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            TransactionResult result = (TransactionResult) inputStream.readObject();
            outputStream.close();
            inputStream.close();

            return result;

        } catch (ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}