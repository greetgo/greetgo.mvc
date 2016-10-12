package kz.greetgo.mvc.security;

import java.io.FileNotFoundException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

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
  
  private final ThreadLocal<PublicKey> publicKey = new ThreadLocal<PublicKey>();
  private final ThreadLocal<PrivateKey> privateKey = new ThreadLocal<PrivateKey>();
  
  private void prepareKeys() {
    if (privateKey.get() != null && publicKey.get() != null) return;
    
    synchronized (this) {
      if (privateKey.get() != null && publicKey.get() != null) return;
      try {
        doPrepareKeys();
      } catch (Exception e) {
        if (e instanceof RuntimeException) throw (RuntimeException)e;
        throw new RuntimeException(e);
      }
    }
  }
  
  @Override
  public PublicKey getPublicKey() {
    prepareKeys();
    return publicKey.get();
  }
  
  @Override
  public PrivateKey getPrivateKey() {
    prepareKeys();
    return privateKey.get();
  }
  
  private final ThreadLocal<SecureRandom> random = new ThreadLocal<SecureRandom>();
  
  @Override
  public SecureRandom getRandom() {
    if (random.get() != null) return random.get();
    try {
      SecureRandom instance = SecureRandom.getInstance(conf().secureRandomAlgorithm());
      random.set(instance);
      return instance;
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
  
  private final ThreadLocal<MessageDigest> messageDigest = new ThreadLocal<MessageDigest>();
  
  @Override
  public MessageDigest getMessageDigest() {
    {
      MessageDigest result = messageDigest.get();
      if (result != null) return result;
    }
    
    try {
      MessageDigest instance = MessageDigest.getInstance(conf().messageDigestAlgorithm());
      messageDigest.set(instance);
      return instance;
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
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
      final PKCS8EncodedKeySpec privateKetSpec = new PKCS8EncodedKeySpec(privateKey.get()
          .getEncoded());
      setPrivateKeyBytes(privateKetSpec.getEncoded());
    }
    {
      X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKey.get().getEncoded());
      setPublicKeyBytes(publicSpec.getEncoded());
    }
  }
  
  private void generateKeys() {
    try {
      
      final KeyPairGenerator kpg = KeyPairGenerator.getInstance(conf().keyPairGeneratorAlgorithm());
      
      kpg.initialize(getKeySize(), getRandom());
      
      final KeyPair keyPair = kpg.generateKeyPair();
      
      privateKey.set(keyPair.getPrivate());
      publicKey.set(keyPair.getPublic());
      
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
  
  private void readKeysFromFiles() throws FileNotFoundException {
    try {
      
      final PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(getPrivateKeyBytes());
      final X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(getPublicKeyBytes());
      
      final KeyFactory keyFactory = KeyFactory.getInstance(conf().keyFactoryAlgorithm());
      
      privateKey.set(keyFactory.generatePrivate(keySpecPrivate));
      publicKey.set(keyFactory.generatePublic(keySpecPublic));
      
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }
}
