package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore;

public class Lock {
    public final String key;
    public final LockType lockType;
    public Lock(String key, LockType lockType) {
        this.key = key;
        this.lockType = lockType;
    }
}
