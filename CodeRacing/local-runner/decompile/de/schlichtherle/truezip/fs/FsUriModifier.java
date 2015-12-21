package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.util.QuotedUriSyntaxException;
import de.schlichtherle.truezip.util.UriBuilder;
import java.net.URI;
import java.net.URISyntaxException;

public enum FsUriModifier
{
  NULL,  CANONICALIZE;
  
  abstract URI modify(URI paramURI, PostFix paramPostFix)
    throws URISyntaxException;
  
  public static abstract enum PostFix
  {
    PATH,  MOUNT_POINT,  ENTRY_NAME;
    
    abstract URI modify(URI paramURI)
      throws URISyntaxException;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\fs\FsUriModifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */