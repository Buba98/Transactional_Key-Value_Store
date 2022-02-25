package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.GlobalVariables;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.KeyValue;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation.OptimizedOperation;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation.Transaction;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.Server;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.ServerRequest;
import jdk.jshell.spi.ExecutionControl;

import java.io.IOException;
import java.util.*;

public class Scheduler {

    DataStore dataStore = new DataStore();
    final Map<String, LockType> keyLockType = new HashMap<>();
    private int schedulerThreadLastId = 0;
    final Server server;

    public Scheduler(Server server) {
        this.server = server;
    }

    public synchronized SchedulerTransactionHandler addTransaction(Transaction transaction) {
        return new SchedulerTransactionHandler(transaction, schedulerThreadLastId++, this);
    }

    public KeyValue executeOperation(OptimizedOperation optimizedOperation, int schedulerTransactionHandlerId) throws IOException, InterruptedException {

        int serverId = serverLockForKey(optimizedOperation.key);

        if (optimizedOperation.lockType == LockType.SHARED) {
            if (schedulerHasAReplica(optimizedOperation.key)) {
                return dataStore.read(optimizedOperation.firstRead.keyValue);
            } else {
                return server.sendRequest(optimizedOperation, serverId, schedulerTransactionHandlerId).keyValue;
            }
        } else if (optimizedOperation.lockType == LockType.EXCLUSIVE) {
            if (serverId == server.serverId) {
                synchronized (keyLockType) {
                    while (keyLockType.getOrDefault(optimizedOperation.key, LockType.FREE) != LockType.FREE) {
                        wait();
                    }
                    keyLockType.put(optimizedOperation.key, LockType.EXCLUSIVE);
                }

                KeyValue keyValue = null;

                if (optimizedOperation.firstRead != null)
                    keyValue = dataStore.read(optimizedOperation.firstRead.keyValue);

                dataStore.write(optimizedOperation.lastWrite.keyValue);

                return keyValue;
            } else {
                return server.sendRequest(optimizedOperation, serverId, schedulerTransactionHandlerId).keyValue;
            }
        } else {
            if (serverId == server.serverId) {
                synchronized (keyLockType) {
                    keyLockType.put(optimizedOperation.key, LockType.FREE);
                    notifyAll();
                }
            } else {
                dataStore.write(optimizedOperation.lastWrite.keyValue);
            }
            return null;
        }
    }

    public boolean schedulerHasAReplica(String key) {
        int serverId = serverLockForKey(key);
        int i = 0;

        while (i < GlobalVariables.numberOfReplica) {
            if (serverId == server.serverId) {
                return true;
            }

            serverId++;

            if (serverId == GlobalVariables.numberOfServers) {
                serverId = 0;
            }

            i++;
        }

        return false;
    }

    /**
     * @param key a key of the dataStore
     * @return the id of the server that holds the lock table for that key
     */
    private static int serverLockForKey(String key) {
        char[] arrayOfChar = key.toCharArray();

        int sum = 0;

        for (char c : arrayOfChar) {
            sum += c;
        }

        return sum % GlobalVariables.numberOfServers;
    }
}
