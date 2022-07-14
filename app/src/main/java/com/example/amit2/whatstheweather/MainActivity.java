package com.example.amit2.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);
    }

    public void getWeather(View view){//onClick function
                resultTextView.setText("");//this will clear the previous results from the screen
        try {
            DownloadTask task = new DownloadTask();//creating the object of the DownloadTask class

            String encodedCityName = URLEncoder.encode(editText.getText().toString(),"UTF-8");//this will handle the spaces between
            //the words if the city name is of multiple words, it will fill the spaces with %20 which will make the url error free

            task.execute("https://api.openweathermap.org/data/2.5/weather?q="+encodedCityName+"&units=metric&appid=7cf658796b3848b0b2e305eeea93a869");
            //now fetching the result of the provided url //in above url if we use &units=metric it will convert the units of temperature in degree celcius

            //below two lines will hide the keyboard from the emulator as soon as the button will be clicked
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"could not find weather details !",Toast.LENGTH_SHORT).show();
            //if any exception found related to url we will toast up the above message
        }

    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        //this class will download the complete content from the above getWeather function
        @Override
        protected String doInBackground(String... urls) {
            try{
                String result="";
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {//this function we used to work on JSON

            super.onPostExecute(s);//here s is the complete details returned by the above function i.e. result

            try {

                JSONObject jsonObject = new JSONObject(s);//creating JSON Object
                String weatherInfo1 = jsonObject.getString("weather");//breaking the String s into small details of heading weather
                String weatherInfo2 = jsonObject.getString("main");//breaking the string s into small details of heading main

                //below code is used to fetch details from jsonArray

                JSONArray arr1 = new JSONArray(weatherInfo1);//now creating json array of string that we reduced above
                String message = "";

                for(int i=0;i<arr1.length();i++){//fetching the required data from the jsonArray "weather"
                    JSONObject jsonPart = arr1.getJSONObject(i);//getting each part of json array
                    String main = jsonPart.getString("main");//displaying the content of the string that matches the "main"

                    if(!main.equals("")) {
                        message += main + "\r\n";// '\ r' is the representation of the special character, it moves the cursor to the beginning of the line. '\ n'(line feed) moves the cursor to the next line
                    }
                }
                // { represents jsonObject and [ represents jsonArray in the json data

                // the below code is used to fetch details from the jsonObject

                JSONObject temperatureDetails = new JSONObject(weatherInfo2);// now creating json object of string that we reduced above


                // fetching the required data from the jsonObject "main"
                message += "Temperature : "+ Double.toString(temperatureDetails.getDouble("temp")) + " C \r\n";
                message += "Min Temperature : "+ Double.toString(temperatureDetails.getDouble("temp_min")) + " C \r\n";
                message += "Max Temperature : "+Double.toString(temperatureDetails.getDouble("temp_max")) + " C \r\n";
                message += "Pressure : "+Double.toString(temperatureDetails.getDouble("pressure")) + "\r\n";
                message += "Humidity : "+Double.toString(temperatureDetails.getDouble("humidity")) + "\r\n";

                if(!message.equals("")){
                    resultTextView.setText(message);//here we setup the message in the result text view i.e. the details that we want to display
                }
                else{
                    Toast.makeText(getApplicationContext(),"could not find weather details !",Toast.LENGTH_SHORT).show();
                    // if the message is empty that means there is no data so we will toast up the above message
                }


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"could not find weather details !",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                //in case of any exception we will toast up the above message
            }
        }
    }
}
//doInBackground function is used to complete the things in Background so we cannot use it to display anything or
//toasting up messages, it will give the error if we try to use anything related to UI. It will do the background processing