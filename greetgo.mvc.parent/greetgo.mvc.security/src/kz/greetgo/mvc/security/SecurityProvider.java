package kz.greetgo.mvc.security;

public interface SecurityProvider {
  String cookieKeySession();

  String cookieKeySignature();

  boolean isUnderSecurityUmbrella(String target);

  String redirectOnSecurityError(String target);

  boolean skipSession(String target);
}
