package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.KeyValue;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation.Read;
import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction.operation.Write;

import java.util.HashMap;
import java.util.Map;

public class DataStore {

    Map<String, String> keyValues = new HashMap<>();

    public synchronized void write(Write write) {
        keyValues.put(write.keyValue.key, write.keyValue.value);
    }

    public synchronized KeyValue read(Read read) {
        return new KeyValue(read.keyValue.key, keyValues.get(read.keyValue.key));
    }
}