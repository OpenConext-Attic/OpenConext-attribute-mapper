package am.mail;

import am.AbstractIntegrationTest;
import am.domain.User;
import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;

import javax.mail.internet.MimeMessage;

import static org.junit.Assert.*;

@WebIntegrationTest(randomPort = true, value = {"spring.profiles.active=dev"})
public class MockMailBoxTest extends AbstractIntegrationTest {

  @Autowired
  private MailBox mailBox ;

  @Rule
  public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.ALL);

  @Test
  public void testDoSendMailOn() throws Exception {
    doSendMail();
  }

  private void doSendMail() throws InterruptedException {
    User user = new User();
    user.setEmail("t@t.org");
    user.setInviteHash("hash");
    mailBox.sendConfirmationMail(user);

    //we send async
    Thread.sleep(1500);

    assertEquals(0, greenMail.getReceivedMessages().length);
  }
}
