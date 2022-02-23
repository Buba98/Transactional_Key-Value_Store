package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store;


import java.io.Serializable;
import java.util.Objects;

public class KeyValue implements Serializable {
    public final String key;
    public final String value;

    public KeyValue(String key, String value) {

        assert value == null || (!value.isEmpty() && !value.isBlank());

        this.key = key;
        this.value = value;
    }

    public String toString() {
        return key + ": " + Objects.requireNonNullElse(this.value, "null");
    }

}
