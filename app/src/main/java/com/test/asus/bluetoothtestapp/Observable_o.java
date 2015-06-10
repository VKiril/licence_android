package com.test.asus.bluetoothtestapp;

public class Observable_o extends java.util.Observable {
    // encapsulates management of the observers watching this datasource for update events:
    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }
}
