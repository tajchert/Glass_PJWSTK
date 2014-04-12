package com.tajchert.glassware.pjwstk;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;

import com.tajchert.glassware.pjwstk.api.Zajecia;

public class Tools {
	//If you want key for it, as me or admins at PJWSTK
	public static final String APIURL = "";
	
	
	public static boolean isNetworkAvailable(Context context){
	    HttpGet httpGet = new HttpGet("http://www.google.com");
	    HttpParams httpParameters = new BasicHttpParams();
	    int timeoutConnection = 3000;
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	    int timeoutSocket = 5000;
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

	    DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
	    try{
	        httpClient.execute(httpGet);
	        return true;
	    }
	    catch(ClientProtocolException e){
	    	return false;
	    }
	    catch(IOException e){
	    	return false;
	    }
	}
	
	public static Date convertStringToDate(String jsonDate) {
	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date date = sdf.parse(jsonDate);
			date.setTime(date.getTime());
			
			return date;
		} catch (ParseException e) {
			return null;
		}
	}

	public static String convertDateToString(Date d, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		// sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String outString = sdf.format(d);
		return outString;
	}
	
	public static List<Zajecia> deleteOldZajecia(List<Zajecia> in){
		List<Zajecia> tmp = new ArrayList<Zajecia>();
		Date curr = new Date();
		for (Zajecia zaj : in) {
			if(convertStringToDate(zaj.getData_roz()).getTime() - curr.getTime() > 0 &&!zaj.getKod().contains("HIS") && !zaj.getKod().contains("NIM")){
				tmp.add(zaj);
			}
		}
		return tmp;
	}
	
	public static Zajecia getCurrent(List<Zajecia> in){
		Zajecia tmp = null;
		Date curr = new Date();
		for (Zajecia zaj : in) {
			if(convertStringToDate(zaj.getData_roz()).getTime() - curr.getTime() < 0 && convertStringToDate(zaj.getData_zak()).getTime() - curr.getTime() > 0 &&!zaj.getKod().contains("HIS") && !zaj.getKod().contains("NIM")){
				tmp = zaj;
			}
		}
		return tmp;
	}

}
