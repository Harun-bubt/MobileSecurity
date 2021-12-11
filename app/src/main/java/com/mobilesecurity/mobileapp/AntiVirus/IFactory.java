package com.mobilesecurity.mobileapp.AntiVirus;


public interface IFactory<T>
{
    T createInstance(String s);
}
