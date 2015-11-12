package org.apache.commons.math3.geometry.euclidean.threed;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.VectorFormat;
import org.apache.commons.math3.util.CompositeFormat;

public class Vector3DFormat
  extends VectorFormat
{
  public Vector3DFormat()
  {
    super("{", "}", "; ", CompositeFormat.getDefaultNumberFormat());
  }
  
  public Vector3DFormat(NumberFormat paramNumberFormat)
  {
    super("{", "}", "; ", paramNumberFormat);
  }
  
  public static Vector3DFormat getInstance()
  {
    return getInstance(Locale.getDefault());
  }
  
  public static Vector3DFormat getInstance(Locale paramLocale)
  {
    return new Vector3DFormat(CompositeFormat.getDefaultNumberFormat(paramLocale));
  }
  
  public StringBuffer format(Vector paramVector, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    Vector3D localVector3D = (Vector3D)paramVector;
    return format(paramStringBuffer, paramFieldPosition, new double[] { localVector3D.getX(), localVector3D.getY(), localVector3D.getZ() });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\math3\geometry\euclidean\threed\Vector3DFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */