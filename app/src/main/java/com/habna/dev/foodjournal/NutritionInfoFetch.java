package com.habna.dev.foodjournal;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jhabs on 8/5/2016.
 */
public class NutritionInfoFetch extends AsyncTask<String, Void, Map<String, Food>> {

  private static final String API_KEY = "9jSTHxB9YFRD9dhwJ7q1Pgi5Mz9MADOrVEfKZQvJ";
  private static final String URL_BASE = "http://api.nal.usda.gov/ndb/nutrients/?format=json&subset=1&max=1000";
  private static final String P_KEY = "203";
  private static final String C_KEY = "205";
  private static final String F_KEY = "204";
  private static final String CAL_KEY = "208";
  private static final String NUTRIENTS_KEY = "&nutrients=";

  @Override
  protected Map<String, Food> doInBackground(String... strings) {
    HttpURLConnection connection = null;
    try {
      URL url = new URL(buildURLString());
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.connect();
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      String line;
      StringBuilder builder = new StringBuilder();
      while ((line = reader.readLine()) != null)  {
        builder.append(line);
      }
      reader.close();
      JSONObject jsonObject = new JSONObject(builder.toString());
      JSONArray foods = jsonObject.getJSONObject("report").getJSONArray("foods");
      Map<String, Food> results = new HashMap<>();
      for (int i = 0; i < 971; i++)  {
        JSONObject foodJson = foods.getJSONObject(i);
        String name = foodJson.getString("name");
        String measure = foodJson.getString("measure");
        JSONArray nutrientsJson = foodJson.getJSONArray("nutrients");
        String protein = sanitizeValue(nutrientsJson.getJSONObject(0).getString("value"));
        String fat = sanitizeValue(nutrientsJson.getJSONObject(1).getString("value"));
        String carbs = sanitizeValue(nutrientsJson.getJSONObject(2).getString("value"));
        String cals = sanitizeValue(nutrientsJson.getJSONObject(3).getString("value"));
        Food food = new Food(name, Double.valueOf(protein), Double.valueOf(carbs),
          Double.valueOf(fat), measure, Double.valueOf(cals));
        results.put(food.getName().toUpperCase(), food);
      }
      return results;
    } catch (Exception e) {
      Log.e("ERROR", e.getMessage(), e);
      return null;
    }
  }

  @Override
  protected void onPostExecute(Map<String, Food> stringFoodMap) {
    super.onPostExecute(stringFoodMap);
    MainSwipeActivity.usdaFoodMap = stringFoodMap;
  }

  private String sanitizeValue(String str)  {
    return str.equals("--") ? String.valueOf("0.0") : str;
  }


  private static String buildURLString()  {
    StringBuilder builder = new StringBuilder();
    builder.append(URL_BASE);
    builder.append(NUTRIENTS_KEY+P_KEY);
    builder.append(NUTRIENTS_KEY+C_KEY);
    builder.append(NUTRIENTS_KEY+F_KEY);
    builder.append(NUTRIENTS_KEY+CAL_KEY);
    builder.append("&api_key="+API_KEY);
    return builder.toString();
  }
}
