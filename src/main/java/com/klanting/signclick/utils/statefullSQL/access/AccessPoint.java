package com.klanting.signclick.utils.statefullSQL.access;

public interface AccessPoint<T> {
    /*
    * Must return the same object, but a wrapper around each function, to track all the information
    * */
    T createRow(T entity);

}
