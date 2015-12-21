package org.apache.log4j.config;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.OptionHandler;

public class PropertySetter
{
  protected Object obj;
  protected PropertyDescriptor[] props;
  
  public PropertySetter(Object paramObject)
  {
    this.obj = paramObject;
  }
  
  protected void introspect()
  {
    try
    {
      BeanInfo localBeanInfo = Introspector.getBeanInfo(this.obj.getClass());
      this.props = localBeanInfo.getPropertyDescriptors();
    }
    catch (IntrospectionException localIntrospectionException)
    {
      LogLog.error("Failed to introspect " + this.obj + ": " + localIntrospectionException.getMessage());
      this.props = new PropertyDescriptor[0];
    }
  }
  
  public static void setProperties(Object paramObject, Properties paramProperties, String paramString)
  {
    new PropertySetter(paramObject).setProperties(paramProperties, paramString);
  }
  
  public void setProperties(Properties paramProperties, String paramString)
  {
    int i = paramString.length();
    Enumeration localEnumeration = paramProperties.propertyNames();
    while (localEnumeration.hasMoreElements())
    {
      String str1 = (String)localEnumeration.nextElement();
      if (str1.startsWith(paramString)) {
        if (str1.indexOf('.', i + 1) <= 0)
        {
          String str2 = OptionConverter.findAndSubst(str1, paramProperties);
          str1 = str1.substring(i);
          if (((!"layout".equals(str1)) && (!"errorhandler".equals(str1))) || (!(this.obj instanceof Appender)))
          {
            PropertyDescriptor localPropertyDescriptor = getPropertyDescriptor(Introspector.decapitalize(str1));
            if ((localPropertyDescriptor != null) && (OptionHandler.class.isAssignableFrom(localPropertyDescriptor.getPropertyType())) && (localPropertyDescriptor.getWriteMethod() != null))
            {
              OptionHandler localOptionHandler = (OptionHandler)OptionConverter.instantiateByKey(paramProperties, paramString + str1, localPropertyDescriptor.getPropertyType(), null);
              PropertySetter localPropertySetter = new PropertySetter(localOptionHandler);
              localPropertySetter.setProperties(paramProperties, paramString + str1 + ".");
              try
              {
                localPropertyDescriptor.getWriteMethod().invoke(this.obj, new Object[] { localOptionHandler });
              }
              catch (IllegalAccessException localIllegalAccessException)
              {
                LogLog.warn("Failed to set property [" + str1 + "] to value \"" + str2 + "\". ", localIllegalAccessException);
              }
              catch (InvocationTargetException localInvocationTargetException)
              {
                if (((localInvocationTargetException.getTargetException() instanceof InterruptedException)) || ((localInvocationTargetException.getTargetException() instanceof InterruptedIOException))) {
                  Thread.currentThread().interrupt();
                }
                LogLog.warn("Failed to set property [" + str1 + "] to value \"" + str2 + "\". ", localInvocationTargetException);
              }
              catch (RuntimeException localRuntimeException)
              {
                LogLog.warn("Failed to set property [" + str1 + "] to value \"" + str2 + "\". ", localRuntimeException);
              }
            }
            else
            {
              setProperty(str1, str2);
            }
          }
        }
      }
    }
    activate();
  }
  
  public void setProperty(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return;
    }
    paramString1 = Introspector.decapitalize(paramString1);
    PropertyDescriptor localPropertyDescriptor = getPropertyDescriptor(paramString1);
    if (localPropertyDescriptor == null) {
      LogLog.warn("No such property [" + paramString1 + "] in " + this.obj.getClass().getName() + ".");
    } else {
      try
      {
        setProperty(localPropertyDescriptor, paramString1, paramString2);
      }
      catch (PropertySetterException localPropertySetterException)
      {
        LogLog.warn("Failed to set property [" + paramString1 + "] to value \"" + paramString2 + "\". ", localPropertySetterException.rootCause);
      }
    }
  }
  
  public void setProperty(PropertyDescriptor paramPropertyDescriptor, String paramString1, String paramString2)
    throws PropertySetterException
  {
    Method localMethod = paramPropertyDescriptor.getWriteMethod();
    if (localMethod == null) {
      throw new PropertySetterException("No setter for property [" + paramString1 + "].");
    }
    Class[] arrayOfClass = localMethod.getParameterTypes();
    if (arrayOfClass.length != 1) {
      throw new PropertySetterException("#params for setter != 1");
    }
    Object localObject;
    try
    {
      localObject = convertArg(paramString2, arrayOfClass[0]);
    }
    catch (Throwable localThrowable)
    {
      throw new PropertySetterException("Conversion to type [" + arrayOfClass[0] + "] failed. Reason: " + localThrowable);
    }
    if (localObject == null) {
      throw new PropertySetterException("Conversion to type [" + arrayOfClass[0] + "] failed.");
    }
    LogLog.debug("Setting property [" + paramString1 + "] to [" + localObject + "].");
    try
    {
      localMethod.invoke(this.obj, new Object[] { localObject });
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new PropertySetterException(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      if (((localInvocationTargetException.getTargetException() instanceof InterruptedException)) || ((localInvocationTargetException.getTargetException() instanceof InterruptedIOException))) {
        Thread.currentThread().interrupt();
      }
      throw new PropertySetterException(localInvocationTargetException);
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new PropertySetterException(localRuntimeException);
    }
  }
  
  protected Object convertArg(String paramString, Class paramClass)
  {
    if (paramString == null) {
      return null;
    }
    String str = paramString.trim();
    if (String.class.isAssignableFrom(paramClass)) {
      return paramString;
    }
    if (Integer.TYPE.isAssignableFrom(paramClass)) {
      return new Integer(str);
    }
    if (Long.TYPE.isAssignableFrom(paramClass)) {
      return new Long(str);
    }
    if (Boolean.TYPE.isAssignableFrom(paramClass))
    {
      if ("true".equalsIgnoreCase(str)) {
        return Boolean.TRUE;
      }
      if ("false".equalsIgnoreCase(str)) {
        return Boolean.FALSE;
      }
    }
    else
    {
      if (Priority.class.isAssignableFrom(paramClass)) {
        return OptionConverter.toLevel(str, Level.DEBUG);
      }
      if (ErrorHandler.class.isAssignableFrom(paramClass)) {
        return OptionConverter.instantiateByClassName(str, ErrorHandler.class, null);
      }
    }
    return null;
  }
  
  protected PropertyDescriptor getPropertyDescriptor(String paramString)
  {
    if (this.props == null) {
      introspect();
    }
    for (int i = 0; i < this.props.length; i++) {
      if (paramString.equals(this.props[i].getName())) {
        return this.props[i];
      }
    }
    return null;
  }
  
  public void activate()
  {
    if ((this.obj instanceof OptionHandler)) {
      ((OptionHandler)this.obj).activateOptions();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\config\PropertySetter.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */