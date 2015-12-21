package org.apache.log4j;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Vector;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.NullEnumeration;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

public class Category
  implements AppenderAttachable
{
  protected String name;
  protected volatile Level level;
  protected volatile Category parent;
  private static final String FQCN = Category.class.getName();
  protected ResourceBundle resourceBundle;
  protected LoggerRepository repository;
  AppenderAttachableImpl aai;
  protected boolean additive = true;
  
  protected Category(String paramString)
  {
    this.name = paramString;
  }
  
  public synchronized void addAppender(Appender paramAppender)
  {
    if (this.aai == null) {
      this.aai = new AppenderAttachableImpl();
    }
    this.aai.addAppender(paramAppender);
    this.repository.fireAddAppenderEvent(this, paramAppender);
  }
  
  public void callAppenders(LoggingEvent paramLoggingEvent)
  {
    int i = 0;
    for (Category localCategory = this; localCategory != null; localCategory = localCategory.parent) {
      synchronized (localCategory)
      {
        if (localCategory.aai != null) {
          i += localCategory.aai.appendLoopOnAppenders(paramLoggingEvent);
        }
        if (!localCategory.additive) {
          break;
        }
      }
    }
    if (i == 0) {
      this.repository.emitNoAppenderWarning(this);
    }
  }
  
  synchronized void closeNestedAppenders()
  {
    Enumeration localEnumeration = getAllAppenders();
    if (localEnumeration != null) {
      while (localEnumeration.hasMoreElements())
      {
        Appender localAppender = (Appender)localEnumeration.nextElement();
        if ((localAppender instanceof AppenderAttachable)) {
          localAppender.close();
        }
      }
    }
  }
  
  public void debug(Object paramObject)
  {
    if (this.repository.isDisabled(10000)) {
      return;
    }
    if (Level.DEBUG.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.DEBUG, paramObject, null);
    }
  }
  
  public void error(Object paramObject)
  {
    if (this.repository.isDisabled(40000)) {
      return;
    }
    if (Level.ERROR.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.ERROR, paramObject, null);
    }
  }
  
  public void error(Object paramObject, Throwable paramThrowable)
  {
    if (this.repository.isDisabled(40000)) {
      return;
    }
    if (Level.ERROR.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.ERROR, paramObject, paramThrowable);
    }
  }
  
  public void fatal(Object paramObject, Throwable paramThrowable)
  {
    if (this.repository.isDisabled(50000)) {
      return;
    }
    if (Level.FATAL.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.FATAL, paramObject, paramThrowable);
    }
  }
  
  protected void forcedLog(String paramString, Priority paramPriority, Object paramObject, Throwable paramThrowable)
  {
    callAppenders(new LoggingEvent(paramString, this, paramPriority, paramObject, paramThrowable));
  }
  
  public synchronized Enumeration getAllAppenders()
  {
    if (this.aai == null) {
      return NullEnumeration.getInstance();
    }
    return this.aai.getAllAppenders();
  }
  
  public Level getEffectiveLevel()
  {
    for (Category localCategory = this; localCategory != null; localCategory = localCategory.parent) {
      if (localCategory.level != null) {
        return localCategory.level;
      }
    }
    return null;
  }
  
  public final String getName()
  {
    return this.name;
  }
  
  public final Level getLevel()
  {
    return this.level;
  }
  
  public void info(Object paramObject)
  {
    if (this.repository.isDisabled(20000)) {
      return;
    }
    if (Level.INFO.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.INFO, paramObject, null);
    }
  }
  
  public void info(Object paramObject, Throwable paramThrowable)
  {
    if (this.repository.isDisabled(20000)) {
      return;
    }
    if (Level.INFO.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.INFO, paramObject, paramThrowable);
    }
  }
  
  public boolean isDebugEnabled()
  {
    if (this.repository.isDisabled(10000)) {
      return false;
    }
    return Level.DEBUG.isGreaterOrEqual(getEffectiveLevel());
  }
  
  public boolean isEnabledFor(Priority paramPriority)
  {
    if (this.repository.isDisabled(paramPriority.level)) {
      return false;
    }
    return paramPriority.isGreaterOrEqual(getEffectiveLevel());
  }
  
  public void log(String paramString, Priority paramPriority, Object paramObject, Throwable paramThrowable)
  {
    if (this.repository.isDisabled(paramPriority.level)) {
      return;
    }
    if (paramPriority.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(paramString, paramPriority, paramObject, paramThrowable);
    }
  }
  
  private void fireRemoveAppenderEvent(Appender paramAppender)
  {
    if (paramAppender != null) {
      if ((this.repository instanceof Hierarchy)) {
        ((Hierarchy)this.repository).fireRemoveAppenderEvent(this, paramAppender);
      } else if ((this.repository instanceof HierarchyEventListener)) {
        ((HierarchyEventListener)this.repository).removeAppenderEvent(this, paramAppender);
      }
    }
  }
  
  public synchronized void removeAllAppenders()
  {
    if (this.aai != null)
    {
      Vector localVector = new Vector();
      Enumeration localEnumeration = this.aai.getAllAppenders();
      while ((localEnumeration != null) && (localEnumeration.hasMoreElements())) {
        localVector.add(localEnumeration.nextElement());
      }
      this.aai.removeAllAppenders();
      localEnumeration = localVector.elements();
      while (localEnumeration.hasMoreElements()) {
        fireRemoveAppenderEvent((Appender)localEnumeration.nextElement());
      }
      this.aai = null;
    }
  }
  
  public void setAdditivity(boolean paramBoolean)
  {
    this.additive = paramBoolean;
  }
  
  final void setHierarchy(LoggerRepository paramLoggerRepository)
  {
    this.repository = paramLoggerRepository;
  }
  
  public void setLevel(Level paramLevel)
  {
    this.level = paramLevel;
  }
  
  public void setResourceBundle(ResourceBundle paramResourceBundle)
  {
    this.resourceBundle = paramResourceBundle;
  }
  
  public void warn(Object paramObject)
  {
    if (this.repository.isDisabled(30000)) {
      return;
    }
    if (Level.WARN.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.WARN, paramObject, null);
    }
  }
  
  public void warn(Object paramObject, Throwable paramThrowable)
  {
    if (this.repository.isDisabled(30000)) {
      return;
    }
    if (Level.WARN.isGreaterOrEqual(getEffectiveLevel())) {
      forcedLog(FQCN, Level.WARN, paramObject, paramThrowable);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\Category.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */