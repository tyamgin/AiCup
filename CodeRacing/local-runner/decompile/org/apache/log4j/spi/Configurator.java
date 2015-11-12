package org.apache.log4j.spi;

import java.net.URL;

public abstract interface Configurator
{
  public abstract void doConfigure(URL paramURL, LoggerRepository paramLoggerRepository);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\spi\Configurator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */