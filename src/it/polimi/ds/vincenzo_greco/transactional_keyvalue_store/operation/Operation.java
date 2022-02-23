package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.KeyValue;

import java.io.Serializable;

public class Operation implements Serializable {

    public final OperationType operationType;
    public final KeyValue keyValue;

    private Operation(OperationType operationType, KeyValue keyValue) {
        if (operationType == OperationType.READ) {
            assert (keyValue.value == null);
        } else {
            assert (keyValue.value != null);
        }
        this.operationType = operationType;
        this.keyValue = keyValue;
    }

    public static Operation create(KeyValue keyValue) {
        if (keyValue.value == null || keyValue.value.isEmpty() || keyValue.value.isBlank()) {
            return new Operation(OperationType.READ, keyValue);
        } else {
            return new Operation(OperationType.WRITE, keyValue);
        }
    }
}
