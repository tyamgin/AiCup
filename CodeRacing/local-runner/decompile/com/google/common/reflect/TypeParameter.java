package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

@Beta
public abstract class TypeParameter
  extends TypeCapture
{
  final TypeVariable typeVariable;
  
  protected TypeParameter()
  {
    Type localType = capture();
    Preconditions.checkArgument(localType instanceof TypeVariable, "%s should be a type variable.", new Object[] { localType });
    this.typeVariable = ((TypeVariable)localType);
  }
  
  public final int hashCode()
  {
    return this.typeVariable.hashCode();
  }
  
  public final boolean equals(Object paramObject)
  {
    if ((paramObject instanceof TypeParameter))
    {
      TypeParameter localTypeParameter = (TypeParameter)paramObject;
      return this.typeVariable.equals(localTypeParameter.typeVariable);
    }
    return false;
  }
  
  public String toString()
  {
    return this.typeVariable.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\reflect\TypeParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */