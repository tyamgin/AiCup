package net.lingala.zip4j.model;

public class UnzipParameters
{
  private boolean ignoreReadOnlyFileAttribute;
  private boolean ignoreHiddenFileAttribute;
  private boolean ignoreArchiveFileAttribute;
  private boolean ignoreSystemFileAttribute;
  private boolean ignoreAllFileAttributes;
  private boolean ignoreDateTimeAttributes;
  
  public boolean isIgnoreReadOnlyFileAttribute()
  {
    return this.ignoreReadOnlyFileAttribute;
  }
  
  public boolean isIgnoreHiddenFileAttribute()
  {
    return this.ignoreHiddenFileAttribute;
  }
  
  public boolean isIgnoreArchiveFileAttribute()
  {
    return this.ignoreArchiveFileAttribute;
  }
  
  public boolean isIgnoreSystemFileAttribute()
  {
    return this.ignoreSystemFileAttribute;
  }
  
  public boolean isIgnoreAllFileAttributes()
  {
    return this.ignoreAllFileAttributes;
  }
  
  public boolean isIgnoreDateTimeAttributes()
  {
    return this.ignoreDateTimeAttributes;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\model\UnzipParameters.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */