package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.ServerRequest;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.KeyValue;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.OptimizedOperation;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.Transaction;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.Server;

import java.io.IOException;

public class Scheduler {

    private final DataStore dataStore = new DataStore();
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

    public KeyValue executeOperation(OptimizedOperation optimizedOperation, int schedulerTransactionHandlerId) throws InterruptedException, IOException {

        int serverLockHolderId = serverLockHolderId(optimizedOperation.key);

        if (optimizedOperation.lockType != LockType.FREE) {
            if (serverLockHolderId == server.serverId) {
                keyLock.lock(new Lock(optimizedOperation.key, optimizedOperation.lockType));

                KeyValue keyValue = null;

                if (optimizedOperation.firstRead != null) keyValue = dataStore.read(optimizedOperation.firstRead);

                if (optimizedOperation.lastWrite != null) dataStore.write(optimizedOperation.lastWrite);

                return keyValue;
            } else {
                return server.sendRequest(new ServerRequest(optimizedOperation, server.serverId, serverLockHolderId, schedulerTransactionHandlerId, true)).keyValue;
            }
        } else {
            assert optimizedOperation.lastWrite != null && schedulerHasAReplica(optimizedOperation.key) && serverLockHolderId != server.serverId;

            dataStore.write(optimizedOperation.lastWrite);
            return null;
        }
    }

    public void free(OptimizedOperation optimizedOperation, int schedulerTransactionHandlerId) throws InterruptedException, IOException {

        assert optimizedOperation.lockType == LockType.FREE;

        int serverLockHolderId = serverLockHolderId(optimizedOperation.key);
        LockType lockType;

        if (optimizedOperation.lastWrite != null) lockType = LockType.EXCLUSIVE;
        else lockType = LockType.SHARED;

        if (serverLockHolderId == server.serverId) {
            if (lockType == LockType.EXCLUSIVE) {
                for (int i = 1; i < server.numberOfReplicas; i++) {
                    server.sendRequest(new ServerRequest(optimizedOperation, server.serverId, (serverLockHolderId + i) % server.numberOfServers, schedulerTransactionHandlerId, false));
                }
            }
            keyLock.free(new Lock(optimizedOperation.key, lockType));
        } else {
            server.sendRequest(new ServerRequest(optimizedOperation, server.serverId, serverLockHolderId, schedulerTransactionHandlerId, false));
        }
    }

    public boolean schedulerHasAReplica(String key) {
        int serverLockHolderId = serverLockHolderId(key);
        int i = 0;

        while (i < server.numberOfReplicas) {
            if (serverLockHolderId == server.serverId) {
                return true;
            }

            serverLockHolderId++;

            if (serverLockHolderId == server.numberOfServers) {
                serverLockHolderId = 0;
            }

            i++;
        }

        return false;
    }

    /**
     * @param key a key of the dataStore
     * @return the id of the server that holds the lock table for that key
     */
    public int serverLockHolderId(String key) {
        char[] arrayOfChar = key.toCharArray();

        int sum = 0;

        for (char c : arrayOfChar) {
            sum += c;
        }

        return sum % server.numberOfServers;
    }
}
