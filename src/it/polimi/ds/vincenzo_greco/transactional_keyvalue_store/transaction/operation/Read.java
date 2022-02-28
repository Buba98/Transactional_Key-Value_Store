package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.KeyValue;

import java.io.Serializable;

public class Read extends Operation implements Serializable {

    public Read(KeyValue keyValue) {

        super(keyValue);

        assert keyValue.value == null;
    }
}
