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

  public static GOAL_TYPE getGoalByString(String str) {
    if ("LOSE".equals(str.toUpperCase())) {
      return GOAL_TYPE.LOSE;
    }
    if ("MAINTAIN".equals(str.toUpperCase())) {
      return GOAL_TYPE.MAINTAIN;
    }
    if ("GAIN".equals(str.toUpperCase())) {
      return GOAL_TYPE.GAIN;
    }
    return null;
  }

  public static ACTIVE_TYPE getActiveByString(String str) {
    if ("SEDENTARY".equals(str.toUpperCase())) {
      return ACTIVE_TYPE.SEDENTARY;
    }
    if ("LIGHTLY".equals(str.toUpperCase())) {
      return ACTIVE_TYPE.LIGHTLY;
    }
    if ("MODERATELY".equals(str.toUpperCase())) {
      return ACTIVE_TYPE.MODERATELY;
    }
    if ("VERY".equals(str.toUpperCase())) {
      return ACTIVE_TYPE.VERY;
    }
    if ("EXTREMELY".equals(str.toUpperCase())) {
      return ACTIVE_TYPE.EXTREMELY;
    }
    return null;
  }

  public double getGoalCals()  {
    double cals = getActiveMultiplier() * calculateBMR();
    return cals;
  }

  private double getActiveMultiplier()  {
    double mult;
    switch (activity) {
      case SEDENTARY:
        mult = 1.2;
        break;
      case LIGHTLY:
        mult = 1.35;
        break;
      case MODERATELY:
        mult = 1.55;
        break;
      case VERY:
        mult = 1.75;
        break;
      case EXTREMELY:
        mult = 2.05;
        break;
      default:
        mult = -1;
        break;
    }
    return mult;
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
