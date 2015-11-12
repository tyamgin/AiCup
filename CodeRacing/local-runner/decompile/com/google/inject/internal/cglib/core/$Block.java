package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm..Label;

public class $Block
{
  private .CodeEmitter e;
  private .Label start;
  private .Label end;
  
  public $Block(.CodeEmitter paramCodeEmitter)
  {
    this.e = paramCodeEmitter;
    this.start = paramCodeEmitter.mark();
  }
  
  public .CodeEmitter getCodeEmitter()
  {
    return this.e;
  }
  
  public void end()
  {
    if (this.end != null) {
      throw new IllegalStateException("end of label already set");
    }
    this.end = this.e.mark();
  }
  
  public .Label getStart()
  {
    return this.start;
  }
  
  public .Label getEnd()
  {
    return this.end;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$Block.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */