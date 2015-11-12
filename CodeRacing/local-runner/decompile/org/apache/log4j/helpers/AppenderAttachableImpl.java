package org.apache.log4j.helpers;

import java.util.Enumeration;
import java.util.Vector;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;

public class AppenderAttachableImpl
  implements AppenderAttachable
{
  protected Vector appenderList;
  
  public void addAppender(Appender paramAppender)
  {
    if (paramAppender == null) {
      return;
    }
    if (this.appenderList == null) {
      this.appenderList = new Vector(1);
    }
    if (!this.appenderList.contains(paramAppender)) {
      this.appenderList.addElement(paramAppender);
    }
  }
  
  public int appendLoopOnAppenders(LoggingEvent paramLoggingEvent)
  {
    int i = 0;
    if (this.appenderList != null)
    {
      i = this.appenderList.size();
      for (int j = 0; j < i; j++)
      {
        Appender localAppender = (Appender)this.appenderList.elementAt(j);
        localAppender.doAppend(paramLoggingEvent);
      }
    }
    return i;
  }
  
  public Enumeration getAllAppenders()
  {
    if (this.appenderList == null) {
      return null;
    }
    return this.appenderList.elements();
  }
  
  public void removeAllAppenders()
  {
    if (this.appenderList != null)
    {
      int i = this.appenderList.size();
      for (int j = 0; j < i; j++)
      {
        Appender localAppender = (Appender)this.appenderList.elementAt(j);
        localAppender.close();
      }
      this.appenderList.removeAllElements();
      this.appenderList = null;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\helpers\AppenderAttachableImpl.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */