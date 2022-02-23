package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore;

import it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.KeyValue;

import java.util.HashMap;
import java.util.Map;

public class DataStore {

    Map<String, String> keyValues = new HashMap<>();

    public synchronized void write(KeyValue keyValue) {
        keyValues.put(keyValue.key, keyValue.value);
    }

    public synchronized KeyValue read(KeyValue keyValue) {
        return new KeyValue(keyValue.key, keyValues.get(keyValue.key));
    }
}