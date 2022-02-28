package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.KeyValue;

import java.io.Serializable;

public abstract class Operation implements Serializable {
    public final KeyValue keyValue;

    protected Operation(KeyValue keyValue) {
        this.keyValue = keyValue;
    }

    public static Operation create(KeyValue keyValue) {
        if (keyValue.value == null) {
            return new Read(keyValue);
        } else {
            return new Write(keyValue);
        }
    }
}
