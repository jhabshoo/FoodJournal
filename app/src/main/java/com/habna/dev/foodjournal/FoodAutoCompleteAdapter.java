package com.habna.dev.foodjournal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FoodAutoCompleteAdapter extends BaseAdapter implements Filterable {

  private static final int MAX_RESULTS = 10;
  private Context mContext;
  private List<Food> resultList = new ArrayList<>();

  private final String SEARCH_URL_BASE = "http://api.nal.usda.gov/ndb/search/?format=json";
  private final String FOOD_INFO_URL_BASE = "http://api.nal.usda.gov/ndb/reports/";
  private final String API_KEY = "9jSTHxB9YFRD9dhwJ7q1Pgi5Mz9MADOrVEfKZQvJ";
  // &q=butter&sort=n&max=25&offset=0&api_key=DEMO_KEY

  public FoodAutoCompleteAdapter(Context context) {
    mContext = context;
  }

  @Override
  public int getCount() {
    return resultList.size();
  }

  @Override
  public Food getItem(int index) {
    return resultList.get(index);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) mContext
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.simple_dropdown_item_2line, parent, false);
    }
    ((TextView) convertView.findViewById(R.id.searchText1)).setText(getItem(position).getName());
    ((TextView) convertView.findViewById(R.id.searchText2)).setText(getItem(position).getNutrition());
    return convertView;
  }

  @Override
  public Filter getFilter() {
    Filter filter = new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();
        if (constraint != null) {
          List<Food> foods = findFoods(constraint.toString());

          // Assign the data to the FilterResults
          filterResults.values = foods;
          filterResults.count = foods.size();
        }
        return filterResults;
      }

      @Override
      protected void publishResults(CharSequence constraint, FilterResults results) {
        if (results != null && results.count > 0) {
          resultList = (List<Food>) results.values;
          notifyDataSetChanged();
        } else {
          notifyDataSetInvalidated();
        }
      }};
    return filter;
  }

  /**
   * Returns a search result for the given book title.
   */
  private List<Food> findFoods(String name) {
    HttpURLConnection connection = null;
    try {
      URL url = new URL(buildSearchUrlString(name));
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.connect();
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      String line;
      List<String> ids = new ArrayList<>();
      while ((line = reader.readLine()) != null)  {
        if (line.contains("ndbno")) {
          ids.add(line.substring(line.indexOf("ndbno")+9).replace("\"", ""));
        }
      }
      reader.close();
      return fetchFoodInfo(ids);
    } catch (Exception e) {
      return new ArrayList<>();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  private List<Food> fetchFoodInfo(List<String> ids) {
    List<Food> results = new ArrayList<>();
    HttpURLConnection connection = null;
    try {
      for (String id : ids) {
        URL url = new URL(buildFoodInfoUrlString(id));
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        String name = "";
        String protein = "";
        String carbs = "";
        String fat = "";
        int nutrientCount = 0;
        boolean nameNext = false;
        while ((line = reader.readLine()) != null)  {
          if (line.contains("ndbno")) {
            nameNext = true;
          } else if (line.contains("name") && nameNext) {
            name = line.substring(line.indexOf("name")+8, line.length()-2);
            nameNext = false;
          } else if (line.contains("name") && line.contains("Protein")) {
            line = reader.readLine();
            line = reader.readLine();
            line = reader.readLine();
            protein = getNutrientValue(line);
          } else if (line.contains("name") && line.contains("Total lipid (fat)")) {
            line = reader.readLine();
            line = reader.readLine();
            line = reader.readLine();
            fat = getNutrientValue(line);
          } else if (line.contains("name") && line.contains("Carbohydrate")) {
            line = reader.readLine();
            line = reader.readLine();
            line = reader.readLine();
            carbs = getNutrientValue(line);
            Food food = new Food(name, Double.valueOf(protein), Double.valueOf(carbs), Double.valueOf(fat));
            results.add(food);
          }
          builder.append(line+"\n");
        }
        reader.close();
      }
      return results;
    } catch (Exception e) {
      Log.e("ERROR", e.getMessage(), e);
      return new ArrayList<>();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  @NonNull
  private String getNutrientValue(String line) {
    return line.substring(line.indexOf("value")+9, line.length()-2);
  }

  private String buildFoodInfoUrlString(String id)  {
    StringBuilder builder = new StringBuilder();
    builder.append(FOOD_INFO_URL_BASE);
    builder.append("?ndbno="+id);
    builder.append("&api_key="+API_KEY);
    return builder.toString();
  }

  private String buildSearchUrlString(String name)  {
    StringBuilder builder = new StringBuilder();
    builder.append(SEARCH_URL_BASE);
    builder.append("&q="+name);
    builder.append("&max=10");
    builder.append("&api_key="+API_KEY);
    return builder.toString();
  }

  private List<Food> getTestData()  {
    Food f1 = new Food("STEAK", 35, 0, 14);
    Food f2 = new Food("CHICKEN BREAST", 16, 0, 3);
    Food f3 = new Food("WHOLE WHEAT BREAD", 2, 24, 0);
    Food f4 = new Food("PASTA", 0, 35, 0);
    Food f5 = new Food("HAMBURGER", 25, 0, 12);
    List<Food> foodList = new ArrayList<>();
    foodList.add(f1);
    foodList.add(f2);
    foodList.add(f3);
    foodList.add(f4);
    foodList.add(f5);
    return foodList;
  }
}