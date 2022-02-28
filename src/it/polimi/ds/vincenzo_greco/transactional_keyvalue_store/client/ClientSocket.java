package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.client;


import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.GlobalVariables;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.KeyValue;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.Transaction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ClientSocket {

    Socket socket;

    public ClientSocket(InetAddress inetAddress) throws IOException {
        socket = new Socket(inetAddress, GlobalVariables.clientPort);
    }

    public List<KeyValue> sendTransaction(Transaction transaction) {

        try {

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            outputStream.writeObject(transaction);
            outputStream.flush();

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            ArrayList<KeyValue> keyValues = (ArrayList<KeyValue>) inputStream.readObject();
            outputStream.close();
            inputStream.close();

            return keyValues;

        } catch (UnknownHostException ex) {

            ex.printStackTrace();

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            ex.printStackTrace();

            System.out.println("I/O error: " + ex.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}