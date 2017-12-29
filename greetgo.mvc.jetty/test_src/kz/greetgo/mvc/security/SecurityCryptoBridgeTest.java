package kz.greetgo.mvc.security;

import kz.greetgo.util.RND;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;
import static org.fest.assertions.api.Assertions.assertThat;

public class SecurityCryptoBridgeTest {

  private File privateFile, publicFile;

  @BeforeMethod
  public void setUp() throws Exception {

    String keyDir = "build/test_keys/test_" + RND.intStr(10) + "/";
    privateFile = new File(keyDir + "private.bin");
    publicFile = new File(keyDir + "public.bin");

    securityCrypto = createOn(privateFile, publicFile);
  }

  private SecurityCrypto createOn(File privateFile, File publicFile) {
    final SecuritySourceOnFiles ss = new SecuritySourceOnFiles(privateFile, publicFile);
    return new SecurityCryptoBridge(ss);
  }

  private SecurityCrypto securityCrypto;

  @DataProvider
  public Object[][] sourceDataProvider() {
    return new Object[][]{

      new Object[]{"Привет всем!!! "},

      new Object[]{"Привет всем!!! " + RND.str(117)},

    };
  }

  @Test(dataProvider = "sourceDataProvider")
  public void encrypt_decrypt(String source) throws Exception {

    //
    //
    final byte[] encryptedBytes = securityCrypto.encrypt(source.getBytes("UTF-8"));
    //
    //

    //
    //
    final byte[] originalBytes = securityCrypto.decrypt(encryptedBytes);
    //
    //

    final String actual = new String(originalBytes, "UTF-8");

    assertThat(actual).isEqualTo(source);
  }

  @Test(dataProvider = "sourceDataProvider")
  public void encrypt_decrypt_load(String source) throws Exception {

    //
    //
    final byte[] encryptedBytes = securityCrypto.encrypt(source.getBytes("UTF-8"));
    //
    //

    final SecurityCrypto newSecurityCrypto = createOn(privateFile, publicFile);

    //
    //
    final byte[] originalBytes = newSecurityCrypto.decrypt(encryptedBytes);
    //
    //

    final String actual = new String(originalBytes, "UTF-8");

    assertThat(actual).isEqualTo(source);
  }

  @Test(dataProvider = "sourceDataProvider")
  public void sign_verifySignature(String source) throws Exception {

    //
    //
    final byte[] signature = securityCrypto.sign(source.getBytes("UTF-8"));
    //
    //

    final SecurityCrypto newSecurityCrypto = createOn(privateFile, publicFile);

    //
    //
    boolean verified = newSecurityCrypto.verifySignature(source.getBytes("UTF-8"), signature);
    //
    //

    assertThat(verified).isTrue();
  }

  @Test
  public void readFromBlockList() {
    byte[] symmetricKey = new byte[10];
    Arrays.fill(symmetricKey, (byte) 0);
    byte[] line1 = RND.byteArray(3);
    byte[] line2 = RND.byteArray(7);
    byte[] line3 = RND.byteArray(10);

    List<byte[]> blockList = new ArrayList<>();
    blockList.add(line1);
    blockList.add(line2);
    blockList.add(line3);

    //
    //
    final byte[] result = SecurityCryptoBridge.readFromBlockList(blockList, symmetricKey);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result).hasSize(20);

    assertThat(copyOfRange(result, 0, 3)).isEqualTo(line1);
    assertThat(copyOfRange(result, 3, 10)).isEqualTo(line2);
    assertThat(copyOfRange(result, 10, 20)).isEqualTo(line3);

  }

  @Test
  public void writeToBlockList() {
    byte[] symmetricKey = new byte[7];
    Arrays.fill(symmetricKey, (byte) 0);
    byte[] line1 = RND.byteArray(7);
    byte[] line2 = RND.byteArray(7);
    byte[] line3 = RND.byteArray(3);

    byte[] bytes = new byte[7 + 7 + 3];
    arraycopy(line1, 0, bytes, 0, 7);
    arraycopy(line2, 0, bytes, 7, 7);
    arraycopy(line3, 0, bytes, 14, 3);

    List<byte[]> blockList = new ArrayList<>();


    //
    //
    SecurityCryptoBridge.writeToBlockList(blockList, symmetricKey, bytes);
    //
    //

    assertThat(blockList).hasSize(3);

    assertThat(blockList.get(0)).isEqualTo(line1);
    assertThat(blockList.get(1)).isEqualTo(line2);
    assertThat(blockList.get(2)).isEqualTo(line3);

  }

  @Test
  public void writeToBlockList_readFromBlockList() {

    byte[] symmetricKey = RND.byteArray(7);
    byte[] bytes = RND.byteArray(17);

    List<byte[]> blockList = new ArrayList<>();

    //
    //
    SecurityCryptoBridge.writeToBlockList(blockList, symmetricKey, bytes);
    //
    //

    assertThat(blockList).hasSize(3);

    //
    //
    final byte[] result = SecurityCryptoBridge.readFromBlockList(blockList, symmetricKey);
    //
    //

    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(bytes);
  }

}
