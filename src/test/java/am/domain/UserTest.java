package am.domain;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class UserTest {

  private User user ;

  @Before
  public void setUp() throws Exception {
    user = new User();
  }

  @Test
  public void testGetGrantedAuthorities() throws Exception {
    assertEquals(0, user.getAuthorities().size());
    user.setGrantedAuthorities("ROLE_USER, ROLE_ADMIN");

    Collection<GrantedAuthority> authorities = user.getAuthorities();
    assertEquals(2, authorities.size());

    assertEquals(authorities, asList(new SimpleGrantedAuthority("ROLE_USER"),new SimpleGrantedAuthority("ROLE_ADMIN")));
  }

  @Test
  public void testAddAuthority() throws Exception {
    user.addAuthority(new SimpleGrantedAuthority("ROLE_USER"));
    assertEquals(1, user.getAuthorities().size());
  }



}
