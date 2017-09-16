package com.squareplaces.squareplaces;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class ConnectionDetector {

	private Context context;
	
	public ConnectionDetector(Context context){
		this.context = context;
	}
	
	public boolean isConnectingToInternet(){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm != null){
			@SuppressWarnings("deprecation")
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if(info !=null)
				for(int i=0; i<info.length; i++)
					if(info[i].getState()==State.CONNECTED){
						return true;
					}
		}
		return false;
	}
}
