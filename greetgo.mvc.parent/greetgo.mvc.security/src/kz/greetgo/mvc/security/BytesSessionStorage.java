package kz.greetgo.mvc.security;

public class BytesSessionStorage implements BytesStorage {

  private BytesSessionStorage() {
  }

  private static BytesSessionStorage instance = null;

  public static BytesSessionStorage get() {
    if (instance == null) instance = new BytesSessionStorage();
    return instance;
  }

  private final ThreadLocal<byte[]> storage = new ThreadLocal<>();

  @Override
  public void setBytes(byte[] bytes) {
    storage.set(bytes);
  }

  @Override
  public byte[] getBytes() {
    return storage.get();
  }
}
