package org.apache.log4j.spi;

public class DefaultRepositorySelector
  implements RepositorySelector
{
  final LoggerRepository repository;
  
  public DefaultRepositorySelector(LoggerRepository paramLoggerRepository)
  {
    this.repository = paramLoggerRepository;
  }
  
  public LoggerRepository getLoggerRepository()
  {
    return this.repository;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\spi\DefaultRepositorySelector.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */