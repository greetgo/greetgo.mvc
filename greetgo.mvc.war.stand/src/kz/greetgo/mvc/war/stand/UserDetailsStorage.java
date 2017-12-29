package kz.greetgo.mvc.war.stand;

import kz.greetgo.mvc.security.ObjectSessionStorage;

public class UserDetailsStorage extends ObjectSessionStorage {
  public UserDetails getUserDetails() {
    return (UserDetails) getObject();
  }

  public void setUserDetails(UserDetails userDetails) {
    setObject(userDetails);
  }

  public String getUsername() {
    final UserDetails userDetails = getUserDetails();
    if (userDetails == null) return null;
    return userDetails.username;
  }

  public void setUsername(String username) {
    if (username == null) {
      setUserDetails(null);
      return;
    }

    UserDetails userDetails = getUserDetails();
    if (userDetails == null) {
      userDetails = new UserDetails();
      setUserDetails(userDetails);
    }

    userDetails.username = username;
  }
}
