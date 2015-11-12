package com.google.gson.stream;

import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.bind.JsonTreeReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class JsonReader
  implements Closeable
{
  private static final char[] NON_EXECUTE_PREFIX = ")]}'\n".toCharArray();
  private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
  private static final int PEEKED_NONE = 0;
  private static final int PEEKED_BEGIN_OBJECT = 1;
  private static final int PEEKED_END_OBJECT = 2;
  private static final int PEEKED_BEGIN_ARRAY = 3;
  private static final int PEEKED_END_ARRAY = 4;
  private static final int PEEKED_TRUE = 5;
  private static final int PEEKED_FALSE = 6;
  private static final int PEEKED_NULL = 7;
  private static final int PEEKED_SINGLE_QUOTED = 8;
  private static final int PEEKED_DOUBLE_QUOTED = 9;
  private static final int PEEKED_UNQUOTED = 10;
  private static final int PEEKED_BUFFERED = 11;
  private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
  private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
  private static final int PEEKED_UNQUOTED_NAME = 14;
  private static final int PEEKED_LONG = 15;
  private static final int PEEKED_NUMBER = 16;
  private static final int PEEKED_EOF = 17;
  private static final int NUMBER_CHAR_NONE = 0;
  private static final int NUMBER_CHAR_SIGN = 1;
  private static final int NUMBER_CHAR_DIGIT = 2;
  private static final int NUMBER_CHAR_DECIMAL = 3;
  private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
  private static final int NUMBER_CHAR_EXP_E = 5;
  private static final int NUMBER_CHAR_EXP_SIGN = 6;
  private static final int NUMBER_CHAR_EXP_DIGIT = 7;
  private final Reader in;
  private boolean lenient = false;
  private final char[] buffer = new char['Ѐ'];
  private int pos = 0;
  private int limit = 0;
  private int lineNumber = 0;
  private int lineStart = 0;
  private int peeked = 0;
  private long peekedLong;
  private int peekedNumberLength;
  private String peekedString;
  private int[] stack = new int[32];
  private int stackSize = 0;
  private String[] pathNames;
  private int[] pathIndices;
  
  public JsonReader(Reader paramReader)
  {
    this.stack[(this.stackSize++)] = 6;
    this.pathNames = new String[32];
    this.pathIndices = new int[32];
    if (paramReader == null) {
      throw new NullPointerException("in == null");
    }
    this.in = paramReader;
  }
  
  public final void setLenient(boolean paramBoolean)
  {
    this.lenient = paramBoolean;
  }
  
  public final boolean isLenient()
  {
    return this.lenient;
  }
  
  public void beginArray()
    throws IOException
  {
    int i = this.peeked;
    if (i == 0) {
      i = doPeek();
    }
    if (i == 3)
    {
      push(1);
      this.pathIndices[(this.stackSize - 1)] = 0;
      this.peeked = 0;
    }
    else
    {
      throw new IllegalStateException("Expected BEGIN_ARRAY but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
    }
  }
  
  public void endArray()
    throws IOException
  {
    int i = this.peeked;
    if (i == 0) {
      i = doPeek();
    }
    if (i == 4)
    {
      this.stackSize -= 1;
      this.pathIndices[(this.stackSize - 1)] += 1;
      this.peeked = 0;
    }
    else
    {
      throw new IllegalStateException("Expected END_ARRAY but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
    }
  }
  
  public void beginObject()
    throws IOException
  {
    int i = this.peeked;
    if (i == 0) {
      i = doPeek();
    }
    if (i == 1)
    {
      push(3);
      this.peeked = 0;
    }
    else
    {
      throw new IllegalStateException("Expected BEGIN_OBJECT but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
    }
  }
  
  public void endObject()
    throws IOException
  {
    int i = this.peeked;
    if (i == 0) {
      i = doPeek();
    }
    if (i == 2)
    {
      this.stackSize -= 1;
      this.pathNames[this.stackSize] = null;
      this.pathIndices[(this.stackSize - 1)] += 1;
      this.peeked = 0;
    }
    else
    {
      throw new IllegalStateException("Expected END_OBJECT but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
    }
  }
  
  public boolean hasNext()
    throws IOException
  {
    int i = this.peeked;
    if (i == 0) {
      i = doPeek();
    }
    return (i != 2) && (i != 4);
  }
  
  public JsonToken peek()
    throws IOException
  {
    int i = this.peeked;
    if (i == 0) {
      i = doPeek();
    }
    switch (i)
    {
    case 1: 
      return JsonToken.BEGIN_OBJECT;
    case 2: 
      return JsonToken.END_OBJECT;
    case 3: 
      return JsonToken.BEGIN_ARRAY;
    case 4: 
      return JsonToken.END_ARRAY;
    case 12: 
    case 13: 
    case 14: 
      return JsonToken.NAME;
    case 5: 
    case 6: 
      return JsonToken.BOOLEAN;
    case 7: 
      return JsonToken.NULL;
    case 8: 
    case 9: 
    case 10: 
    case 11: 
      return JsonToken.STRING;
    case 15: 
    case 16: 
      return JsonToken.NUMBER;
    case 17: 
      return JsonToken.END_DOCUMENT;
    }
    throw new AssertionError();
  }
  
  private int doPeek()
    throws IOException
  {
    int i = this.stack[(this.stackSize - 1)];
    if (i == 1)
    {
      this.stack[(this.stackSize - 1)] = 2;
    }
    else if (i == 2)
    {
      j = nextNonWhitespace(true);
      switch (j)
      {
      case 93: 
        return this.peeked = 4;
      case 59: 
        checkLenient();
      case 44: 
        break;
      default: 
        throw syntaxError("Unterminated array");
      }
    }
    else
    {
      if ((i == 3) || (i == 5))
      {
        this.stack[(this.stackSize - 1)] = 4;
        if (i == 5)
        {
          j = nextNonWhitespace(true);
          switch (j)
          {
          case 125: 
            return this.peeked = 2;
          case 59: 
            checkLenient();
          case 44: 
            break;
          default: 
            throw syntaxError("Unterminated object");
          }
        }
        j = nextNonWhitespace(true);
        switch (j)
        {
        case 34: 
          return this.peeked = 13;
        case 39: 
          checkLenient();
          return this.peeked = 12;
        case 125: 
          if (i != 5) {
            return this.peeked = 2;
          }
          throw syntaxError("Expected name");
        }
        checkLenient();
        this.pos -= 1;
        if (isLiteral((char)j)) {
          return this.peeked = 14;
        }
        throw syntaxError("Expected name");
      }
      if (i == 4)
      {
        this.stack[(this.stackSize - 1)] = 5;
        j = nextNonWhitespace(true);
        switch (j)
        {
        case 58: 
          break;
        case 61: 
          checkLenient();
          if (((this.pos < this.limit) || (fillBuffer(1))) && (this.buffer[this.pos] == '>')) {
            this.pos += 1;
          }
          break;
        default: 
          throw syntaxError("Expected ':'");
        }
      }
      else if (i == 6)
      {
        if (this.lenient) {
          consumeNonExecutePrefix();
        }
        this.stack[(this.stackSize - 1)] = 7;
      }
      else if (i == 7)
      {
        j = nextNonWhitespace(false);
        if (j == -1) {
          return this.peeked = 17;
        }
        checkLenient();
        this.pos -= 1;
      }
      else if (i == 8)
      {
        throw new IllegalStateException("JsonReader is closed");
      }
    }
    int j = nextNonWhitespace(true);
    switch (j)
    {
    case 93: 
      if (i == 1) {
        return this.peeked = 4;
      }
    case 44: 
    case 59: 
      if ((i == 1) || (i == 2))
      {
        checkLenient();
        this.pos -= 1;
        return this.peeked = 7;
      }
      throw syntaxError("Unexpected value");
    case 39: 
      checkLenient();
      return this.peeked = 8;
    case 34: 
      if (this.stackSize == 1) {
        checkLenient();
      }
      return this.peeked = 9;
    case 91: 
      return this.peeked = 3;
    case 123: 
      return this.peeked = 1;
    }
    this.pos -= 1;
    if (this.stackSize == 1) {
      checkLenient();
    }
    int k = peekKeyword();
    if (k != 0) {
      return k;
    }
    k = peekNumber();
    if (k != 0) {
      return k;
    }
    if (!isLiteral(this.buffer[this.pos])) {
      throw syntaxError("Expected value");
    }
    checkLenient();
    return this.peeked = 10;
  }
  
  private int peekKeyword()
    throws IOException
  {
    int i = this.buffer[this.pos];
    String str1;
    String str2;
    int j;
    if ((i == 116) || (i == 84))
    {
      str1 = "true";
      str2 = "TRUE";
      j = 5;
    }
    else if ((i == 102) || (i == 70))
    {
      str1 = "false";
      str2 = "FALSE";
      j = 6;
    }
    else if ((i == 110) || (i == 78))
    {
      str1 = "null";
      str2 = "NULL";
      j = 7;
    }
    else
    {
      return 0;
    }
    int k = str1.length();
    for (int m = 1; m < k; m++)
    {
      if ((this.pos + m >= this.limit) && (!fillBuffer(m + 1))) {
        return 0;
      }
      i = this.buffer[(this.pos + m)];
      if ((i != str1.charAt(m)) && (i != str2.charAt(m))) {
        return 0;
      }
    }
    if (((this.pos + k < this.limit) || (fillBuffer(k + 1))) && (isLiteral(this.buffer[(this.pos + k)]))) {
      return 0;
    }
    this.pos += k;
    return this.peeked = j;
  }
  
  private int peekNumber()
    throws IOException
  {
    char[] arrayOfChar = this.buffer;
    int i = this.pos;
    int j = this.limit;
    long l1 = 0L;
    int k = 0;
    int m = 1;
    int n = 0;
    for (int i1 = 0;; i1++)
    {
      if (i + i1 == j)
      {
        if (i1 == arrayOfChar.length) {
          return 0;
        }
        if (!fillBuffer(i1 + 1)) {
          break;
        }
        i = this.pos;
        j = this.limit;
      }
      char c = arrayOfChar[(i + i1)];
      switch (c)
      {
      case '-': 
        if (n == 0)
        {
          k = 1;
          n = 1;
        }
        else if (n == 5)
        {
          n = 6;
        }
        else
        {
          return 0;
        }
        break;
      case '+': 
        if (n == 5) {
          n = 6;
        } else {
          return 0;
        }
        break;
      case 'E': 
      case 'e': 
        if ((n == 2) || (n == 4)) {
          n = 5;
        } else {
          return 0;
        }
        break;
      case '.': 
        if (n == 2) {
          n = 3;
        } else {
          return 0;
        }
        break;
      default: 
        if ((c < '0') || (c > '9'))
        {
          if (!isLiteral(c)) {
            break label372;
          }
          return 0;
        }
        if ((n == 1) || (n == 0))
        {
          l1 = -(c - '0');
          n = 2;
        }
        else if (n == 2)
        {
          if (l1 == 0L) {
            return 0;
          }
          long l2 = l1 * 10L - (c - '0');
          m &= ((l1 > -922337203685477580L) || ((l1 == -922337203685477580L) && (l2 < l1)) ? 1 : 0);
          l1 = l2;
        }
        else if (n == 3)
        {
          n = 4;
        }
        else if ((n == 5) || (n == 6))
        {
          n = 7;
        }
        break;
      }
    }
    label372:
    if ((n == 2) && (m != 0) && ((l1 != Long.MIN_VALUE) || (k != 0)))
    {
      this.peekedLong = (k != 0 ? l1 : -l1);
      this.pos += i1;
      return this.peeked = 15;
    }
    if ((n == 2) || (n == 4) || (n == 7))
    {
      this.peekedNumberLength = i1;
      return this.peeked = 16;
    }
    return 0;
  }
  
  private boolean isLiteral(char paramChar)
    throws IOException
  {
    switch (paramChar)
    {
    case '#': 
    case '/': 
    case ';': 
    case '=': 
    case '\\': 
      checkLenient();
    case '\t': 
    case '\n': 
    case '\f': 
    case '\r': 
    case ' ': 
    case ',': 
    case ':': 
    case '[': 
    case ']': 
    case '{': 
    case '}': 
      return false;
    }
    return true;
  }
  
  public String nextName()
    throws IOException
  {
    int i = this.peeked;
    if (i == 0) {
      i = doPeek();
    }
    String str;
    if (i == 14) {
      str = nextUnquotedValue();
    } else if (i == 12) {
      str = nextQuotedValue('\'');
    } else if (i == 13) {
      str = nextQuotedValue('"');
    } else {
      throw new IllegalStateException("Expected a name but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
    }
    this.peeked = 0;
    this.pathNames[(this.stackSize - 1)] = str;
    return str;
  }
  
  public String nextString()
    throws IOException
  {
    int i = this.peeked;
    if (i == 0) {
      i = doPeek();
    }
    String str;
    if (i == 10)
    {
      str = nextUnquotedValue();
    }
    else if (i == 8)
    {
      str = nextQuotedValue('\'');
    }
    else if (i == 9)
    {
      str = nextQuotedValue('"');
    }
    else if (i == 11)
    {
      str = this.peekedString;
      this.peekedString = null;
    }
    else if (i == 15)
    {
      str = Long.toString(this.peekedLong);
    }
    else if (i == 16)
    {
      str = new String(this.buffer, this.pos, this.peekedNumberLength);
      this.pos += this.peekedNumberLength;
    }
    else
    {
      throw new IllegalStateException("Expected a string but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
    }
    this.peeked = 0;
    this.pathIndices[(this.stackSize - 1)] += 1;
    return str;
  }
  
  public boolean nextBoolean()
    throws IOException
  {
    int i = this.peeked;
    if (i == 0) {
      i = doPeek();
    }
    if (i == 5)
    {
      this.peeked = 0;
      this.pathIndices[(this.stackSize - 1)] += 1;
      return true;
    }
    if (i == 6)
    {
      this.peeked = 0;
      this.pathIndices[(this.stackSize - 1)] += 1;
      return false;
    }
    throw new IllegalStateException("Expected a boolean but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
  }
  
  public void nextNull()
    throws IOException
  {
    int i = this.peeked;
    if (i == 0) {
      i = doPeek();
    }
    if (i == 7)
    {
      this.peeked = 0;
      this.pathIndices[(this.stackSize - 1)] += 1;
    }
    else
    {
      throw new IllegalStateException("Expected null but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
    }
  }
  
  public double nextDouble()
    throws IOException
  {
    int i = this.peeked;
    if (i == 0) {
      i = doPeek();
    }
    if (i == 15)
    {
      this.peeked = 0;
      this.pathIndices[(this.stackSize - 1)] += 1;
      return this.peekedLong;
    }
    if (i == 16)
    {
      this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
      this.pos += this.peekedNumberLength;
    }
    else if ((i == 8) || (i == 9))
    {
      this.peekedString = nextQuotedValue(i == 8 ? '\'' : '"');
    }
    else if (i == 10)
    {
      this.peekedString = nextUnquotedValue();
    }
    else if (i != 11)
    {
      throw new IllegalStateException("Expected a double but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
    }
    this.peeked = 11;
    double d = Double.parseDouble(this.peekedString);
    if ((!this.lenient) && ((Double.isNaN(d)) || (Double.isInfinite(d)))) {
      throw new MalformedJsonException("JSON forbids NaN and infinities: " + d + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
    }
    this.peekedString = null;
    this.peeked = 0;
    this.pathIndices[(this.stackSize - 1)] += 1;
    return d;
  }
  
  public long nextLong()
    throws IOException
  {
    int i = this.peeked;
    if (i == 0) {
      i = doPeek();
    }
    if (i == 15)
    {
      this.peeked = 0;
      this.pathIndices[(this.stackSize - 1)] += 1;
      return this.peekedLong;
    }
    if (i == 16)
    {
      this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
      this.pos += this.peekedNumberLength;
    }
    else if ((i == 8) || (i == 9))
    {
      this.peekedString = nextQuotedValue(i == 8 ? '\'' : '"');
      try
      {
        long l1 = Long.parseLong(this.peekedString);
        this.peeked = 0;
        this.pathIndices[(this.stackSize - 1)] += 1;
        return l1;
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    else
    {
      throw new IllegalStateException("Expected a long but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
    }
    this.peeked = 11;
    double d = Double.parseDouble(this.peekedString);
    long l2 = d;
    if (l2 != d) {
      throw new NumberFormatException("Expected a long but was " + this.peekedString + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
    }
    this.peekedString = null;
    this.peeked = 0;
    this.pathIndices[(this.stackSize - 1)] += 1;
    return l2;
  }
  
  private String nextQuotedValue(char paramChar)
    throws IOException
  {
    char[] arrayOfChar = this.buffer;
    StringBuilder localStringBuilder = new StringBuilder();
    for (;;)
    {
      int i = this.pos;
      int j = this.limit;
      int k = i;
      while (i < j)
      {
        char c = arrayOfChar[(i++)];
        if (c == paramChar)
        {
          this.pos = i;
          localStringBuilder.append(arrayOfChar, k, i - k - 1);
          return localStringBuilder.toString();
        }
        if (c == '\\')
        {
          this.pos = i;
          localStringBuilder.append(arrayOfChar, k, i - k - 1);
          localStringBuilder.append(readEscapeCharacter());
          i = this.pos;
          j = this.limit;
          k = i;
        }
        else if (c == '\n')
        {
          this.lineNumber += 1;
          this.lineStart = i;
        }
      }
      localStringBuilder.append(arrayOfChar, k, i - k);
      this.pos = i;
      if (!fillBuffer(1)) {
        throw syntaxError("Unterminated string");
      }
    }
  }
  
  private String nextUnquotedValue()
    throws IOException
  {
    StringBuilder localStringBuilder = null;
    int i = 0;
    for (;;)
    {
      if (this.pos + i < this.limit)
      {
        switch (this.buffer[(this.pos + i)])
        {
        case '#': 
        case '/': 
        case ';': 
        case '=': 
        case '\\': 
          checkLenient();
        case '\t': 
        case '\n': 
        case '\f': 
        case '\r': 
        case ' ': 
        case ',': 
        case ':': 
        case '[': 
        case ']': 
        case '{': 
        case '}': 
          break;
        default: 
          i++;
          break;
        }
      }
      else if (i < this.buffer.length)
      {
        if (!fillBuffer(i + 1)) {
          break;
        }
      }
      else
      {
        if (localStringBuilder == null) {
          localStringBuilder = new StringBuilder();
        }
        localStringBuilder.append(this.buffer, this.pos, i);
        this.pos += i;
        i = 0;
        if (!fillBuffer(1)) {
          break;
        }
      }
    }
    String str;
    if (localStringBuilder == null)
    {
      str = new String(this.buffer, this.pos, i);
    }
    else
    {
      localStringBuilder.append(this.buffer, this.pos, i);
      str = localStringBuilder.toString();
    }
    this.pos += i;
    return str;
  }
  
  private void skipQuotedValue(char paramChar)
    throws IOException
  {
    char[] arrayOfChar = this.buffer;
    do
    {
      int i = this.pos;
      int j = this.limit;
      while (i < j)
      {
        char c = arrayOfChar[(i++)];
        if (c == paramChar)
        {
          this.pos = i;
          return;
        }
        if (c == '\\')
        {
          this.pos = i;
          readEscapeCharacter();
          i = this.pos;
          j = this.limit;
        }
        else if (c == '\n')
        {
          this.lineNumber += 1;
          this.lineStart = i;
        }
      }
      this.pos = i;
    } while (fillBuffer(1));
    throw syntaxError("Unterminated string");
  }
  
  private void skipUnquotedValue()
    throws IOException
  {
    do
    {
      for (int i = 0; this.pos + i < this.limit; i++) {
        switch (this.buffer[(this.pos + i)])
        {
        case '#': 
        case '/': 
        case ';': 
        case '=': 
        case '\\': 
          checkLenient();
        case '\t': 
        case '\n': 
        case '\f': 
        case '\r': 
        case ' ': 
        case ',': 
        case ':': 
        case '[': 
        case ']': 
        case '{': 
        case '}': 
          this.pos += i;
          return;
        }
      }
      this.pos += i;
    } while (fillBuffer(1));
  }
  
  public int nextInt()
    throws IOException
  {
    int i = this.peeked;
    if (i == 0) {
      i = doPeek();
    }
    if (i == 15)
    {
      j = (int)this.peekedLong;
      if (this.peekedLong != j) {
        throw new NumberFormatException("Expected an int but was " + this.peekedLong + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
      }
      this.peeked = 0;
      this.pathIndices[(this.stackSize - 1)] += 1;
      return j;
    }
    if (i == 16)
    {
      this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
      this.pos += this.peekedNumberLength;
    }
    else if ((i == 8) || (i == 9))
    {
      this.peekedString = nextQuotedValue(i == 8 ? '\'' : '"');
      try
      {
        j = Integer.parseInt(this.peekedString);
        this.peeked = 0;
        this.pathIndices[(this.stackSize - 1)] += 1;
        return j;
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    else
    {
      throw new IllegalStateException("Expected an int but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
    }
    this.peeked = 11;
    double d = Double.parseDouble(this.peekedString);
    int j = (int)d;
    if (j != d) {
      throw new NumberFormatException("Expected an int but was " + this.peekedString + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
    }
    this.peekedString = null;
    this.peeked = 0;
    this.pathIndices[(this.stackSize - 1)] += 1;
    return j;
  }
  
  public void close()
    throws IOException
  {
    this.peeked = 0;
    this.stack[0] = 8;
    this.stackSize = 1;
    this.in.close();
  }
  
  public void skipValue()
    throws IOException
  {
    int i = 0;
    do
    {
      int j = this.peeked;
      if (j == 0) {
        j = doPeek();
      }
      if (j == 3)
      {
        push(1);
        i++;
      }
      else if (j == 1)
      {
        push(3);
        i++;
      }
      else if (j == 4)
      {
        this.stackSize -= 1;
        i--;
      }
      else if (j == 2)
      {
        this.stackSize -= 1;
        i--;
      }
      else if ((j == 14) || (j == 10))
      {
        skipUnquotedValue();
      }
      else if ((j == 8) || (j == 12))
      {
        skipQuotedValue('\'');
      }
      else if ((j == 9) || (j == 13))
      {
        skipQuotedValue('"');
      }
      else if (j == 16)
      {
        this.pos += this.peekedNumberLength;
      }
      this.peeked = 0;
    } while (i != 0);
    this.pathIndices[(this.stackSize - 1)] += 1;
    this.pathNames[(this.stackSize - 1)] = "null";
  }
  
  private void push(int paramInt)
  {
    if (this.stackSize == this.stack.length)
    {
      int[] arrayOfInt1 = new int[this.stackSize * 2];
      int[] arrayOfInt2 = new int[this.stackSize * 2];
      String[] arrayOfString = new String[this.stackSize * 2];
      System.arraycopy(this.stack, 0, arrayOfInt1, 0, this.stackSize);
      System.arraycopy(this.pathIndices, 0, arrayOfInt2, 0, this.stackSize);
      System.arraycopy(this.pathNames, 0, arrayOfString, 0, this.stackSize);
      this.stack = arrayOfInt1;
      this.pathIndices = arrayOfInt2;
      this.pathNames = arrayOfString;
    }
    this.stack[(this.stackSize++)] = paramInt;
  }
  
  private boolean fillBuffer(int paramInt)
    throws IOException
  {
    char[] arrayOfChar = this.buffer;
    this.lineStart -= this.pos;
    if (this.limit != this.pos)
    {
      this.limit -= this.pos;
      System.arraycopy(arrayOfChar, this.pos, arrayOfChar, 0, this.limit);
    }
    else
    {
      this.limit = 0;
    }
    this.pos = 0;
    int i;
    while ((i = this.in.read(arrayOfChar, this.limit, arrayOfChar.length - this.limit)) != -1)
    {
      this.limit += i;
      if ((this.lineNumber == 0) && (this.lineStart == 0) && (this.limit > 0) && (arrayOfChar[0] == 65279))
      {
        this.pos += 1;
        this.lineStart += 1;
        paramInt++;
      }
      if (this.limit >= paramInt) {
        return true;
      }
    }
    return false;
  }
  
  private int getLineNumber()
  {
    return this.lineNumber + 1;
  }
  
  private int getColumnNumber()
  {
    return this.pos - this.lineStart + 1;
  }
  
  private int nextNonWhitespace(boolean paramBoolean)
    throws IOException
  {
    char[] arrayOfChar = this.buffer;
    int i = this.pos;
    int j = this.limit;
    for (;;)
    {
      if (i == j)
      {
        this.pos = i;
        if (!fillBuffer(1)) {
          break;
        }
        i = this.pos;
        j = this.limit;
      }
      int k = arrayOfChar[(i++)];
      if (k == 10)
      {
        this.lineNumber += 1;
        this.lineStart = i;
      }
      else if ((k != 32) && (k != 13) && (k != 9))
      {
        if (k == 47)
        {
          this.pos = i;
          if (i == j)
          {
            this.pos -= 1;
            boolean bool = fillBuffer(2);
            this.pos += 1;
            if (!bool) {
              return k;
            }
          }
          checkLenient();
          int m = arrayOfChar[this.pos];
          switch (m)
          {
          case 42: 
            this.pos += 1;
            if (!skipTo("*/")) {
              throw syntaxError("Unterminated comment");
            }
            i = this.pos + 2;
            j = this.limit;
            break;
          case 47: 
            this.pos += 1;
            skipToEndOfLine();
            i = this.pos;
            j = this.limit;
            break;
          default: 
            return k;
          }
        }
        else if (k == 35)
        {
          this.pos = i;
          checkLenient();
          skipToEndOfLine();
          i = this.pos;
          j = this.limit;
        }
        else
        {
          this.pos = i;
          return k;
        }
      }
    }
    if (paramBoolean) {
      throw new EOFException("End of input at line " + getLineNumber() + " column " + getColumnNumber());
    }
    return -1;
  }
  
  private void checkLenient()
    throws IOException
  {
    if (!this.lenient) {
      throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
    }
  }
  
  private void skipToEndOfLine()
    throws IOException
  {
    while ((this.pos < this.limit) || (fillBuffer(1)))
    {
      int i = this.buffer[(this.pos++)];
      if (i == 10)
      {
        this.lineNumber += 1;
        this.lineStart = this.pos;
      }
      else
      {
        if (i == 13) {
          break;
        }
      }
    }
  }
  
  private boolean skipTo(String paramString)
    throws IOException
  {
    while ((this.pos + paramString.length() <= this.limit) || (fillBuffer(paramString.length())))
    {
      if (this.buffer[this.pos] == '\n')
      {
        this.lineNumber += 1;
        this.lineStart = (this.pos + 1);
      }
      else
      {
        for (int i = 0; i < paramString.length(); i++) {
          if (this.buffer[(this.pos + i)] != paramString.charAt(i)) {
            break label104;
          }
        }
        return true;
      }
      label104:
      this.pos += 1;
    }
    return false;
  }
  
  public String toString()
  {
    return getClass().getSimpleName() + " at line " + getLineNumber() + " column " + getColumnNumber();
  }
  
  public String getPath()
  {
    StringBuilder localStringBuilder = new StringBuilder().append('$');
    int i = 0;
    int j = this.stackSize;
    while (i < j)
    {
      switch (this.stack[i])
      {
      case 1: 
      case 2: 
        localStringBuilder.append('[').append(this.pathIndices[i]).append(']');
        break;
      case 3: 
      case 4: 
      case 5: 
        localStringBuilder.append('.');
        if (this.pathNames[i] != null) {
          localStringBuilder.append(this.pathNames[i]);
        }
        break;
      }
      i++;
    }
    return localStringBuilder.toString();
  }
  
  private char readEscapeCharacter()
    throws IOException
  {
    if ((this.pos == this.limit) && (!fillBuffer(1))) {
      throw syntaxError("Unterminated escape sequence");
    }
    char c = this.buffer[(this.pos++)];
    switch (c)
    {
    case 'u': 
      if ((this.pos + 4 > this.limit) && (!fillBuffer(4))) {
        throw syntaxError("Unterminated escape sequence");
      }
      int i = 0;
      int j = this.pos;
      int k = j + 4;
      while (j < k)
      {
        int m = this.buffer[j];
        i = (char)(i << 4);
        if ((m >= 48) && (m <= 57)) {
          i = (char)(i + (m - 48));
        } else if ((m >= 97) && (m <= 102)) {
          i = (char)(i + (m - 97 + 10));
        } else if ((m >= 65) && (m <= 70)) {
          i = (char)(i + (m - 65 + 10));
        } else {
          throw new NumberFormatException("\\u" + new String(this.buffer, this.pos, 4));
        }
        j++;
      }
      this.pos += 4;
      return i;
    case 't': 
      return '\t';
    case 'b': 
      return '\b';
    case 'n': 
      return '\n';
    case 'r': 
      return '\r';
    case 'f': 
      return '\f';
    case '\n': 
      this.lineNumber += 1;
      this.lineStart = this.pos;
    }
    return c;
  }
  
  private IOException syntaxError(String paramString)
    throws IOException
  {
    throw new MalformedJsonException(paramString + " at line " + getLineNumber() + " column " + getColumnNumber() + " path " + getPath());
  }
  
  private void consumeNonExecutePrefix()
    throws IOException
  {
    nextNonWhitespace(true);
    this.pos -= 1;
    if ((this.pos + NON_EXECUTE_PREFIX.length > this.limit) && (!fillBuffer(NON_EXECUTE_PREFIX.length))) {
      return;
    }
    for (int i = 0; i < NON_EXECUTE_PREFIX.length; i++) {
      if (this.buffer[(this.pos + i)] != NON_EXECUTE_PREFIX[i]) {
        return;
      }
    }
    this.pos += NON_EXECUTE_PREFIX.length;
  }
  
  static
  {
    JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess()
    {
      public void promoteNameToValue(JsonReader paramAnonymousJsonReader)
        throws IOException
      {
        if ((paramAnonymousJsonReader instanceof JsonTreeReader))
        {
          ((JsonTreeReader)paramAnonymousJsonReader).promoteNameToValue();
          return;
        }
        int i = paramAnonymousJsonReader.peeked;
        if (i == 0) {
          i = paramAnonymousJsonReader.doPeek();
        }
        if (i == 13) {
          paramAnonymousJsonReader.peeked = 9;
        } else if (i == 12) {
          paramAnonymousJsonReader.peeked = 8;
        } else if (i == 14) {
          paramAnonymousJsonReader.peeked = 10;
        } else {
          throw new IllegalStateException("Expected a name but was " + paramAnonymousJsonReader.peek() + " " + " at line " + paramAnonymousJsonReader.getLineNumber() + " column " + paramAnonymousJsonReader.getColumnNumber() + " path " + paramAnonymousJsonReader.getPath());
        }
      }
    };
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\stream\JsonReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */