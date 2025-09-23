package be.esi.prj.easyeval.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<K, T> {
    public Optional<T> findById(K key);
    public List<T> findAll();
    public void save(T item);
    public void deleteById(K key);
    public void close();
}
