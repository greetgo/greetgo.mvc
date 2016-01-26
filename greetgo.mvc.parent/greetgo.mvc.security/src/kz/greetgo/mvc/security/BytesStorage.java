package kz.greetgo.mvc.security;

public interface BytesStorage {
  void setBytes(byte[] bytes);

  byte[] getBytes();
}
