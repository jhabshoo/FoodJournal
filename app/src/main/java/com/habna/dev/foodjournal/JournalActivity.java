package com.habna.dev.foodjournal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JournalActivity extends AppCompatActivity {

  private static final Type LIST_TYPE = new TypeToken<List<Food>>() {}.getType();
  private static ArrayList<Food> customFoods;
  private static ArrayList<Food> currentFoods;
  private static Map<String, Food> allFoods;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_journal);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    allFoods = getAllFoods();

    ArrayAdapter<String> testAdapter = new ArrayAdapter<>(this,
      android.R.layout.simple_dropdown_item_1line, new ArrayList(allFoods.keySet()));

    AutoCompleteTextView searchText = (AutoCompleteTextView) findViewById(R.id.searchText);
    searchText.setImeOptions(EditorInfo.IME_ACTION_DONE);
    searchText.setAdapter(testAdapter);
    searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (KeyEvent.ACTION_DOWN == event.getAction()) {
          if (textView.getText() != null) {
            String name = textView.getText().toString().toUpperCase();
            Food food = allFoods.get(name);
            addFoodToCurrent(food);
          }
          return true;
        }
        return false;
      }
    });

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
    saveCustomFoods(false);
    saveCurrentFoods(false);
  }

  private void validateAndCreateCustomFood(String name, String protein, String carbs, String fat)  {
    if (validateFood(name, protein, carbs, fat)) return;
    createNewFoodItem(name, protein, carbs, fat);
  }

  private boolean validateFood(String name, String protein, String carbs, String fat) {
    if (name == null || name == "")
      return true;
    try {
      if (Double.valueOf(protein) == null || Double.valueOf(carbs) == null || Double.valueOf(fat) == null)  {
        return true;
      }
    }catch (NumberFormatException nfe)  {
      return true;
    }
    return false;
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

  private void saveCustomFoods(boolean refresh)  {
    SharedPreferences.Editor editor = PreferenceManager.
      getDefaultSharedPreferences(JournalActivity.this).edit();
    editor.putString("currentFoodsList", new Gson().toJson(currentFoods));
    editor.commit();
    if (refresh)  {
      finish();
      startActivity(getIntent());
    }
  }

  private void saveCurrentFoods(boolean refresh) {
    SharedPreferences.Editor editor = PreferenceManager.
      getDefaultSharedPreferences(JournalActivity.this).edit();
    editor.putString("itemsList", new Gson().toJson(customFoods));
    editor.commit();
    if (refresh)  {
      calculateTotalCalories();
      finish();
      startActivity(getIntent());
    }
  }

  private void addFoodToCustom(Food f)  {
    if (customFoods == null)  {
      customFoods = new ArrayList<>();
    }
    customFoods.add(f);
    allFoods.put(f.getName().toUpperCase(), f);
    saveCustomFoods(true);
  }

  private void addFoodToCurrent(Food f) {
    if (currentFoods == null) {
      currentFoods = new ArrayList<>();
    }
    currentFoods.add(f);
    saveCurrentFoods(true);
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

  Map<String, Food> getAllFoods() {
    Map<String, Food> result = new HashMap<>();
    for (int i = 0; i < 10; i++)  {
      result.put(testFood[i].getName().toUpperCase(), testFood[i]);
    }
    for (Food f : customFoods)  {
      result.put(f.getName().toUpperCase(), f);
    }
    return result;
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    Intent intent = new Intent(JournalActivity.this, MainActivity.class);
    startActivity(intent);
  }

  private static Food test1 = new Food("Chicken Breast", 12, 0, 6);
  private static Food test2 = new Food("Steak", 2, 15, 8);
  private static Food test3 = new Food("Hamburger", 1, 0, 4);
  private static Food test4 = new Food("Chicken Drumstick", 21, 0, 3);
  private static Food test5 = new Food("Ice Cream", 6, 4, 7);
  private static Food test6 = new Food("Turkey Sandwich", 6, 16, 12);
  private static Food test7 = new Food("Bean Burrito", 3, 8, 9);
  private static Food test8 = new Food("Hot Dog", 17, 3, 3);
  private static Food test9 = new Food("Turkey Drumstick", 12, 5, 1);
  private static Food test0 = new Food("Burger City QP", 30, 0, 20);

  private static Food[] testFood = new Food[]{test1,test2,test3,test4,test5,test6,test7,test8,test9,test0};
}
