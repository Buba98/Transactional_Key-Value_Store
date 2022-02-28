package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.GlobalVariables;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.KeyValue;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.OptimizedOperation;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.Transaction;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.Server;

import java.io.IOException;

public class Scheduler {

    DataStore dataStore = new DataStore();
    private Integer schedulerThreadLastId = 0;
    final Server server;
    private final KeyLock keyLock = new KeyLock();
    private final Object lock = new Object();

    public Scheduler(Server server) {
        this.server = server;
    }

    public SchedulerTransactionHandler addTransaction(Transaction transaction) {
        int schedulerThreadLastId;

        synchronized (lock) {
            schedulerThreadLastId = this.schedulerThreadLastId++;
        }
        return new SchedulerTransactionHandler(transaction, schedulerThreadLastId, this);
    }

    public KeyValue executeOperation(OptimizedOperation optimizedOperation, int schedulerTransactionHandlerId) throws IOException, InterruptedException {

        int serverId = serverLockForKey(optimizedOperation.key);

        if (optimizedOperation.lockType != LockType.FREE) {
            if (serverId == server.serverId) {
                keyLock.lock(new Lock(optimizedOperation.key, optimizedOperation.lockType));

                KeyValue keyValue = null;

                if (optimizedOperation.firstRead != null)
                    keyValue = dataStore.read(optimizedOperation.firstRead);

                if (optimizedOperation.lastWrite != null)
                    dataStore.write(optimizedOperation.lastWrite);

                return keyValue;
            } else {
                return server.sendRequest(optimizedOperation, serverId, schedulerTransactionHandlerId).keyValue;
            }
        } else {

            assert optimizedOperation.lastWrite != null && schedulerHasAReplica(optimizedOperation.key) && serverId != server.serverId;

            dataStore.write(optimizedOperation.lastWrite);
            return null;
        }
    }

    public void free(OptimizedOperation optimizedOperation, int schedulerTransactionHandlerId) throws InterruptedException, IOException {

        assert optimizedOperation.lockType == LockType.FREE;

        int serverId = serverLockForKey(optimizedOperation.key);
        LockType lockType;

        if (optimizedOperation.lastWrite != null)
            lockType = LockType.EXCLUSIVE;
        else
            lockType = LockType.SHARED;

        if (serverId == server.serverId) {
            if (lockType == LockType.EXCLUSIVE) {
                for (int i = 1; i < GlobalVariables.numberOfReplica; i++) {
                    server.sendRequest(optimizedOperation, serverId + i, schedulerTransactionHandlerId);
                }
            }
            keyLock.free(new Lock(optimizedOperation.key, lockType));
        } else {
            server.sendRequest(optimizedOperation, serverId, schedulerTransactionHandlerId);
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
    public static int serverLockForKey(String key) {
        char[] arrayOfChar = key.toCharArray();

        int sum = 0;

        for (char c : arrayOfChar) {
            sum += c;
        }

        return sum % GlobalVariables.numberOfServers;
    }
}
