package com.habna.dev.foodjournal;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jhabs on 8/5/2016.
 */
public class NutritionInfoFetch {

  private static final String API_KEY = "9jSTHxB9YFRD9dhwJ7q1Pgi5Mz9MADOrVEfKZQvJ";
  private static final String API_URL = "http://api.nal.usda.gov/ndb/search/?format=json";
  // &q=butter&sort=n&max=25&offset=0&api_key=DEMO_KEY

  public static String get(Context context, String name)  {
    try {
      String urlString = API_URL + "&q=" + name + "&max=25" + "$api_key=" + API_KEY;
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)  {
          builder.append(line).append("\n");
        }
        reader.close();
        return builder.toString();
      } finally {
        connection.disconnect();
      }
    } catch (Exception e) {
      Log.e("ERROR", e.getMessage(), e);
      return null;
    }
  }
}
