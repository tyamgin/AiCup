package org.apache.commons.math3.util;

public class MathArrays
{
  public static double linearCombination(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    double d1 = 1.34217729E8D * paramDouble1;
    double d2 = d1 - (d1 - paramDouble1);
    double d3 = paramDouble1 - d2;
    double d4 = 1.34217729E8D * paramDouble2;
    double d5 = d4 - (d4 - paramDouble2);
    double d6 = paramDouble2 - d5;
    double d7 = paramDouble1 * paramDouble2;
    double d8 = d3 * d6 - (d7 - d2 * d5 - d3 * d5 - d2 * d6);
    double d9 = 1.34217729E8D * paramDouble3;
    double d10 = d9 - (d9 - paramDouble3);
    double d11 = paramDouble3 - d10;
    double d12 = 1.34217729E8D * paramDouble4;
    double d13 = d12 - (d12 - paramDouble4);
    double d14 = paramDouble4 - d13;
    double d15 = paramDouble3 * paramDouble4;
    double d16 = d11 * d14 - (d15 - d10 * d13 - d11 * d13 - d10 * d14);
    double d17 = d7 + d15;
    double d18 = d17 - d15;
    double d19 = d15 - (d17 - d18) + (d7 - d18);
    double d20 = d17 + (d8 + d16 + d19);
    if (Double.isNaN(d20)) {
      d20 = paramDouble1 * paramDouble2 + paramDouble3 * paramDouble4;
    }
    return d20;
  }
  
  public static double linearCombination(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
  {
    double d1 = 1.34217729E8D * paramDouble1;
    double d2 = d1 - (d1 - paramDouble1);
    double d3 = paramDouble1 - d2;
    double d4 = 1.34217729E8D * paramDouble2;
    double d5 = d4 - (d4 - paramDouble2);
    double d6 = paramDouble2 - d5;
    double d7 = paramDouble1 * paramDouble2;
    double d8 = d3 * d6 - (d7 - d2 * d5 - d3 * d5 - d2 * d6);
    double d9 = 1.34217729E8D * paramDouble3;
    double d10 = d9 - (d9 - paramDouble3);
    double d11 = paramDouble3 - d10;
    double d12 = 1.34217729E8D * paramDouble4;
    double d13 = d12 - (d12 - paramDouble4);
    double d14 = paramDouble4 - d13;
    double d15 = paramDouble3 * paramDouble4;
    double d16 = d11 * d14 - (d15 - d10 * d13 - d11 * d13 - d10 * d14);
    double d17 = 1.34217729E8D * paramDouble5;
    double d18 = d17 - (d17 - paramDouble5);
    double d19 = paramDouble5 - d18;
    double d20 = 1.34217729E8D * paramDouble6;
    double d21 = d20 - (d20 - paramDouble6);
    double d22 = paramDouble6 - d21;
    double d23 = paramDouble5 * paramDouble6;
    double d24 = d19 * d22 - (d23 - d18 * d21 - d19 * d21 - d18 * d22);
    double d25 = d7 + d15;
    double d26 = d25 - d15;
    double d27 = d15 - (d25 - d26) + (d7 - d26);
    double d28 = d25 + d23;
    double d29 = d28 - d23;
    double d30 = d23 - (d28 - d29) + (d25 - d29);
    double d31 = d28 + (d8 + d16 + d24 + d27 + d30);
    if (Double.isNaN(d31)) {
      d31 = paramDouble1 * paramDouble2 + paramDouble3 * paramDouble4 + paramDouble5 * paramDouble6;
    }
    return d31;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\math3\util\MathArrays.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */