package org.vl4ds4m.banking.service.expection;

public class EntityNotFoundException extends ServiceException {

    public EntityNotFoundException(String entity) {
        super(entity + " not found");
    }
}
