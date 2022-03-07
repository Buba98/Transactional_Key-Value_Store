package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore.LockType;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation.Operation;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation.Read;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation.Write;

import java.io.Serializable;
import java.util.*;

public class Transaction implements Serializable {
    final public ArrayList<String> sortedKeys = new ArrayList<>();
    final public HashMap<String, OptimizedOperation> optimizedOperationMap = new HashMap<>();
    final public ArrayList<Operation> operations;

    public Transaction(ArrayList<Operation> operations) {
        this.operations = operations;
        Operation operation;
        String key;

        for (int i = 0; i < operations.size(); i++) {

            key = operations.get(i).key;

            if (!sortedKeys.contains(key)) {

                ArrayList<Operation> operationList = new ArrayList<>();

                for (int j = i; j < operations.size(); j++) {

                    operation = operations.get(j);

                    if (Objects.equals(operation.key, key)) {
                        if (operationList.size() > 0 &&
                                operationList.get(operationList.size() - 1).getClass() == operation.getClass()) {
                            operationList.remove(operationList.size() - 1);
                        }
                        operationList.add(operation);
                    }
                }

                Write lastWrite = null;
                Read firstRead = null;
                LockType maxLock = LockType.SHARED;

                if (operationList.get(0) instanceof Read) {
                    firstRead = (Read) operationList.get(0);
                }

                for (int k = operationList.size() - 1; k >= 0; k--) {
                    if (operationList.get(k) instanceof Write) {
                        lastWrite = (Write) operationList.get(k);
                        maxLock = LockType.EXCLUSIVE;
                        break;
                    }
                }

                optimizedOperationMap.put(key, new OptimizedOperation(firstRead, lastWrite, maxLock, key));
                sortedKeys.add(key);
            }
        }
        sortedKeys.sort(Comparator.naturalOrder());
    }

    public static Transaction fromString(String string) throws IllegalArgumentException {
        if (!string.matches("((r\\(\\w\\)|w\\(\\w\\)\\w),)*(r\\(\\w\\)|w\\(\\w\\)\\w)$"))
            throw new IllegalArgumentException(string + " not a valid transaction");

        ArrayList<Operation> operations = new ArrayList<>();

        for (String operationString : string.split(",")) {
            if (operationString.startsWith("r")) {
                operations.add(new Read(operationString.split("\\(")[1].split("\\)")[0]));
            } else {
                operations.add(new Write(operationString.split("\\(")[1].split("\\)")[0], operationString.split("\\)")[1]));
            }
        }
        return new Transaction(operations);
    }
}