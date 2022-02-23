package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.client;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.GlobalVariables;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.KeyValue;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation.Transaction;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Once created the transaction will establish a connection with a server sending the transaction and receiving the list
 * of the reads requested, that will than print in console
 */
public class Client {

    public InetAddress ipAddress;
    public Transaction transaction;

    public Client(String ipAddress, String stringOperations) throws IllegalArgumentException {
        if (ipAddress.matches(GlobalVariables.ipRegex))
            try {
                this.ipAddress = InetAddress.getByName(ipAddress);

            } catch (UnknownHostException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(ipAddress + " not a valid address");
            }
        else
            throw new IllegalArgumentException(ipAddress + " not a valid address");

        this.transaction = Transaction.fromString(stringOperations);
    }

    public void run() {
        try {
            for (KeyValue keyValue : new ClientSocket(ipAddress).sendTransaction(transaction)) {
                System.out.println(keyValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ClientSocket {

        Socket socket;

        public ClientSocket(InetAddress inetAddress) throws IOException {
            socket = new Socket(inetAddress, GlobalVariables.clientPort);
        }

        public List<KeyValue> sendTransaction(Transaction transaction) {

            try {

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

                outputStream.writeObject(transaction);
                outputStream.flush();
                outputStream.close();

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                ArrayList<KeyValue> keyValues = (ArrayList<KeyValue>) inputStream.readObject();
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
}
