package com.tajchert.glassware.pjwstk.api;

import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.google.gson.Gson;
import com.tajchert.glassware.pjwstk.GlassOwner;
import com.tajchert.glassware.pjwstk.Tools;

import eu.masconsult.android_ntlm.NTLMSchemeFactory;

public class getAPI {
	public static String getNTLMResponse(String username, String password){
		try{
			DefaultHttpClient httpclient = new DefaultHttpClient();
		    httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
		    httpclient.getCredentialsProvider().setCredentials(
		            // Limit the credentials only to the specified domain and port
		            new AuthScope("ws.pjwstk.edu.pl", 443),
		            // Specify credentials, most of the time only user/pass is needed
		            new NTCredentials( username + "@pjwstk.edu.pl", password+"", "", "")
		            );
		    HttpHost targetHost = new HttpHost("ws.pjwstk.edu.pl", 443, "https");
	        
		    String url = "";
		    //Get and set dates for getting api
		    Date dNow = new Date();
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    String nowDateS = sdf.format(dNow);
		    Calendar cal = Calendar.getInstance();
	        cal.setTime(dNow);
	        cal.add(Calendar.DATE, 7);
	        String tillDateS = sdf.format(cal.getTime());
	        //ask api for schedule
	        url = Tools.APIURL + nowDateS + "&end=" + tillDateS;
	        Log.e("PJWSTK_GLASS", url +"");
		    HttpGet httpget = new HttpGet(url);
		    
	        httpget.setHeader("Content-Type", "application/json");
	        BasicHttpContext localContext = new BasicHttpContext();
	        BasicScheme basicAuth = new BasicScheme();
	        localContext.setAttribute("preemptive-auth", basicAuth);
	        HttpResponse response = httpclient.execute(targetHost, httpget, localContext);
	        
	        final int statusCode = response.getStatusLine().getStatusCode();
	        
	        if (statusCode != HttpStatus.SC_OK) {
				return null;
			}

	        HttpEntity entity = response.getEntity();
	        
	        Gson gson = new Gson();
	        
	        Reader reader = new InputStreamReader(entity.getContent());
	        
	        //Tools.apiResponse = gson.fromJson(reader, APIResponse.class);
	        //Tools.apiResponse = gson.fromJson(reader, JsonZajecia.class);
	        Zajecia[] zajecia = gson.fromJson(reader, Zajecia[].class);
	        //Tools.apiResponse  = (ArrayList<JsonZajecia>) Arrays.asList(zajecia);
	        ArrayList<Zajecia> tmp  = new ArrayList<Zajecia>();
	        Collections.addAll(tmp , zajecia);
	        for(Zajecia zaj : tmp){
	        	zaj.date = Tools.convertStringToDate(zaj.getData_roz());
				zaj.endDate = Tools.convertStringToDate(zaj.getData_zak());
				if(!zaj.getKod().equals("HIS6") && !zaj.getKod().equals("NIM6"))
	        	GlassOwner.zajecia.put(zaj.date.getTime(), zaj);
	        }
	        
	        //Log.e("PJWSTK_GLASS", "Lessons fetched: "+ Tools.apiResponse.size());
	        Log.d("PJWSTK_GLASS", "Lessons fetched: "+ GlassOwner.zajecia.size());
	        Object content = EntityUtils.toString(entity);
	        Log.d("PJWSTK_GLASS", "OK: " + content.toString());
	        
	        return content.toString();
		} catch (Exception e) {
			return null;
        }
	}
}
