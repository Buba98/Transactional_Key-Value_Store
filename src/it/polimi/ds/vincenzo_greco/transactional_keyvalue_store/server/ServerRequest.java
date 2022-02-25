package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation.OptimizedOperation;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore.Lock;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore.LockType;

import java.io.Serializable;


/**
 * A server request to another server
 */
public class ServerRequest implements Serializable {

    public final OptimizedOperation optimizedOperation;
    public final int sourceId;
    public final int destinationId;
    public final int schedulerTransactionHandlerId;

    public ServerRequest(OptimizedOperation optimizedOperation, int sourceId, int destinationId, int schedulerTransactionHandlerId) {
        this.optimizedOperation = optimizedOperation;
        this.sourceId = sourceId;
        this.destinationId = destinationId;
        this.schedulerTransactionHandlerId = schedulerTransactionHandlerId;
    }
}
