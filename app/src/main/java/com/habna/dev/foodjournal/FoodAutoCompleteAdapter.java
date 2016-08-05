package com.habna.dev.foodjournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FoodAutoCompleteAdapter extends BaseAdapter implements Filterable {

  private static final int MAX_RESULTS = 10;
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
          List<Food> foods = findFoods(mContext, constraint.toString());

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
  private List<Food> findFoods(Context context, String bookTitle) {
    // GoogleBooksProtocol is a wrapper for the Google Books API
//    GoogleBooksProtocol protocol = new GoogleBooksProtocol(context, MAX_RESULTS);
//    return protocol.findBooks(bookTitle);
    return getTestData();
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