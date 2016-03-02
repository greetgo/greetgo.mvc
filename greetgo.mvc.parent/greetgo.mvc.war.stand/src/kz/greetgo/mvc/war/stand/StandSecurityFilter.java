package kz.greetgo.mvc.war.stand;

import kz.greetgo.mvc.security.*;
import kz.greetgo.mvc.war.SecurityFilter;

import java.io.File;

public class StandSecurityFilter extends SecurityFilter {

  private final SessionStorage sessionStorage;

  private final SecurityCrypto sessionCrypto, signatureCrypto;

  public StandSecurityFilter(String securityDir, SessionStorage sessionStorage) {
    this.sessionStorage = sessionStorage;
    {
      File privateKeyFile = new File(securityDir + "/session.private.key");
      File publicKeyFile = new File(securityDir + "/session.public.key");
      SecuritySource_RSA_SHA256 ss = new SecuritySource_RSA_SHA256(privateKeyFile, publicKeyFile);
      sessionCrypto = new SecurityCryptoBridge(ss);
    }
    {
      File privateKeyFile = new File(securityDir + "/signature.private.key");
      File publicKeyFile = new File(securityDir + "/signature.public.key");
      SecuritySource_RSA_SHA256 ss = new SecuritySource_RSA_SHA256(privateKeyFile, publicKeyFile);
      signatureCrypto = new SecurityCryptoBridge(ss);
    }
  }

  @Override
  protected SecurityCrypto getSessionCrypto() {
    return sessionCrypto;
  }

  @Override
  protected SecurityCrypto getSignatureCrypto() {
    return signatureCrypto;
  }

  @Override
  protected SessionStorage getSessionStorage() {
    return sessionStorage;
  }

  private final SecurityProvider securityProvider = new SecurityProvider() {
    @Override
    public String cookieKeySession() {
      return "GG_SESSION";
    }

    @Override
    public String cookieKeySignature() {
      return "GG_SIGNATURE";
    }

    @Override
    public boolean isUnderSecurityUmbrella(String target) {
      if (target.startsWith("/login")) return false;
      //noinspection RedundantIfStatement
      if (target.startsWith("/img/")) return false;
      return true;
    }

    @Override
    public String redirectOnSecurityError(String target) {
      return "/login.html";
    }

    @Override
    public boolean skipSession(String target) {
      if (target.startsWith("/img/")) return false;
      return false;
    }
  };

  @Override
  protected SecurityProvider getProvider() {
    return securityProvider;
  }
}
