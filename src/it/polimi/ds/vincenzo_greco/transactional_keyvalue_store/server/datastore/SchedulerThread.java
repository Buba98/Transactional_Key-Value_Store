package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.KeyValue;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation.OptimizedOperation;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation.Transaction;

import java.util.ArrayList;
import java.util.List;

public class SchedulerThread extends Thread {

    final Transaction transaction;
    public final ArrayList<KeyValue> result = new ArrayList<>();
    final int id;
    final Scheduler scheduler;
    final List<Lock> locks = new ArrayList<>();

    public SchedulerThread(Transaction transaction, int id, Scheduler scheduler) {
        this.transaction = transaction;
        this.id = id;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {

        OptimizedOperation optimizedOperation;

        for (String key : transaction.sortedKeys){
            optimizedOperation = transaction.optimizedOperationMap.get(key);

            KeyValue keyValue = scheduler.executeOperation(optimizedOperation);

            locks.add(new Lock(key, optimizedOperation.lockType));

            if(keyValue.value != null){
                result.add(keyValue);
            }
        }

        for(Lock lock : locks){
            scheduler.releaseLock(lock);
        }
    }
}
