package org.vl4ds4m.banking.webui.service;

import java.util.UUID;

public class UuidGenerator {

    private UuidGenerator() {
        throw new AssertionError();
    }

    public static UUID random() {
        return UUID.randomUUID();
    }
}
