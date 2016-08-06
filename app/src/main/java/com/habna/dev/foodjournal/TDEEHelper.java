package com.habna.dev.foodjournal;

import android.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jhabs on 8/6/2016.
 */
public class TDEEHelper {
  public static Map<Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>, Double> map = new HashMap<>();

  static {
    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.SEDENTARY, Calculator.ACTIVE_TYPE.SEDENTARY), 1.0);
    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.SEDENTARY, Calculator.ACTIVE_TYPE.LIGHTLY), 1.125);
    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.SEDENTARY, Calculator.ACTIVE_TYPE.MODERATELY), 1.375);
    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.SEDENTARY, Calculator.ACTIVE_TYPE.VERY), 1.5);

    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.LIGHTLY, Calculator.ACTIVE_TYPE.SEDENTARY), 1.375);
    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.LIGHTLY, Calculator.ACTIVE_TYPE.LIGHTLY), 1.425);
    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.LIGHTLY, Calculator.ACTIVE_TYPE.MODERATELY), 1.503);
    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.LIGHTLY, Calculator.ACTIVE_TYPE.VERY), 1.583);

    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.MODERATELY, Calculator.ACTIVE_TYPE.SEDENTARY), 1.55);
    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.MODERATELY, Calculator.ACTIVE_TYPE.LIGHTLY), 1.589);
    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.MODERATELY, Calculator.ACTIVE_TYPE.MODERATELY), 1.628);
    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.MODERATELY, Calculator.ACTIVE_TYPE.VERY), 1.66);

    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.VERY, Calculator.ACTIVE_TYPE.SEDENTARY), 1.725);
    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.VERY, Calculator.ACTIVE_TYPE.LIGHTLY), 1.733);
    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.VERY, Calculator.ACTIVE_TYPE.MODERATELY), 1.742);
    map.put(new Pair<Calculator.ACTIVE_TYPE, Calculator.ACTIVE_TYPE>(Calculator.ACTIVE_TYPE.VERY, Calculator.ACTIVE_TYPE.VERY), 1.75);
  }

  public double getTDEE(Calculator.ACTIVE_TYPE work, Calculator.ACTIVE_TYPE free) {
    return map.get(new Pair<>(work, free));
  }
}
