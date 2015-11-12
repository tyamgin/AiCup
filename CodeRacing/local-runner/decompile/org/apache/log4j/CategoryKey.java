package org.apache.log4j;

class CategoryKey
{
  String name;
  int hashCache;
  
  CategoryKey(String paramString)
  {
    this.name = paramString;
    this.hashCache = paramString.hashCode();
  }
  
  public final int hashCode()
  {
    return this.hashCache;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject != null) && (CategoryKey.class == paramObject.getClass())) {
      return this.name.equals(((CategoryKey)paramObject).name);
    }
    return false;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\CategoryKey.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */