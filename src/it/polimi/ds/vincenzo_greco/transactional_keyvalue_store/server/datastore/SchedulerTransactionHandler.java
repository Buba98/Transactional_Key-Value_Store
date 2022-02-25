package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.KeyValue;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation.OptimizedOperation;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SchedulerTransactionHandler {

    final Transaction transaction;
    public final ArrayList<KeyValue> result = new ArrayList<>();
    public final int id;
    final Scheduler scheduler;
    final List<Lock> locks = new ArrayList<>();

    public SchedulerTransactionHandler(Transaction transaction, int id, Scheduler scheduler) {
        this.transaction = transaction;
        this.id = id;
        this.scheduler = scheduler;
    }

    public void run() throws IOException, InterruptedException {

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
            scheduler.executeOperation(new OptimizedOperation(null, null, LockType.FREE, lock.key), id);
        }
    }
}
