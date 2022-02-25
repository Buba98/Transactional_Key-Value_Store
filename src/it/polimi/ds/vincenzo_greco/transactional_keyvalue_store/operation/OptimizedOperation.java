package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore.LockType;

import java.io.Serializable;

public class OptimizedOperation implements Serializable {
    public final Operation firstRead;
    public final Operation lastWrite;
    public final LockType lockType;
    public final String key;

    public OptimizedOperation(Operation firstRead, Operation lastWrite, LockType lockType, String key) {

        assert lockType != null && lockType != LockType.FREE && key != null && !key.isEmpty() && !key.isBlank();

        if (lockType == LockType.EXCLUSIVE) {
            assert lastWrite != null;
            assert lastWrite.operationType == OperationType.WRITE;
            assert key.equals(lastWrite.keyValue.key);
            assert firstRead == null || (firstRead.operationType == OperationType.READ && key.equals(firstRead.keyValue.key));
        } else if (lockType == LockType.SHARED) {
            assert lastWrite == null && firstRead != null;
            assert firstRead.operationType == OperationType.READ;
            assert key.equals(firstRead.keyValue.key);
        }

        this.key = key;
        this.firstRead = firstRead;
        this.lastWrite = lastWrite;
        this.lockType = lockType;
    }
}
