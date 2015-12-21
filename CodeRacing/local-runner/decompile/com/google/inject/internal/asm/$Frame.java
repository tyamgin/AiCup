package com.google.inject.internal.asm;

final class $Frame
{
  static final int[] a;
  .Label b;
  int[] c;
  int[] d;
  private int[] e;
  private int[] f;
  private int g;
  private int h;
  private int[] i;
  
  private int a(int paramInt)
  {
    if ((this.e == null) || (paramInt >= this.e.length)) {
      return 0x2000000 | paramInt;
    }
    int j = this.e[paramInt];
    if (j == 0) {
      j = this.e[paramInt] = 0x2000000 | paramInt;
    }
    return j;
  }
  
  private void a(int paramInt1, int paramInt2)
  {
    if (this.e == null) {
      this.e = new int[10];
    }
    int j = this.e.length;
    if (paramInt1 >= j)
    {
      int[] arrayOfInt = new int[Math.max(paramInt1 + 1, 2 * j)];
      System.arraycopy(this.e, 0, arrayOfInt, 0, j);
      this.e = arrayOfInt;
    }
    this.e[paramInt1] = paramInt2;
  }
  
  private void b(int paramInt)
  {
    if (this.f == null) {
      this.f = new int[10];
    }
    int j = this.f.length;
    if (this.g >= j)
    {
      int[] arrayOfInt = new int[Math.max(this.g + 1, 2 * j)];
      System.arraycopy(this.f, 0, arrayOfInt, 0, j);
      this.f = arrayOfInt;
    }
    this.f[(this.g++)] = paramInt;
    int k = this.b.f + this.g;
    if (k > this.b.g) {
      this.b.g = k;
    }
  }
  
  private void a(.ClassWriter paramClassWriter, String paramString)
  {
    int j = b(paramClassWriter, paramString);
    if (j != 0)
    {
      b(j);
      if ((j == 16777220) || (j == 16777219)) {
        b(16777216);
      }
    }
  }
  
  private static int b(.ClassWriter paramClassWriter, String paramString)
  {
    int j = paramString.charAt(0) == '(' ? paramString.indexOf(')') + 1 : 0;
    String str;
    switch (paramString.charAt(j))
    {
    case 'V': 
      return 0;
    case 'B': 
    case 'C': 
    case 'I': 
    case 'S': 
    case 'Z': 
      return 16777217;
    case 'F': 
      return 16777218;
    case 'J': 
      return 16777220;
    case 'D': 
      return 16777219;
    case 'L': 
      str = paramString.substring(j + 1, paramString.length() - 1);
      return 0x1700000 | paramClassWriter.c(str);
    }
    for (int k = j + 1; paramString.charAt(k) == '['; k++) {}
    int m;
    switch (paramString.charAt(k))
    {
    case 'Z': 
      m = 16777225;
      break;
    case 'C': 
      m = 16777227;
      break;
    case 'B': 
      m = 16777226;
      break;
    case 'S': 
      m = 16777228;
      break;
    case 'I': 
      m = 16777217;
      break;
    case 'F': 
      m = 16777218;
      break;
    case 'J': 
      m = 16777220;
      break;
    case 'D': 
      m = 16777219;
      break;
    case 'E': 
    case 'G': 
    case 'H': 
    case 'K': 
    case 'L': 
    case 'M': 
    case 'N': 
    case 'O': 
    case 'P': 
    case 'Q': 
    case 'R': 
    case 'T': 
    case 'U': 
    case 'V': 
    case 'W': 
    case 'X': 
    case 'Y': 
    default: 
      str = paramString.substring(k + 1, paramString.length() - 1);
      m = 0x1700000 | paramClassWriter.c(str);
    }
    return k - j << 28 | m;
  }
  
  private int a()
  {
    if (this.g > 0) {
      return this.f[(--this.g)];
    }
    return 0x3000000 | ---this.b.f;
  }
  
  private void c(int paramInt)
  {
    if (this.g >= paramInt)
    {
      this.g -= paramInt;
    }
    else
    {
      this.b.f -= paramInt - this.g;
      this.g = 0;
    }
  }
  
  private void a(String paramString)
  {
    int j = paramString.charAt(0);
    if (j == 40) {
      c((.Type.getArgumentsAndReturnSizes(paramString) >> 2) - 1);
    } else if ((j == 74) || (j == 68)) {
      c(2);
    } else {
      c(1);
    }
  }
  
  private void d(int paramInt)
  {
    if (this.i == null) {
      this.i = new int[2];
    }
    int j = this.i.length;
    if (this.h >= j)
    {
      int[] arrayOfInt = new int[Math.max(this.h + 1, 2 * j)];
      System.arraycopy(this.i, 0, arrayOfInt, 0, j);
      this.i = arrayOfInt;
    }
    this.i[(this.h++)] = paramInt;
  }
  
  private int a(.ClassWriter paramClassWriter, int paramInt)
  {
    int j;
    if (paramInt == 16777222)
    {
      j = 0x1700000 | paramClassWriter.c(paramClassWriter.I);
    }
    else if ((paramInt & 0xFFF00000) == 25165824)
    {
      String str = paramClassWriter.H[(paramInt & 0xFFFFF)].g;
      j = 0x1700000 | paramClassWriter.c(str);
    }
    else
    {
      return paramInt;
    }
    for (int k = 0; k < this.h; k++)
    {
      int m = this.i[k];
      int n = m & 0xF0000000;
      int i1 = m & 0xF000000;
      if (i1 == 33554432) {
        m = n + this.c[(m & 0x7FFFFF)];
      } else if (i1 == 50331648) {
        m = n + this.d[(this.d.length - (m & 0x7FFFFF))];
      }
      if (paramInt == m) {
        return j;
      }
    }
    return paramInt;
  }
  
  void a(.ClassWriter paramClassWriter, int paramInt1, .Type[] paramArrayOfType, int paramInt2)
  {
    this.c = new int[paramInt2];
    this.d = new int[0];
    int j = 0;
    if ((paramInt1 & 0x8) == 0) {
      if ((paramInt1 & 0x80000) == 0) {
        this.c[(j++)] = (0x1700000 | paramClassWriter.c(paramClassWriter.I));
      } else {
        this.c[(j++)] = 16777222;
      }
    }
    for (int k = 0; k < paramArrayOfType.length; k++)
    {
      int m = b(paramClassWriter, paramArrayOfType[k].getDescriptor());
      this.c[(j++)] = m;
      if ((m == 16777220) || (m == 16777219)) {
        this.c[(j++)] = 16777216;
      }
    }
    while (j < paramInt2) {
      this.c[(j++)] = 16777216;
    }
  }
  
  void a(int paramInt1, int paramInt2, .ClassWriter paramClassWriter, .Item paramItem)
  {
    int j;
    int k;
    int m;
    String str;
    switch (paramInt1)
    {
    case 0: 
    case 116: 
    case 117: 
    case 118: 
    case 119: 
    case 145: 
    case 146: 
    case 147: 
    case 167: 
    case 177: 
      break;
    case 1: 
      b(16777221);
      break;
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 16: 
    case 17: 
    case 21: 
      b(16777217);
      break;
    case 9: 
    case 10: 
    case 22: 
      b(16777220);
      b(16777216);
      break;
    case 11: 
    case 12: 
    case 13: 
    case 23: 
      b(16777218);
      break;
    case 14: 
    case 15: 
    case 24: 
      b(16777219);
      b(16777216);
      break;
    case 18: 
      switch (paramItem.b)
      {
      case 3: 
        b(16777217);
        break;
      case 5: 
        b(16777220);
        b(16777216);
        break;
      case 4: 
        b(16777218);
        break;
      case 6: 
        b(16777219);
        b(16777216);
        break;
      case 7: 
        b(0x1700000 | paramClassWriter.c("java/lang/Class"));
        break;
      case 8: 
        b(0x1700000 | paramClassWriter.c("java/lang/String"));
        break;
      case 16: 
        b(0x1700000 | paramClassWriter.c("java/lang/invoke/MethodType"));
        break;
      case 9: 
      case 10: 
      case 11: 
      case 12: 
      case 13: 
      case 14: 
      case 15: 
      default: 
        b(0x1700000 | paramClassWriter.c("java/lang/invoke/MethodHandle"));
      }
      break;
    case 25: 
      b(a(paramInt2));
      break;
    case 46: 
    case 51: 
    case 52: 
    case 53: 
      c(2);
      b(16777217);
      break;
    case 47: 
    case 143: 
      c(2);
      b(16777220);
      b(16777216);
      break;
    case 48: 
      c(2);
      b(16777218);
      break;
    case 49: 
    case 138: 
      c(2);
      b(16777219);
      b(16777216);
      break;
    case 50: 
      c(1);
      j = a();
      b(-268435456 + j);
      break;
    case 54: 
    case 56: 
    case 58: 
      j = a();
      a(paramInt2, j);
      if (paramInt2 > 0)
      {
        k = a(paramInt2 - 1);
        if ((k == 16777220) || (k == 16777219)) {
          a(paramInt2 - 1, 16777216);
        } else if ((k & 0xF000000) != 16777216) {
          a(paramInt2 - 1, k | 0x800000);
        }
      }
      break;
    case 55: 
    case 57: 
      c(1);
      j = a();
      a(paramInt2, j);
      a(paramInt2 + 1, 16777216);
      if (paramInt2 > 0)
      {
        k = a(paramInt2 - 1);
        if ((k == 16777220) || (k == 16777219)) {
          a(paramInt2 - 1, 16777216);
        } else if ((k & 0xF000000) != 16777216) {
          a(paramInt2 - 1, k | 0x800000);
        }
      }
      break;
    case 79: 
    case 81: 
    case 83: 
    case 84: 
    case 85: 
    case 86: 
      c(3);
      break;
    case 80: 
    case 82: 
      c(4);
      break;
    case 87: 
    case 153: 
    case 154: 
    case 155: 
    case 156: 
    case 157: 
    case 158: 
    case 170: 
    case 171: 
    case 172: 
    case 174: 
    case 176: 
    case 191: 
    case 194: 
    case 195: 
    case 198: 
    case 199: 
      c(1);
      break;
    case 88: 
    case 159: 
    case 160: 
    case 161: 
    case 162: 
    case 163: 
    case 164: 
    case 165: 
    case 166: 
    case 173: 
    case 175: 
      c(2);
      break;
    case 89: 
      j = a();
      b(j);
      b(j);
      break;
    case 90: 
      j = a();
      k = a();
      b(j);
      b(k);
      b(j);
      break;
    case 91: 
      j = a();
      k = a();
      m = a();
      b(j);
      b(m);
      b(k);
      b(j);
      break;
    case 92: 
      j = a();
      k = a();
      b(k);
      b(j);
      b(k);
      b(j);
      break;
    case 93: 
      j = a();
      k = a();
      m = a();
      b(k);
      b(j);
      b(m);
      b(k);
      b(j);
      break;
    case 94: 
      j = a();
      k = a();
      m = a();
      int n = a();
      b(k);
      b(j);
      b(n);
      b(m);
      b(k);
      b(j);
      break;
    case 95: 
      j = a();
      k = a();
      b(j);
      b(k);
      break;
    case 96: 
    case 100: 
    case 104: 
    case 108: 
    case 112: 
    case 120: 
    case 122: 
    case 124: 
    case 126: 
    case 128: 
    case 130: 
    case 136: 
    case 142: 
    case 149: 
    case 150: 
      c(2);
      b(16777217);
      break;
    case 97: 
    case 101: 
    case 105: 
    case 109: 
    case 113: 
    case 127: 
    case 129: 
    case 131: 
      c(4);
      b(16777220);
      b(16777216);
      break;
    case 98: 
    case 102: 
    case 106: 
    case 110: 
    case 114: 
    case 137: 
    case 144: 
      c(2);
      b(16777218);
      break;
    case 99: 
    case 103: 
    case 107: 
    case 111: 
    case 115: 
      c(4);
      b(16777219);
      b(16777216);
      break;
    case 121: 
    case 123: 
    case 125: 
      c(3);
      b(16777220);
      b(16777216);
      break;
    case 132: 
      a(paramInt2, 16777217);
      break;
    case 133: 
    case 140: 
      c(1);
      b(16777220);
      b(16777216);
      break;
    case 134: 
      c(1);
      b(16777218);
      break;
    case 135: 
    case 141: 
      c(1);
      b(16777219);
      b(16777216);
      break;
    case 139: 
    case 190: 
    case 193: 
      c(1);
      b(16777217);
      break;
    case 148: 
    case 151: 
    case 152: 
      c(4);
      b(16777217);
      break;
    case 168: 
    case 169: 
      throw new RuntimeException("JSR/RET are not supported with computeFrames option");
    case 178: 
      a(paramClassWriter, paramItem.i);
      break;
    case 179: 
      a(paramItem.i);
      break;
    case 180: 
      c(1);
      a(paramClassWriter, paramItem.i);
      break;
    case 181: 
      a(paramItem.i);
      a();
      break;
    case 182: 
    case 183: 
    case 184: 
    case 185: 
      a(paramItem.i);
      if (paramInt1 != 184)
      {
        j = a();
        if ((paramInt1 == 183) && (paramItem.h.charAt(0) == '<')) {
          d(j);
        }
      }
      a(paramClassWriter, paramItem.i);
      break;
    case 186: 
      a(paramItem.h);
      a(paramClassWriter, paramItem.h);
      break;
    case 187: 
      b(0x1800000 | paramClassWriter.a(paramItem.g, paramInt2));
      break;
    case 188: 
      a();
      switch (paramInt2)
      {
      case 4: 
        b(285212681);
        break;
      case 5: 
        b(285212683);
        break;
      case 8: 
        b(285212682);
        break;
      case 9: 
        b(285212684);
        break;
      case 10: 
        b(285212673);
        break;
      case 6: 
        b(285212674);
        break;
      case 7: 
        b(285212675);
        break;
      default: 
        b(285212676);
      }
      break;
    case 189: 
      str = paramItem.g;
      a();
      if (str.charAt(0) == '[') {
        a(paramClassWriter, '[' + str);
      } else {
        b(0x11700000 | paramClassWriter.c(str));
      }
      break;
    case 192: 
      str = paramItem.g;
      a();
      if (str.charAt(0) == '[') {
        a(paramClassWriter, str);
      } else {
        b(0x1700000 | paramClassWriter.c(str));
      }
      break;
    case 19: 
    case 20: 
    case 26: 
    case 27: 
    case 28: 
    case 29: 
    case 30: 
    case 31: 
    case 32: 
    case 33: 
    case 34: 
    case 35: 
    case 36: 
    case 37: 
    case 38: 
    case 39: 
    case 40: 
    case 41: 
    case 42: 
    case 43: 
    case 44: 
    case 45: 
    case 59: 
    case 60: 
    case 61: 
    case 62: 
    case 63: 
    case 64: 
    case 65: 
    case 66: 
    case 67: 
    case 68: 
    case 69: 
    case 70: 
    case 71: 
    case 72: 
    case 73: 
    case 74: 
    case 75: 
    case 76: 
    case 77: 
    case 78: 
    case 196: 
    case 197: 
    default: 
      c(paramInt2);
      a(paramClassWriter, paramItem.g);
    }
  }
  
