package com.example.yelpapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import android.widget.TextView;

public class MainActivity extends Activity {

private TextView text = null;
	
	//messages from background thread contain data for UI
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			String title =(String) msg.obj;
			text.append(title + "\n" +"\n");
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		text=(TextView)findViewById(R.id.texter);
		
		Thread t = new Thread(background);
		t.start();
	}

	//thread connects to the Yelp Api, gets response data, JSON search results,
	//places data into Log and sends messages to display data on UI
	Runnable background = new Runnable() {
		public void run(){
			
			String consumerKey = "T2pVZpC8c6BuwjDDzqiMmg";
		    String consumerSecret = "DMT6dD6WlBFDE7ltsT4ivbPTzMg";
		    String token = "NQINOSfA4JvVfB4rZUeAt9v1AgeA5gNC";
		    String tokenSecret = "4EC1d8gshNIYw4Cj_HXrBnIVvVo";
		    
		    int search_limit = 5;

		    Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
		    String JSONFeed = yelp.search("pizza", 42.3889167, -71.2208033, search_limit);

		   // System.out.println(JSONFeed);			
			
			//decode JSON
			try {		
				JSONObject obj = new JSONObject(JSONFeed);
				
				//get total of businesses in response without limit
				int total = obj.getInt("total");
				Log.i("JSON", "total " + total);
						
				JSONArray businesses = new JSONArray();
				businesses = obj.getJSONArray("businesses");
				Log.i("JSON",
						"Number of entries " + businesses.length());
				
				//for each array item get name and date
				for (int i = 0; i < businesses.length(); i++) {
					JSONObject jsonObject = businesses.getJSONObject(i);
					String name = jsonObject.getString("name");
				    double rating = jsonObject.getDouble("rating");
				    
				    JSONObject location = new JSONObject();
				    location = jsonObject.getJSONObject("location");
				    
				    String city = location.getString("city");
				    
				    String data = name + ", " + city + " - " + rating;
				    
				    //sent to Handler queue 
				    Message msg = handler.obtainMessage();
				    msg.obj = data;
				    handler.sendMessage(msg);
				    
					Log.i("JSON", data);
					
				}
			} catch (JSONException e) {e.getMessage();
				e.printStackTrace();
			}
		}
	
	};
	
	
}
