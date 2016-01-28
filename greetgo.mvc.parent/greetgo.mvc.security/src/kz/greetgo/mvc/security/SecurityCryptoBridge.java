package kz.greetgo.mvc.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;

public class SecurityCryptoBridge implements SecurityCrypto {

  private final SecuritySource securitySource;

  public SecurityCryptoBridge(SecuritySource securitySource) {
    this.securitySource = securitySource;
  }

  @Override
  public byte[] encrypt(byte[] bytes) {
    try {

      Cipher cipher = securitySource.getCipher();
      cipher.init(Cipher.ENCRYPT_MODE, securitySource.getPublicKey());
      return cipher.doFinal(bytes);

    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public byte[] decrypt(byte[] encryptedBytes) {
    if (encryptedBytes == null) return null;
    try {

      Cipher cipher = securitySource.getCipher();
      cipher.init(Cipher.DECRYPT_MODE, securitySource.getPrivateKey());
      return cipher.doFinal(encryptedBytes);

    } catch (BadPaddingException e) {
      return null;
    } catch (InvalidKeyException | IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public byte[] sign(byte[] bytes) {
    if (bytes == null) return null;
    try {

      final byte[] hash1 = securitySource.getMessageDigest().digest(bytes);

      Cipher cipher = securitySource.getCipher();
      cipher.init(Cipher.ENCRYPT_MODE, securitySource.getPrivateKey());

      return cipher.doFinal(hash1);

    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean verifySignature(byte[] bytes, byte[] signature) {
    if (signature == null || bytes == null) return false;
    try {

      final byte[] hash1 = securitySource.getMessageDigest().digest(bytes);

      Cipher cipher = securitySource.getCipher();
      cipher.init(Cipher.DECRYPT_MODE, securitySource.getPublicKey());

      final byte[] hash2 = cipher.doFinal(signature);

      if (hash1.length != hash2.length) return false;

      for (int i = 0, n = hash1.length; i < n; i++) {
        if (hash1[i] != hash2[i]) return false;
      }

      return true;

    } catch (BadPaddingException e) {

      return false;

    } catch (InvalidKeyException | IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    }
  }
}
