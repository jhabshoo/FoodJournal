package com.habna.dev.foodjournal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainSwipeActivity extends AppCompatActivity {
  public static Map<String, Food> usdaFoodMap = new HashMap<>();

  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide
   * fragments for each of the sections. We use a
   * {@link FragmentPagerAdapter} derivative, which will keep every
   * loaded fragment in memory. If this becomes too memory intensive, it
   * may be best to switch to a
   * {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  private SectionsPagerAdapter mSectionsPagerAdapter;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  private ViewPager mViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_swipe);
    AsyncTask loadUsdaFoodsTask = new NutritionInfoFetch().execute();

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = (ViewPager) findViewById(R.id.container);
    mViewPager.setAdapter(mSectionsPagerAdapter);
    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        int oldFrag = position == 0 ? 1 : 0;
        FragmentLifecycle oldFragment = (FragmentLifecycle) mSectionsPagerAdapter.instantiateItem(mViewPager, oldFrag);
        oldFragment.save();
        FragmentLifecycle fragment = (FragmentLifecycle) mSectionsPagerAdapter.instantiateItem(mViewPager, position);
        fragment.load();
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main_swipe, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
   * one of the sections/tabs/pages.
   */
  public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      Fragment fragment;
      if (position == 0) {
        fragment = new JournalFragment();
      }else {
        fragment = new CalculatorFragment();
      }
      return fragment;
    }

    @Override
    public int getCount() {
      return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0:
          return "Journal";
        case 1:
          return "Macro Calculator";
      }
      return null;
    }
  }

  public static class JournalFragment extends Fragment implements FragmentLifecycle {

    private static Map<String, Food> allFoods;
    private ListView currentFoodsListView;
    private FoodListAdapter currentFoodsListAdapter;
    private TextView goalCaloriesTextView;
    private TextView goalProteinTextView;
    private TextView goalCarbsTextView;
    private TextView goalFatTextView;
    private TextView totalCaloriesTextView;
    private TextView totalProteinTextView;
    private TextView totalCarbsTextView;
    private TextView totalFatTextView;
    private Double goalCalories;
    private Double goalProtein;
    private Double goalCarbs;
    private Double goalFat;
    private Food currentFood;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);
      final View view = inflater.inflate(R.layout.fragment_journal, container, false);
      currentFoodsListAdapter = new FoodListAdapter(getActivity(), new ArrayList<Food>());

      goalCaloriesTextView = (TextView) view.findViewById(R.id.goalCaloriesTextView);
      goalProteinTextView = (TextView) view.findViewById(R.id.goalProteinTextView);
      goalCarbsTextView = (TextView) view.findViewById(R.id.goalCarbsTextView);
      goalFatTextView = (TextView) view.findViewById(R.id.goalFatTextView);

      totalCaloriesTextView = (TextView) view.findViewById(R.id.totalCaloriesTextView);
      totalProteinTextView = (TextView) view.findViewById(R.id.totalProteinTextView);
      totalCarbsTextView = (TextView) view.findViewById(R.id.totalCarbsTextView);
      totalFatTextView = (TextView) view.findViewById(R.id.totalFatTextView);
      load();
      recalculateTotals();
      setGoals();

      currentFoodsListView = (ListView) view.findViewById(R.id.currentFoodsListView);
      currentFoodsListView.setAdapter(currentFoodsListAdapter);
      currentFoodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          final Context context = getActivity();
          final int position = i;
          String key = ((Food)currentFoodsListAdapter.getItem(i)).getName().toUpperCase();
          Food food = allFoods.get(key);
          if (food != null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = layoutInflater.inflate(R.layout.current_food_list_item_popup, null);

            TextView currentFoodName = (TextView) layout.findViewById(R.id.currentFoodName);
            TextView currentFoodMeasure = (TextView) layout.findViewById(R.id.currentFoodMeasure);
            TextView currentFoodProtein = (TextView) layout.findViewById(R.id.currentFoodProtein);
            TextView currentFoodCarbs = (TextView) layout.findViewById(R.id.currentFoodCarbs);
            TextView currentFoodFat = (TextView) layout.findViewById(R.id.currentFoodFat);
            currentFoodName.setText(food.getName());
            currentFoodMeasure.setText(food.getMeasure());
            currentFoodProtein.setText(food.getProteinDisplay());
            currentFoodCarbs.setText(food.getCarbsDisplay());
            currentFoodFat.setText(food.getFatDisplay());

            final EditText currentQuantityText = (EditText) layout.findViewById(R.id.currentFoodQuantity);
            final int currentQuantity = currentFoodsListAdapter.getQuantity(position);
            currentQuantityText.setText(String.valueOf(currentQuantity));

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(food.getName());
            builder.setView(layout);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                try {
                  int newQuantity = Integer.valueOf(currentQuantityText.getText().toString());
                  if (newQuantity > 0 && newQuantity != currentQuantity) {
                    currentFoodsListAdapter.setQuantity(position, newQuantity);
                    recalculateTotals();
                  } else if (newQuantity == 0)  {
                    currentFoodsListAdapter.removeFood(position, currentQuantity);
                    recalculateTotals();
                  }
                } catch (NumberFormatException nfe) {

                }
              }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
              }
            });
            builder.show();
          }
        }
      });

      final FoodAutoCompleteAdapter autoSearchTextViewAdapter = new FoodAutoCompleteAdapter(getActivity());
      final DelayAutoCompleteTextView autoSearchTextView = (DelayAutoCompleteTextView) view.findViewById(R.id.searchTextView);
      autoSearchTextView.setThreshold(1);
      autoSearchTextView.setAdapter(autoSearchTextViewAdapter);
      autoSearchTextView.setLoadingIndicator((android.widget.ProgressBar) view.findViewById(R.id.pb_loading_indicator));
      autoSearchTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          currentFood = (Food)adapterView.getItemAtPosition(i);
          autoSearchTextView.setText(currentFood.getName());
        }
      });
      autoSearchTextView.setOnEditorActionListener( new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
          if (i == EditorInfo.IME_ACTION_DONE)  {
            if (!textView.getText().toString().isEmpty()) {
              String key = textView.getText().toString().toUpperCase();
              Food f = getFoodFromAllFoods(key);
              if (f == null && currentFood != null)  {
                f = currentFood;
                addToAllFoods(f);
              }
              if (f != null) {
                currentFoodsListAdapter.addFood(f);
                recalculateTotals();
                currentFood = null;
              }
            }
            textView.setText("");
          }
          return false;
        }
      });

      Button goalButton = (Button) view.findViewById(R.id.goalButton);
      goalButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Context context = getActivity();
          final EditText goalCals = new EditText(context);
          goalCals.setHint("Daily calories goal (kcal)");
          goalCals.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

          final EditText goalPro = new EditText(context);
          goalPro.setHint("Daily protein goal (g)");
          goalPro.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

          final EditText goalCarb = new EditText(context);
          goalCarb.setHint("Daily carbs goal (g)");
          goalCarb.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

          final EditText goalFat = new EditText(context);
          goalFat.setHint("Daily fat goal (g)");
          goalFat.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

          LinearLayout linearLayout = new LinearLayout(context);
          linearLayout.setOrientation(LinearLayout.VERTICAL);
          linearLayout.addView(goalCals);
          linearLayout.addView(goalPro);
          linearLayout.addView(goalCarb);
          linearLayout.addView(goalFat);

          final AlertDialog.Builder builder = new AlertDialog.Builder(context);
          builder.setTitle("Edit Daily Goal");
          builder.setView(linearLayout);
          builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              try {
                goalCalories = Double.valueOf(goalCals.getText().toString());
                goalProtein = Double.valueOf(goalPro.getText().toString());
                goalCarbs = Double.valueOf(goalCarb.getText().toString());
                goalCalories = Double.valueOf(goalFat.getText().toString());
                setGoals();
              } catch (NumberFormatException nfe) {
              }
            }
          });
          builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
          });
          builder.show();
        }
      });

      Button addCustomFoodButton = (Button) view.findViewById(R.id.addCustomFoodButton);
      addCustomFoodButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Context context = getActivity();
          // input
          final EditText name = new EditText(context);
          name.setHint("Name");
          final EditText protein = new EditText(context);
          protein.setHint("Protein (g)");
          protein.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
          final EditText carbs = new EditText(context);
          carbs.setHint("Carbs (g)");
          carbs.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
          final EditText fat = new EditText(context);
          fat.setHint("Fat (g)");
          fat.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
          final EditText measure = new EditText(context);
          measure.setHint("Measurement (ie. 1 cup)");

          LinearLayout layout = new LinearLayout(context);
          layout.setOrientation(LinearLayout.VERTICAL);
          layout.addView(name);
          layout.addView(protein);
          layout.addView(carbs);
          layout.addView(fat);
          layout.addView(measure);

          final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
              final String measureText = measure.getText().toString();
              if (validateFoodForm(nameText, proteinText, carbsText, fatText))  {
                Food food = new Food(nameText, Double.valueOf(proteinText),
                  Double.valueOf(carbsText), Double.valueOf(fatText), measureText);
                addToAllFoods(food);
                saveAllFoods();
                currentFoodsListAdapter.addFood(food);
                recalculateTotals();
                Toast.makeText(getActivity(), "Added " + nameText, Toast.LENGTH_SHORT).show();
              }
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
      return view;
    }

    private void setGoals() {
      goalCaloriesTextView.setText("Goal Calories: " + String.valueOf(Math.round(goalCalories)) + " kcal");
      goalProteinTextView.setText("Goal Protein: " + String.valueOf(Math.round(goalProtein)) + " g");
      goalCarbsTextView.setText("Goal Carbs: " + String.valueOf(Math.round(goalCarbs)) + " g");
      goalFatTextView.setText("Goal Fat: " + String.valueOf(Math.round(goalFat)) + " g");
      goalCaloriesTextView.invalidate();
      goalProteinTextView.invalidate();
      goalCarbsTextView.invalidate();
      goalFatTextView.invalidate();
      checkGoalColors();
      saveGoals();
    }

    private void checkGoalColors() {
      NutrientTally nutrientTally = new NutrientTally();
      if (goalCalories > nutrientTally.getCals()) {
        goalCaloriesTextView.setTextColor(Color.RED);
      }else {
        goalCaloriesTextView.setTextColor(Color.GREEN);
      }
      if (goalProtein > nutrientTally.getProtein()) {
        goalProteinTextView.setTextColor(Color.RED);
      }else {
        goalProteinTextView.setTextColor(Color.GREEN);
      }
      if (goalCarbs > nutrientTally.getCarbs()) {
        goalCarbsTextView.setTextColor(Color.RED);
      }else {
        goalCarbsTextView.setTextColor(Color.GREEN);
      }
      if (goalFat > nutrientTally.getFat()) {
        goalFatTextView.setTextColor(Color.RED);
      }else {
        goalFatTextView.setTextColor(Color.GREEN);
      }
      goalCaloriesTextView.invalidate();
      goalProteinTextView.invalidate();
      goalCarbsTextView.invalidate();
      goalFatTextView.invalidate();
    }

    @Override
    public void onStart() {
      super.onStart();
      load();
    }

    @Override
    public void onStop() {
      super.onStop();
      save();
    }

    public void load()  {
      loadAllFoods();
      loadCurrentFoods(currentFoodsListAdapter);
      loadGoals();
    }

    public void save()  {
      saveAllFoods();
      saveCurrentFoods(currentFoodsListAdapter);
      saveGoals();
    }

    private void loadAllFoods() {
      SharedPreferences preferences = getActivity().getSharedPreferences("all_foods", 0);
      String allFoodsString = preferences.getString("all_foods", null);
      if (allFoodsString != null) {
        Type type = new TypeToken<List<Food>>() {
        }.getType();
        List<Food> allFoodsList = new Gson().fromJson(allFoodsString, type);
        for (Food food : allFoodsList)  {
          // this is called on every switch only needed on first time or when new allFood
          if (allFoods == null) {
            allFoods = new HashMap<>();
          }
          allFoods.put(food.getName().toUpperCase(), food);
          MainSwipeActivity.usdaFoodMap.put(food.getName().toUpperCase(), food);
        }
      }
    }

    private void saveGoals()  {
      SharedPreferences preferences = getActivity().getSharedPreferences("goals", 0);
      SharedPreferences.Editor editor = preferences.edit();
      String cal, pro, carbs, fat;
      if (goalCalories == null) {
        cal = String.valueOf(0.0);
      }else {
        cal = String.valueOf(goalCalories);
      }
      if (goalProtein == null) {
        pro = String.valueOf(0.0);
      }else {
        pro = String.valueOf(goalProtein);
      }
      if (goalCarbs == null) {
        carbs = String.valueOf(0.0);
      }else {
        carbs = String.valueOf(goalCarbs);
      }
      if (goalFat == null) {
        fat = String.valueOf(0.0);
      }else {
        fat = String.valueOf(goalFat);
      }
      editor.putString("calories", cal);
      editor.putString("protein", pro);
      editor.putString("carbs", carbs);
      editor.putString("fat", fat);
      editor.apply();
    }

    public void loadGoals()  {
      SharedPreferences preferences = getActivity().getSharedPreferences("goals", 0);
      try {
        goalCalories = Double.valueOf(preferences.getString("calories", "0.0"));
        goalProtein = Double.valueOf(preferences.getString("protein", "0.0"));
        goalCarbs = Double.valueOf(preferences.getString("carbs", "0.0"));
        goalFat = Double.valueOf(preferences.getString("fat", "0.0"));
      } catch (NumberFormatException nfe) {
        goalCalories = 0.0;
        goalProtein = 0.0;
        goalCarbs = 0.0;
        goalFat = 0.0;
      }
      setGoals();
    }

    private void loadCurrentFoods(FoodListAdapter adapter) {
      SharedPreferences preferences = getActivity().getSharedPreferences("current_foods", 0);
      Set<String> strings = preferences.getStringSet("current_food_strings", null);
      if (strings != null)  {
        adapter.clear();
        for (String str : strings)  {
          Food food = allFoods.get(str.toUpperCase());
          adapter.addFood(food);
        }
      }
    }

    private void saveAllFoods() {
      SharedPreferences preferences = getActivity().getSharedPreferences("all_foods", 0);
      SharedPreferences.Editor editor = preferences.edit();
      List<Food> allFoodsList = new ArrayList<>();
      if (allFoods != null) {
        for (Map.Entry entry : allFoods.entrySet()) {
          allFoodsList.add((Food) entry.getValue());
        }
        String allFoodsListString = new Gson().toJson(allFoodsList);
        editor.putString("all_foods", allFoodsListString);
        editor.commit();
      }
    }

    private void saveCurrentFoods(ListAdapter adapter) {
      SharedPreferences preferences = getActivity().getSharedPreferences("current_foods", 0);
      SharedPreferences.Editor editor = preferences.edit();
      Set<String> set = new HashSet<>();
      for (int i = 0; i < adapter.getCount(); i++)  {
        set.add(((Food)adapter.getItem(i)).getName().toUpperCase());
      }
      editor.putStringSet("current_food_strings", set);
      editor.apply();
      currentFoodsListAdapter.clear();
    }

    private void recalculateTotals() {
      NutrientTally nutrientTally = new NutrientTally();
      totalCaloriesTextView.setText("Total Calories: " + String.valueOf(Math.round(nutrientTally.getCals())) + " kcal");
      totalProteinTextView.setText("Total Protein: " + String.valueOf(Math.round(nutrientTally.getProtein())) + " g");
      totalCarbsTextView.setText("Total Carbs: " + String.valueOf(Math.round(nutrientTally.getCarbs())) + " g");
      totalFatTextView.setText("Total Fat: " + String.valueOf(Math.round(nutrientTally.getFat())) + " g");
      checkGoalColors();
    }

    private boolean validateFoodForm(String name, String protein, String carbs, String fat)  {
      if (name.isEmpty() || inAllFoods(name)) {
        return false;
      }
      if (!isNumber(protein) || !isNumber(carbs) || !isNumber(fat)) {
        return false;
      }
      return true;
    }

    private boolean isNumber(String str)  {
      try {
        double d = Double.valueOf(str);
      } catch (NumberFormatException nfe) {
        return false;
      }
      return true;
    }

    // ----- allFoods methods

    private Food getFoodFromAllFoods(String key)  {
      initAllFoodsIfNeeded();
      return allFoods.get(key);
    }

    private List<String> getAllFoodsKeys()  {
      initAllFoodsIfNeeded();
      return new ArrayList<>(allFoods.keySet());
    }

    private void addToAllFoods(Food food) {
      initAllFoodsIfNeeded();
      allFoods.put(food.getName().toUpperCase(), food);
      if (MainSwipeActivity.usdaFoodMap == null)  {
        MainSwipeActivity.usdaFoodMap = new HashMap<>();
      }
      MainSwipeActivity.usdaFoodMap.put(food.getName().toUpperCase(), food);
    }

    private void initAllFoodsIfNeeded() {
      if (allFoods == null) {
        allFoods = new HashMap<>();
      }
    }

    private boolean inAllFoods(String key)  {
      if (allFoods == null) {
        allFoods = new HashMap<>();
        return false;
      }
      return allFoods.containsKey(key.toUpperCase());
    }

    private class NutrientTally {
      private double cals;
      private double protein;
      private double carbs;
      private double fat;

      public NutrientTally()  {
        tally();
      }


      private void tally() {
        double totalCals = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        for (int i = 0; i < currentFoodsListAdapter.getCount(); i++)  {
          Food f = allFoods.get(((Food)currentFoodsListAdapter.getItem(i)).getName().toUpperCase());
          int quantity = currentFoodsListAdapter.getQuantity(i);
          totalCals += f.getCalories() * quantity;
          totalProtein += f.getProtein() * quantity;
          totalCarbs += f.getCarbs() * quantity;
          totalFat += f.getFat() * quantity;
        }
        cals = totalCals;
        protein = totalProtein;
        carbs = totalCarbs;
        fat = totalFat;
      }

      public double getCals() {
        return cals;
      }

      public double getProtein() {
        return protein;
      }

      public double getCarbs() {
        return carbs;
      }

      public double getFat() {
        return fat;
      }
    }
  }

  private static String getName(ArrayAdapter<String> adapter, int pos) {
    String item = adapter.getItem(pos);
    return item.indexOf("\n") == -1 ? item : item.substring(0, item.indexOf("\n"));
  }

  public static class CalculatorFragment extends Fragment implements FragmentLifecycle  {

    public static final String DAILY_CALORIES_TO_MAINTAIN = "Daily Calories To Maintain: ";
    public static final String GOAL = "Goal: ";
    public static final String CALORIES_PER_DAY = " kcal";
    private TextView goalText;
    private Double goalCalories;
    private Double goalProtein;
    private Double goalCarbs;
    private Double goalFat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);
      Context context = getActivity();
      View view = inflater.inflate(R.layout.fragment_calculator, container, false);

      goalText = (TextView) view.findViewById(R.id.goalTextView);

      final EditText heightText = (EditText) view.findViewById(R.id.heightText);
      final EditText weightText = (EditText) view.findViewById(R.id.weightText);
      final EditText bodyFatText = (EditText) view.findViewById(R.id.bodyFatText);
      final EditText ageText = (EditText) view.findViewById(R.id.ageTextView);
      final EditText weekLossText = (EditText) view.findViewById(R.id.weekLossText);
      final Spinner sexSpinner = (Spinner) view.findViewById(R.id.sexSpinner);
      final Spinner activeSpinner = (Spinner) view.findViewById(R.id.activitySpinner);
      final Spinner workTypeSpinner = (Spinner) view.findViewById(R.id.workTypeSpinner);
      final Spinner goalSpinner = (Spinner) view.findViewById(R.id.goalSpinner);

      final ArrayAdapter<CharSequence> sexAdapter = ArrayAdapter.createFromResource(context, R.array.sexes,
        android.R.layout.simple_spinner_dropdown_item);
      final ArrayAdapter<CharSequence> activeAdapter = ArrayAdapter.createFromResource(context, R.array.activities,
        android.R.layout.simple_spinner_item);
      final ArrayAdapter<CharSequence> workTypeAdapter = ArrayAdapter.createFromResource(context, R.array.activities,
        android.R.layout.simple_spinner_item);
      final ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(context, R.array.goals,
        android.R.layout.simple_spinner_item);

      sexSpinner.setAdapter(sexAdapter);
      activeSpinner.setAdapter(activeAdapter);
      workTypeSpinner.setAdapter(workTypeAdapter);
      goalSpinner.setAdapter(goalAdapter);
      goalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
          String key = goalAdapter.getItem(goalSpinner.getSelectedItemPosition()).toString().toUpperCase();
          if (key.equals("MAINTAIN")) {
            weekLossText.setVisibility(View.INVISIBLE);
          }else {
            weekLossText.setVisibility(View.VISIBLE);
          }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
      });

      Button submitButton = (Button) view.findViewById(R.id.submitButton);
      submitButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          String height = heightText.getText().toString();
          String weight = weightText.getText().toString();
          String bodyFat = bodyFatText.getText().toString();
          String sex = sexAdapter.getItem(sexSpinner.getSelectedItemPosition()).toString();
          String active = activeAdapter.getItem(activeSpinner.getSelectedItemPosition()).toString();
          String workType = activeAdapter.getItem(workTypeSpinner.getSelectedItemPosition()).toString();
          String goal = goalAdapter.getItem(goalSpinner.getSelectedItemPosition()).toString();
          String age = ageText.getText().toString();
          String weekLoss = weekLossText.getText().toString();
          try {
            double h = Double.valueOf(height);
            double w = Double.valueOf(weight);
            double bf = bodyFat.isEmpty() ? -1 : Double.valueOf(bodyFat);
            boolean male = sex.toUpperCase().equals("DUDE") ? true : false;
            Calculator.ACTIVE_TYPE act = Calculator.getActiveByString(active);
            Calculator.ACTIVE_TYPE work = Calculator.getActiveByString(workType);
            Calculator.GOAL_TYPE g = Calculator.getGoalByString(goal);
            int a = Integer.valueOf(age);
            double wl = weekLoss.isEmpty() ? Double.valueOf(0.0) : Double.valueOf(weekLoss);
            Calculator calculator = new Calculator(h, w, a, bf, male, g, act, work, wl);
            Calculator.Macros macros = calculator.getMacros();
            goalCalories = macros.getTdee();
            goalProtein = macros.getProtein();
            goalCarbs = macros.getCarbs();
            goalFat = macros.getFat();
            double limit = male ? Calculator.MALE_FLOOR : Calculator.FEMALE_FLOOR;
            if (goalCalories < limit)  {
              goalText.setText("Goal exceeds your minimal caloric intake.");
              goalText.setTextColor(Color.RED);
            } else {
              goalText.setText(buildDisplayText());
              goalText.setTextColor(Color.BLACK);
              saveGoals();
            }
          }catch (NumberFormatException nfe)  {

          }
        }
      });
      Button imperialButton = (Button) view.findViewById(R.id.imperialButton);
      imperialButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          String height = heightText.getText().toString();
          String weight = weightText.getText().toString();
          String bodyFat = bodyFatText.getText().toString();
          String sex = sexAdapter.getItem(sexSpinner.getSelectedItemPosition()).toString();
          String active = activeAdapter.getItem(activeSpinner.getSelectedItemPosition()).toString();
          String workType = activeAdapter.getItem(workTypeSpinner.getSelectedItemPosition()).toString();
          String goal = goalAdapter.getItem(goalSpinner.getSelectedItemPosition()).toString();
          String age = ageText.getText().toString();
          String weekLoss = weekLossText.getText().toString();
          try {
            double h = Double.valueOf(height);
            double w = Double.valueOf(weight);
            double bf = bodyFat.isEmpty() ? -1 : Double.valueOf(bodyFat);
            boolean male = sex.toUpperCase().equals("DUDE") ? true : false;
            Calculator.ACTIVE_TYPE act = Calculator.getActiveByString(active);
            Calculator.ACTIVE_TYPE work = Calculator.getActiveByString(workType);
            Calculator.GOAL_TYPE g = Calculator.getGoalByString(goal);
            int a = Integer.valueOf(age);
            double wl = weekLoss.isEmpty() ? Double.valueOf(0.0) : Double.valueOf(weekLoss);
            Calculator calculator = new Calculator(imperialToMetricHeight(h),
              imperialToMetricWeight(w), a, bf, male, g, act, work, wl);
            Calculator.Macros macros = calculator.getMacros();
            goalCalories = macros.getTdee();
            goalProtein = macros.getProtein();
            goalCarbs = macros.getCarbs();
            goalFat = macros.getFat();
            double limit = male ? Calculator.MALE_FLOOR : Calculator.FEMALE_FLOOR;
            if (goalCalories < limit)  {
              goalText.setText("Goal exceeds your minimal caloric intake.");
              goalText.setTextColor(Color.RED);
            } else {
              goalText.setText(buildDisplayText());
              goalText.setTextColor(Color.BLACK);
              saveGoals();
            }
          }catch (NumberFormatException nfe)  {

          }
        }
      });
      return view;
    }

    private String buildDisplayText() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(GOAL);
      stringBuilder.append(Math.round(goalCalories));
      stringBuilder.append(CALORIES_PER_DAY+"\n");
      stringBuilder.append(Math.round(goalProtein));
      stringBuilder.append(" g protein\n");
      stringBuilder.append(Math.round(goalCarbs));
      stringBuilder.append(" g carbs\n");
      stringBuilder.append(Math.round(goalFat));
      stringBuilder.append(" g fat");
      return stringBuilder.toString();
    }

    private double imperialToMetricHeight(double inches) {
      return inches * 2.54;
    }

    private double imperialToMetricWeight(double lbs) {
      return lbs * 0.453592;
    }

    private void loadGoalsText() {
      goalText.setText(buildDisplayText());
    }

    @Override
    public void onStop() {
      super.onStop();
      saveGoals();
    }

    @Override
    public void onStart() {
      super.onStart();
      loadGoals();
    }

    private void saveGoals() {
      SharedPreferences preferences = getActivity().getSharedPreferences("goals", 0);
      SharedPreferences.Editor editor = preferences.edit();
      String cal, pro, carbs, fat;
      if (goalCalories == null) {
        cal = String.valueOf(0.0);
      }else {
        cal = String.valueOf(goalCalories);
      }
      if (goalProtein == null) {
        pro = String.valueOf(0.0);
      }else {
        pro = String.valueOf(goalProtein);
      }
      if (goalCarbs == null) {
        carbs = String.valueOf(0.0);
      }else {
        carbs = String.valueOf(goalCarbs);
      }
      if (goalFat == null) {
        fat = String.valueOf(0.0);
      }else {
        fat = String.valueOf(goalCalories);
      }
      editor.putString("calories", cal);
      editor.putString("protein", pro);
      editor.putString("carbs", carbs);
      editor.putString("fat", fat);
      editor.apply();
    }

    public void save()  {
      saveGoals();
    }

    public void load()  {
      loadGoals();
    }

    private void loadGoals()  {
      SharedPreferences preferences = getActivity().getSharedPreferences("goals", 0);
      String cal = preferences.getString("calories", "n/a");
      String pro = preferences.getString("protein", "n/a");
      String carbs = preferences.getString("carbs", "n/a");
      String fat = preferences.getString("fat", "n/a");
      try {
        goalCalories = Double.valueOf(cal);
        goalProtein = Double.valueOf(pro);
        goalCarbs = Double.valueOf(carbs);
        goalFat = Double.valueOf(fat);
      } catch (NumberFormatException nfe) {
        goalCalories = 0.0;
        goalProtein = 0.0;
        goalCarbs = 0.0;
        goalFat = 0.0;
      }
      loadGoalsText();
    }
  }
}
