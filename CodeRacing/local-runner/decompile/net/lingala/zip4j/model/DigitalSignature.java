package net.lingala.zip4j.model;

public class DigitalSignature
{
  private int headerSignature;
  private int sizeOfData;
  private String signatureData;
  
  public void setHeaderSignature(int paramInt)
  {
    this.headerSignature = paramInt;
  }
  
  public void setSizeOfData(int paramInt)
  {
    this.sizeOfData = paramInt;
  }
  
  public void setSignatureData(String paramString)
  {
    this.signatureData = paramString;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\net\lingala\zip4j\model\DigitalSignature.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */