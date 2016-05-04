package am.model;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity(name = "users")
public class User implements UserDetails, CredentialsContainer {

  @Id
  @GeneratedValue
  private Long id;

  @Column
  private String unspecifiedId;

  @Column
  private String username;

  @Column
  private String email;

  @Column
  private String centralIdp;

  @Column
  private boolean mapped;

  @Column
  private String institution;

  @Column
  private String affiliations;

  @Column
  private String inviteHash;

  @Column
  private Date created;

  @Transient
  private Collection<? extends GrantedAuthority> authorities;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUnspecifiedId() {
    return unspecifiedId;
  }

  public void setUnspecifiedId(String unspecifiedId) {
    this.unspecifiedId = unspecifiedId;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
    this.authorities = authorities;
  }

  @Override
  public String getPassword() {
    return "N/A";
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCentralIdp() {
    return centralIdp;
  }

  public void setCentralIdp(String centralIdp) {
    this.centralIdp = centralIdp;
  }

  public boolean isMapped() {
    return mapped;
  }

  public void setMapped(boolean mapped) {
    this.mapped = mapped;
  }

  public String getAffiliations() {
    return affiliations;
  }

  public void setAffiliations(String affiliations) {
    this.affiliations = affiliations;
  }

  public String getInviteHash() {
    return inviteHash;
  }

  public void setInviteHash(String inviteHash) {
    this.inviteHash = inviteHash;
  }

  @Override
  public void eraseCredentials() {
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getInstitution() {
    return institution;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }
}
