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

  public Food() {

  }

  public Food(String name, double protein, double carbs, double fat) {
    super();
    this.name = name;
    this.protein = protein;
    this.carbs = carbs;
    this.fat = fat;
    calculateCalories();
  }

  private void calculateCalories()  {
    calories = (4 * protein) + (4 * carbs) + (9 * fat);
  }

  public String getName() {
    return name;
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

  public double getCalories() {
    return calories;
  }

  @Override
  public String toString()  {
    return "Food [name=" + name + ",protein=" + protein + ",carbs=" + carbs + ",fat=" + fat + "]";
  }

  public String displayString() {
    return name;
  }

}
