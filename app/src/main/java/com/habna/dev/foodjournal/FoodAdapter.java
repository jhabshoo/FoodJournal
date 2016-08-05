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

/**
 * Created by jhabs on 8/5/2016.
 */
public class FoodAdapter extends BaseAdapter implements Filterable {

  private static final int MAX_RESULTS = 10;
  private Context context;
  private List<Food> resultList = new ArrayList<>();

  public FoodAdapter(Context context) {
    this.context = context;
  }

  @Override
  public int getCount() {
    return resultList.size();
  }

  @Override
  public Food getItem(int i) {
    return resultList.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    if (view == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, viewGroup, false);
    }
//    ((TextView) view.findViewById())
    return view;
  }

  @Override
  public Filter getFilter() {
    final Filter filter = new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults filterResults = new FilterResults();
        if (charSequence != null) {
          List<Food> foods = findFoods(context, charSequence.toString());
          filterResults.values = foods;
          filterResults.count = foods.size();
        }
        return filterResults;
      }

      @Override
      protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        if (filterResults != null && filterResults.count > 0) {
          resultList = (List<Food>) filterResults.values;
          notifyDataSetChanged();
        } else  {
          notifyDataSetInvalidated();
        }
      }};
    return filter;
  }

  private List<Food> findFoods(Context context, String foodName)  {
    return getTestFoods();
  }

  private List<Food> getTestFoods() {
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
