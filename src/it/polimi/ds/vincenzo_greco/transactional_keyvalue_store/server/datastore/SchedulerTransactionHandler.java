package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.KeyValue;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.OptimizedOperation;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SchedulerTransactionHandler {

    final Transaction transaction;
    public final int id;
    final Scheduler scheduler;
    public final ArrayList<KeyValue> result = new ArrayList<>();
    final List<Lock> locks = new ArrayList<>();

    public SchedulerTransactionHandler(Transaction transaction, int id, Scheduler scheduler) {
        this.transaction = transaction;
        this.id = id;
        this.scheduler = scheduler;
    }

    public ArrayList<KeyValue> run() throws IOException, InterruptedException {

        OptimizedOperation optimizedOperation;

        for (String key : transaction.sortedKeys) {
            optimizedOperation = transaction.optimizedOperationMap.get(key);

            KeyValue keyValue = scheduler.executeOperation(optimizedOperation, id);

            locks.add(new Lock(key, optimizedOperation.lockType));

            if (keyValue.value != null) {
                result.add(keyValue);
            }
        }

        for (Lock lock : locks) {
            scheduler.free(new OptimizedOperation(null, transaction.optimizedOperationMap.get(lock.key).lastWrite, LockType.FREE, lock.key), id);
        }

        return result;
    }
}
