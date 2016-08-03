package com.habna.dev.foodjournal;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JournalActivity extends AppCompatActivity {

  private static final Type LIST_TYPE = new TypeToken<List<Food>>() {}.getType();
  private static ArrayList<Food> customFoods;
  private static ArrayList<Food> currentFoods;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_journal);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    loadCustomFoods();
    loadCurrentFoods();
    calculateTotalCalories();
    ListView currentFoodsList = (ListView) findViewById(R.id.currentFoodsList);
    FoodAdapter foodAdapter = new FoodAdapter(this, currentFoods);
    currentFoodsList.setAdapter(foodAdapter);

    TextView caloriesText = (TextView) findViewById(R.id.caloriesText);
    caloriesText.setText("Total Calories: " + calculateTotalCalories());

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // input
        final EditText name = new EditText(JournalActivity.this);
        name.setHint("Name");
        final EditText protein = new EditText(JournalActivity.this);
        protein.setHint("Protein (g)");
        final EditText carbs = new EditText(JournalActivity.this);
        carbs.setHint("Carbs (g)");
        final EditText fat = new EditText(JournalActivity.this);
        fat.setHint("Fat (g)");

        LinearLayout layout = new LinearLayout(JournalActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(name);
        layout.addView(protein);
        layout.addView(carbs);
        layout.addView(fat);

        final AlertDialog.Builder builder = new AlertDialog.Builder(JournalActivity.this);
        builder.setTitle("Add custom food item");
        builder.setView(layout);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            // save item
            final String nameText = name.getText().toString();
            final String proteinText = protein.getText().toString();
            final String carbsText = carbs.getText().toString();
            final String fatText = fat.getText().toString();
            validateAndCreateCustomFood(nameText, proteinText, carbsText, fatText);
          }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            // close
          }
        });
        builder.show();
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    saveCustomFoods();
    saveCurrentFoods();
  }

  private void validateAndCreateCustomFood(String name, String protein, String carbs, String fat)  {
    if (name == null || name == "")
      return;
    try {
      if (Double.valueOf(protein) == null || Double.valueOf(carbs) == null || Double.valueOf(fat) == null)  {
        return;
      }
    }catch (NumberFormatException nfe)  {
      return;
    }
    createNewFoodItem(name, protein, carbs, fat);
  }

  private void createNewFoodItem(String name, String protein, String carbs, String fat) {
    Food food = new Food(name, Double.valueOf(protein), Double.valueOf(carbs), Double.valueOf(fat));
    addFoodToCustom(food);
    addFoodToCurrent(food);
  }

  private void loadCustomFoods()  {
    if (customFoods == null) {
      customFoods = new Gson().fromJson(PreferenceManager.
        getDefaultSharedPreferences(JournalActivity.this).getString("customFoodsList", null), LIST_TYPE);
      if (customFoods == null) {
        customFoods = new ArrayList<>();
      }
    }
  }

  private void loadCurrentFoods() {
    if (currentFoods == null)  {
      currentFoods = new Gson().fromJson(PreferenceManager.
        getDefaultSharedPreferences(JournalActivity.this).getString("currentFoodsList", null), LIST_TYPE);
      if (currentFoods == null)  {
        currentFoods = new ArrayList<>();
      }
    }
  }

  private void saveCustomFoods()  {
    SharedPreferences.Editor editor = PreferenceManager.
      getDefaultSharedPreferences(JournalActivity.this).edit();
    editor.putString("currentFoodsList", new Gson().toJson(currentFoods));
    editor.commit();
    finish();
    startActivity(getIntent());
  }

  private void saveCurrentFoods() {
    SharedPreferences.Editor editor = PreferenceManager.
      getDefaultSharedPreferences(JournalActivity.this).edit();
    editor.putString("itemsList", new Gson().toJson(customFoods));
    editor.commit();
  }

  private void addFoodToCustom(Food f)  {
    if (customFoods == null)  {
      customFoods = new ArrayList<>();
    }
    customFoods.add(f);
    saveCustomFoods();
  }

  private void addFoodToCurrent(Food f) {
    if (currentFoods == null) {
      currentFoods = new ArrayList<>();
    }
    currentFoods.add(f);
    saveCurrentFoods();
  }

  public double calculateTotalCalories() {
    double totalCalories = 0;
    if (currentFoods != null) {
      for (Food food : currentFoods) {
        totalCalories += food.getCalories();
      }
    }
    return totalCalories;
  }

}
