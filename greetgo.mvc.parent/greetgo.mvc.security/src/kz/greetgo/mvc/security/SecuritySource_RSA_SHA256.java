package kz.greetgo.mvc.security;

import kz.greetgo.util.ServerUtil;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static kz.greetgo.util.ServerUtil.dummyCheck;
import static kz.greetgo.util.ServerUtil.streamToByteArray;

public class SecuritySource_RSA_SHA256 implements SecuritySource {

  private final int keySize;
  private final File privateKeyFile;
  private final File publicKeyFile;

  public SecuritySource_RSA_SHA256(int keySize, File privateKeyFile, File publicKeyFile) {
    this.keySize = keySize;
    this.privateKeyFile = privateKeyFile;
    this.publicKeyFile = publicKeyFile;
  }

  private final Cipher cipher;

  {
    try {
      cipher = Cipher.getInstance("RSA");
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Cipher getCipher() {
    return cipher;
  }

  private PublicKey publicKey = null;

  @Override
  public PublicKey getPublicKey() {
    if (publicKey == null) prepareKeys();
    return publicKey;
  }

  private PrivateKey privateKey = null;

  @Override
  public PrivateKey getPrivateKey() {
    if (privateKey == null) prepareKeys();
    return privateKey;
  }

  @Override
  public MessageDigest getMessageDigest() {
    return messageDigest;
  }

  private final MessageDigest messageDigest;

  {
    try {
      messageDigest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private void prepareKeys() {
    try {
      readKeysFromFiles();
    } catch (FileNotFoundException e) {
      generateKeys();
      saveKeys();
    }
  }

  private void saveKeys() {
    {
      dummyCheck(privateKeyFile.getParentFile().mkdirs());
      try (FileOutputStream out = new FileOutputStream(privateKeyFile)) {
        final PKCS8EncodedKeySpec privateKetSpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        out.write(privateKetSpec.getEncoded());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    {
      dummyCheck(publicKeyFile.getParentFile().mkdirs());
      try (FileOutputStream out = new FileOutputStream(publicKeyFile)) {
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKey.getEncoded());
        out.write(publicSpec.getEncoded());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void generateKeys() {
    try {

      final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
      final SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

      kpg.initialize(keySize, random);

      final KeyPair keyPair = kpg.generateKeyPair();

      privateKey = keyPair.getPrivate();
      publicKey = keyPair.getPublic();

    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private void readKeysFromFiles() throws FileNotFoundException {
    try {

      final byte[] privateKeyBytes = streamToByteArray(new FileInputStream(privateKeyFile));
      final byte[] publicKeyBytes = streamToByteArray(new FileInputStream(publicKeyFile));

      final PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(privateKeyBytes);
      final X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(publicKeyBytes);

      final KeyFactory keyFactory = KeyFactory.getInstance("RSA");

      privateKey = keyFactory.generatePrivate(keySpecPrivate);
      publicKey = keyFactory.generatePublic(keySpecPublic);

    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }
}
