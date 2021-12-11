package com.mobilesecurity.mobileapp.AntiVirus;


public interface IResultsAdapterItem
{
    enum ResultsAdapterItemType { Header, AppMenace, SystemMenace}

    public ResultsAdapterItemType getType();

}


