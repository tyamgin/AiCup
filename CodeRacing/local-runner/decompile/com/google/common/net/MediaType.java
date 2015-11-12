package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@Beta
@GwtCompatible
public final class MediaType
{
  private static final String CHARSET_ATTRIBUTE = "charset";
  private static final ImmutableListMultimap UTF_8_CONSTANT_PARAMETERS = ImmutableListMultimap.of("charset", Ascii.toLowerCase(Charsets.UTF_8.name()));
  private static final CharMatcher TOKEN_MATCHER = CharMatcher.ASCII.and(CharMatcher.JAVA_ISO_CONTROL.negate()).and(CharMatcher.isNot(' ')).and(CharMatcher.noneOf("()<>@,;:\\\"/[]?="));
  private static final CharMatcher QUOTED_TEXT_MATCHER = CharMatcher.ASCII.and(CharMatcher.noneOf("\"\\\r"));
  private static final CharMatcher LINEAR_WHITE_SPACE = CharMatcher.anyOf(" \t\r\n");
  private static final String APPLICATION_TYPE = "application";
  private static final String AUDIO_TYPE = "audio";
  private static final String IMAGE_TYPE = "image";
  private static final String TEXT_TYPE = "text";
  private static final String VIDEO_TYPE = "video";
  private static final String WILDCARD = "*";
  public static final MediaType ANY_TYPE = createConstant("*", "*");
  public static final MediaType ANY_TEXT_TYPE = createConstant("text", "*");
  public static final MediaType ANY_IMAGE_TYPE = createConstant("image", "*");
  public static final MediaType ANY_AUDIO_TYPE = createConstant("audio", "*");
  public static final MediaType ANY_VIDEO_TYPE = createConstant("video", "*");
  public static final MediaType ANY_APPLICATION_TYPE = createConstant("application", "*");
  public static final MediaType CACHE_MANIFEST_UTF_8 = createConstantUtf8("text", "cache-manifest");
  public static final MediaType CSS_UTF_8 = createConstantUtf8("text", "css");
  public static final MediaType CSV_UTF_8 = createConstantUtf8("text", "csv");
  public static final MediaType HTML_UTF_8 = createConstantUtf8("text", "html");
  public static final MediaType I_CALENDAR_UTF_8 = createConstantUtf8("text", "calendar");
  public static final MediaType PLAIN_TEXT_UTF_8 = createConstantUtf8("text", "plain");
  public static final MediaType TEXT_JAVASCRIPT_UTF_8 = createConstantUtf8("text", "javascript");
  public static final MediaType VCARD_UTF_8 = createConstantUtf8("text", "vcard");
  public static final MediaType WML_UTF_8 = createConstantUtf8("text", "vnd.wap.wml");
  public static final MediaType XML_UTF_8 = createConstantUtf8("text", "xml");
  public static final MediaType BMP = createConstant("image", "bmp");
  public static final MediaType GIF = createConstant("image", "gif");
  public static final MediaType ICO = createConstant("image", "vnd.microsoft.icon");
  public static final MediaType JPEG = createConstant("image", "jpeg");
  public static final MediaType PNG = createConstant("image", "png");
  public static final MediaType SVG_UTF_8 = createConstantUtf8("image", "svg+xml");
  public static final MediaType TIFF = createConstant("image", "tiff");
  public static final MediaType WEBP = createConstant("image", "webp");
  public static final MediaType MP4_AUDIO = createConstant("audio", "mp4");
  public static final MediaType MPEG_AUDIO = createConstant("audio", "mpeg");
  public static final MediaType OGG_AUDIO = createConstant("audio", "ogg");
  public static final MediaType WEBM_AUDIO = createConstant("audio", "webm");
  public static final MediaType MP4_VIDEO = createConstant("video", "mp4");
  public static final MediaType MPEG_VIDEO = createConstant("video", "mpeg");
  public static final MediaType OGG_VIDEO = createConstant("video", "ogg");
  public static final MediaType QUICKTIME = createConstant("video", "quicktime");
  public static final MediaType WEBM_VIDEO = createConstant("video", "webm");
  public static final MediaType WMV = createConstant("video", "x-ms-wmv");
  public static final MediaType APPLICATION_XML_UTF_8 = createConstantUtf8("application", "xml");
  public static final MediaType ATOM_UTF_8 = createConstantUtf8("application", "atom+xml");
  public static final MediaType BZIP2 = createConstant("application", "x-bzip2");
  public static final MediaType FORM_DATA = createConstant("application", "x-www-form-urlencoded");
  public static final MediaType APPLICATION_BINARY = createConstant("application", "binary");
  public static final MediaType GZIP = createConstant("application", "x-gzip");
  public static final MediaType JAVASCRIPT_UTF_8 = createConstantUtf8("application", "javascript");
  public static final MediaType JSON_UTF_8 = createConstantUtf8("application", "json");
  public static final MediaType KML = createConstant("application", "vnd.google-earth.kml+xml");
  public static final MediaType KMZ = createConstant("application", "vnd.google-earth.kmz");
  public static final MediaType MBOX = createConstant("application", "mbox");
  public static final MediaType MICROSOFT_EXCEL = createConstant("application", "vnd.ms-excel");
  public static final MediaType MICROSOFT_POWERPOINT = createConstant("application", "vnd.ms-powerpoint");
  public static final MediaType MICROSOFT_WORD = createConstant("application", "msword");
  public static final MediaType OCTET_STREAM = createConstant("application", "octet-stream");
  public static final MediaType OGG_CONTAINER = createConstant("application", "ogg");
  public static final MediaType OOXML_DOCUMENT = createConstant("application", "vnd.openxmlformats-officedocument.wordprocessingml.document");
  public static final MediaType OOXML_PRESENTATION = createConstant("application", "vnd.openxmlformats-officedocument.presentationml.presentation");
  public static final MediaType OOXML_SHEET = createConstant("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
  public static final MediaType OPENDOCUMENT_GRAPHICS = createConstant("application", "vnd.oasis.opendocument.graphics");
  public static final MediaType OPENDOCUMENT_PRESENTATION = createConstant("application", "vnd.oasis.opendocument.presentation");
  public static final MediaType OPENDOCUMENT_SPREADSHEET = createConstant("application", "vnd.oasis.opendocument.spreadsheet");
  public static final MediaType OPENDOCUMENT_TEXT = createConstant("application", "vnd.oasis.opendocument.text");
  public static final MediaType PDF = createConstant("application", "pdf");
  public static final MediaType POSTSCRIPT = createConstant("application", "postscript");
  public static final MediaType RDF_XML_UTF_8 = createConstantUtf8("application", "rdf+xml");
  public static final MediaType RTF_UTF_8 = createConstantUtf8("application", "rtf");
  public static final MediaType SHOCKWAVE_FLASH = createConstant("application", "x-shockwave-flash");
  public static final MediaType SKETCHUP = createConstant("application", "vnd.sketchup.skp");
  public static final MediaType TAR = createConstant("application", "x-tar");
  public static final MediaType XHTML_UTF_8 = createConstantUtf8("application", "xhtml+xml");
  public static final MediaType XRD_UTF_8 = createConstantUtf8("application", "xrd+xml");
  public static final MediaType ZIP = createConstant("application", "zip");
  private static final ImmutableMap KNOWN_TYPES = new ImmutableMap.Builder().put(ANY_TYPE, ANY_TYPE).put(ANY_TEXT_TYPE, ANY_TEXT_TYPE).put(ANY_IMAGE_TYPE, ANY_IMAGE_TYPE).put(ANY_AUDIO_TYPE, ANY_AUDIO_TYPE).put(ANY_VIDEO_TYPE, ANY_VIDEO_TYPE).put(ANY_APPLICATION_TYPE, ANY_APPLICATION_TYPE).put(CACHE_MANIFEST_UTF_8, CACHE_MANIFEST_UTF_8).put(CSS_UTF_8, CSS_UTF_8).put(CSV_UTF_8, CSV_UTF_8).put(HTML_UTF_8, HTML_UTF_8).put(I_CALENDAR_UTF_8, I_CALENDAR_UTF_8).put(PLAIN_TEXT_UTF_8, PLAIN_TEXT_UTF_8).put(TEXT_JAVASCRIPT_UTF_8, TEXT_JAVASCRIPT_UTF_8).put(VCARD_UTF_8, VCARD_UTF_8).put(WML_UTF_8, WML_UTF_8).put(XML_UTF_8, XML_UTF_8).put(BMP, BMP).put(GIF, GIF).put(ICO, ICO).put(JPEG, JPEG).put(PNG, PNG).put(SVG_UTF_8, SVG_UTF_8).put(TIFF, TIFF).put(WEBP, WEBP).put(MP4_AUDIO, MP4_AUDIO).put(MPEG_AUDIO, MPEG_AUDIO).put(OGG_AUDIO, OGG_AUDIO).put(WEBM_AUDIO, WEBM_AUDIO).put(MP4_VIDEO, MP4_VIDEO).put(MPEG_VIDEO, MPEG_VIDEO).put(OGG_VIDEO, OGG_VIDEO).put(QUICKTIME, QUICKTIME).put(WEBM_VIDEO, WEBM_VIDEO).put(WMV, WMV).put(APPLICATION_XML_UTF_8, APPLICATION_XML_UTF_8).put(ATOM_UTF_8, ATOM_UTF_8).put(BZIP2, BZIP2).put(FORM_DATA, FORM_DATA).put(APPLICATION_BINARY, APPLICATION_BINARY).put(GZIP, GZIP).put(JAVASCRIPT_UTF_8, JAVASCRIPT_UTF_8).put(JSON_UTF_8, JSON_UTF_8).put(KML, KML).put(KMZ, KMZ).put(MBOX, MBOX).put(MICROSOFT_EXCEL, MICROSOFT_EXCEL).put(MICROSOFT_POWERPOINT, MICROSOFT_POWERPOINT).put(MICROSOFT_WORD, MICROSOFT_WORD).put(OCTET_STREAM, OCTET_STREAM).put(OGG_CONTAINER, OGG_CONTAINER).put(OOXML_DOCUMENT, OOXML_DOCUMENT).put(OOXML_PRESENTATION, OOXML_PRESENTATION).put(OOXML_SHEET, OOXML_SHEET).put(OPENDOCUMENT_GRAPHICS, OPENDOCUMENT_GRAPHICS).put(OPENDOCUMENT_PRESENTATION, OPENDOCUMENT_PRESENTATION).put(OPENDOCUMENT_SPREADSHEET, OPENDOCUMENT_SPREADSHEET).put(OPENDOCUMENT_TEXT, OPENDOCUMENT_TEXT).put(PDF, PDF).put(POSTSCRIPT, POSTSCRIPT).put(RDF_XML_UTF_8, RDF_XML_UTF_8).put(RTF_UTF_8, RTF_UTF_8).put(SHOCKWAVE_FLASH, SHOCKWAVE_FLASH).put(SKETCHUP, SKETCHUP).put(TAR, TAR).put(XHTML_UTF_8, XHTML_UTF_8).put(XRD_UTF_8, XRD_UTF_8).put(ZIP, ZIP).build();
  private final String type;
  private final String subtype;
  private final ImmutableListMultimap parameters;
  private static final Joiner.MapJoiner PARAMETER_JOINER = Joiner.on("; ").withKeyValueSeparator("=");
  
  private MediaType(String paramString1, String paramString2, ImmutableListMultimap paramImmutableListMultimap)
  {
    this.type = paramString1;
    this.subtype = paramString2;
    this.parameters = paramImmutableListMultimap;
  }
  
  private static MediaType createConstant(String paramString1, String paramString2)
  {
    return new MediaType(paramString1, paramString2, ImmutableListMultimap.of());
  }
  
  private static MediaType createConstantUtf8(String paramString1, String paramString2)
  {
    return new MediaType(paramString1, paramString2, UTF_8_CONSTANT_PARAMETERS);
  }
  
  public String type()
  {
    return this.type;
  }
  
  public String subtype()
  {
    return this.subtype;
  }
  
  public ImmutableListMultimap parameters()
  {
    return this.parameters;
  }
  
  private Map parametersAsMap()
  {
    Maps.transformValues(this.parameters.asMap(), new Function()
    {
      public ImmutableMultiset apply(Collection paramAnonymousCollection)
      {
        return ImmutableMultiset.copyOf(paramAnonymousCollection);
      }
    });
  }
  
  public Optional charset()
  {
    ImmutableSet localImmutableSet = ImmutableSet.copyOf(this.parameters.get("charset"));
    switch (localImmutableSet.size())
    {
    case 0: 
      return Optional.absent();
    case 1: 
      return Optional.of(Charset.forName((String)Iterables.getOnlyElement(localImmutableSet)));
    }
    throw new IllegalStateException("Multiple charset values defined: " + localImmutableSet);
  }
  
  public MediaType withoutParameters()
  {
    return this.parameters.isEmpty() ? this : create(this.type, this.subtype);
  }
  
  public MediaType withParameters(Multimap paramMultimap)
  {
    return create(this.type, this.subtype, paramMultimap);
  }
  
  public MediaType withParameter(String paramString1, String paramString2)
  {
    Preconditions.checkNotNull(paramString1);
    Preconditions.checkNotNull(paramString2);
    String str1 = normalizeToken(paramString1);
    ImmutableListMultimap.Builder localBuilder = ImmutableListMultimap.builder();
    Object localObject = this.parameters.entries().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
      String str2 = (String)localEntry.getKey();
      if (!str1.equals(str2)) {
        localBuilder.put(str2, localEntry.getValue());
      }
    }
    localBuilder.put(str1, normalizeParameterValue(str1, paramString2));
    localObject = new MediaType(this.type, this.subtype, localBuilder.build());
    return (MediaType)Objects.firstNonNull(KNOWN_TYPES.get(localObject), localObject);
  }
  
  public MediaType withCharset(Charset paramCharset)
  {
    Preconditions.checkNotNull(paramCharset);
    return withParameter("charset", paramCharset.name());
  }
  
  public boolean hasWildcard()
  {
    return ("*".equals(this.type)) || ("*".equals(this.subtype));
  }
  
  public boolean is(MediaType paramMediaType)
  {
    return ((paramMediaType.type.equals("*")) || (paramMediaType.type.equals(this.type))) && ((paramMediaType.subtype.equals("*")) || (paramMediaType.subtype.equals(this.subtype))) && (this.parameters.entries().containsAll(paramMediaType.parameters.entries()));
  }
  
  public static MediaType create(String paramString1, String paramString2)
  {
    return create(paramString1, paramString2, ImmutableListMultimap.of());
  }
  
  static MediaType createApplicationType(String paramString)
  {
    return create("application", paramString);
  }
  
  static MediaType createAudioType(String paramString)
  {
    return create("audio", paramString);
  }
  
  static MediaType createImageType(String paramString)
  {
    return create("image", paramString);
  }
  
  static MediaType createTextType(String paramString)
  {
    return create("text", paramString);
  }
  
  static MediaType createVideoType(String paramString)
  {
    return create("video", paramString);
  }
  
  private static MediaType create(String paramString1, String paramString2, Multimap paramMultimap)
  {
    Preconditions.checkNotNull(paramString1);
    Preconditions.checkNotNull(paramString2);
    Preconditions.checkNotNull(paramMultimap);
    String str1 = normalizeToken(paramString1);
    String str2 = normalizeToken(paramString2);
    Preconditions.checkArgument((!"*".equals(str1)) || ("*".equals(str2)), "A wildcard type cannot be used with a non-wildcard subtype");
    ImmutableListMultimap.Builder localBuilder = ImmutableListMultimap.builder();
    Object localObject = paramMultimap.entries().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
      String str3 = normalizeToken((String)localEntry.getKey());
      localBuilder.put(str3, normalizeParameterValue(str3, (String)localEntry.getValue()));
    }
    localObject = new MediaType(str1, str2, localBuilder.build());
    return (MediaType)Objects.firstNonNull(KNOWN_TYPES.get(localObject), localObject);
  }
  
  private static String normalizeToken(String paramString)
  {
    Preconditions.checkArgument(TOKEN_MATCHER.matchesAllOf(paramString));
    return Ascii.toLowerCase(paramString);
  }
  
  private static String normalizeParameterValue(String paramString1, String paramString2)
  {
    return "charset".equals(paramString1) ? Ascii.toLowerCase(paramString2) : paramString2;
  }
  
  public static MediaType parse(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    Tokenizer localTokenizer = new Tokenizer(paramString);
    try
    {
      String str1 = localTokenizer.consumeToken(TOKEN_MATCHER);
      localTokenizer.consumeCharacter('/');
      String str2 = localTokenizer.consumeToken(TOKEN_MATCHER);
      ImmutableListMultimap.Builder localBuilder = ImmutableListMultimap.builder();
      while (localTokenizer.hasMore())
      {
        localTokenizer.consumeCharacter(';');
        localTokenizer.consumeTokenIfPresent(LINEAR_WHITE_SPACE);
        String str3 = localTokenizer.consumeToken(TOKEN_MATCHER);
        localTokenizer.consumeCharacter('=');
        String str4;
        if ('"' == localTokenizer.previewChar())
        {
          localTokenizer.consumeCharacter('"');
          StringBuilder localStringBuilder = new StringBuilder();
          while ('"' != localTokenizer.previewChar()) {
            if ('\\' == localTokenizer.previewChar())
            {
              localTokenizer.consumeCharacter('\\');
              localStringBuilder.append(localTokenizer.consumeCharacter(CharMatcher.ASCII));
            }
            else
            {
              localStringBuilder.append(localTokenizer.consumeToken(QUOTED_TEXT_MATCHER));
            }
          }
          str4 = localStringBuilder.toString();
          localTokenizer.consumeCharacter('"');
        }
        else
        {
          str4 = localTokenizer.consumeToken(TOKEN_MATCHER);
        }
        localBuilder.put(str3, str4);
      }
      return create(str1, str2, localBuilder.build());
    }
    catch (IllegalStateException localIllegalStateException)
    {
      throw new IllegalArgumentException(localIllegalStateException);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof MediaType))
    {
      MediaType localMediaType = (MediaType)paramObject;
      return (this.type.equals(localMediaType.type)) && (this.subtype.equals(localMediaType.subtype)) && (parametersAsMap().equals(localMediaType.parametersAsMap()));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { this.type, this.subtype, parametersAsMap() });
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append(this.type).append('/').append(this.subtype);
    if (!this.parameters.isEmpty())
    {
      localStringBuilder.append("; ");
      ListMultimap localListMultimap = Multimaps.transformValues(this.parameters, new Function()
      {
        public String apply(String paramAnonymousString)
        {
          return MediaType.TOKEN_MATCHER.matchesAllOf(paramAnonymousString) ? paramAnonymousString : MediaType.escapeAndQuote(paramAnonymousString);
        }
      });
      PARAMETER_JOINER.appendTo(localStringBuilder, localListMultimap.entries());
    }
    return localStringBuilder.toString();
  }
  
  private static String escapeAndQuote(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramString.length() + 16).append('"');
    for (char c : paramString.toCharArray())
    {
      if ((c == '\r') || (c == '\\') || (c == '"')) {
        localStringBuilder.append('\\');
      }
      localStringBuilder.append(c);
    }
    return '"';
  }
  
  private static final class Tokenizer
  {
    final String input;
    int position = 0;
    
    Tokenizer(String paramString)
    {
      this.input = paramString;
    }
    
    String consumeTokenIfPresent(CharMatcher paramCharMatcher)
    {
      Preconditions.checkState(hasMore());
      int i = this.position;
      this.position = paramCharMatcher.negate().indexIn(this.input, i);
      return hasMore() ? this.input.substring(i, this.position) : this.input.substring(i);
    }
    
    String consumeToken(CharMatcher paramCharMatcher)
    {
      int i = this.position;
      String str = consumeTokenIfPresent(paramCharMatcher);
      Preconditions.checkState(this.position != i);
      return str;
    }
    
    char consumeCharacter(CharMatcher paramCharMatcher)
    {
      Preconditions.checkState(hasMore());
      char c = previewChar();
      Preconditions.checkState(paramCharMatcher.matches(c));
      this.position += 1;
      return c;
    }
    
    char consumeCharacter(char paramChar)
    {
      Preconditions.checkState(hasMore());
      Preconditions.checkState(previewChar() == paramChar);
      this.position += 1;
      return paramChar;
    }
    
    char previewChar()
    {
      Preconditions.checkState(hasMore());
      return this.input.charAt(this.position);
    }
    
    boolean hasMore()
    {
      return (this.position >= 0) && (this.position < this.input.length());
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\net\MediaType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */