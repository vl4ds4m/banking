package org.vl4ds4m.banking.service.expection;

public class DuplicateEntityException extends ServiceException {

    public DuplicateEntityException(String entity) {
        super(entity + " already exists");
    }
}