  boolean a(.ClassWriter paramClassWriter, Frame paramFrame, int paramInt)
  {
    boolean bool = false;
    int j = this.c.length;
    int k = this.d.length;
    if (paramFrame.c == null)
    {
      paramFrame.c = new int[j];
      bool = true;
    }
    int n;
    int i1;
    int i2;
    int i3;
    for (int m = 0; m < j; m++)
    {
      if ((this.e != null) && (m < this.e.length))
      {
        n = this.e[m];
        if (n == 0)
        {
          i1 = this.c[m];
        }
        else
        {
          i2 = n & 0xF0000000;
          i3 = n & 0xF000000;
          if (i3 == 16777216)
          {
            i1 = n;
          }
          else
          {
            if (i3 == 33554432) {
              i1 = i2 + this.c[(n & 0x7FFFFF)];
            } else {
              i1 = i2 + this.d[(k - (n & 0x7FFFFF))];
            }
            if (((n & 0x800000) != 0) && ((i1 == 16777220) || (i1 == 16777219))) {
              i1 = 16777216;
            }
          }
        }
      }
      else
      {
        i1 = this.c[m];
      }
      if (this.i != null) {
        i1 = a(paramClassWriter, i1);
      }
      bool |= a(paramClassWriter, i1, paramFrame.c, m);
    }
    if (paramInt > 0)
    {
      for (m = 0; m < j; m++)
      {
        i1 = this.c[m];
        bool |= a(paramClassWriter, i1, paramFrame.c, m);
      }
      if (paramFrame.d == null)
      {
        paramFrame.d = new int[1];
        bool = true;
      }
      bool |= a(paramClassWriter, paramInt, paramFrame.d, 0);
      return bool;
    }
    int i4 = this.d.length + this.b.f;
    if (paramFrame.d == null)
    {
      paramFrame.d = new int[i4 + this.g];
      bool = true;
    }
    for (m = 0; m < i4; m++)
    {
      i1 = this.d[m];
      if (this.i != null) {
        i1 = a(paramClassWriter, i1);
      }
      bool |= a(paramClassWriter, i1, paramFrame.d, m);
    }
    for (m = 0; m < this.g; m++)
    {
      n = this.f[m];
      i2 = n & 0xF0000000;
      i3 = n & 0xF000000;
      if (i3 == 16777216)
      {
        i1 = n;
      }
      else
      {
        if (i3 == 33554432) {
          i1 = i2 + this.c[(n & 0x7FFFFF)];
        } else {
          i1 = i2 + this.d[(k - (n & 0x7FFFFF))];
        }
        if (((n & 0x800000) != 0) && ((i1 == 16777220) || (i1 == 16777219))) {
          i1 = 16777216;
        }
      }
      if (this.i != null) {
        i1 = a(paramClassWriter, i1);
      }
      bool |= a(paramClassWriter, i1, paramFrame.d, i4 + m);
    }
    return bool;
  }
  
  private static boolean a(.ClassWriter paramClassWriter, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    int j = paramArrayOfInt[paramInt2];
    if (j == paramInt1) {
      return false;
    }
    if ((paramInt1 & 0xFFFFFFF) == 16777221)
    {
      if (j == 16777221) {
        return false;
      }
      paramInt1 = 16777221;
    }
    if (j == 0)
    {
      paramArrayOfInt[paramInt2] = paramInt1;
      return true;
    }
    int k;
    if (((j & 0xFF00000) == 24117248) || ((j & 0xF0000000) != 0))
    {
      if (paramInt1 == 16777221) {
        return false;
      }
      int m;
      if ((paramInt1 & 0xFFF00000) == (j & 0xFFF00000))
      {
        if ((j & 0xFF00000) == 24117248)
        {
          k = paramInt1 & 0xF0000000 | 0x1700000 | paramClassWriter.a(paramInt1 & 0xFFFFF, j & 0xFFFFF);
        }
        else
        {
          m = -268435456 + (j & 0xF0000000);
          k = m | 0x1700000 | paramClassWriter.c("java/lang/Object");
        }
      }
      else if (((paramInt1 & 0xFF00000) == 24117248) || ((paramInt1 & 0xF0000000) != 0))
      {
        m = (((paramInt1 & 0xF0000000) == 0) || ((paramInt1 & 0xFF00000) == 24117248) ? 0 : -268435456) + (paramInt1 & 0xF0000000);
        int n = (((j & 0xF0000000) == 0) || ((j & 0xFF00000) == 24117248) ? 0 : -268435456) + (j & 0xF0000000);
        k = Math.min(m, n) | 0x1700000 | paramClassWriter.c("java/lang/Object");
      }
      else
      {
        k = 16777216;
      }
    }
    else if (j == 16777221)
    {
      k = ((paramInt1 & 0xFF00000) == 24117248) || ((paramInt1 & 0xF0000000) != 0) ? paramInt1 : 16777216;
    }
    else
    {
      k = 16777216;
    }
    if (j != k)
    {
      paramArrayOfInt[paramInt2] = k;
      return true;
    }
    return false;
  }
  
  static
  {
    _clinit_();
    int[] arrayOfInt = new int['Ê'];
    String str = "EFFFFFFFFGGFFFGGFFFEEFGFGFEEEEEEEEEEEEEEEEEEEEDEDEDDDDDCDCDEEEEEEEEEEEEEEEEEEEEBABABBBBDCFFFGGGEDCDCDCDCDCDCDCDCDCDCEEEEDDDDDDDCDCDCEFEFDDEEFFDEDEEEBDDBBDDDDDDCCCCCCCCEFEDDDCDCDEEEEEEEEEEFEEEEEEDDEEDDEE";
    for (int j = 0; j < arrayOfInt.length; j++) {
      arrayOfInt[j] = (str.charAt(j) - 'E');
    }
    a = arrayOfInt;
  }
  
  static void _clinit_() {}
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\asm\$Frame.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */