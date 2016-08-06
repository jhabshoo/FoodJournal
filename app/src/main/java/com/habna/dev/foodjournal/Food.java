package com.habna.dev.foodjournal;

/**
 * Created by jhabs on 8/3/2016.
 */
public class Food {

  private String name;
  private double protein;
  private double carbs;
  private double fat;
  private double calories;
  private String measure;

  public Food() {

  }

  public Food(String name, double protein, double carbs, double fat, String measure) {
    super();
    this.name = name;
    this.protein = protein;
    this.carbs = carbs;
    this.fat = fat;
    this.measure = measure;
    calculateCalories();
  }

  public Food(String name, double protein, double carbs, double fat, String measure, double calories) {
    super();
    this.name = name;
    this.protein = protein;
    this.carbs = carbs;
    this.fat = fat;
    this.measure = measure;
    this.calories = calories;
  }

  private void calculateCalories()  {
    calories = (4 * protein) + (4 * carbs) + (9 * fat);
  }

  public String getName() {
    return name;
  }

  public String getNameAndMeasure() {
    return name + ", " + measure;
  }
  public double getCalories() {
    return calories;
  }

  @Override
  public String toString()  {
//    return "Food [name=" + name + ",protein=" + protein + ",carbs=" + carbs + ",fat=" + fat + "]";
    return name + "\n" + getNutrition();
  }

  public String displayString() {
    return toString();
  }

  public String getNutrition()  {
    return calories + " cal  " + protein + " p  " + carbs + " c  " + fat + " f\n";
  }

  public String getProteinDisplay() {
    return protein + " g Protein";
  }

  public String getCarbsDisplay() {
    return carbs + " g Carbs";
  }
  public String getFatDisplay() {
    return fat + " g Fat";
  }
  public String getCalsDisplay() {
    return calories + " Calories";
  }

}
