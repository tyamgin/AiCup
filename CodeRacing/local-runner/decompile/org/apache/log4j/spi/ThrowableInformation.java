package org.apache.log4j.spi;

import java.io.Serializable;
import org.apache.log4j.Category;

public class ThrowableInformation
  implements Serializable
{
  private transient Throwable throwable;
  private transient Category category;
  
  public ThrowableInformation(Throwable paramThrowable, Category paramCategory)
  {
    this.throwable = paramThrowable;
    this.category = paramCategory;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\spi\ThrowableInformation.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */