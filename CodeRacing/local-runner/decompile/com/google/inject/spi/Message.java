package com.google.inject.spi;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.internal.Errors;
import com.google.inject.internal.util.SourceProvider;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.List;

public final class Message
  implements Element, Serializable
{
  private final String message;
  private final Throwable cause;
  private final List sources;
  private static final long serialVersionUID = 0L;
  
  public Message(List paramList, String paramString, Throwable paramThrowable)
  {
    this.sources = ImmutableList.copyOf(paramList);
    this.message = ((String)Preconditions.checkNotNull(paramString, "message"));
    this.cause = paramThrowable;
  }
  
  public Message(String paramString, Throwable paramThrowable)
  {
    this(ImmutableList.of(), paramString, paramThrowable);
  }
  
  public Message(Object paramObject, String paramString)
  {
    this(ImmutableList.of(paramObject), paramString, null);
  }
  
  public Message(String paramString)
  {
    this(ImmutableList.of(), paramString, null);
  }
  
  public String getSource()
  {
    return this.sources.isEmpty() ? SourceProvider.UNKNOWN_SOURCE.toString() : Errors.convert(this.sources.get(this.sources.size() - 1)).toString();
  }
  
  public List getSources()
  {
    return this.sources;
  }
  
  public String getMessage()
  {
    return this.message;
  }
  
  public Object acceptVisitor(ElementVisitor paramElementVisitor)
  {
    return paramElementVisitor.visit(this);
  }
  
  public Throwable getCause()
  {
    return this.cause;
  }
  
  public String toString()
  {
    return this.message;
  }
  
  public int hashCode()
  {
    return this.message.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Message)) {
      return false;
    }
    Message localMessage = (Message)paramObject;
    return (this.message.equals(localMessage.message)) && (Objects.equal(this.cause, localMessage.cause)) && (this.sources.equals(localMessage.sources));
  }
  
  public void applyTo(Binder paramBinder)
  {
    paramBinder.withSource(getSource()).addError(this);
  }
  
  private Object writeReplace()
    throws ObjectStreamException
  {
    Object[] arrayOfObject = this.sources.toArray();
    for (int i = 0; i < arrayOfObject.length; i++) {
      arrayOfObject[i] = Errors.convert(arrayOfObject[i]).toString();
    }
    return new Message(ImmutableList.copyOf(arrayOfObject), this.message, this.cause);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\Message.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */