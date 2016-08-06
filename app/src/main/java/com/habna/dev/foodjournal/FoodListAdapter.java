package com.habna.dev.foodjournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import java.util.ArrayList;

public class FoodListAdapter extends BaseAdapter {

  private Context context;
  private ArrayList<Food> foods;

  public FoodListAdapter(Context context, ArrayList<Food> foods) {
    this.context = context;
    this.foods = foods;
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
    foods.add(food);
    notifyDataSetChanged();
  }


  public void remove(int position)  {
    foods.remove(position);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    TwoLineListItem twoLineListItem;

    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      twoLineListItem = (TwoLineListItem) inflater.inflate(
        android.R.layout.simple_list_item_2, null);
    } else {
      twoLineListItem = (TwoLineListItem) convertView;
    }

    TextView text1 = twoLineListItem.getText1();
    TextView text2 = twoLineListItem.getText2();

    text1.setText(foods.get(position).getName());
    text2.setText("" + foods.get(position).getNutrition());

    return twoLineListItem;
  }
}