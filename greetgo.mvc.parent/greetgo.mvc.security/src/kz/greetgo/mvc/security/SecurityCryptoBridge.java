package kz.greetgo.mvc.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.*;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

public class SecurityCryptoBridge implements SecurityCrypto {

  private final SecuritySource securitySource;

  public SecurityCryptoBridge(SecuritySource securitySource) {
    this.securitySource = securitySource;
  }


  private static byte[] encryptBlock(byte[] bytes, SecuritySource securitySource) {
    try {
      Cipher cipher = securitySource.getCipher();
      cipher.init(Cipher.ENCRYPT_MODE, securitySource.getPublicKey());
      return cipher.doFinal(bytes);
    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    }
  }

  private static byte[] decryptBlock(byte[] encryptedBytes, SecuritySource securitySource) {
    try {

      Cipher cipher = securitySource.getCipher();
      cipher.init(Cipher.DECRYPT_MODE, securitySource.getPrivateKey());
      return cipher.doFinal(encryptedBytes);

    } catch (BadPaddingException | IllegalBlockSizeException e) {
      return null;
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    }
  }

  private interface EncryptedData {
    void encryptAndSet(byte[] bytes, SecuritySource securitySource);

    byte[] decryptAndGet(SecuritySource securitySource);
  }

  private static EncryptedData createEncryptedData(byte[] bytes, SecuritySource securitySource) {
    if (bytes == null) return null;
    final EncryptedData ret;
    if (securitySource.getBlockSize() < bytes.length) {
      ret = new ManyBlocks();
    } else {
      ret = new SmallBlock();
    }
    ret.encryptAndSet(bytes, securitySource);
    return ret;
  }


  private static class SmallBlock implements EncryptedData, Serializable {

    byte[] encryptedBytes;

    @Override
    public void encryptAndSet(byte[] bytes, SecuritySource securitySource) {
      encryptedBytes = encryptBlock(bytes, securitySource);
    }

    @Override
    public byte[] decryptAndGet(SecuritySource securitySource) {
      return decryptBlock(encryptedBytes, securitySource);
    }
  }

  private static class ManyBlocks implements EncryptedData, Serializable {

    byte[] encryptedSymmetricKey;

    final List<byte[]> blockList = new ArrayList<>();

    @Override
    public void encryptAndSet(byte[] bytes, SecuritySource securitySource) {
      final byte[] symmetricKey = new byte[securitySource.getBlockSize()];
      securitySource.getRandom().nextBytes(symmetricKey);

      encryptedSymmetricKey = encryptBlock(symmetricKey, securitySource);

      writeToBlockList(blockList, symmetricKey, bytes);
    }

    @Override
    public byte[] decryptAndGet(SecuritySource securitySource) {
      final byte[] symmetricKey = decryptBlock(encryptedSymmetricKey, securitySource);
      if (symmetricKey == null) return null;
      return readFromBlockList(blockList, symmetricKey);
    }
  }

  static byte[] readFromBlockList(List<byte[]> blockList, byte[] symmetricKey) {
    int bytesCount = 0;

    for (final byte[] block : blockList) {
      final int blockLength = block.length;
      for (int j = 0; j < blockLength; j++) {
        block[j] ^= symmetricKey[j];
      }
      bytesCount += blockLength;
    }

    byte[] ret = new byte[bytesCount];

    int filledCount = 0;
    for (byte[] block : blockList) {
      int blockLength = block.length;
      System.arraycopy(block, 0, ret, filledCount, blockLength);
      filledCount += blockLength;
    }

    return ret;
  }

  static void writeToBlockList(List<byte[]> blockList, byte[] symmetricKey, byte[] bytes) {
    int performedCount = 0;

    final int bytesLength = bytes.length;
    final int symmetricKeyLength = symmetricKey.length;

    while (performedCount < bytesLength) {
      int performBorder = performedCount + symmetricKeyLength;
      if (performBorder > bytesLength) performBorder = bytesLength;
      int currentBlockSize = performBorder - performedCount;
      byte[] block = new byte[currentBlockSize];
      System.arraycopy(bytes, performedCount, block, 0, currentBlockSize);
      for (int i = 0; i < currentBlockSize; i++) {
        block[i] ^= symmetricKey[i];
      }
      blockList.add(block);
      performedCount = performBorder;
    }
  }

  @Override
  public byte[] encrypt(byte[] bytes) {
    EncryptedData encryptedData = createEncryptedData(bytes, securitySource);
    if (encryptedData == null) return null;

    ByteArrayOutputStream bOut = new ByteArrayOutputStream();

    try (ObjectOutputStream oos = new ObjectOutputStream(bOut)) {
      oos.writeObject(encryptedData);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return bOut.toByteArray();
  }


  @Override
  public byte[] decrypt(byte[] encryptedBytes) {
    if (encryptedBytes == null) return null;

    ByteArrayInputStream bIn = new ByteArrayInputStream(encryptedBytes);
    try (ObjectInputStream ois = new ObjectInputStream(bIn)) {

      final EncryptedData encryptedData = (EncryptedData) ois.readObject();

      return encryptedData.decryptAndGet(securitySource);

    } catch (IOException | ClassNotFoundException | ClassCastException e) {
      return null;
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

    } catch (BadPaddingException | IllegalBlockSizeException e) {

      return false;

    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    }
  }
}
