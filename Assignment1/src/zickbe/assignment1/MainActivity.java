package zickbe.assignment1;

import zickbe.assignment1.NetworkTask;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	String text1;
	String text2;
	String zipcode = "24060";
	TextView http;
	TextView zip;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final NetworkTask weatherTask = new NetworkTask();
    	weatherTask.execute("http://www.weather.com/weather/today/Blacksburg+VA+24060:4:US");
    	
    	final NetworkTask secondOpinionTask = new NetworkTask();
    	secondOpinionTask.execute("http://weather.cnn.com/weather/forecast.jsp?locCode=USVA0068&zipCode=24060");
    	
    	
    	
    	final Button pressedButton = (Button) findViewById(R.id.executeButton);
    	http = (TextView) findViewById(R.id.httpText);
    	
    	final Button cancelledButton = (Button) findViewById(R.id.stopButton);
    	zip = (TextView) findViewById(R.id.zipCode);
    	
    	new Thread(new Runnable() {
    		@Override
    		public void run(){
    			cancelledButton.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					http.setText("Cancelled");
    				}

    			});
    			
    			pressedButton.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					
    					http.setText("");
    					
    					final String tempString = zip.getText().toString();
    					if(tempString.equals("24060"))
    					{
	    					text1 = weatherTask.getHttpString();
	    					parseAndSetWeather(text1);
	    					text2 = secondOpinionTask.getHttpString();
	    					parseAndSetSecondOpinion(text2);
    					}
    					else
    					{
    						final NetworkTask nonBlacksburg = new NetworkTask();
    						new Thread(new Runnable(){
    							@Override
    							public void run(){
    								
    								
    	    				    	nonBlacksburg.execute("http://www.weather.com/search/enhancedlocalsearch?where="+tempString);
    	    				    	http.setText("Press Go again to retrieve weather\n");
    								pressedButton.setOnClickListener(new OnClickListener(){
    									@Override
    									public void onClick(View v){
    										http.setText("");
    										text1 = nonBlacksburg.getHttpString();
    				    					parseAndSetWeather(text1);
    				    					text2 = secondOpinionTask.getHttpString();
    				    					parseAndSetSecondOpinion(text2);
    				    					
    										
    									}
    								});
    							}
    						}).start();
    						
    				    	
    				    	text2 = secondOpinionTask.getHttpString();
	    					parseAndSetSecondOpinion(text2);
	    					text1 = nonBlacksburg.getHttpString();
	    					parseAndSetWeather(text1);
	    					
	    					
    					}
    					
    				}

    			});
    			
    		}
    	}).start();
    	
    	
    	
    	
	}

	/*private void setHTTPText(String textToPut)
	{
		http.setText(textToPut);
	}*/
	private void parseAndSetWeather(String textToParse)
	{
		int loc = textToParse.indexOf("<span itemprop=\"feels-like-temperature-fahrenheit\">");
		if(loc<0)
		{
			http.append("Error in parsing weather.com. Try again.");
		}
		else
		{
			http.append("Information from weather.com:\n");
			http.append("Temperature for ZipCode" + zip.getText().toString() +"\n");
			http.append("Feels like:");
			http.append(textToParse.substring(loc+"<span itemprop=\"feels-like-temperature-fahrenheit\">".length(),loc+"<span itemprop=\"feels-like-temperature-fahrenheit\">".length() +2));
			http.append("\n");
		}
	}
	private void parseAndSetSecondOpinion(String textToParse)
	{
		
		int loc = textToParse.indexOf("Feels like: </b>");

		if(loc<0)
		{
			//http.setText(textToParse);
			http.append("Error Parsing weather.cnn.com. Try again.");
		}
		else
		{
			http.append("Information from weather.cnn.com:\n");
			http.append("The temperature for ZipCode 24060 is:\n");
			String str = textToParse.substring(loc+"Feels like: </b>".length(), loc+"Feels like: </b>".length()+30);
			str = str.trim();
			str = str.substring(0,2);
			http.append(str);
			http.append("\n");
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}