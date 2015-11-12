package org.apache.log4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.RendererSupport;
import org.apache.log4j.spi.ThrowableRenderer;
import org.apache.log4j.spi.ThrowableRendererSupport;

public class PropertyConfigurator
  implements Configurator
{
  protected Hashtable registry = new Hashtable(11);
  private LoggerRepository repository;
  protected LoggerFactory loggerFactory = new DefaultCategoryFactory();
  
  public void doConfigure(Properties paramProperties, LoggerRepository paramLoggerRepository)
  {
    this.repository = paramLoggerRepository;
    String str1 = paramProperties.getProperty("log4j.debug");
    if (str1 == null)
    {
      str1 = paramProperties.getProperty("log4j.configDebug");
      if (str1 != null) {
        LogLog.warn("[log4j.configDebug] is deprecated. Use [log4j.debug] instead.");
      }
    }
    if (str1 != null) {
      LogLog.setInternalDebugging(OptionConverter.toBoolean(str1, true));
    }
    String str2 = paramProperties.getProperty("log4j.reset");
    if ((str2 != null) && (OptionConverter.toBoolean(str2, false))) {
      paramLoggerRepository.resetConfiguration();
    }
    String str3 = OptionConverter.findAndSubst("log4j.threshold", paramProperties);
    if (str3 != null)
    {
      paramLoggerRepository.setThreshold(OptionConverter.toLevel(str3, Level.ALL));
      LogLog.debug("Hierarchy threshold set to [" + paramLoggerRepository.getThreshold() + "].");
    }
    configureRootCategory(paramProperties, paramLoggerRepository);
    configureLoggerFactory(paramProperties);
    parseCatsAndRenderers(paramProperties, paramLoggerRepository);
    LogLog.debug("Finished configuring.");
    this.registry.clear();
  }
  
  public void doConfigure(URL paramURL, LoggerRepository paramLoggerRepository)
  {
    Properties localProperties = new Properties();
    LogLog.debug("Reading configuration from URL " + paramURL);
    InputStream localInputStream = null;
    URLConnection localURLConnection = null;
    try
    {
      localURLConnection = paramURL.openConnection();
      localURLConnection.setUseCaches(false);
      localInputStream = localURLConnection.getInputStream();
      localProperties.load(localInputStream);
      if (localInputStream != null) {
        try
        {
          localInputStream.close();
        }
        catch (InterruptedIOException localInterruptedIOException1)
        {
          Thread.currentThread().interrupt();
        }
        catch (IOException localIOException1) {}catch (RuntimeException localRuntimeException1) {}
      }
      doConfigure(localProperties, paramLoggerRepository);
    }
    catch (Exception localException)
    {
      if (((localException instanceof InterruptedIOException)) || ((localException instanceof InterruptedException))) {
        Thread.currentThread().interrupt();
      }
      LogLog.error("Could not read configuration file from URL [" + paramURL + "].", localException);
      LogLog.error("Ignoring configuration file [" + paramURL + "].");
      return;
    }
    finally
    {
      if (localInputStream != null) {
        try
        {
          localInputStream.close();
        }
        catch (InterruptedIOException localInterruptedIOException3)
        {
          Thread.currentThread().interrupt();
        }
        catch (IOException localIOException3) {}catch (RuntimeException localRuntimeException3) {}
      }
    }
  }
  
  protected void configureLoggerFactory(Properties paramProperties)
  {
    String str = OptionConverter.findAndSubst("log4j.loggerFactory", paramProperties);
    if (str != null)
    {
      LogLog.debug("Setting category factory to [" + str + "].");
      this.loggerFactory = ((LoggerFactory)OptionConverter.instantiateByClassName(str, LoggerFactory.class, this.loggerFactory));
      PropertySetter.setProperties(this.loggerFactory, paramProperties, "log4j.factory.");
    }
  }
  
  void configureRootCategory(Properties paramProperties, LoggerRepository paramLoggerRepository)
  {
    String str1 = "log4j.rootLogger";
    String str2 = OptionConverter.findAndSubst("log4j.rootLogger", paramProperties);
    if (str2 == null)
    {
      str2 = OptionConverter.findAndSubst("log4j.rootCategory", paramProperties);
      str1 = "log4j.rootCategory";
    }
    if (str2 == null)
    {
      LogLog.debug("Could not find root logger information. Is this OK?");
    }
    else
    {
      Logger localLogger = paramLoggerRepository.getRootLogger();
      synchronized (localLogger)
      {
        parseCategory(paramProperties, localLogger, str1, "root", str2);
      }
    }
  }
  
  protected void parseCatsAndRenderers(Properties paramProperties, LoggerRepository paramLoggerRepository)
  {
    Enumeration localEnumeration = paramProperties.propertyNames();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      Object localObject1;
      Object localObject2;
      if ((str.startsWith("log4j.category.")) || (str.startsWith("log4j.logger.")))
      {
        localObject1 = null;
        if (str.startsWith("log4j.category.")) {
          localObject1 = str.substring("log4j.category.".length());
        } else if (str.startsWith("log4j.logger.")) {
          localObject1 = str.substring("log4j.logger.".length());
        }
        localObject2 = OptionConverter.findAndSubst(str, paramProperties);
        Logger localLogger = paramLoggerRepository.getLogger((String)localObject1, this.loggerFactory);
        synchronized (localLogger)
        {
          parseCategory(paramProperties, localLogger, str, (String)localObject1, (String)localObject2);
          parseAdditivityForLogger(paramProperties, localLogger, (String)localObject1);
        }
      }
      else if (str.startsWith("log4j.renderer."))
      {
        localObject1 = str.substring("log4j.renderer.".length());
        localObject2 = OptionConverter.findAndSubst(str, paramProperties);
        if ((paramLoggerRepository instanceof RendererSupport)) {
          RendererMap.addRenderer((RendererSupport)paramLoggerRepository, (String)localObject1, (String)localObject2);
        }
      }
      else if ((str.equals("log4j.throwableRenderer")) && ((paramLoggerRepository instanceof ThrowableRendererSupport)))
      {
        localObject1 = (ThrowableRenderer)OptionConverter.instantiateByKey(paramProperties, "log4j.throwableRenderer", ThrowableRenderer.class, null);
        if (localObject1 == null)
        {
          LogLog.error("Could not instantiate throwableRenderer.");
        }
        else
        {
          localObject2 = new PropertySetter(localObject1);
          ((PropertySetter)localObject2).setProperties(paramProperties, "log4j.throwableRenderer.");
          ((ThrowableRendererSupport)paramLoggerRepository).setThrowableRenderer((ThrowableRenderer)localObject1);
        }
      }
    }
  }
  
  void parseAdditivityForLogger(Properties paramProperties, Logger paramLogger, String paramString)
  {
    String str = OptionConverter.findAndSubst("log4j.additivity." + paramString, paramProperties);
    LogLog.debug("Handling log4j.additivity." + paramString + "=[" + str + "]");
    if ((str != null) && (!str.equals("")))
    {
      boolean bool = OptionConverter.toBoolean(str, true);
      LogLog.debug("Setting additivity for \"" + paramString + "\" to " + bool);
      paramLogger.setAdditivity(bool);
    }
  }
  
  void parseCategory(Properties paramProperties, Logger paramLogger, String paramString1, String paramString2, String paramString3)
  {
    LogLog.debug("Parsing for [" + paramString2 + "] with value=[" + paramString3 + "].");
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString3, ",");
    Object localObject;
    if ((!paramString3.startsWith(",")) && (!paramString3.equals("")))
    {
      if (!localStringTokenizer.hasMoreTokens()) {
        return;
      }
      localObject = localStringTokenizer.nextToken();
      LogLog.debug("Level token is [" + (String)localObject + "].");
      if (("inherited".equalsIgnoreCase((String)localObject)) || ("null".equalsIgnoreCase((String)localObject)))
      {
        if (paramString2.equals("root")) {
          LogLog.warn("The root logger cannot be set to null.");
        } else {
          paramLogger.setLevel(null);
        }
      }
      else {
        paramLogger.setLevel(OptionConverter.toLevel((String)localObject, Level.DEBUG));
      }
      LogLog.debug("Category " + paramString2 + " set to " + paramLogger.getLevel());
    }
    paramLogger.removeAllAppenders();
    while (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken().trim();
      if ((str != null) && (!str.equals(",")))
      {
        LogLog.debug("Parsing appender named \"" + str + "\".");
        localObject = parseAppender(paramProperties, str);
        if (localObject != null) {
          paramLogger.addAppender((Appender)localObject);
        }
      }
    }
  }
  
  Appender parseAppender(Properties paramProperties, String paramString)
  {
    Appender localAppender = registryGet(paramString);
    if (localAppender != null)
    {
      LogLog.debug("Appender \"" + paramString + "\" was already parsed.");
      return localAppender;
    }
    String str1 = "log4j.appender." + paramString;
    String str2 = str1 + ".layout";
    localAppender = (Appender)OptionConverter.instantiateByKey(paramProperties, str1, Appender.class, null);
    if (localAppender == null)
    {
      LogLog.error("Could not instantiate appender named \"" + paramString + "\".");
      return null;
    }
    localAppender.setName(paramString);
    if ((localAppender instanceof OptionHandler))
    {
      if (localAppender.requiresLayout())
      {
        localObject = (Layout)OptionConverter.instantiateByKey(paramProperties, str2, Layout.class, null);
        if (localObject != null)
        {
          localAppender.setLayout((Layout)localObject);
          LogLog.debug("Parsing layout options for \"" + paramString + "\".");
          PropertySetter.setProperties(localObject, paramProperties, str2 + ".");
          LogLog.debug("End of parsing for \"" + paramString + "\".");
        }
      }
      Object localObject = str1 + ".errorhandler";
      String str3 = OptionConverter.findAndSubst((String)localObject, paramProperties);
      if (str3 != null)
      {
        ErrorHandler localErrorHandler = (ErrorHandler)OptionConverter.instantiateByKey(paramProperties, (String)localObject, ErrorHandler.class, null);
        if (localErrorHandler != null)
        {
          localAppender.setErrorHandler(localErrorHandler);
          LogLog.debug("Parsing errorhandler options for \"" + paramString + "\".");
          parseErrorHandler(localErrorHandler, (String)localObject, paramProperties, this.repository);
          Properties localProperties = new Properties();
          String[] arrayOfString = { (String)localObject + "." + "root-ref", (String)localObject + "." + "logger-ref", (String)localObject + "." + "appender-ref" };
          Iterator localIterator = paramProperties.entrySet().iterator();
          while (localIterator.hasNext())
          {
            Map.Entry localEntry = (Map.Entry)localIterator.next();
            for (int i = 0; (i < arrayOfString.length) && (!arrayOfString[i].equals(localEntry.getKey())); i++) {}
            if (i == arrayOfString.length) {
              localProperties.put(localEntry.getKey(), localEntry.getValue());
            }
          }
          PropertySetter.setProperties(localErrorHandler, localProperties, (String)localObject + ".");
          LogLog.debug("End of errorhandler parsing for \"" + paramString + "\".");
        }
      }
      PropertySetter.setProperties(localAppender, paramProperties, str1 + ".");
      LogLog.debug("Parsed \"" + paramString + "\" options.");
    }
    parseAppenderFilters(paramProperties, paramString, localAppender);
    registryPut(localAppender);
    return localAppender;
  }
  
  private void parseErrorHandler(ErrorHandler paramErrorHandler, String paramString, Properties paramProperties, LoggerRepository paramLoggerRepository)
  {
    boolean bool = OptionConverter.toBoolean(OptionConverter.findAndSubst(paramString + "root-ref", paramProperties), false);
    if (bool) {
      paramErrorHandler.setLogger(paramLoggerRepository.getRootLogger());
    }
    String str = OptionConverter.findAndSubst(paramString + "logger-ref", paramProperties);
    if (str != null)
    {
      localObject = this.loggerFactory == null ? paramLoggerRepository.getLogger(str) : paramLoggerRepository.getLogger(str, this.loggerFactory);
      paramErrorHandler.setLogger((Logger)localObject);
    }
    Object localObject = OptionConverter.findAndSubst(paramString + "appender-ref", paramProperties);
    if (localObject != null)
    {
      Appender localAppender = parseAppender(paramProperties, (String)localObject);
      if (localAppender != null) {
        paramErrorHandler.setBackupAppender(localAppender);
      }
    }
  }
  
  void parseAppenderFilters(Properties paramProperties, String paramString, Appender paramAppender)
  {
    String str1 = "log4j.appender." + paramString + ".filter.";
    int i = str1.length();
    Hashtable localHashtable = new Hashtable();
    Enumeration localEnumeration1 = paramProperties.keys();
    String str2 = "";
    Object localObject2;
    Object localObject3;
    Object localObject4;
    while (localEnumeration1.hasMoreElements())
    {
      localObject1 = (String)localEnumeration1.nextElement();
      if (((String)localObject1).startsWith(str1))
      {
        int j = ((String)localObject1).indexOf('.', i);
        localObject2 = localObject1;
        if (j != -1)
        {
          localObject2 = ((String)localObject1).substring(0, j);
          str2 = ((String)localObject1).substring(j + 1);
        }
        localObject3 = (Vector)localHashtable.get(localObject2);
        if (localObject3 == null)
        {
          localObject3 = new Vector();
          localHashtable.put(localObject2, localObject3);
        }
        if (j != -1)
        {
          localObject4 = OptionConverter.findAndSubst((String)localObject1, paramProperties);
          ((Vector)localObject3).add(new NameValue(str2, (String)localObject4));
        }
      }
    }
    Object localObject1 = new SortedKeyEnumeration(localHashtable);
    while (((Enumeration)localObject1).hasMoreElements())
    {
      String str3 = (String)((Enumeration)localObject1).nextElement();
      localObject2 = paramProperties.getProperty(str3);
      if (localObject2 != null)
      {
        LogLog.debug("Filter key: [" + str3 + "] class: [" + paramProperties.getProperty(str3) + "] props: " + localHashtable.get(str3));
        localObject3 = (Filter)OptionConverter.instantiateByClassName((String)localObject2, Filter.class, null);
        if (localObject3 != null)
        {
          localObject4 = new PropertySetter(localObject3);
          Vector localVector = (Vector)localHashtable.get(str3);
          Enumeration localEnumeration2 = localVector.elements();
          while (localEnumeration2.hasMoreElements())
          {
            NameValue localNameValue = (NameValue)localEnumeration2.nextElement();
            ((PropertySetter)localObject4).setProperty(localNameValue.key, localNameValue.value);
          }
          ((PropertySetter)localObject4).activate();
          LogLog.debug("Adding filter of type [" + localObject3.getClass() + "] to appender named [" + paramAppender.getName() + "].");
          paramAppender.addFilter((Filter)localObject3);
        }
      }
      else
      {
        LogLog.warn("Missing class definition for filter: [" + str3 + "]");
      }
    }
  }
  
  void registryPut(Appender paramAppender)
  {
    this.registry.put(paramAppender.getName(), paramAppender);
  }
  
  Appender registryGet(String paramString)
  {
    return (Appender)this.registry.get(paramString);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\PropertyConfigurator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */