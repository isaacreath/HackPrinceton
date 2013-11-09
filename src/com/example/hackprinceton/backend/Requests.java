package com.example.hackprinceton.backend;




import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.util.Log;

public class Requests {
	

	  private static String cookies;
	  private static HttpClient client = new DefaultHttpClient();
	  private static final String USER_AGENT = "Mozilla/5.0";
	  
	  public static ArrayList<String> getTweet(String hashtag){
		  
			String result;
			try {
				result = Requests.GetPageContent("https://api.twitter.com/1.1/search/tweets.json?q=%23" + hashtag + "&locale=ja&");
				System.out.println(result);
				Object jsonResult = JSONValue.parse(result);
				JSONObject resultArray = (JSONObject)jsonResult;
				JSONArray a =(JSONArray) resultArray.get("statuses");
				System.out.println(a.get(0));
				ArrayList<JSONObject> objectList = new ArrayList<JSONObject>();
				for(int i = 0; i < a.size(); i++){
					JSONObject b = (JSONObject)a.get(i);
					objectList.add(b);
				}
				ArrayList<String> tweets = new ArrayList<String>();
				for(int i = 0; i < objectList.size(); i++ ){
					tweets.add((String)objectList.get(i).get("text"));
					
				}
				return tweets;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;

	  }
	  public static ArrayList<String> getTrending(){
		  String result;
		  try {
			result = Requests.GetPageContent("https://api.twitter.com/1.1/trends/place.json?id=1");
			Object resultJSON = JSONValue.parse(result);
			JSONArray a = (JSONArray)resultJSON;
			
			ArrayList<JSONObject> tweetObj = new ArrayList<JSONObject>();
			for(int i = 0; i < a.size(); i++){
				tweetObj.add((JSONObject)a.get(i));
			}
			JSONObject o = tweetObj.get(0);
			JSONArray array = (JSONArray) o.get("trends");
			ArrayList<JSONObject> tweets = new ArrayList<JSONObject>();
			for(int i = 0; i < array.size(); i++){
				tweets.add((JSONObject) array.get(i));
			}
			ArrayList<String> tweetStrings = new ArrayList<String>();
			for(int i = 0; i < tweets.size(); i++){
				tweetStrings.add((String) tweets.get(i).get("name"));
				
			}
			return tweetStrings;
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  return null;
		
	  }
			  
	  private static String GetPageContent(String url) throws Exception {
		  
		  
			OAuthConsumer consumer = new CommonsHttpOAuthConsumer("xJuhWDFDybWf4u8ezQehA", "h9ztt35DyuoPoDHKVghcDj29xtPOVaqmuSpZCUtTS28");
			consumer.setTokenWithSecret("1381142456-ThDPPh0xkQj85IYJWLgfzl9IlHAhkds0CgXifvp", "dGV4Dswc3qTQwbuMAwgAl2zy2UjTNJE6tsKbVxiUGmj63");
			URL urlz = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlz.openConnection();
			HttpGet request = new HttpGet(url);
			consumer.sign(request);
			conn.connect();
			
			
		 
			request.setHeader("User-Agent", USER_AGENT);
			request.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			request.setHeader("Accept-Language", "en-US,en;q=0.5");
		 
			HttpResponse response = client.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();
		 
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
			
			BufferedReader rd = new BufferedReader(
		                new InputStreamReader(response.getEntity().getContent()));
		 
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
		 
			// set cookies
			setCookies(response.getFirstHeader("Set-Cookie") == null ? "" : 
		                     response.getFirstHeader("Set-Cookie").toString());
		 
			return result.toString();
		 
		  }
		 

		 
		  public static String getCookies() {
			return cookies;
		  }
		 
		  public static void setCookies(String cookies) {
			cookies = cookies;
		  }
		 
}
