package com.habna.dev.foodjournal;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhabs on 8/3/2016.
 */
public class FoodAdapter extends ArrayAdapter<Food> {

  List<Food> foods = new ArrayList<>();

  public FoodAdapter(Context context, ArrayList<Food> foods)  {
    super(context, 0, foods);
    this.foods = foods;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    Food food = getItem(position);
    if (convertView == null)  {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_food, parent, false);
    }
    TextView foodName = (TextView) convertView.findViewById(R.id.foodName);
    TextView foodCalories = (TextView) convertView.findViewById(R.id.foodCalories);
    foodName.setText(food.getName());
    foodCalories.setText(Double.toString(food.getCalories()) + " calories");

    ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.foodItemDeleteButton);
    deleteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        foods.remove(position);
        notifyDataSetChanged();
        final JournalActivity activity = (JournalActivity) getContext();
        activity.calculateTotalCalories();
        activity.finish();
        activity.startActivity(activity.getIntent());

      }
    });

    return convertView;
  }
}
