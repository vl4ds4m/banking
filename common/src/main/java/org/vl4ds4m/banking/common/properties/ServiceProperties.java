package org.vl4ds4m.banking.common.properties;

public interface ServiceProperties {

    String host();

    Integer port();

    default String httpUrl() {
        return "http://" + host() + ":" + port();
    }
}
