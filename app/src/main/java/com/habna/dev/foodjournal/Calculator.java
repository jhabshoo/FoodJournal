package com.habna.dev.foodjournal;

/**
 * Created by jhabs on 8/4/2016.
 */
public class Calculator {

  public enum GOAL_TYPE {
    LOSE,
    MAINTAIN,
    GAIN
  }

  public enum ACTIVE_TYPE {
    SEDENTARY,
    LIGHTLY,
    MODERATELY,
    VERY,
    EXTREMELY
  }

  private double height;
  private double weight;
  private int age;
  private double bodyFat;
  private boolean male;
  private GOAL_TYPE goal;
  private ACTIVE_TYPE activity;

  public Calculator(double height, double weight, int age, double bodyFat, boolean male, GOAL_TYPE goal, ACTIVE_TYPE activity) {
    this.height = height;
    this.weight = weight;
    this.age = age;
    this.bodyFat = bodyFat;
    this.male = male;
    this.goal = goal;
    this.activity = activity;
  }

  private double calculateBMR() {
    if (bodyFat == -1) {
      double bmr = bmrBenedict() + bmrMifflin() + bmrOwen() + bmrKatch();
      bmr /= 4.0;
      return bmr;
    }
    return bmrKatch();
  }

  private double bmrBenedict()  {
    double constant, weightMult, heightMult, ageMult;
    if (male) {
      constant = 66.5;
      weightMult = 13.7;
      heightMult = 5;
      ageMult = 6.76;
    } else  {
      constant = 655;
      weightMult = 9.56;
      heightMult = 1.8;
      ageMult = 4.68;
    }
    return constant + (weightMult*weight) + (heightMult*height) - (ageMult*age);
  }

  private double bmrMifflin() {
    double constant = male ? 5 : -161;
    return (10*weight) + (6.25*height) - (5*age) + constant;
  }

  private double bmrOwen()  {
    double constant = male ? 879 : 795;
    double weightMult = male ? 10.2 : 7.2;
    return constant + (weightMult*weight);
  }

  private double bmrKatch() {
    double lbm = weight * (100-bodyFat) / 100;
    return 370 + (21.6 * lbm);
  }
}
