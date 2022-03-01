package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.client;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.GlobalVariables;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.KeyValue;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.Transaction;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
            new ClientSocket(ipAddress).sendTransaction(transaction).printResult();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
