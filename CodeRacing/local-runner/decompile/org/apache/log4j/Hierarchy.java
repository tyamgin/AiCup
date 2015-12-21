package org.apache.log4j;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RendererSupport;
import org.apache.log4j.spi.ThrowableRenderer;
import org.apache.log4j.spi.ThrowableRendererSupport;

public class Hierarchy
  implements LoggerRepository, RendererSupport, ThrowableRendererSupport
{
  private LoggerFactory defaultFactory;
  private Vector listeners = new Vector(1);
  Hashtable ht = new Hashtable();
  Logger root;
  RendererMap rendererMap;
  int thresholdInt;
  Level threshold;
  boolean emittedNoAppenderWarning = false;
  boolean emittedNoResourceBundleWarning = false;
  private ThrowableRenderer throwableRenderer = null;
  
  public Hierarchy(Logger paramLogger)
  {
    this.root = paramLogger;
    setThreshold(Level.ALL);
    this.root.setHierarchy(this);
    this.rendererMap = new RendererMap();
    this.defaultFactory = new DefaultCategoryFactory();
  }
  
  public void emitNoAppenderWarning(Category paramCategory)
  {
    if (!this.emittedNoAppenderWarning)
    {
      LogLog.warn("No appenders could be found for logger (" + paramCategory.getName() + ").");
      LogLog.warn("Please initialize the log4j system properly.");
      LogLog.warn("See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.");
      this.emittedNoAppenderWarning = true;
    }
  }
  
  public void setThreshold(Level paramLevel)
  {
    if (paramLevel != null)
    {
      this.thresholdInt = paramLevel.level;
      this.threshold = paramLevel;
    }
  }
  
  public void fireAddAppenderEvent(Category paramCategory, Appender paramAppender)
  {
    if (this.listeners != null)
    {
      int i = this.listeners.size();
      for (int j = 0; j < i; j++)
      {
        HierarchyEventListener localHierarchyEventListener = (HierarchyEventListener)this.listeners.elementAt(j);
        localHierarchyEventListener.addAppenderEvent(paramCategory, paramAppender);
      }
    }
  }
  
  void fireRemoveAppenderEvent(Category paramCategory, Appender paramAppender)
  {
    if (this.listeners != null)
    {
      int i = this.listeners.size();
      for (int j = 0; j < i; j++)
      {
        HierarchyEventListener localHierarchyEventListener = (HierarchyEventListener)this.listeners.elementAt(j);
        localHierarchyEventListener.removeAppenderEvent(paramCategory, paramAppender);
      }
    }
  }
  
  public Level getThreshold()
  {
    return this.threshold;
  }
  
  public Logger getLogger(String paramString)
  {
    return getLogger(paramString, this.defaultFactory);
  }
  
  public Logger getLogger(String paramString, LoggerFactory paramLoggerFactory)
  {
    CategoryKey localCategoryKey = new CategoryKey(paramString);
    synchronized (this.ht)
    {
      Object localObject1 = this.ht.get(localCategoryKey);
      Logger localLogger;
      if (localObject1 == null)
      {
        localLogger = paramLoggerFactory.makeNewLoggerInstance(paramString);
        localLogger.setHierarchy(this);
        this.ht.put(localCategoryKey, localLogger);
        updateParents(localLogger);
        return localLogger;
      }
      if ((localObject1 instanceof Logger)) {
        return (Logger)localObject1;
      }
      if ((localObject1 instanceof ProvisionNode))
      {
        localLogger = paramLoggerFactory.makeNewLoggerInstance(paramString);
        localLogger.setHierarchy(this);
        this.ht.put(localCategoryKey, localLogger);
        updateChildren((ProvisionNode)localObject1, localLogger);
        updateParents(localLogger);
        return localLogger;
      }
      return null;
    }
  }
  
  public Enumeration getCurrentLoggers()
  {
    Vector localVector = new Vector(this.ht.size());
    Enumeration localEnumeration = this.ht.elements();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject = localEnumeration.nextElement();
      if ((localObject instanceof Logger)) {
        localVector.addElement(localObject);
      }
    }
    return localVector.elements();
  }
  
  public Logger getRootLogger()
  {
    return this.root;
  }
  
  public boolean isDisabled(int paramInt)
  {
    return this.thresholdInt > paramInt;
  }
  
  public void resetConfiguration()
  {
    getRootLogger().setLevel(Level.DEBUG);
    this.root.setResourceBundle(null);
    setThreshold(Level.ALL);
    synchronized (this.ht)
    {
      shutdown();
      Enumeration localEnumeration = getCurrentLoggers();
      while (localEnumeration.hasMoreElements())
      {
        Logger localLogger = (Logger)localEnumeration.nextElement();
        localLogger.setLevel(null);
        localLogger.setAdditivity(true);
        localLogger.setResourceBundle(null);
      }
    }
    this.rendererMap.clear();
    this.throwableRenderer = null;
  }
  
  public void setRenderer(Class paramClass, ObjectRenderer paramObjectRenderer)
  {
    this.rendererMap.put(paramClass, paramObjectRenderer);
  }
  
  public void setThrowableRenderer(ThrowableRenderer paramThrowableRenderer)
  {
    this.throwableRenderer = paramThrowableRenderer;
  }
  
  public void shutdown()
  {
    Logger localLogger1 = getRootLogger();
    localLogger1.closeNestedAppenders();
    synchronized (this.ht)
    {
      Enumeration localEnumeration = getCurrentLoggers();
      Logger localLogger2;
      while (localEnumeration.hasMoreElements())
      {
        localLogger2 = (Logger)localEnumeration.nextElement();
        localLogger2.closeNestedAppenders();
      }
      localLogger1.removeAllAppenders();
      localEnumeration = getCurrentLoggers();
      while (localEnumeration.hasMoreElements())
      {
        localLogger2 = (Logger)localEnumeration.nextElement();
        localLogger2.removeAllAppenders();
      }
    }
  }
  
  private final void updateParents(Logger paramLogger)
  {
    String str1 = paramLogger.name;
    int i = str1.length();
    int j = 0;
    for (int k = str1.lastIndexOf('.', i - 1); k >= 0; k = str1.lastIndexOf('.', k - 1))
    {
      String str2 = str1.substring(0, k);
      CategoryKey localCategoryKey = new CategoryKey(str2);
      Object localObject1 = this.ht.get(localCategoryKey);
      Object localObject2;
      if (localObject1 == null)
      {
        localObject2 = new ProvisionNode(paramLogger);
        this.ht.put(localCategoryKey, localObject2);
      }
      else
      {
        if ((localObject1 instanceof Category))
        {
          j = 1;
          paramLogger.parent = ((Category)localObject1);
          break;
        }
        if ((localObject1 instanceof ProvisionNode))
        {
          ((ProvisionNode)localObject1).addElement(paramLogger);
        }
        else
        {
          localObject2 = new IllegalStateException("unexpected object type " + localObject1.getClass() + " in ht.");
          ((Exception)localObject2).printStackTrace();
        }
      }
    }
    if (j == 0) {
      paramLogger.parent = this.root;
    }
  }
  
  private final void updateChildren(ProvisionNode paramProvisionNode, Logger paramLogger)
  {
    int i = paramProvisionNode.size();
    for (int j = 0; j < i; j++)
    {
      Logger localLogger = (Logger)paramProvisionNode.elementAt(j);
      if (!localLogger.parent.name.startsWith(paramLogger.name))
      {
        paramLogger.parent = localLogger.parent;
        localLogger.parent = paramLogger;
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\Hierarchy.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */