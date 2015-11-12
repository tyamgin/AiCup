package org.apache.commons.math3.util;

public class FastMath
{
  private static final double LOG_MAX_VALUE = StrictMath.log(Double.MAX_VALUE);
  private static final double[][] LN_QUICK_COEF = { { 1.0D, 5.669184079525E-24D }, { -0.25D, -0.25D }, { 0.3333333134651184D, 1.986821492305628E-8D }, { -0.25D, -6.663542893624021E-14D }, { 0.19999998807907104D, 1.1921056801463227E-8D }, { -0.1666666567325592D, -7.800414592973399E-9D }, { 0.1428571343421936D, 5.650007086920087E-9D }, { -0.12502530217170715D, -7.44321345601866E-11D }, { 0.11113807559013367D, 9.219544613762692E-9D } };
  private static final double[][] LN_HI_PREC_COEF = { { 1.0D, -6.032174644509064E-23D }, { -0.25D, -0.25D }, { 0.3333333134651184D, 1.9868161777724352E-8D }, { -0.2499999701976776D, -2.957007209750105E-8D }, { 0.19999954104423523D, 1.5830993332061267E-10D }, { -0.16624879837036133D, -2.6033824355191673E-8D } };
  private static final double[] SINE_TABLE_A = { 0.0D, 0.1246747374534607D, 0.24740394949913025D, 0.366272509098053D, 0.4794255495071411D, 0.5850973129272461D, 0.6816387176513672D, 0.7675435543060303D, 0.8414709568023682D, 0.902267575263977D, 0.9489846229553223D, 0.9808930158615112D, 0.9974949359893799D, 0.9985313415527344D };
  private static final double[] SINE_TABLE_B = { 0.0D, -4.068233003401932E-9D, 9.755392680573412E-9D, 1.9987994582857286E-8D, -1.0902938113007961E-8D, -3.9986783938944604E-8D, 4.23719669792332E-8D, -5.207000323380292E-8D, 2.800552834259E-8D, 1.883511811213715E-8D, -3.5997360512765566E-9D, 4.116164446561962E-8D, 5.0614674548127384E-8D, -1.0129027912496858E-9D };
  private static final double[] COSINE_TABLE_A = { 1.0D, 0.9921976327896118D, 0.9689123630523682D, 0.9305076599121094D, 0.8775825500488281D, 0.8109631538391113D, 0.7316888570785522D, 0.6409968137741089D, 0.5403022766113281D, 0.4311765432357788D, 0.3153223395347595D, 0.19454771280288696D, 0.07073719799518585D, -0.05417713522911072D };
  private static final double[] COSINE_TABLE_B = { 0.0D, 3.4439717236742845E-8D, 5.865827662008209E-8D, -3.7999795083850525E-8D, 1.184154459111628E-8D, -3.43338934259355E-8D, 1.1795268640216787E-8D, 4.438921624363781E-8D, 2.925681159240093E-8D, -2.6437112632041807E-8D, 2.2860509143963117E-8D, -4.813899778443457E-9D, 3.6725170580355583E-9D, 2.0217439756338078E-10D };
  private static final double[] TANGENT_TABLE_A = { 0.0D, 0.1256551444530487D, 0.25534194707870483D, 0.3936265707015991D, 0.5463024377822876D, 0.7214844226837158D, 0.9315965175628662D, 1.1974215507507324D, 1.5574076175689697D, 2.092571258544922D, 3.0095696449279785D, 5.041914939880371D, 14.101419448852539D, -18.430862426757812D };
  private static final double[] TANGENT_TABLE_B = { 0.0D, -7.877917738262007E-9D, -2.5857668567479893E-8D, 5.2240336371356666E-9D, 5.206150291559893E-8D, 1.8307188599677033E-8D, -5.7618793749770706E-8D, 7.848361555046424E-8D, 1.0708593250394448E-7D, 1.7827257129423813E-8D, 2.893485277253286E-8D, 3.1660099222737955E-7D, 4.983191803254889E-7D, -3.356118100840571E-7D };
  private static final long[] RECIP_2PI = { 2935890503282001226L, 9154082963658192752L, 3952090531849364496L, 9193070505571053912L, 7910884519577875640L, 113236205062349959L, 4577762542105553359L, -5034868814120038111L, 4208363204685324176L, 5648769086999809661L, 2819561105158720014L, -4035746434778044925L, -302932621132653753L, -2644281811660520851L, -3183605296591799669L, 6722166367014452318L, -3512299194304650054L, -7278142539171889152L };
  private static final long[] PI_O_4_BITS = { -3958705157555305932L, -4267615245585081135L };
  private static final double[] EIGHTHS = { 0.0D, 0.125D, 0.25D, 0.375D, 0.5D, 0.625D, 0.75D, 0.875D, 1.0D, 1.125D, 1.25D, 1.375D, 1.5D, 1.625D };
  private static final double[] CBRTTWO = { 0.6299605249474366D, 0.7937005259840998D, 1.0D, 1.2599210498948732D, 1.5874010519681994D };
  
  public static double sqrt(double paramDouble)
  {
    return Math.sqrt(paramDouble);
  }
  
  private static double polySine(double paramDouble)
  {
    double d1 = paramDouble * paramDouble;
    double d2 = 2.7553817452272217E-6D;
    d2 = d2 * d1 + -1.9841269659586505E-4D;
    d2 = d2 * d1 + 0.008333333333329196D;
    d2 = d2 * d1 + -0.16666666666666666D;
    d2 = d2 * d1 * paramDouble;
    return d2;
  }
  
  private static double polyCosine(double paramDouble)
  {
    double d1 = paramDouble * paramDouble;
    double d2 = 2.479773539153719E-5D;
    d2 = d2 * d1 + -0.0013888888689039883D;
    d2 = d2 * d1 + 0.041666666666621166D;
    d2 = d2 * d1 + -0.49999999999999994D;
    d2 *= d1;
    return d2;
  }
  
  private static double sinQ(double paramDouble1, double paramDouble2)
  {
    int i = (int)(paramDouble1 * 8.0D + 0.5D);
    double d1 = paramDouble1 - EIGHTHS[i];
    double d2 = SINE_TABLE_A[i];
    double d3 = SINE_TABLE_B[i];
    double d4 = COSINE_TABLE_A[i];
    double d5 = COSINE_TABLE_B[i];
    double d6 = d1;
    double d7 = polySine(d1);
    double d8 = 1.0D;
    double d9 = polyCosine(d1);
    double d10 = d6 * 1.073741824E9D;
    double d11 = d6 + d10 - d10;
    d7 += d6 - d11;
    d6 = d11;
    double d13 = 0.0D;
    double d14 = 0.0D;
    double d15 = d2;
    double d16 = d13 + d15;
    double d17 = -(d16 - d13 - d15);
    d13 = d16;
    d14 += d17;
    d15 = d4 * d6;
    d16 = d13 + d15;
    d17 = -(d16 - d13 - d15);
    d13 = d16;
    d14 += d17;
    d14 = d14 + d2 * d9 + d4 * d7;
    d14 = d14 + d3 + d5 * d6 + d3 * d9 + d5 * d7;
    if (paramDouble2 != 0.0D)
    {
      d15 = ((d4 + d5) * (1.0D + d9) - (d2 + d3) * (d6 + d7)) * paramDouble2;
      d16 = d13 + d15;
      d17 = -(d16 - d13 - d15);
      d13 = d16;
      d14 += d17;
    }
    double d12 = d13 + d14;
    return d12;
  }
  
  private static double cosQ(double paramDouble1, double paramDouble2)
  {
    double d1 = 1.5707963267948966D;
    double d2 = 6.123233995736766E-17D;
    double d3 = 1.5707963267948966D - paramDouble1;
    double d4 = -(d3 - 1.5707963267948966D + paramDouble1);
    d4 += 6.123233995736766E-17D - paramDouble2;
    return sinQ(d3, d4);
  }
  
  private static void reducePayneHanek(double paramDouble, double[] paramArrayOfDouble)
  {
    long l1 = Double.doubleToRawLongBits(paramDouble);
    int i = (int)(l1 >> 52 & 0x7FF) - 1023;
    l1 &= 0xFFFFFFFFFFFFF;
    l1 |= 0x10000000000000;
    i++;
    l1 <<= 11;
    int j = i >> 6;
    int k = i - (j << 6);
    long l2;
    long l3;
    long l4;
    if (k != 0)
    {
      l2 = j == 0 ? 0L : RECIP_2PI[(j - 1)] << k;
      l2 |= RECIP_2PI[j] >>> 64 - k;
      l3 = RECIP_2PI[j] << k | RECIP_2PI[(j + 1)] >>> 64 - k;
      l4 = RECIP_2PI[(j + 1)] << k | RECIP_2PI[(j + 2)] >>> 64 - k;
    }
    else
    {
      l2 = j == 0 ? 0L : RECIP_2PI[(j - 1)];
      l3 = RECIP_2PI[j];
      l4 = RECIP_2PI[(j + 1)];
    }
    long l5 = l1 >>> 32;
    long l6 = l1 & 0xFFFFFFFF;
    long l7 = l3 >>> 32;
    long l8 = l3 & 0xFFFFFFFF;
    long l9 = l5 * l7;
    long l10 = l6 * l8;
    long l11 = l6 * l7;
    long l12 = l5 * l8;
    long l13 = l10 + (l12 << 32);
    long l14 = l9 + (l12 >>> 32);
    int m = (l10 & 0x8000000000000000) != 0L ? 1 : 0;
    int n = (l12 & 0x80000000) != 0L ? 1 : 0;
    int i1 = (l13 & 0x8000000000000000) != 0L ? 1 : 0;
    if (((m != 0) && (n != 0)) || (((m != 0) || (n != 0)) && (i1 == 0))) {
      l14 += 1L;
    }
    m = (l13 & 0x8000000000000000) != 0L ? 1 : 0;
    n = (l11 & 0x80000000) != 0L ? 1 : 0;
    l13 += (l11 << 32);
    l14 += (l11 >>> 32);
    i1 = (l13 & 0x8000000000000000) != 0L ? 1 : 0;
    if (((m != 0) && (n != 0)) || (((m != 0) || (n != 0)) && (i1 == 0))) {
      l14 += 1L;
    }
    l7 = l4 >>> 32;
    l8 = l4 & 0xFFFFFFFF;
    l9 = l5 * l7;
    l11 = l6 * l7;
    l12 = l5 * l8;
    l9 += (l11 + l12 >>> 32);
    m = (l13 & 0x8000000000000000) != 0L ? 1 : 0;
    n = (l9 & 0x8000000000000000) != 0L ? 1 : 0;
    l13 += l9;
    i1 = (l13 & 0x8000000000000000) != 0L ? 1 : 0;
    if (((m != 0) && (n != 0)) || (((m != 0) || (n != 0)) && (i1 == 0))) {
      l14 += 1L;
    }
    l7 = l2 >>> 32;
    l8 = l2 & 0xFFFFFFFF;
    l10 = l6 * l8;
    l11 = l6 * l7;
    l12 = l5 * l8;
    l14 += l10 + (l11 + l12 << 32);
    int i2 = (int)(l14 >>> 62);
    l14 <<= 2;
    l14 |= l13 >>> 62;
    l13 <<= 2;
    l5 = l14 >>> 32;
    l6 = l14 & 0xFFFFFFFF;
    l7 = PI_O_4_BITS[0] >>> 32;
    l8 = PI_O_4_BITS[0] & 0xFFFFFFFF;
    l9 = l5 * l7;
    l10 = l6 * l8;
    l11 = l6 * l7;
    l12 = l5 * l8;
    long l15 = l10 + (l12 << 32);
    long l16 = l9 + (l12 >>> 32);
    m = (l10 & 0x8000000000000000) != 0L ? 1 : 0;
    n = (l12 & 0x80000000) != 0L ? 1 : 0;
    i1 = (l15 & 0x8000000000000000) != 0L ? 1 : 0;
    if (((m != 0) && (n != 0)) || (((m != 0) || (n != 0)) && (i1 == 0))) {
      l16 += 1L;
    }
    m = (l15 & 0x8000000000000000) != 0L ? 1 : 0;
    n = (l11 & 0x80000000) != 0L ? 1 : 0;
    l15 += (l11 << 32);
    l16 += (l11 >>> 32);
    i1 = (l15 & 0x8000000000000000) != 0L ? 1 : 0;
    if (((m != 0) && (n != 0)) || (((m != 0) || (n != 0)) && (i1 == 0))) {
      l16 += 1L;
    }
    l7 = PI_O_4_BITS[1] >>> 32;
    l8 = PI_O_4_BITS[1] & 0xFFFFFFFF;
    l9 = l5 * l7;
    l11 = l6 * l7;
    l12 = l5 * l8;
    l9 += (l11 + l12 >>> 32);
    m = (l15 & 0x8000000000000000) != 0L ? 1 : 0;
    n = (l9 & 0x8000000000000000) != 0L ? 1 : 0;
    l15 += l9;
    i1 = (l15 & 0x8000000000000000) != 0L ? 1 : 0;
    if (((m != 0) && (n != 0)) || (((m != 0) || (n != 0)) && (i1 == 0))) {
      l16 += 1L;
    }
    l5 = l13 >>> 32;
    l6 = l13 & 0xFFFFFFFF;
    l7 = PI_O_4_BITS[0] >>> 32;
    l8 = PI_O_4_BITS[0] & 0xFFFFFFFF;
    l9 = l5 * l7;
    l11 = l6 * l7;
    l12 = l5 * l8;
    l9 += (l11 + l12 >>> 32);
    m = (l15 & 0x8000000000000000) != 0L ? 1 : 0;
    n = (l9 & 0x8000000000000000) != 0L ? 1 : 0;
    l15 += l9;
    i1 = (l15 & 0x8000000000000000) != 0L ? 1 : 0;
    if (((m != 0) && (n != 0)) || (((m != 0) || (n != 0)) && (i1 == 0))) {
      l16 += 1L;
    }
    double d1 = (l16 >>> 12) / 4.503599627370496E15D;
    double d2 = (((l16 & 0xFFF) << 40) + (l15 >>> 24)) / 4.503599627370496E15D / 4.503599627370496E15D;
    double d3 = d1 + d2;
    double d4 = -(d3 - d1 - d2);
    paramArrayOfDouble[0] = i2;
    paramArrayOfDouble[1] = (d3 * 2.0D);
    paramArrayOfDouble[2] = (d4 * 2.0D);
  }
  
  public static double sin(double paramDouble)
  {
    int i = 0;
    int j = 0;
    double d2 = 0.0D;
    double d1 = paramDouble;
    if (paramDouble < 0.0D)
    {
      i = 1;
      d1 = -d1;
    }
    if (d1 == 0.0D)
    {
      long l = Double.doubleToRawLongBits(paramDouble);
      if (l < 0L) {
        return -0.0D;
      }
      return 0.0D;
    }
    if ((d1 != d1) || (d1 == Double.POSITIVE_INFINITY)) {
      return NaN.0D;
    }
    Object localObject;
    if (d1 > 3294198.0D)
    {
      localObject = new double[3];
      reducePayneHanek(d1, (double[])localObject);
      j = (int)localObject[0] & 0x3;
      d1 = localObject[1];
      d2 = localObject[2];
    }
    else if (d1 > 1.5707963267948966D)
    {
      localObject = new CodyWaite(d1);
      j = ((CodyWaite)localObject).getK() & 0x3;
      d1 = ((CodyWaite)localObject).getRemA();
      d2 = ((CodyWaite)localObject).getRemB();
    }
    if (i != 0) {
      j ^= 0x2;
    }
    switch (j)
    {
    case 0: 
      return sinQ(d1, d2);
    case 1: 
      return cosQ(d1, d2);
    case 2: 
      return -sinQ(d1, d2);
    case 3: 
      return -cosQ(d1, d2);
    }
    return NaN.0D;
  }
  
  public static double cos(double paramDouble)
  {
    int i = 0;
    double d1 = paramDouble;
    if (paramDouble < 0.0D) {
      d1 = -d1;
    }
    if ((d1 != d1) || (d1 == Double.POSITIVE_INFINITY)) {
      return NaN.0D;
    }
    double d2 = 0.0D;
    Object localObject;
    if (d1 > 3294198.0D)
    {
      localObject = new double[3];
      reducePayneHanek(d1, (double[])localObject);
      i = (int)localObject[0] & 0x3;
      d1 = localObject[1];
      d2 = localObject[2];
    }
    else if (d1 > 1.5707963267948966D)
    {
      localObject = new CodyWaite(d1);
      i = ((CodyWaite)localObject).getK() & 0x3;
      d1 = ((CodyWaite)localObject).getRemA();
      d2 = ((CodyWaite)localObject).getRemB();
    }
    switch (i)
    {
    case 0: 
      return cosQ(d1, d2);
    case 1: 
      return -sinQ(d1, d2);
    case 2: 
      return -cosQ(d1, d2);
    case 3: 
      return sinQ(d1, d2);
    }
    return NaN.0D;
  }
  
  public static double abs(double paramDouble)
  {
    return Double.longBitsToDouble(0x7FFFFFFFFFFFFFFF & Double.doubleToRawLongBits(paramDouble));
  }
  
  public static double scalb(double paramDouble, int paramInt)
  {
    if ((paramInt > 64513) && (paramInt < 1024)) {
      return paramDouble * Double.longBitsToDouble(paramInt + 1023 << 52);
    }
    if ((Double.isNaN(paramDouble)) || (Double.isInfinite(paramDouble)) || (paramDouble == 0.0D)) {
      return paramDouble;
    }
    if (paramInt < 63438) {
      return paramDouble > 0.0D ? 0.0D : -0.0D;
    }
    if (paramInt > 2097) {
      return paramDouble > 0.0D ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    }
    long l1 = Double.doubleToRawLongBits(paramDouble);
    long l2 = l1 & 0x8000000000000000;
    int i = (int)(l1 >>> 52) & 0x7FF;
    long l3 = l1 & 0xFFFFFFFFFFFFF;
    int j = i + paramInt;
    if (paramInt < 0)
    {
      if (j > 0) {
        return Double.longBitsToDouble(l2 | j << 52 | l3);
      }
      if (j > -53)
      {
        l3 |= 0x10000000000000;
        long l4 = l3 & 1L << -j;
        l3 >>>= 1 - j;
        if (l4 != 0L) {
          l3 += 1L;
        }
        return Double.longBitsToDouble(l2 | l3);
      }
      return l2 == 0L ? 0.0D : -0.0D;
    }
    if (i == 0)
    {
      while (l3 >>> 52 != 1L)
      {
        l3 <<= 1;
        j--;
      }
      j++;
      l3 &= 0xFFFFFFFFFFFFF;
      if (j < 2047) {
        return Double.longBitsToDouble(l2 | j << 52 | l3);
      }
      return l2 == 0L ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    }
    if (j < 2047) {
      return Double.longBitsToDouble(l2 | j << 52 | l3);
    }
    return l2 == 0L ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
  }
  
  public static double floor(double paramDouble)
  {
    if (paramDouble != paramDouble) {
      return paramDouble;
    }
    if ((paramDouble >= 4.503599627370496E15D) || (paramDouble <= -4.503599627370496E15D)) {
      return paramDouble;
    }
    long l = paramDouble;
    if ((paramDouble < 0.0D) && (l != paramDouble)) {
      l -= 1L;
    }
    if (l == 0L) {
      return paramDouble * l;
    }
    return l;
  }
  
  public static double hypot(double paramDouble1, double paramDouble2)
  {
    if ((Double.isInfinite(paramDouble1)) || (Double.isInfinite(paramDouble2))) {
      return Double.POSITIVE_INFINITY;
    }
    if ((Double.isNaN(paramDouble1)) || (Double.isNaN(paramDouble2))) {
      return NaN.0D;
    }
    int i = getExponent(paramDouble1);
    int j = getExponent(paramDouble2);
    if (i > j + 27) {
      return abs(paramDouble1);
    }
    if (j > i + 27) {
      return abs(paramDouble2);
    }
    int k = (i + j) / 2;
    double d1 = scalb(paramDouble1, -k);
    double d2 = scalb(paramDouble2, -k);
    double d3 = sqrt(d1 * d1 + d2 * d2);
    return scalb(d3, k);
  }
  
  public static int getExponent(double paramDouble)
  {
    return (int)(Double.doubleToRawLongBits(paramDouble) >>> 52 & 0x7FF) - 1023;
  }
  
  private static class CodyWaite
  {
    private final int finalK;
    private final double finalRemA;
    private final double finalRemB;
    
    CodyWaite(double paramDouble)
    {
      double d1;
      double d2;
      for (int i = (int)(paramDouble * 0.6366197723675814D);; i--)
      {
        double d3 = -i * 1.570796251296997D;
        d1 = paramDouble + d3;
        d2 = -(d1 - paramDouble - d3);
        d3 = -i * 7.549789948768648E-8D;
        double d4 = d1;
        d1 = d3 + d4;
        d2 += -(d1 - d4 - d3);
        d3 = -i * 6.123233995736766E-17D;
        d4 = d1;
        d1 = d3 + d4;
        d2 += -(d1 - d4 - d3);
        if (d1 > 0.0D) {
          break;
        }
      }
      this.finalK = i;
      this.finalRemA = d1;
      this.finalRemB = d2;
    }
    
    int getK()
    {
      return this.finalK;
    }
    
    double getRemA()
    {
      return this.finalRemA;
    }
    
    double getRemB()
    {
      return this.finalRemB;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\math3\util\FastMath.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */