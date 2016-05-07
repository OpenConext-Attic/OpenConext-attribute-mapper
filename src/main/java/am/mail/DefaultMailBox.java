package am.mail;

import am.domain.User;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

public class DefaultMailBox implements MailBox {

  @Autowired
  private JavaMailSender mailSender;
  private final String baseUrl;
  private final String from;

  public DefaultMailBox(String baseUrl, String from) {
    this.baseUrl = baseUrl;
    this.from = from;
  }

  @Override
  public void sendConfirmationMail(User user) {
    try {
      Map<String, String> variables = new HashMap();
      variables.put("@@unique_invite_link@@", baseUrl + "/confirmation?inviteHash=" + encodeInviteHash(user.getInviteHash()));
      variables.put("@@from@@", from);
      sendMail("mail/confirmation.html", "SURFConext confirmation", user.getEmail(), variables);
    } catch (MessagingException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String encodeInviteHash(String inviteHash) throws UnsupportedEncodingException {
    String replaced = inviteHash.replaceAll("\\+","%2B");
    return URLEncoder.encode(replaced, "UTF-8");
  }

  private void sendMail(String templateName, String subject, String to, Map<String, String> variables) throws MessagingException, IOException {
    String html = IOUtils.toString(new ClassPathResource(templateName).getInputStream());
    for (Map.Entry<String, String> var : variables.entrySet()) {
      html = html.replaceAll(var.getKey(), var.getValue());
    }
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setSubject(subject);
    helper.setTo(to);
    setText(html, helper);
    helper.setFrom(from);
    doSendMail(message);
  }

  protected void setText(String html, MimeMessageHelper helper) throws MessagingException {
    helper.setText(html, true);
  }

  protected void doSendMail(MimeMessage message) {
    new Thread(() -> mailSender.send(message)).start();
  }

}
