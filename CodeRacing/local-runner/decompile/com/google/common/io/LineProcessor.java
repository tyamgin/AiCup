package com.google.common.io;

import com.google.common.annotations.Beta;
import java.io.IOException;

@Beta
public abstract interface LineProcessor
{
  public abstract boolean processLine(String paramString)
    throws IOException;
  
  public abstract Object getResult();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\LineProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */