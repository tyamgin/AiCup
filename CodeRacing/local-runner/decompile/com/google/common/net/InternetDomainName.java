package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;

@Beta
@GwtCompatible
public final class InternetDomainName
{
  private static final CharMatcher DOTS_MATCHER = CharMatcher.anyOf(".。．｡");
  private static final Splitter DOT_SPLITTER = Splitter.on('.');
  private static final Joiner DOT_JOINER = Joiner.on('.');
  private static final int NO_PUBLIC_SUFFIX_FOUND = -1;
  private static final String DOT_REGEX = "\\.";
  private static final int MAX_PARTS = 127;
  private static final int MAX_LENGTH = 253;
  private static final int MAX_DOMAIN_PART_LENGTH = 63;
  private final String name;
  private final ImmutableList parts;
  private final int publicSuffixIndex;
  private static final CharMatcher DASH_MATCHER = CharMatcher.anyOf("-_");
  private static final CharMatcher PART_CHAR_MATCHER = CharMatcher.JAVA_LETTER_OR_DIGIT.or(DASH_MATCHER);
  
  InternetDomainName(String paramString)
  {
    paramString = Ascii.toLowerCase(DOTS_MATCHER.replaceFrom(paramString, '.'));
    if (paramString.endsWith(".")) {
      paramString = paramString.substring(0, paramString.length() - 1);
    }
    Preconditions.checkArgument(paramString.length() <= 253, "Domain name too long: '%s':", new Object[] { paramString });
    this.name = paramString;
    this.parts = ImmutableList.copyOf(DOT_SPLITTER.split(paramString));
    Preconditions.checkArgument(this.parts.size() <= 127, "Domain has too many parts: '%s'", new Object[] { paramString });
    Preconditions.checkArgument(validateSyntax(this.parts), "Not a valid domain name: '%s'", new Object[] { paramString });
    this.publicSuffixIndex = findPublicSuffix();
  }
  
  private int findPublicSuffix()
  {
    int i = this.parts.size();
    for (int j = 0; j < i; j++)
    {
      String str = DOT_JOINER.join(this.parts.subList(j, i));
      if (TldPatterns.EXACT.contains(str)) {
        return j;
      }
      if (TldPatterns.EXCLUDED.contains(str)) {
        return j + 1;
      }
      if (matchesWildcardPublicSuffix(str)) {
        return j;
      }
    }
    return -1;
  }
  
  @Deprecated
  public static InternetDomainName fromLenient(String paramString)
  {
    return from(paramString);
  }
  
  public static InternetDomainName from(String paramString)
  {
    return new InternetDomainName((String)Preconditions.checkNotNull(paramString));
  }
  
  private static boolean validateSyntax(List paramList)
  {
    int i = paramList.size() - 1;
    if (!validatePart((String)paramList.get(i), true)) {
      return false;
    }
    for (int j = 0; j < i; j++)
    {
      String str = (String)paramList.get(j);
      if (!validatePart(str, false)) {
        return false;
      }
    }
    return true;
  }
  
  private static boolean validatePart(String paramString, boolean paramBoolean)
  {
    if ((paramString.length() < 1) || (paramString.length() > 63)) {
      return false;
    }
    String str = CharMatcher.ASCII.retainFrom(paramString);
    if (!PART_CHAR_MATCHER.matchesAllOf(str)) {
      return false;
    }
    if ((DASH_MATCHER.matches(paramString.charAt(0))) || (DASH_MATCHER.matches(paramString.charAt(paramString.length() - 1)))) {
      return false;
    }
    return (!paramBoolean) || (!CharMatcher.DIGIT.matches(paramString.charAt(0)));
  }
  
  public String name()
  {
    return this.name;
  }
  
  public ImmutableList parts()
  {
    return this.parts;
  }
  
  public boolean isPublicSuffix()
  {
    return this.publicSuffixIndex == 0;
  }
  
  public boolean hasPublicSuffix()
  {
    return this.publicSuffixIndex != -1;
  }
  
  public InternetDomainName publicSuffix()
  {
    return hasPublicSuffix() ? ancestor(this.publicSuffixIndex) : null;
  }
  
  public boolean isUnderPublicSuffix()
  {
    return this.publicSuffixIndex > 0;
  }
  
  public boolean isTopPrivateDomain()
  {
    return this.publicSuffixIndex == 1;
  }
  
  public InternetDomainName topPrivateDomain()
  {
    if (isTopPrivateDomain()) {
      return this;
    }
    Preconditions.checkState(isUnderPublicSuffix(), "Not under a public suffix: %s", new Object[] { this.name });
    return ancestor(this.publicSuffixIndex - 1);
  }
  
  public boolean hasParent()
  {
    return this.parts.size() > 1;
  }
  
  public InternetDomainName parent()
  {
    Preconditions.checkState(hasParent(), "Domain '%s' has no parent", new Object[] { this.name });
    return ancestor(1);
  }
  
  private InternetDomainName ancestor(int paramInt)
  {
    return from(DOT_JOINER.join(this.parts.subList(paramInt, this.parts.size())));
  }
  
  public InternetDomainName child(String paramString)
  {
    return from((String)Preconditions.checkNotNull(paramString) + "." + this.name);
  }
  
  @Deprecated
  public static boolean isValidLenient(String paramString)
  {
    return isValid(paramString);
  }
  
  public static boolean isValid(String paramString)
  {
    try
    {
      from(paramString);
      return true;
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return false;
  }
  
  private static boolean matchesWildcardPublicSuffix(String paramString)
  {
    String[] arrayOfString = paramString.split("\\.", 2);
    return (arrayOfString.length == 2) && (TldPatterns.UNDER.contains(arrayOfString[1]));
  }
  
  public String toString()
  {
    return Objects.toStringHelper(this).add("name", this.name).toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof InternetDomainName))
    {
      InternetDomainName localInternetDomainName = (InternetDomainName)paramObject;
      return this.name.equals(localInternetDomainName.name);
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.name.hashCode();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\net\InternetDomainName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */