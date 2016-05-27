package am.saml;

import org.junit.Test;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.stream.IntStream;

public class KeyPairGenerator {

  @Test
  public void testCert() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, IOException, CertificateException, SignatureException {
    String test = System.getProperty("test");
    if (test == null || !test.equals("am.saml.KeyPairGenerator") ) {
      return;
    }
    CertAndKeyGen certGen = new CertAndKeyGen("RSA", "SHA256WithRSA", null);
    certGen.generate(2048);
    long validSecs = 10 * 365 * 24 * 60 * 60;
    X509Certificate cert = certGen.getSelfCertificate(new X500Name("CN=AttributeMapper,O=SURFnet,L=Utrecht,C=NL"), validSecs);
    println(3);
    System.out.println("Certificate");
    println(1);
    System.out.println(new String(Base64.getEncoder().encode(cert.getEncoded())));
    println(3);
    System.out.println("Private key");
    println(1);
    System.out.println(new String(Base64.getEncoder().encode(certGen.getPrivateKey().getEncoded())));
    println(3);
  }


  private void println(int i) {
    IntStream.range(0, i).forEach(j -> System.out.println());
  }
}
