package com.habna.dev.foodjournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FoodListAdapter extends BaseAdapter {

  private Context context;
  private ArrayList<Food> foods;
  private Map<String, Integer> quantityMap;


  public FoodListAdapter(Context context, ArrayList<Food> foods) {
    this.context = context;
    this.foods = foods;
    quantityMap = new HashMap<>();
  }

  @Override
  public int getCount() {
    return foods.size();
  }

  @Override
  public Object getItem(int position) {
    return foods.get(position);
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  public void clear() {
    foods.clear();
  }

  public void addFood(Food food) {
    for (Food f : foods)  {
      String key = f.getName().toUpperCase();
      if (key.equals(food.getName().toUpperCase())) {
        int newQuantity = quantityMap.get(key) + 1;
        quantityMap.put(key, newQuantity);
        notifyDataSetChanged();
        return;
      }
    }
    foods.add(food);
    quantityMap.put(food.getName().toUpperCase(), 1);
    notifyDataSetChanged();
  }

  public Integer getQuantity(int position)  {
    return quantityMap.get(foods.get(position).getName().toUpperCase());
  }

  public void setQuantity(int position, int newValue) {
    quantityMap.put(foods.get(position).getName().toUpperCase(), newValue);
    notifyDataSetChanged();
  }

  public void removeFood(int position, int quantity)  {
    int baseQuantity = quantityMap.get(foods.get(position).getName().toUpperCase());
    if (baseQuantity == quantity) {
      remove(position);
    } else  {
      quantityMap.put(foods.get(position).getName().toUpperCase(), baseQuantity - quantity);
    }
    notifyDataSetChanged();
  }

  public void remove(int position)  {
    foods.remove(position);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    View view = convertView;

    if (view == null) {
      LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.simple_dropdown_item_3line, null);
    }

    TextView text1 = (TextView) view.findViewById(R.id.searchText1);
    TextView text2 = (TextView) view.findViewById(R.id.searchText2);
    TextView text3 = (TextView) view.findViewById(R.id.searchText3);

    Food food = foods.get(position);

    text1.setText(food.getName());
    text2.setText("" + food.getNutrition());
    text3.setText("Quantity: " + quantityMap.get(food.getName().toUpperCase()));

    return view;
  }
}