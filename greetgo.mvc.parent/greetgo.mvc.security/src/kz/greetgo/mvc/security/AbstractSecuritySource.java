package kz.greetgo.mvc.security;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.FileNotFoundException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public abstract class AbstractSecuritySource implements SecuritySource {

  public static final int DEFAULT_KEY_SIZE = 1024;
  public static final int DEFAULT_BLOCK_SIZE = 117;

  protected int getKeySize() {
    return DEFAULT_KEY_SIZE;
  }

  protected abstract byte[] getPrivateKeyBytes();

  protected abstract void setPrivateKeyBytes(byte[] bytes);

  protected abstract byte[] getPublicKeyBytes();

  protected abstract void setPublicKeyBytes(byte[] bytes);

  protected abstract boolean hasKeys();

  protected abstract SecuritySourceConfig conf();

  @Override
  public int getBlockSize() {
    return DEFAULT_BLOCK_SIZE;
  }

  @Override
  public Cipher getCipher() {
    try {
      return Cipher.getInstance(conf().cipherAlgorithm());
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      throw new RuntimeException(e);
    }
  }

  private PublicKey publicKey = null;

  @Override
  public PublicKey getPublicKey() {
    prepareKeys();
    return publicKey;
  }

  private PrivateKey privateKey = null;

  @Override
  public PrivateKey getPrivateKey() {
    prepareKeys();
    return privateKey;
  }

  private SecureRandom random = null;

  @Override
  public SecureRandom getRandom() {
    if (random != null) return random;
    synchronized (this) {
      if (random != null) return random;
      try {
        return random = SecureRandom.getInstance(conf().secureRandomAlgorithm());
      } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private MessageDigest messageDigest = null;

  @Override
  public MessageDigest getMessageDigest() {
    if (messageDigest != null) return messageDigest;
    synchronized (this) {
      if (messageDigest != null) return messageDigest;
      try {
        return messageDigest = MessageDigest.getInstance(conf().messageDigestAlgorithm());
      } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void prepareKeys() {
    if (privateKey != null && publicKey != null) return;
    synchronized (this) {
      if (privateKey != null && publicKey != null) return;
      try {
        doPrepareKeys();
      } catch (Exception e) {
        if (e instanceof RuntimeException) throw (RuntimeException) e;
        throw new RuntimeException(e);
      }
    }
  }

  private void doPrepareKeys() throws Exception {
    if (hasKeys()) {
      readKeysFromFiles();
    } else {
      generateKeys();
      saveKeys();
    }
  }

  private void saveKeys() {
    {
      final PKCS8EncodedKeySpec privateKetSpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
      setPrivateKeyBytes(privateKetSpec.getEncoded());
    }
    {
      X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKey.getEncoded());
      setPublicKeyBytes(publicSpec.getEncoded());
    }
  }

  private void generateKeys() {
    try {

      final KeyPairGenerator kpg = KeyPairGenerator.getInstance(conf().keyPairGeneratorAlgorithm());

      kpg.initialize(getKeySize(), getRandom());

      final KeyPair keyPair = kpg.generateKeyPair();

      privateKey = keyPair.getPrivate();
      publicKey = keyPair.getPublic();

    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private void readKeysFromFiles() throws FileNotFoundException {
    try {

      final PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(getPrivateKeyBytes());
      final X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(getPublicKeyBytes());

      final KeyFactory keyFactory = KeyFactory.getInstance(conf().keyFactoryAlgorithm());

      privateKey = keyFactory.generatePrivate(keySpecPrivate);
      publicKey = keyFactory.generatePublic(keySpecPublic);

    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }
}
