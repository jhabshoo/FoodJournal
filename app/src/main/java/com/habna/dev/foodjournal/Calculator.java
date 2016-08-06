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
    VERY
  }

  public static final double MALE_FLOOR = 1400;
  public static final double FEMALE_FLOOR = 800;

  private double height;
  private double weight;
  private int age;
  private double bodyFat;
  private boolean male;
  private GOAL_TYPE goal;
  private double weeklyLbs;
  private ACTIVE_TYPE activity;
  private ACTIVE_TYPE workType;

  private TDEEHelper tdeeHelper;

  public Calculator(double height, double weight, int age, double bodyFat, boolean male,
                    GOAL_TYPE goal, ACTIVE_TYPE activity, ACTIVE_TYPE workType, double weeklyLbs) {
    this.height = height;
    this.weight = weight;
    this.age = age;
    this.bodyFat = bodyFat;
    this.male = male;
    this.goal = goal;
    this.activity = activity;
    this.workType = workType;
    this.weeklyLbs = weeklyLbs;
    tdeeHelper = new TDEEHelper();
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
    if ("LIGHT".equals(str.toUpperCase())) {
      return ACTIVE_TYPE.LIGHTLY;
    }
    if ("MODERATE".equals(str.toUpperCase())) {
      return ACTIVE_TYPE.MODERATELY;
    }
    if ("VERY".equals(str.toUpperCase())) {
      return ACTIVE_TYPE.VERY;
    }
    return null;
  }

  public Macros getMacros()  {
    double cals = getTDEE() * calculateBMR();
    cals = adjustForWeightLoss(cals);
    return new Macros(weight, cals);
  }


  private double getTDEE()  {
    return tdeeHelper.getTDEE(workType, activity);
  }

  private double adjustForWeightLoss(double cals) {
    double adjustment = (goal.equals(GOAL_TYPE.MAINTAIN) ? 1 : getAdjustment());
    return cals + adjustment;
  }

  private double getAdjustment() {
    double mult = goal.equals(GOAL_TYPE.GAIN) ? 1 : -1;
    return weeklyLbs * 500 * mult;
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

  protected class Macros  {

    private final double FAT_CALS = 9;
    private final double PRO_CARB_CALS = 4;
    private final double PRO_G_PER_LB = .825;
    private double protein;
    private double carbs;
    private double fat;
    private double tdee;

    public Macros(double bodyWeight, double tdee)  {
      this.tdee = tdee;
      calculateMacros(bodyWeight);
    }

    private void calculateMacros(double bodyWeight) {
      bodyWeight = bodyWeight * 2.2046226218;
      protein = PRO_G_PER_LB * bodyWeight;
      fat = (tdee * .25) / FAT_CALS;
      double remainder = tdee - ((PRO_CARB_CALS * protein) + (FAT_CALS * fat));
      carbs = remainder / PRO_CARB_CALS;
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

    public double getTdee() {
      return tdee;
    }
  }

}
