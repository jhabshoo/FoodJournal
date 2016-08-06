package com.habna.dev.foodjournal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
    private Double goalCalories;
    private Food currentFood;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);
      final View view = inflater.inflate(R.layout.fragment_journal, container, false);
      currentFoodsListAdapter = new FoodListAdapter(getActivity(), new ArrayList<Food>());

      goalCaloriesTextView = (TextView) view.findViewById(R.id.goalCaloriesTextView);

      final TextView totalCaloriesTextView = (TextView) view.findViewById(R.id.totalCaloriesTextView);
      load();
      recalculateTotalCalories(totalCaloriesTextView, currentFoodsListAdapter);
      setGoal(goalCaloriesTextView);

      currentFoodsListView = (ListView) view.findViewById(R.id.currentFoodsListView);
      currentFoodsListView.setAdapter(currentFoodsListAdapter);
      currentFoodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          final int position = i;
          String key = ((Food)currentFoodsListAdapter.getItem(i)).getName().toUpperCase();
          Food food = allFoods.get(key);
          if (food != null) {
            Context context = getActivity();
            TextView protein = new TextView(context);
            protein.setText(food.getProteinDisplay());
            TextView carbs = new TextView(context);
            carbs.setText(food.getCarbsDisplay());
            TextView fat = new TextView(context);
            fat.setText(food.getFatDisplay());
            TextView calories = new TextView(context);
            calories.setText(food.getCalsDisplay());

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(protein);
            layout.addView(carbs);
            layout.addView(fat);
            layout.addView(calories);

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(food.getName());
            builder.setView(layout);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {}
            });
            builder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                currentFoodsListAdapter.remove(position);
                recalculateTotalCalories(totalCaloriesTextView, currentFoodsListAdapter);
              }
            });
            builder.show();
          }
        }
      });

      final FoodAutoCompleteAdapter autoSearchTextViewAdapter = new FoodAutoCompleteAdapter(getActivity());
      final DelayAutoCompleteTextView autoSearchTextView = (DelayAutoCompleteTextView) view.findViewById(R.id.searchTextView);
      autoSearchTextView.setThreshold(3);
      autoSearchTextView.setAdapter(autoSearchTextViewAdapter);
      autoSearchTextView.setLoadingIndicator((android.widget.ProgressBar) view.findViewById(R.id.pb_loading_indicator));
      autoSearchTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          currentFood = (Food)adapterView.getItemAtPosition(i);
          autoSearchTextView.setText(currentFood.getName());
        }
      });
      autoSearchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
          if (i == KeyEvent.ACTION_DOWN)  {
            if (!textView.getText().toString().isEmpty()) {
              String key = textView.getText().toString().toUpperCase();
              Food f = getFoodFromAllFoods(key);
              if (f == null && currentFood != null)  {
                f = currentFood;
                addToAllFoods(f);
              }
              if (f != null) {
                currentFoodsListAdapter.addFood(f);
                recalculateTotalCalories(totalCaloriesTextView, currentFoodsListAdapter);
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
          final EditText goal = new EditText(context);
          goal.setHint("Daily goal calories");
          goal.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

          final AlertDialog.Builder builder = new AlertDialog.Builder(context);
          builder.setTitle("Edit Daily Goal");
          builder.setView(goal);
          builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              try {
                goalCalories = Double.valueOf(goal.getText().toString());
                setGoal(goalCaloriesTextView);
              } catch (NumberFormatException nfe) {
              }
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

          LinearLayout layout = new LinearLayout(context);
          layout.setOrientation(LinearLayout.VERTICAL);
          layout.addView(name);
          layout.addView(protein);
          layout.addView(carbs);
          layout.addView(fat);

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
              if (validateFoodForm(nameText, proteinText, carbsText, fatText))  {
                Food food = new Food(nameText, Double.valueOf(proteinText),
                  Double.valueOf(carbsText), Double.valueOf(fatText));
                addToAllFoods(food);
                currentFoodsListAdapter.addFood(food);
                recalculateTotalCalories(totalCaloriesTextView, currentFoodsListAdapter);
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

    private void setGoal(TextView goalCaloriesTextView) {
      goalCaloriesTextView.setText("Goal Calories: " + String.valueOf(goalCalories));
      checkGoalColor(goalCaloriesTextView);
      goalCaloriesTextView.invalidate();
      saveGoals();
    }

    private void checkGoalColor(TextView goalCaloriesTextView) {
      if (goalCalories > getTotalCalories(currentFoodsListAdapter)) {
        goalCaloriesTextView.setTextColor(Color.RED);
      }else {
        goalCaloriesTextView.setTextColor(Color.GREEN);
      }
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
          if (allFoods == null) {
            allFoods = new HashMap<>();
          }
          allFoods.put(food.getName().toUpperCase(), food);
        }
      }
    }

    private void saveGoals()  {
      SharedPreferences preferences = getActivity().getSharedPreferences("goals", 0);
      SharedPreferences.Editor editor = preferences.edit();
      String val;
      if (goalCalories == null) {
        val = String.valueOf(0.0);
      }else {
        val = String.valueOf(goalCalories);
      }
      editor.putString("calories", val);
      editor.apply();
    }

    public void loadGoals()  {
      SharedPreferences preferences = getActivity().getSharedPreferences("goals", 0);
      try {
        goalCalories = Double.valueOf(preferences.getString("calories", "0.0"));
      } catch (NumberFormatException nfe) {
        goalCalories = 0.0;
      }
      setGoal(goalCaloriesTextView);
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
      for (Map.Entry entry : allFoods.entrySet()) {
        allFoodsList.add((Food)entry.getValue());
      }
      String allFoodsListString = new Gson().toJson(allFoodsList);
      editor.putString("all_foods", allFoodsListString);
      editor.commit();
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

    private void recalculateTotalCalories(TextView caloriesText, FoodListAdapter currentFoodsListAdapter) {
      caloriesText.setText("Total Calories: " + String.valueOf(getTotalCalories(currentFoodsListAdapter)));
      checkGoalColor(goalCaloriesTextView);
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

    private double getTotalCalories(FoodListAdapter adapter) {
      double total = 0;
      for (int i = 0; i < adapter.getCount(); i++)  {
        Food f = allFoods.get(((Food)adapter.getItem(i)).getName().toUpperCase());
        total += f.getCalories();
      }
      return total;
    }
  }

  private static String getName(ArrayAdapter<String> adapter, int pos) {
    String item = adapter.getItem(pos);
    return item.indexOf("\n") == -1 ? item : item.substring(0, item.indexOf("\n"));
  }

  public static class CalculatorFragment extends Fragment implements FragmentLifecycle  {

    public static final String DAILY_CALORIES_TO_MAINTAIN = "Daily Calories To Maintain: ";
    public static final String GOAL = "Goal: ";
    public static final String CALORIES_PER_DAY = " calories per day";
    private TextView goalText;
    private Double goalCalories;

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
            goalCalories = calculator.getGoalCals();
            double limit = male ? Calculator.MALE_FLOOR : Calculator.FEMALE_FLOOR;
            if (goalCalories < limit)  {
              goalText.setText("Goal exceeds your minimal caloric intake.");
              goalText.setTextColor(Color.RED);
            } else {
              goalText.setText(GOAL + goalCalories + CALORIES_PER_DAY);
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
            goalCalories = calculator.getGoalCals();
            double limit = male ? Calculator.MALE_FLOOR : Calculator.FEMALE_FLOOR;
            if (goalCalories < limit)  {
              goalText.setText("Goal exceeds your minimal caloric intake.");
              goalText.setTextColor(Color.RED);
            } else {
              goalText.setText(GOAL + goalCalories + CALORIES_PER_DAY);
              goalText.setTextColor(Color.BLACK);
              saveGoals();
            }
          }catch (NumberFormatException nfe)  {

          }
        }
      });
      return view;
    }

    private double imperialToMetricHeight(double inches) {
      return inches * 2.54;
    }

    private double imperialToMetricWeight(double lbs) {
      return lbs * 0.453592;
    }

    private void loadGoalsText() {
      goalText.setText(DAILY_CALORIES_TO_MAINTAIN + goalCalories);
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
      String val;
      if (goalCalories == null) {
        val = String.valueOf(0.0);
      }else {
        val = String.valueOf(goalCalories);
      }
      editor.putString("calories", val);
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
      String str = preferences.getString("calories", "n/a");
      try {
        goalCalories = Double.valueOf(str);
      } catch (NumberFormatException nfe) {
        goalCalories = 0.0;
      }
      loadGoalsText();
    }


  }
}
