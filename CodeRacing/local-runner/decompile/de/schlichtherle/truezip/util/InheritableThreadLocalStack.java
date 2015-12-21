package de.schlichtherle.truezip.util;

import java.lang.ref.WeakReference;
import java.util.NoSuchElementException;

public final class InheritableThreadLocalStack
{
  private final InheritableThreadLocal nodes = new InheritableThreadLocal();
  
  public Object peekOrElse(Object paramObject)
  {
    Node localNode = (Node)this.nodes.get();
    return null != localNode ? localNode.element : paramObject;
  }
  
  public Object push(Object paramObject)
  {
    Node localNode1 = (Node)this.nodes.get();
    Node localNode2 = new Node(localNode1, paramObject);
    this.nodes.set(localNode2);
    return paramObject;
  }
  
  public Object pop()
  {
    Node localNode = (Node)this.nodes.get();
    if (null == localNode) {
      throw new NoSuchElementException();
    }
    if (!Thread.currentThread().equals(localNode.get())) {
      throw new NoSuchElementException();
    }
    this.nodes.set(localNode.previous);
    return localNode.element;
  }
  
  public void popIf(Object paramObject)
  {
    try
    {
      Object localObject = pop();
      if (localObject != paramObject)
      {
        push(localObject);
        throw new IllegalStateException(localObject + " (expected " + paramObject + " as the top element of the inheritable thread local stack)");
      }
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      throw new IllegalStateException("The inheritable thread local stack is empty!", localNoSuchElementException);
    }
  }
  
  private static class Node
    extends WeakReference
  {
    final Node previous;
    Object element;
    
    Node(Node paramNode, Object paramObject)
    {
      super();
      this.previous = paramNode;
      this.element = paramObject;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\InheritableThreadLocalStack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */