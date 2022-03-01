package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore.LockType;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation.Read;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation.Write;

import java.io.Serializable;
import java.util.Objects;

/**
 * This represents an optimized operation on one variable
 * In case the lockType is free the operation represents a fetch
 */
public class OptimizedOperation implements Serializable {
    public final Read firstRead;
    public final Write lastWrite;
    public final LockType lockType;
    public final String key;

    public OptimizedOperation(Read firstRead, Write lastWrite, LockType lockType, String key) {

        assert (lockType == LockType.EXCLUSIVE && lastWrite != null) || lockType == LockType.SHARED;

        assert (firstRead == null || Objects.equals(firstRead.key, key)) && (lastWrite == null || Objects.equals(lastWrite.value, key));

        this.key = key;
        this.firstRead = firstRead;
        this.lastWrite = lastWrite;
        this.lockType = lockType;
    }
}
