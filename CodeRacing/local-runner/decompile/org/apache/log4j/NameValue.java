package org.apache.log4j;

class NameValue
{
  String key;
  String value;
  
  public NameValue(String paramString1, String paramString2)
  {
    this.key = paramString1;
    this.value = paramString2;
  }
  
  public String toString()
  {
    return this.key + "=" + this.value;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\NameValue.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */