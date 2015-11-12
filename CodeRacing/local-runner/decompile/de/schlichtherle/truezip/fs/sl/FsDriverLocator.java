package de.schlichtherle.truezip.fs.sl;

import de.schlichtherle.truezip.fs.FsDriver;
import de.schlichtherle.truezip.fs.FsDriverProvider;
import de.schlichtherle.truezip.fs.FsScheme;
import de.schlichtherle.truezip.fs.spi.FsDriverService;
import de.schlichtherle.truezip.util.HashMaps;
import de.schlichtherle.truezip.util.ServiceLocator;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FsDriverLocator
  implements FsDriverProvider
{
  public static final FsDriverLocator SINGLETON = new FsDriverLocator();
  
  public Map get()
  {
    return Boot.DRIVERS;
  }
  
  private static final class Boot
  {
    static final Map DRIVERS;
    
    static
    {
      Logger localLogger = Logger.getLogger(FsDriverLocator.class.getName(), FsDriverLocator.class.getName());
      Iterator localIterator1 = new ServiceLocator(FsDriverLocator.class.getClassLoader()).getServices(FsDriverService.class);
      TreeMap localTreeMap = new TreeMap();
      if (!localIterator1.hasNext()) {
        localLogger.log(Level.WARNING, "null", FsDriverService.class);
      }
      Map.Entry localEntry;
      FsScheme localFsScheme;
      FsDriver localFsDriver1;
      while (localIterator1.hasNext())
      {
        localObject = (FsDriverService)localIterator1.next();
        localLogger.log(Level.CONFIG, "located", localObject);
        localIterator2 = ((FsDriverService)localObject).get().entrySet().iterator();
        while (localIterator2.hasNext())
        {
          localEntry = (Map.Entry)localIterator2.next();
          localFsScheme = (FsScheme)localEntry.getKey();
          localFsDriver1 = (FsDriver)localEntry.getValue();
          if ((null != localFsScheme) && (null != localFsDriver1))
          {
            FsDriver localFsDriver2 = (FsDriver)localTreeMap.put(localFsScheme, localFsDriver1);
            if ((null != localFsDriver2) && (localFsDriver2.getPriority() > localFsDriver1.getPriority())) {
              localTreeMap.put(localFsScheme, localFsDriver2);
            }
          }
        }
      }
      Object localObject = new LinkedHashMap(HashMaps.initialCapacity(localTreeMap.size()));
      Iterator localIterator2 = localTreeMap.entrySet().iterator();
      while (localIterator2.hasNext())
      {
        localEntry = (Map.Entry)localIterator2.next();
        localFsScheme = (FsScheme)localEntry.getKey();
        localFsDriver1 = (FsDriver)localEntry.getValue();
        localLogger.log(Level.CONFIG, "mapping", new Object[] { localFsScheme, localFsDriver1 });
        ((Map)localObject).put(localFsScheme, localFsDriver1);
      }
      DRIVERS = Collections.unmodifiableMap((Map)localObject);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\sl\FsDriverLocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */