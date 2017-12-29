package kz.greetgo.mvc.interfaces;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public interface RequestSession {
  HttpSession getSession(boolean create);

  HttpSession getSession();

  String changeSessionId();

  boolean isRequestedSessionIdValid();

  boolean isRequestedSessionIdFromCookie();

  boolean isRequestedSessionIdFromURL();

  boolean authenticate() throws IOException, ServletException;

  void login(String username, String password) throws ServletException;

  void logout() throws ServletException;

  String getRemoteUser();

  String getAuthType();

  boolean isSecure();
}
