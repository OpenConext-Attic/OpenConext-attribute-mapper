package am.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class MailConfiguration {

  @Value("${email.base_url}")
  private String baseUrl;

  @Value("${email.from}")
  private String emailFrom;

  @Bean
  @Profile({"!dev"})
  public MailBox mailSenderProd() {
    return new DefaultMailBox(baseUrl, emailFrom);
  }

  @Bean
  @Profile({"dev"})
  @Primary
  public MailBox mailSenderDev() {
    return new MockMailBox(baseUrl, emailFrom);
  }
}
