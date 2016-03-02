package kz.greetgo.mvc.security;

import javax.crypto.Cipher;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;

public interface SecuritySource {
  Cipher getCipher();

  PublicKey getPublicKey();

  PrivateKey getPrivateKey();

  MessageDigest getMessageDigest();

  Random getRandom();

  int getBlockSize();
}
