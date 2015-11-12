package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.LinkedList;
import java.util.Queue;

@Beta
public final class LineReader
{
  private final Readable readable;
  private final Reader reader;
  private final char[] buf = new char['က'];
  private final CharBuffer cbuf = CharBuffer.wrap(this.buf);
  private final Queue lines = new LinkedList();
  private final LineBuffer lineBuf = new LineBuffer()
  {
    protected void handleLine(String paramAnonymousString1, String paramAnonymousString2)
    {
      LineReader.this.lines.add(paramAnonymousString1);
    }
  };
  
  public LineReader(Readable paramReadable)
  {
    Preconditions.checkNotNull(paramReadable);
    this.readable = paramReadable;
    this.reader = ((paramReadable instanceof Reader) ? (Reader)paramReadable : null);
  }
  
  public String readLine()
    throws IOException
  {
    while (this.lines.peek() == null)
    {
      this.cbuf.clear();
      int i = this.reader != null ? this.reader.read(this.buf, 0, this.buf.length) : this.readable.read(this.cbuf);
      if (i == -1)
      {
        this.lineBuf.finish();
        break;
      }
      this.lineBuf.add(this.buf, 0, i);
    }
    return (String)this.lines.poll();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\LineReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */