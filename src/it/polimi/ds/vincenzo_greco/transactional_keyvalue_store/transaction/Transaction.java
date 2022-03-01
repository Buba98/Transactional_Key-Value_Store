package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.GlobalVariables;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore.LockType;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation.Operation;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation.Read;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation.Write;

import java.io.Serializable;
import java.util.*;

public class Transaction implements Serializable {
    final public ArrayList<String> sortedKeys = new ArrayList<>();
    final public HashMap<String, ArrayList<Operation>> operationMap = new HashMap<>();
    final public HashMap<String, LockType> maxLockForKey = new HashMap<>();
    final public HashMap<String, OptimizedOperation> optimizedOperationMap = new HashMap<>();
    final public HashMap<String, String> optimizedResults = new HashMap<>();
    final public ArrayList<Operation> operations;

    public Transaction(ArrayList<Operation> operations) {
        this.operations = operations;
        Operation operation;
        String key;

        for (int i = 0; i < operations.size(); i++) {

            key = operations.get(i).keyValue.key;

            if (!sortedKeys.contains(key)) {

                ArrayList<Operation> operationList = new ArrayList<>();

                operationMap.put(key, operationList);

                for (int j = i; j < operations.size(); j++) {

                    operation = operations.get(j);

                    if (Objects.equals(operation.keyValue.key, key)) {
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

                maxLockForKey.put(key, maxLock);

                optimizedOperationMap.put(key, new OptimizedOperation(firstRead, lastWrite, maxLock, key));
                sortedKeys.add(key);
            }
        }
        sortedKeys.sort(Comparator.naturalOrder());
    }

    public static Transaction fromString(String string) throws IllegalArgumentException {
        ArrayList<Operation> operations = new ArrayList<>();
        if (!string.matches(GlobalVariables.operationRegex))
            throw new IllegalArgumentException(string + " not a valid transaction");
        String[] operationsString = string.split(",");
        for (String operationString : operationsString) {
            if (operationString.startsWith("r")) {
                operations.add(Operation.create(new KeyValue(operationString.split("\\(")[1].split("\\)")[0], null)));
            } else {
                operations.add(Operation.create(new KeyValue(operationString.split("\\(")[1].split("\\)")[0], operationString.split("\\)")[1])));
            }
        }
        return new Transaction(operations);
    }

    public void printResult() {
        Map<String, List<String>> completeResults = new HashMap<>();
        List<String> list;
        List<Operation> support;

        Map<String, List<Operation>> operationMap = new HashMap<>();

        for(int i = 0; i < operations.size(); i++){
            if(operationMap.get(operations.get(i).keyValue.key) == null){
                support = new ArrayList<>();
                operationMap.put(operations.get(i).keyValue.key, support);
                for (int j = i; j < operations.size(); j++){
                    if(Objects.equals(operations.get(j).keyValue.key, operations.get(i).keyValue.key)){
                        support.add(operations.get(j));
                    }
                }
            }
        }



        for (String key : sortedKeys) {
            list = new ArrayList<>();
            completeResults.put(key, list);
            String value = optimizedResults.get(key);
            for (Operation operation : operationMap.get(key)) {
                if (operation instanceof Read) {
                    list.add(value);
                } else {
                    value = operation.keyValue.value;
                }
            }
        }

        for (Operation operation : operations) {
            if (operation instanceof Read) {
                System.out.println(operation.keyValue.key + ": " + completeResults.get(operation.keyValue.key).remove(0));
            }
        }
    }
}