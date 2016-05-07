package am.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity(name = "users")
public class User implements UserDetails {

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
  private boolean confirmed;

  @Column
  private String institution;

  @Column
  private String affiliations;

  @Column
  private String inviteHash;

  @Column
  private Date created;

  @Column
  private String grantedAuthorities;

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
  public Collection<GrantedAuthority> getAuthorities() {
    return this.grantedAuthorities != null ? AuthorityUtils.commaSeparatedStringToAuthorityList(grantedAuthorities) :
      new ArrayList<>();
  }

  public String getGrantedAuthorities() {
    return grantedAuthorities;
  }

  public void setGrantedAuthorities(String grantedAuthorities) {
    this.grantedAuthorities = grantedAuthorities;
  }

  public void addAuthority(GrantedAuthority authority) {
    Collection<GrantedAuthority> authorities = getAuthorities();
    authorities.add(authority);
    this.grantedAuthorities = String.join(",", authorities.stream().map(GrantedAuthority::getAuthority).collect(toList()));
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

  public boolean isConfirmed() {
    return confirmed;
  }

  public void setConfirmed(boolean confirmed) {
    this.confirmed = confirmed;
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

  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", unspecifiedId='" + unspecifiedId + '\'' +
      ", username='" + username + '\'' +
      ", email='" + email + '\'' +
      ", centralIdp='" + centralIdp + '\'' +
      ", mapped=" + mapped +
      ", confirmed=" + confirmed +
      ", institution='" + institution + '\'' +
      ", affiliations='" + affiliations + '\'' +
      ", inviteHash='" + inviteHash + '\'' +
      ", created=" + created +
      ", grantedAuthorities='" + grantedAuthorities + '\'' +
      '}';
  }
}
