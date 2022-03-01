package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation;

import java.io.Serializable;

public class Write extends Operation implements Serializable {

    public final String value;

    public Write(String key, String value) {
        super(key);
        assert value != null;
        this.value = value;
    }
}
