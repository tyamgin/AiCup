package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.net.InetAddress;
import java.text.ParseException;

@Beta
public final class HostSpecifier
{
  private final String canonicalForm;
  
  private HostSpecifier(String paramString)
  {
    this.canonicalForm = paramString;
  }
  
  public static HostSpecifier fromValid(String paramString)
  {
    HostAndPort localHostAndPort = HostAndPort.fromString(paramString);
    Preconditions.checkArgument(!localHostAndPort.hasPort());
    String str = localHostAndPort.getHostText();
    InetAddress localInetAddress = null;
    try
    {
      localInetAddress = InetAddresses.forString(str);
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    if (localInetAddress != null) {
      return new HostSpecifier(InetAddresses.toUriString(localInetAddress));
    }
    InternetDomainName localInternetDomainName = InternetDomainName.from(str);
    if (localInternetDomainName.hasPublicSuffix()) {
      return new HostSpecifier(localInternetDomainName.name());
    }
    throw new IllegalArgumentException("Domain name does not have a recognized public suffix: " + str);
  }
  
  public static HostSpecifier from(String paramString)
    throws ParseException
  {
    try
    {
      return fromValid(paramString);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      ParseException localParseException = new ParseException("Invalid host specifier: " + paramString, 0);
      localParseException.initCause(localIllegalArgumentException);
      throw localParseException;
    }
  }
  
  public static boolean isValid(String paramString)
  {
    try
    {
      fromValid(paramString);
      return true;
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof HostSpecifier))
    {
      HostSpecifier localHostSpecifier = (HostSpecifier)paramObject;
      return this.canonicalForm.equals(localHostSpecifier.canonicalForm);
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.canonicalForm.hashCode();
  }
  
  public String toString()
  {
    return this.canonicalForm;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\net\HostSpecifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */