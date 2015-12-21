package org.apache.commons.math3.geometry;

import java.text.FieldPosition;
import java.text.NumberFormat;
import org.apache.commons.math3.util.CompositeFormat;

public abstract class VectorFormat
{
  private final String prefix;
  private final String suffix;
  private final String separator;
  private final String trimmedPrefix;
  private final String trimmedSuffix;
  private final String trimmedSeparator;
  private final NumberFormat format;
  
  protected VectorFormat(String paramString1, String paramString2, String paramString3, NumberFormat paramNumberFormat)
  {
    this.prefix = paramString1;
    this.suffix = paramString2;
    this.separator = paramString3;
    this.trimmedPrefix = paramString1.trim();
    this.trimmedSuffix = paramString2.trim();
    this.trimmedSeparator = paramString3.trim();
    this.format = paramNumberFormat;
  }
  
  public String format(Vector paramVector)
  {
    return format(paramVector, new StringBuffer(), new FieldPosition(0)).toString();
  }
  
  public abstract StringBuffer format(Vector paramVector, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  
  protected StringBuffer format(StringBuffer paramStringBuffer, FieldPosition paramFieldPosition, double... paramVarArgs)
  {
    paramFieldPosition.setBeginIndex(0);
    paramFieldPosition.setEndIndex(0);
    paramStringBuffer.append(this.prefix);
    for (int i = 0; i < paramVarArgs.length; i++)
    {
      if (i > 0) {
        paramStringBuffer.append(this.separator);
      }
      CompositeFormat.formatDouble(paramVarArgs[i], this.format, paramStringBuffer, paramFieldPosition);
    }
    paramStringBuffer.append(this.suffix);
    return paramStringBuffer;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\math3\geometry\VectorFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */