package com.google.common.io;

import java.io.IOException;

abstract class LineBuffer
{
  private StringBuilder line = new StringBuilder();
  private boolean sawReturn;
  
  protected void add(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = paramInt1;
    if ((this.sawReturn) && (paramInt2 > 0)) {
      if (finishLine(paramArrayOfChar[i] == '\n')) {
        i++;
      }
    }
    int j = i;
    int k = paramInt1 + paramInt2;
    while (i < k)
    {
      switch (paramArrayOfChar[i])
      {
      case '\r': 
        this.line.append(paramArrayOfChar, j, i - j);
        this.sawReturn = true;
        if (i + 1 < k) {
          if (finishLine(paramArrayOfChar[(i + 1)] == '\n')) {
            i++;
          }
        }
        j = i + 1;
        break;
      case '\n': 
        this.line.append(paramArrayOfChar, j, i - j);
        finishLine(true);
        j = i + 1;
        break;
      }
      i++;
    }
    this.line.append(paramArrayOfChar, j, paramInt1 + paramInt2 - j);
  }
  
  private boolean finishLine(boolean paramBoolean)
    throws IOException
  {
    handleLine(this.line.toString(), paramBoolean ? "\n" : this.sawReturn ? "\r" : paramBoolean ? "\r\n" : "");
    this.line = new StringBuilder();
    this.sawReturn = false;
    return paramBoolean;
  }
  
  protected void finish()
    throws IOException
  {
    if ((this.sawReturn) || (this.line.length() > 0)) {
      finishLine(false);
    }
  }
  
  protected abstract void handleLine(String paramString1, String paramString2)
    throws IOException;
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\LineBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */