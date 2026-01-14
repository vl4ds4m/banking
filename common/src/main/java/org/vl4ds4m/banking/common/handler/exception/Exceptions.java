package org.vl4ds4m.banking.common.handler.exception;

public class Exceptions {

    private Exceptions() {}

    public static <T> T rethrow(Throwable t) {
        switch (t) {
            case Error e -> throw e;
            case RuntimeException e -> throw e;
            case null -> throw new IllegalArgumentException("Throwable is null");
            default -> throw new RuntimeException(t);
        }
    }

}
