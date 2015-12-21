package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Beta
public final class HostAndPort
  implements Serializable
{
  private static final int NO_PORT = -1;
  private final String host;
  private final int port;
  private final boolean hasBracketlessColons;
  private static final Pattern BRACKET_PATTERN = Pattern.compile("^\\[(.*:.*)\\](?::(\\d*))?$");
  private static final long serialVersionUID = 0L;
  
  private HostAndPort(String paramString, int paramInt, boolean paramBoolean)
  {
    this.host = paramString;
    this.port = paramInt;
    this.hasBracketlessColons = paramBoolean;
  }
  
  public String getHostText()
  {
    return this.host;
  }
  
  public boolean hasPort()
  {
    return this.port >= 0;
  }
  
  public int getPort()
  {
    Preconditions.checkState(hasPort());
    return this.port;
  }
  
  public int getPortOrDefault(int paramInt)
  {
    return hasPort() ? this.port : paramInt;
  }
  
  public static HostAndPort fromParts(String paramString, int paramInt)
  {
    Preconditions.checkArgument(isValidPort(paramInt));
    HostAndPort localHostAndPort = fromString(paramString);
    Preconditions.checkArgument(!localHostAndPort.hasPort());
    return new HostAndPort(localHostAndPort.host, paramInt, localHostAndPort.hasBracketlessColons);
  }
  
  public static HostAndPort fromString(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    String str2 = null;
    boolean bool = false;
    String str1;
    if (paramString.startsWith("["))
    {
      Matcher localMatcher = BRACKET_PATTERN.matcher(paramString);
      Preconditions.checkArgument(localMatcher.matches(), "Invalid bracketed host/port: %s", new Object[] { paramString });
      str1 = localMatcher.group(1);
      str2 = localMatcher.group(2);
    }
    else
    {
      i = paramString.indexOf(':');
      if ((i >= 0) && (paramString.indexOf(':', i + 1) == -1))
      {
        str1 = paramString.substring(0, i);
        str2 = paramString.substring(i + 1);
      }
      else
      {
        str1 = paramString;
        bool = i >= 0;
      }
    }
    int i = -1;
    if (!Strings.isNullOrEmpty(str2))
    {
      Preconditions.checkArgument(!str2.startsWith("+"), "Unparseable port number: %s", new Object[] { paramString });
      try
      {
        i = Integer.parseInt(str2);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new IllegalArgumentException("Unparseable port number: " + paramString);
      }
      Preconditions.checkArgument(isValidPort(i), "Port number out of range: %s", new Object[] { paramString });
    }
    return new HostAndPort(str1, i, bool);
  }
  
  public HostAndPort withDefaultPort(int paramInt)
  {
    Preconditions.checkArgument(isValidPort(paramInt));
    if ((hasPort()) || (this.port == paramInt)) {
      return this;
    }
    return new HostAndPort(this.host, paramInt, this.hasBracketlessColons);
  }
  
  public HostAndPort requireBracketsForIPv6()
  {
    Preconditions.checkArgument(!this.hasBracketlessColons, "Possible bracketless IPv6 literal: %s", new Object[] { this.host });
    return this;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof HostAndPort))
    {
      HostAndPort localHostAndPort = (HostAndPort)paramObject;
      return (Objects.equal(this.host, localHostAndPort.host)) && (this.port == localHostAndPort.port) && (this.hasBracketlessColons == localHostAndPort.hasBracketlessColons);
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { this.host, Integer.valueOf(this.port), Boolean.valueOf(this.hasBracketlessColons) });
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(this.host.length() + 7);
    if (this.host.indexOf(':') >= 0) {
      localStringBuilder.append('[').append(this.host).append(']');
    } else {
      localStringBuilder.append(this.host);
    }
    if (hasPort()) {
      localStringBuilder.append(':').append(this.port);
    }
    return localStringBuilder.toString();
  }
  
  private static boolean isValidPort(int paramInt)
  {
    return (paramInt >= 0) && (paramInt <= 65535);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\net\HostAndPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */