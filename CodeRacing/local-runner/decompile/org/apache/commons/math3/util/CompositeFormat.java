package org.apache.commons.math3.util;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;

public class CompositeFormat
{
  public static NumberFormat getDefaultNumberFormat()
  {
    return getDefaultNumberFormat(Locale.getDefault());
  }
  
  public static NumberFormat getDefaultNumberFormat(Locale paramLocale)
  {
    NumberFormat localNumberFormat = NumberFormat.getInstance(paramLocale);
    localNumberFormat.setMaximumFractionDigits(10);
    return localNumberFormat;
  }
  
  public static StringBuffer formatDouble(double paramDouble, NumberFormat paramNumberFormat, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    if ((Double.isNaN(paramDouble)) || (Double.isInfinite(paramDouble)))
    {
      paramStringBuffer.append('(');
      paramStringBuffer.append(paramDouble);
      paramStringBuffer.append(')');
    }
    else
    {
      paramNumberFormat.format(paramDouble, paramStringBuffer, paramFieldPosition);
    }
    return paramStringBuffer;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\math3\util\CompositeFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */