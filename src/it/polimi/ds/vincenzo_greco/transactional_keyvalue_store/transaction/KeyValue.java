package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.transaction;


import java.io.Serializable;
import java.util.Objects;

/**
 * Simple representation of a key - value pair
 */
public class KeyValue implements Serializable {
    public final String key;
    public final String value;

    public KeyValue(String key, String value) {

        assert value == null || (!value.isEmpty() && !value.isBlank());
        assert key != null && !key.isEmpty() && !key.isBlank();

        this.key = key;
        this.value = value;
    }

    public String toString() {
        return key + ": " + Objects.requireNonNullElse(this.value, "null");
    }
}
