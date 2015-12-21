package org.apache.commons.math3.geometry.euclidean.threed;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class Vector3D
  implements Serializable, Vector
{
  public static final Vector3D ZERO = new Vector3D(0.0D, 0.0D, 0.0D);
  public static final Vector3D PLUS_I = new Vector3D(1.0D, 0.0D, 0.0D);
  public static final Vector3D MINUS_I = new Vector3D(-1.0D, 0.0D, 0.0D);
  public static final Vector3D PLUS_J = new Vector3D(0.0D, 1.0D, 0.0D);
  public static final Vector3D MINUS_J = new Vector3D(0.0D, -1.0D, 0.0D);
  public static final Vector3D PLUS_K = new Vector3D(0.0D, 0.0D, 1.0D);
  public static final Vector3D MINUS_K = new Vector3D(0.0D, 0.0D, -1.0D);
  public static final Vector3D NaN = new Vector3D(NaN.0D, NaN.0D, NaN.0D);
  public static final Vector3D POSITIVE_INFINITY = new Vector3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
  public static final Vector3D NEGATIVE_INFINITY = new Vector3D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
  private final double x;
  private final double y;
  private final double z;
  
  public Vector3D(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    this.x = paramDouble1;
    this.y = paramDouble2;
    this.z = paramDouble3;
  }
  
  public double getX()
  {
    return this.x;
  }
  
  public double getY()
  {
    return this.y;
  }
  
  public double getZ()
  {
    return this.z;
  }
  
  public double getNorm()
  {
    return FastMath.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
  }
  
  public double getNormSq()
  {
    return this.x * this.x + this.y * this.y + this.z * this.z;
  }
  
  public Vector3D add(Vector paramVector)
  {
    Vector3D localVector3D = (Vector3D)paramVector;
    return new Vector3D(this.x + localVector3D.x, this.y + localVector3D.y, this.z + localVector3D.z);
  }
  
  public Vector3D subtract(Vector paramVector)
  {
    Vector3D localVector3D = (Vector3D)paramVector;
    return new Vector3D(this.x - localVector3D.x, this.y - localVector3D.y, this.z - localVector3D.z);
  }
  
  public Vector3D normalize()
    throws MathArithmeticException
  {
    double d = getNorm();
    if (d == 0.0D) {
      throw new MathArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR, new Object[0]);
    }
    return scalarMultiply(1.0D / d);
  }
  
  public Vector3D scalarMultiply(double paramDouble)
  {
    return new Vector3D(paramDouble * this.x, paramDouble * this.y, paramDouble * this.z);
  }
  
  public boolean isNaN()
  {
    return (Double.isNaN(this.x)) || (Double.isNaN(this.y)) || (Double.isNaN(this.z));
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof Vector3D))
    {
      Vector3D localVector3D = (Vector3D)paramObject;
      if (localVector3D.isNaN()) {
        return isNaN();
      }
      return (this.x == localVector3D.x) && (this.y == localVector3D.y) && (this.z == localVector3D.z);
    }
    return false;
  }
  
  public int hashCode()
  {
    if (isNaN()) {
      return 642;
    }
    return 643 * (164 * MathUtils.hash(this.x) + 3 * MathUtils.hash(this.y) + MathUtils.hash(this.z));
  }
  
  public double dotProduct(Vector paramVector)
  {
    Vector3D localVector3D = (Vector3D)paramVector;
    return MathArrays.linearCombination(this.x, localVector3D.x, this.y, localVector3D.y, this.z, localVector3D.z);
  }
  
  public Vector3D crossProduct(Vector paramVector)
  {
    Vector3D localVector3D = (Vector3D)paramVector;
    return new Vector3D(MathArrays.linearCombination(this.y, localVector3D.z, -this.z, localVector3D.y), MathArrays.linearCombination(this.z, localVector3D.x, -this.x, localVector3D.z), MathArrays.linearCombination(this.x, localVector3D.y, -this.y, localVector3D.x));
  }
  
  public String toString()
  {
    return Vector3DFormat.getInstance().format(this);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\math3\geometry\euclidean\threed\Vector3D.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */