package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore;

import java.io.Serializable;

public enum LockType implements Serializable {
    FREE,
    SHARED,
    EXCLUSIVE
}
