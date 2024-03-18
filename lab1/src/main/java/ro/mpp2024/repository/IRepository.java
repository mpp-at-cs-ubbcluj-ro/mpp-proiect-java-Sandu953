package ro.mpp2024.repository;

import ro.mpp2024.domain.Entity;

public interface IRepository<ID, T> {
    int size();
    void save(T entity);
    void delete(ID id);
    void update(ID id, T entity);
    T findOne(ID id);
    Iterable<T> findAll();
}