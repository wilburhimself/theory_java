package io.github.wilburhimself.theory.interfaces;

import java.util.List;

public interface IRepository<T> {
    T find(Integer id);
    List<T> list();
    T save(T t);
    T update(T t);
    void remove(T t);
}
