package com.mobilesecurity.mobileapp.AntiVirus;

import java.util.Collection;
import java.util.Set;


public interface IDataSet<T>
{
    int getItemCount();
    boolean addItem(T item);
    boolean removeItem(T item);
    boolean addItems(Collection<? extends T> item);
    Set<T> getSet();
}
