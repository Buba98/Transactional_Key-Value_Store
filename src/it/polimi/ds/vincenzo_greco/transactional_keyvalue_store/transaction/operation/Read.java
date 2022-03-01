package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation;

import java.io.Serializable;

public class Read extends Operation implements Serializable {

    public Read(String key) {
        super(key);
    }
}
