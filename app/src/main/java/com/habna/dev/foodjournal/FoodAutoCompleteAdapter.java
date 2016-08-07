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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FoodAutoCompleteAdapter extends BaseAdapter implements Filterable {

  private Context mContext;
  private List<Food> resultList = new ArrayList<>();

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
    ((TextView) convertView.findViewById(R.id.searchText1)).setText(getItem(position).getNameAndMeasure());
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
    String[] terms = name.split(" ");
    List<Food> results = new ArrayList<>();
    for (Map.Entry<String, Food> entry : MainSwipeActivity.usdaFoodMap.entrySet()) {
      if (validResult(entry.getKey(), terms)) {
        results.add(entry.getValue());
      }
    }
    return results;
  }

  private boolean validResult(String key, String[] terms) {
    for (String str : terms)  {
      if (!key.contains(str.toUpperCase())) {
        return false;
      }
    }
    return true;
  }
}