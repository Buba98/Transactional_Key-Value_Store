package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore.LockType;

import java.io.Serializable;
import java.util.Objects;

public class OptimizedOperation implements Serializable {
    public final Operation firstRead;
    public final Operation lastWrite;
    public final LockType lockType;

    public OptimizedOperation(Operation firstRead, Operation lastWrite, LockType lockType) {

        assert lockType != null && lockType != LockType.FREE;

        if (lockType == LockType.EXCLUSIVE) {
            assert lastWrite != null;
            assert lastWrite.operationType == OperationType.WRITE;
            assert firstRead == null || (firstRead.operationType == OperationType.READ);
        } else if (lockType == LockType.SHARED) {
            assert lastWrite == null && firstRead != null;
            assert firstRead.operationType == OperationType.READ;
        }

        this.firstRead = firstRead;
        this.lastWrite = lastWrite;
        this.lockType = lockType;
    }
}
