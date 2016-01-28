package kz.greetgo.mvc.security;

import kz.greetgo.util.RND;
import org.fest.assertions.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

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
    final SecuritySource_RSA_SHA256 ss = new SecuritySource_RSA_SHA256(1024, privateFile, publicFile);
    return new SecurityCryptoBridge(ss);
  }

  private SecurityCrypto securityCrypto;

  @Test
  public void encrypt_decrypt() throws Exception {
    String source = "Привет всем!!!";

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

  @Test
  public void encrypt_decrypt_load() throws Exception {
    String source = "Привет всем!!!";

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

  @Test
  public void sign_verifySignature() throws Exception {
    String source = "Привет всем!!!";

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

}