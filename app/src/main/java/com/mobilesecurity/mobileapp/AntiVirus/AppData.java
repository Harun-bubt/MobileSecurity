package com.mobilesecurity.mobileapp.AntiVirus;

import java.io.IOException;
import java.io.Serializable;

import android.content.Context;

import org.joda.time.DateTime;


public class AppData implements Serializable
{
    transient final static String filePath="state.data";

	transient static AppData _instance=null;


	private boolean _firstScanDone=false;
	public boolean getFirstScanDone() { return _firstScanDone;}
	public void setFirstScanDone(boolean firstScanDone) { _firstScanDone=firstScanDone; }

	private boolean _eulaAccepted=false;

	public void setEulaAccepted(boolean eulaAccepted) { _eulaAccepted=eulaAccepted; }

	transient static public DateTime kNullDate=new DateTime(1973,1,1,0,0);
    private DateTime _lastScanDate=new DateTime(1973,1,1,0,0);
	public DateTime getLastScanDate() { return _lastScanDate;}
	public void setLastScanDate(DateTime date) {_lastScanDate=date;}

    private DateTime _lastAdDate=new DateTime(1973,1,1,0,0);
    public DateTime getLastAdDate() { return _lastAdDate;}
    public void setLastAdDate(DateTime date) {_lastAdDate=date;}



	static public  synchronized AppData getInstance(Context context)
	{
		if(_instance!=null)
		{
			return _instance;
		}
		else
		{
			_instance=StaticTools.deserializeFromDataFolder(context,filePath);

			if(_instance==null)
				_instance = new AppData();

			return _instance;
		}
	}


	public AppData() 
	{
		
	}
	
	
	public synchronized  void serialize(Context ctx)
	{
		try
		{
			StaticTools.serializeToDataFolder(ctx, this, filePath);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
}
