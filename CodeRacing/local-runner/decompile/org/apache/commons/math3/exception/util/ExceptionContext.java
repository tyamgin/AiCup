package org.apache.commons.math3.exception.util;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExceptionContext
  implements Serializable
{
  private Throwable throwable;
  private List msgPatterns;
  private List msgArguments;
  private Map context;
  
  public ExceptionContext(Throwable paramThrowable)
  {
    this.throwable = paramThrowable;
    this.msgPatterns = new ArrayList();
    this.msgArguments = new ArrayList();
    this.context = new HashMap();
  }
  
  public void addMessage(Localizable paramLocalizable, Object... paramVarArgs)
  {
    this.msgPatterns.add(paramLocalizable);
    this.msgArguments.add(ArgUtils.flatten(paramVarArgs));
  }
  
  public String getMessage()
  {
    return getMessage(Locale.US);
  }
  
  public String getLocalizedMessage()
  {
    return getMessage(Locale.getDefault());
  }
  
  public String getMessage(Locale paramLocale)
  {
    return buildMessage(paramLocale, ": ");
  }
  
  private String buildMessage(Locale paramLocale, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    int j = this.msgPatterns.size();
    for (int k = 0; k < j; k++)
    {
      Localizable localLocalizable = (Localizable)this.msgPatterns.get(k);
      Object[] arrayOfObject = (Object[])this.msgArguments.get(k);
      MessageFormat localMessageFormat = new MessageFormat(localLocalizable.getLocalizedString(paramLocale), paramLocale);
      localStringBuilder.append(localMessageFormat.format(arrayOfObject));
      i++;
      if (i < j) {
        localStringBuilder.append(paramString);
      }
    }
    return localStringBuilder.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\math3\exception\util\ExceptionContext.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */