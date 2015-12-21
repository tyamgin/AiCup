package org.apache.log4j;

import org.apache.log4j.spi.OptionHandler;

public abstract class Layout
  implements OptionHandler
{
  public static final String LINE_SEP = System.getProperty("line.separator");
  public static final int LINE_SEP_LEN = LINE_SEP.length();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\Layout.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */