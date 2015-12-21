package org.apache.commons.io.output;

import java.io.Serializable;
import java.io.Writer;

public class StringBuilderWriter
  extends Writer
  implements Serializable
{
  private final StringBuilder builder;
  
  public StringBuilderWriter()
  {
    this.builder = new StringBuilder();
  }
  
  public StringBuilderWriter(int paramInt)
  {
    this.builder = new StringBuilder(paramInt);
  }
  
  public Writer append(char paramChar)
  {
    this.builder.append(paramChar);
    return this;
  }
  
  public Writer append(CharSequence paramCharSequence)
  {
    this.builder.append(paramCharSequence);
    return this;
  }
  
  public Writer append(CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    this.builder.append(paramCharSequence, paramInt1, paramInt2);
    return this;
  }
  
  public void close() {}
  
  public void flush() {}
  
  public void write(String paramString)
  {
    if (paramString != null) {
      this.builder.append(paramString);
    }
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramArrayOfChar != null) {
      this.builder.append(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public String toString()
  {
    return this.builder.toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\io\output\StringBuilderWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */