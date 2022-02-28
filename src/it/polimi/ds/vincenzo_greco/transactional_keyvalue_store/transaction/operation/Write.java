package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.KeyValue;

import java.io.Serializable;

public class Write extends Operation implements Serializable {

    public Write(KeyValue keyValue) {
        super(keyValue);

        assert keyValue.key != null;
    }
}
