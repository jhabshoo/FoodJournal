package com.habna.dev.foodjournal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        fragment = PlaceholderFragment.newInstance(position+1);
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

  public static class JournalFragment extends Fragment  {

    private static Map<String, Food> allFoods;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);
      final View view = inflater.inflate(R.layout.fragment_journal, container, false);

      final TextView totalCaloriesTextView = (TextView) view.findViewById(R.id.totalCaloriesTextView);

      final ListView currentFoodsListView = (ListView) view.findViewById(R.id.currentFoodsListView);
      final ArrayAdapter<String> currentFoodsListAdapter = new ArrayAdapter<>(getActivity(),
        android.R.layout.simple_dropdown_item_1line, getCurrentFoodStrings());
      currentFoodsListView.setAdapter(currentFoodsListAdapter);
      currentFoodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          currentFoodsListAdapter.remove(currentFoodsListAdapter.getItem(i));
          recalculateTotalCalories(totalCaloriesTextView, currentFoodsListAdapter);
        }
      });

      final AutoCompleteTextView searchTextView = (AutoCompleteTextView) (view.findViewById(R.id.searchTextView));
      searchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
          if (i == KeyEvent.ACTION_DOWN)  {
            if (!textView.getText().toString().isEmpty()) {
              String key = textView.getText().toString().toUpperCase();
              Food f = getFoodFromAllFoods(key);
              if (f != null) {
                currentFoodsListAdapter.add(f.displayString());
                recalculateTotalCalories(totalCaloriesTextView, currentFoodsListAdapter);
              }
            }
            textView.setText("");
          }
          return false;
        }
      });
      final ArrayAdapter<String> searchAdapter = new ArrayAdapter(getActivity(),
        android.R.layout.simple_dropdown_item_1line, getAllFoodsKeys());
      searchTextView.setAdapter(searchAdapter);

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
                searchAdapter.add(nameText.toUpperCase());
                addToAllFoods(new Food(nameText, Double.valueOf(proteinText),
                  Double.valueOf(carbsText), Double.valueOf(fatText)));
                Toast.makeText(getActivity(), "Added " + nameText, Toast.LENGTH_LONG).show();
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

    private void recalculateTotalCalories(TextView caloriesText, ArrayAdapter<String> currentFoodsListAdapter) {
      caloriesText.setText("Total Calories: " + String.valueOf(getTotalCalories(currentFoodsListAdapter)));
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

    private List<String> getCurrentFoodStrings()  {
      List<String> strings = new ArrayList<>();
      return strings;
    }

    private double getTotalCalories(ArrayAdapter<String> adapter) {
      double total = 0;
      for (int i = 0; i < adapter.getCount(); i++)  {
        Food f = allFoods.get(adapter.getItem(i).toUpperCase());
        total += f.getCalories();
      }
      return total;
    }
  }

  /**
   * A placeholder fragment containing a simple view.
   */
  public static class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER= "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
      PlaceholderFragment fragment = new PlaceholderFragment();
      Bundle args = new Bundle();
      args.putInt(ARG_SECTION_NUMBER, sectionNumber);
      fragment.setArguments(args);
      return fragment;
    }

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_main_swipe, container, false);
      TextView textView = (TextView) rootView.findViewById(R.id.section_label);
      textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
      return rootView;
    }
  }
}
