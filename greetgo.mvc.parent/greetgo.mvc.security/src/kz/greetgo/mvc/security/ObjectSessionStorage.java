package kz.greetgo.mvc.security;

import static kz.greetgo.mvc.security.SerializeUtil.deserialize;
import static kz.greetgo.mvc.security.SerializeUtil.serialize;

public class ObjectSessionStorage implements SessionStorage {

  private static final ThreadLocal<byte[]> storage = new ThreadLocal<>();

  @Override
  public void setSessionBytes(byte[] bytes) {
    storage.set(serialize(deserialize(bytes)));
  }

  @Override
  public byte[] getSessionBytes() {
    return storage.get();
  }

  protected Object getObject() {
    return deserialize(getSessionBytes());
  }

  protected void setObject(Object object) {
    setSessionBytes(serialize(object));
  }
}
