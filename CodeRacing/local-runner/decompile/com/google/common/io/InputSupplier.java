package com.google.common.io;

import java.io.IOException;

public abstract interface InputSupplier
{
  public abstract Object getInput()
    throws IOException;
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\InputSupplier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */