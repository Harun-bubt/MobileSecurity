package com.mobilesecurity.mobileapp.AntiVirus;

class ResultsAdapterHeaderItem implements IResultsAdapterItem
{
    String _description=null;

    public ResultsAdapterHeaderItem(String description)
    {
        _description=description;
    }

    public String getDescription() { return _description;}

    public ResultsAdapterItemType getType() { return ResultsAdapterItemType.Header;}
}
