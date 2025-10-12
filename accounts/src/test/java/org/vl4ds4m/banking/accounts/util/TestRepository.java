package org.vl4ds4m.banking.accounts.util;

import org.springframework.data.repository.CrudRepository;

import java.util.*;

public abstract class TestRepository<T, ID> implements CrudRepository<T, ID> {

    private final Map<ID, T> storage = new HashMap<>();

    protected abstract Optional<ID> extractId(T entity);

    protected abstract void setId(ID id, T entity);

    protected abstract ID produceNextId();

    @Override
    public <S extends T> S save(S entity) {
        var id = extractId(entity).orElseGet(() -> {
            var newId = produceNextId();
            setId(newId, entity);
            return newId;
        });
        storage.put(id, entity);
        return entity;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
        return entities;
    }

    @Override
    public Optional<T> findById(ID id) {
        return existsById(id)
                ? Optional.of(storage.get(id))
                : Optional.empty();
    }

    @Override
    public boolean existsById(ID id) {
        return storage.containsKey(id);
    }

    @Override
    public Iterable<T> findAll() {
        return storage.values();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        var found = new ArrayList<T>();
        for (var id : ids) {
            findById(id).ifPresent(found::add);
        }
        return found;
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public void deleteById(ID id) {
        storage.remove(id);
    }

    @Override
    public void delete(T entity) {
        extractId(entity).ifPresent(this::deleteById);
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }
}
