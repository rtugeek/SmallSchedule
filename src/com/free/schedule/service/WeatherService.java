package com.free.schedule.service;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class WeatherService {
	private Context context;
	private Location location;
	private LocationManager loctionManager;
	public WeatherService(Context context){
		this.context = context;
	}
	private String[] array;
	public String[] getWeather(){
		loctionManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
	    Criteria criteria = new Criteria();
	    criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
	    criteria.setAltitudeRequired(false);
	    criteria.setBearingRequired(false);
	    criteria.setPowerRequirement(Criteria.POWER_LOW);
	    criteria.setCostAllowed(false);
	    String provider = loctionManager.getBestProvider(criteria, false);
	    location = loctionManager.getLastKnownLocation(provider);
	    Log.i("provider", provider);
	    array = new String[]{""};
	    if(location == null){
	    	array = null;
	    }else{
	    	String url = "http://api.map.baidu.com/geocoder?location="+ getLocation(location)+"&output=json";
	    	array = getWeather(getLocationName(getRestule(url)));
	    }
		return array;
	}
	
	private String getLocation(Location location){
		String  laitude = location.getLatitude()+"";//经度
    	String longitude = location.getLongitude()+"";//纬度
    	return laitude +","+ longitude;
	}
	
	private String getRestule(String url){
		String reslut = "";
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if(httpResponse.getStatusLine().getStatusCode() == 200){
				reslut = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return reslut;
		
	}

	public String getLocationName(String json){
		String result = "漳州";
		try {
			JSONObject jsonObject = new JSONObject(json);
			if(jsonObject.getString("status").equals("OK")){
				jsonObject = jsonObject.getJSONObject("result");
				jsonObject = jsonObject.getJSONObject("addressComponent");
				result = jsonObject.getString("city");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}
	
	public String[] getWeather(String city){
		String result = city+":";
		String url = "http://api.map.baidu.com/telematics/v3/weather?location="+ city+"&output=json&ak=5slgyqGDENN7Sy7pw29IUvrZ";
		String[] array = new String[]{""};
		try {
			JSONObject jsonObject = new JSONObject(getRestule(url));
			System.out.println(jsonObject);
			if(jsonObject.getString("status").equals("success")){
		        // Convert from Unicode to UTF-8
				jsonObject = new JSONObject(jsonObject.getJSONArray("results").get(0).toString());
				JSONArray jsonArray = jsonObject.getJSONArray("weather_data");
				array = new String[jsonArray.length()];
				for(int i = 0 ;i < jsonArray.length();i++){
					result = city+":";
					jsonObject = jsonArray.getJSONObject(i);
					result += jsonObject.getString("date") + ";" 
					+ jsonObject.getString("weather") +";" 
					+ jsonObject.getString("wind") +";"
					+ jsonObject.getString("temperature") +";";
					array[i] = result;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return array;
	}
}
