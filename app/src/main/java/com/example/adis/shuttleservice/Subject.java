package com.example.adis.shuttleservice;

/**
 * Created by Adis on 10/24/2014.
 */
public interface Subject {
    public void registerObserver(Observer o);
    public void removeObserver(Observer o);
}
