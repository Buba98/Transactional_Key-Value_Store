package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation.Operation;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation.Read;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation.Write;

import java.io.Serializable;
import java.util.*;

public class TransactionResult implements Serializable {

    public final Transaction transaction;
    public long startInMilliseconds;
    public long endInMilliseconds;
    public final HashMap<String, String> optimizedResults = new HashMap<>();

    public TransactionResult(Transaction transaction) {
        this.transaction = transaction;
        this.startInMilliseconds = System.nanoTime();
    }

    public void setComplete() {
        this.endInMilliseconds = System.nanoTime();
    }

    public void printResult() {
        Map<String, List<String>> completeResults = new HashMap<>();
        List<String> list;
        List<Operation> support;

        Map<String, List<Operation>> operationMap = new HashMap<>();

        for (int i = 0; i < this.transaction.operations.size(); i++) {
            if (operationMap.get(this.transaction.operations.get(i).key) == null) {
                support = new ArrayList<>();
                operationMap.put(this.transaction.operations.get(i).key, support);
                for (int j = i; j < this.transaction.operations.size(); j++) {
                    if (Objects.equals(this.transaction.operations.get(j).key, this.transaction.operations.get(i).key)) {
                        support.add(this.transaction.operations.get(j));
                    }
                }
            }
        }


        for (String key : this.transaction.sortedKeys) {
            list = new ArrayList<>();
            completeResults.put(key, list);
            String value = this.optimizedResults.get(key);
            for (Operation operation : operationMap.get(key)) {
                if (operation instanceof Read) {
                    list.add(value);
                } else if (operation instanceof Write) {
                    value = ((Write) operation).value;
                }
            }
        }

        for (Operation operation : this.transaction.operations) {
            if (operation instanceof Read) {
                System.out.println(operation.key + ": " + completeResults.get(operation.key).remove(0));
            }
        }

        long duration = (endInMilliseconds - startInMilliseconds) / 1000;
        long micro = duration % 1000;
        duration /= 1000;
        long milli = duration % 1000;
        duration /= 1000;
        long sec = duration;

        System.out.println("Execution time: " + sec + "s " + milli + "ms " + micro + "mu");
    }
}
