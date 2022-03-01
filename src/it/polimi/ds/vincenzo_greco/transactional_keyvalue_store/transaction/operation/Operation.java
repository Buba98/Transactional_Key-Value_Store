package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation;

import java.io.Serializable;

public abstract class Operation implements Serializable {
    public final String key;

    protected Operation(String key) {
        assert key != null;
        this.key = key;
    }
}
