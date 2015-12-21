package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.internal.Errors;
import com.google.inject.matcher.Matcher;

public final class TypeConverterBinding
  implements Element
{
  private final Object source;
  private final Matcher typeMatcher;
  private final TypeConverter typeConverter;
  
  public TypeConverterBinding(Object paramObject, Matcher paramMatcher, TypeConverter paramTypeConverter)
  {
    this.source = Preconditions.checkNotNull(paramObject, "source");
    this.typeMatcher = ((Matcher)Preconditions.checkNotNull(paramMatcher, "typeMatcher"));
    this.typeConverter = ((TypeConverter)Preconditions.checkNotNull(paramTypeConverter, "typeConverter"));
  }
  
  public Object getSource()
  {
    return this.source;
  }
  
  public Matcher getTypeMatcher()
  {
    return this.typeMatcher;
  }
  
  public TypeConverter getTypeConverter()
  {
    return this.typeConverter;
  }
  
  public Object acceptVisitor(ElementVisitor paramElementVisitor)
  {
    return paramElementVisitor.visit(this);
  }
  
  public void applyTo(Binder paramBinder)
  {
    paramBinder.withSource(getSource()).convertToTypes(this.typeMatcher, this.typeConverter);
  }
  
  public String toString()
  {
    String str1 = String.valueOf(String.valueOf(this.typeConverter));
    String str2 = String.valueOf(String.valueOf(this.typeMatcher));
    String str3 = String.valueOf(String.valueOf(Errors.convert(this.source)));
    return 27 + str1.length() + str2.length() + str3.length() + str1 + " which matches " + str2 + " (bound at " + str3 + ")";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\TypeConverterBinding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */