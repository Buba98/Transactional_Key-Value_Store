package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.GlobalVariables;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.KeyValue;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation.OptimizedOperation;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation.Transaction;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.Server;
import jdk.jshell.spi.ExecutionControl;

import java.util.*;

public class Scheduler {

    DataStore dataStore = new DataStore();
    Map<String, LockType> keyLockType = new HashMap<>();
    private int schedulerThreadLastId = 0;
    final Server server;

    public Scheduler(Server server) {
        this.server = server;
    }

    public synchronized SchedulerThread addTransaction(Transaction transaction) {
        return new SchedulerThread(transaction, schedulerThreadLastId++, this);
    }

    public synchronized KeyValue executeOperation(OptimizedOperation optimizedOperation) {
        throw new ExecutionControl.NotImplementedException("not implemented yet");
    }

    public synchronized void releaseLock(Lock lock){
        throw new ExecutionControl.NotImplementedException("not implemented yet");

    }

    /**
     * @param key a key of the dataStore
     * @return the id of the server that holds the lock table for that key
     */
    public static int serverLockForKey(String key) {
        char[] arrayOfChar = key.toCharArray();

        int sum = 0;

        for (char c : arrayOfChar) {
            sum += c;
        }

        return sum % GlobalVariables.numberOfServers;
    }
}
