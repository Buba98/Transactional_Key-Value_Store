package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.operation;

import java.io.Serializable;

public enum OperationType implements Serializable {
    READ("r"),
    WRITE("w");

    final String operation;

    OperationType(String operation) {
        this.operation = operation;
    }

    static OperationType byValue(String value) {
        return switch (value) {
            case "r" -> READ;
            case "w" -> WRITE;
            default -> null;
        };
    }
}
