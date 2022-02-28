package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.KeyValue;

import java.io.Serializable;

/**
 * A server answer to a server request
 */
public class ServerResponse implements Serializable {

    final public KeyValue keyValue;
    final public int schedulerTransactionId;

    public ServerResponse(KeyValue keyValue, int schedulerTransactionId) {
        this.keyValue = keyValue;
        this.schedulerTransactionId = schedulerTransactionId;
    }
}
