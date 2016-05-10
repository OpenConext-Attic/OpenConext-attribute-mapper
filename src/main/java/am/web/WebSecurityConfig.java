package am.web;

import am.saml.*;
import am.web.mock.MockAuthenticationFilter;
import org.apache.catalina.Container;
import org.apache.catalina.Wrapper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.saml2.binding.decoding.HTTPPostDecoder;
import org.opensaml.saml2.binding.encoding.HTTPPostEncoder;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.saml.*;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.context.SAMLContextProviderLB;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.*;
import org.springframework.security.saml.parser.ParserPoolHolder;
import org.springframework.security.saml.processor.*;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.*;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.Filter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private DefaultSAMLUserDetailsService samlUserDetailsService;

  @Autowired
  private Environment environment;

  @Value("${surfconext_idp.metadata_url}")
  private String surfConextMetadataUrl;

  @Value("${surfconext_idp.entity_id}")
  private String surfConextEntityId;

  @Value("${surfconext_idp.certificate}")
  private String surfConextPublicCertificate;

  @Value("${central_idp.metadata_url}")
  private String centralIdpMetadataUrl;

  @Value("${central_idp.entity_id}")
  private String centralIdpEntityId;

  @Value("${central_idp.certificate}")
  private String centralIdpCertificate;

  @Value("${am.entity_id}")
  private String amEntityId;

  @Value("${am.private_key}")
  private String amPrivateKey;

  @Value("${am.certificate}")
  private String amPublicCertificate;

  @Value("${am.entity_base_url}")
  private String amEntityBaseUrl;

  @Value("${am.passphrase}")
  private String amPassphrase;

  @Bean
  public VelocityEngine velocityEngine() {
    return VelocityFactory.getEngine();
  }

  @Bean(initMethod = "initialize")
  public StaticBasicParserPool parserPool() {
    return new StaticBasicParserPool();
  }

  @Bean(name = "parserPoolHolder")
  public ParserPoolHolder parserPoolHolder() {
    return new ParserPoolHolder();
  }

  @Bean
  public MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager() {
    return new MultiThreadedHttpConnectionManager();
  }

  @Bean
  public HttpClient httpClient() {
    return new HttpClient(multiThreadedHttpConnectionManager());
  }

  @Bean
  @Autowired
  public SAMLAuthenticationProvider samlAuthenticationProvider() {
    SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
    samlAuthenticationProvider.setUserDetails(samlUserDetailsService);
    samlAuthenticationProvider.setForcePrincipalAsString(false);
    return samlAuthenticationProvider;
  }

  @Bean
  public SAMLContextProviderImpl contextProvider() throws URISyntaxException {
    return new ProxiedSAMLContextProviderLB(new URI(amEntityBaseUrl));
  }

  @Bean
  public static SAMLBootstrap sAMLBootstrap() {
    return new SAMLBootstrap();
  }

  @Bean
  public SAMLDefaultLogger samlLogger() {
    return new SAMLDefaultLogger();
  }

  @Bean
  public WebSSOProfileConsumer webSSOprofileConsumer() {
    return new WebSSOProfileConsumerImpl();
  }

  @Bean
  public WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
    return new WebSSOProfileConsumerHoKImpl();
  }

  @Bean
  public WebSSOProfile webSSOprofile() {
    return new WebSSOProfileImpl();
  }

  @Bean
  public WebSSOProfileECPImpl ecpprofile() {
    return new WebSSOProfileECPImpl();
  }

  @Bean
  public SingleLogoutProfile logoutprofile() {
    return new SingleLogoutProfileImpl();
  }

  @Bean
  public KeyManager keyManager() throws InvalidKeySpecException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
    KeyStoreLocator keyStoreLocator = new KeyStoreLocator();
    KeyStore keyStore = keyStoreLocator.createKeyStore(amPassphrase);

    keyStoreLocator.addPrivateKey(keyStore, amEntityId, amPrivateKey, amPublicCertificate, amPassphrase);
    keyStoreLocator.addCertificate(keyStore, centralIdpEntityId, centralIdpCertificate);
    keyStoreLocator.addCertificate(keyStore, surfConextEntityId, surfConextPublicCertificate);

    return new JKSKeyManager(keyStore, Collections.singletonMap(amEntityId, amPassphrase), amEntityId);
  }

  @Bean
  public WebSSOProfileOptions defaultWebSSOProfileOptions() {
    WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
    webSSOProfileOptions.setIncludeScoping(false);
    return webSSOProfileOptions;
  }

  @Bean
  public SAMLEntryPoint samlEntryPoint() {
    SAMLEntryPoint samlEntryPoint = new DefaultSAMLEntryPoint(centralIdpEntityId, surfConextEntityId);
    samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
    return samlEntryPoint;
  }

  @Bean
  public ExtendedMetadata extendedMetadata() {
    ExtendedMetadata extendedMetadata = new ExtendedMetadata();
    extendedMetadata.setIdpDiscoveryEnabled(false);
    extendedMetadata.setSignMetadata(false);
    return extendedMetadata;
  }

  private MetadataProvider metadataProvider(String resource) throws MetadataProviderException {
    ResourceMetadataProvider resourceMetadataProvider = new ResourceMetadataProvider(new DefaultResourceLoader().getResource(resource));
    resourceMetadataProvider.setParserPool(parserPool());
    ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(resourceMetadataProvider, extendedMetadata());
    extendedMetadataDelegate.setMetadataTrustCheck(true);
    extendedMetadataDelegate.setMetadataRequireSignature(false);
    return extendedMetadataDelegate;
  }

  @Bean
  @Qualifier("central-idp")
  public MetadataProvider centralIdpExtendedMetadataProvider()
    throws MetadataProviderException {
    return metadataProvider(centralIdpMetadataUrl);
  }

  @Bean
  @Qualifier("eb")
  public MetadataProvider engineBlockExtendedMetadataProvider()
    throws MetadataProviderException {
    return metadataProvider(surfConextMetadataUrl);
  }

  @Bean
  @Qualifier("metadata")
  public CachingMetadataManager metadata() throws MetadataProviderException {
    List<MetadataProvider> providers = new ArrayList<>();
    providers.add(centralIdpExtendedMetadataProvider());
    providers.add(engineBlockExtendedMetadataProvider());

    CachingMetadataManager cachingMetadataManager = new CachingMetadataManager(providers);
    //todo add more metadata
    return cachingMetadataManager;
  }

  @Bean
  public MetadataGenerator metadataGenerator() throws NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, KeyStoreException, IOException {
    MetadataGenerator metadataGenerator = new MetadataGenerator();
    metadataGenerator.setEntityId(amEntityId);
    metadataGenerator.setEntityBaseURL(amEntityBaseUrl);
    metadataGenerator.setExtendedMetadata(extendedMetadata());
    metadataGenerator.setIncludeDiscoveryExtension(false);
    metadataGenerator.setKeyManager(keyManager());
    return metadataGenerator;
  }

  @Bean
  public MetadataDisplayFilter metadataDisplayFilter() {
    return new DefaultMetadataDisplayFilter();
  }

  @Bean
  public SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler() {
    SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    successRedirectHandler.setDefaultTargetUrl("/mappings");
    return successRedirectHandler;
  }

  @Bean
  public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
    SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
    failureHandler.setUseForward(true);
    failureHandler.setDefaultFailureUrl("/error");
    return failureHandler;
  }

  @Bean
  public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
    SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
    samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager());
    samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(successRedirectHandler());
    samlWebSSOProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
    return samlWebSSOProcessingFilter;
  }

  @Bean
  public MetadataGeneratorFilter metadataGeneratorFilter() throws InvalidKeySpecException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
    return new MetadataGeneratorFilter(metadataGenerator());
  }

  @Bean
  public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
    SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
    successLogoutHandler.setDefaultTargetUrl("/");
    return successLogoutHandler;
  }

  @Bean
  public SecurityContextLogoutHandler logoutHandler() {
    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    logoutHandler.setInvalidateHttpSession(true);
    logoutHandler.setClearAuthentication(true);
    return logoutHandler;
  }

  @Bean
  public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
    return new SAMLLogoutProcessingFilter(successLogoutHandler(), logoutHandler());
  }

  @Bean
  public SAMLLogoutFilter samlLogoutFilter() {
    return new SAMLLogoutFilter(successLogoutHandler(), new LogoutHandler[]{logoutHandler()}, new LogoutHandler[]{logoutHandler()});
  }

  private ArtifactResolutionProfile artifactResolutionProfile() {
    final ArtifactResolutionProfileImpl artifactResolutionProfile = new ArtifactResolutionProfileImpl(httpClient());
    artifactResolutionProfile.setProcessor(new SAMLProcessorImpl(soapBinding()));
    return artifactResolutionProfile;
  }

  @Bean
  public HTTPArtifactBinding artifactBinding(ParserPool parserPool, VelocityEngine velocityEngine) {
    return new HTTPArtifactBinding(parserPool, velocityEngine, artifactResolutionProfile());
  }

  @Bean
  public HTTPSOAP11Binding soapBinding() {
    return new HTTPSOAP11Binding(parserPool());
  }

  @Bean
  public HTTPPostBinding httpPostBinding() {
    ParserPool parserPool = parserPool();
    HTTPPostDecoder decoder = new HTTPPostDecoder(parserPool);
    HTTPPostEncoder encoder = new HTTPPostEncoder(velocityEngine(), "/templates/saml2-post-binding.vm");

    decoder.setURIComparator(new DefaultURIComparator());
    return new HTTPPostBinding(parserPool(), decoder, encoder);
  }

  @Bean
  public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
    return new HTTPRedirectDeflateBinding(parserPool());
  }

  @Bean
  public HTTPSOAP11Binding httpSOAP11Binding() {
    return new HTTPSOAP11Binding(parserPool());
  }

  @Bean
  public HTTPPAOS11Binding httpPAOS11Binding() {
    return new HTTPPAOS11Binding(parserPool());
  }

  @Bean
  public SAMLProcessorImpl processor() {
    Collection<SAMLBinding> bindings = new ArrayList<>();
    bindings.add(httpRedirectDeflateBinding());
    bindings.add(httpPostBinding());
    bindings.add(artifactBinding(parserPool(), velocityEngine()));
    bindings.add(httpSOAP11Binding());
    bindings.add(httpPAOS11Binding());
    return new SAMLProcessorImpl(bindings);
  }

  @Bean
  public FilterChainProxy samlFilter() throws Exception {
    List<SecurityFilterChain> chains = new ArrayList<>();
    chains.add(chain("/saml/login/**", samlEntryPoint()));
    chains.add(chain("/saml/logout/**", samlLogoutFilter()));
    chains.add(chain("/saml/metadata/**", metadataDisplayFilter()));
    chains.add(chain("/saml/SSO/**", samlWebSSOProcessingFilter()));
    chains.add(chain("/saml/SingleLogout/**", samlLogoutProcessingFilter()));
    return new FilterChainProxy(chains);
  }

  private DefaultSecurityFilterChain chain(String pattern, Filter entryPoint) {
    return new DefaultSecurityFilterChain(new AntPathRequestMatcher(pattern), entryPoint);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/health");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .httpBasic().authenticationEntryPoint(samlEntryPoint())
      .and()
      .csrf().disable()
      .addFilterBefore(metadataGeneratorFilter(), ChannelProcessingFilter.class)
      .addFilterAfter(samlFilter(), BasicAuthenticationFilter.class)
      .authorizeRequests()
      .antMatchers("/", "/health", "/info", "/confirmation", "/error", "/404", "/saml/**").permitAll()
      .anyRequest().hasRole("USER")
      .and()
      .logout()
      .logoutSuccessUrl("/");

    if (environment.acceptsProfiles("dev")) {
      http.addFilterBefore(new MockAuthenticationFilter(), BasicAuthenticationFilter.class);
    }
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(samlAuthenticationProvider());
  }

  @Order(1)
  @Configuration
  public static class ApiSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Value("${am.api.name}")
    private String name;

    @Value("${am.api.password}")
    private String password;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
        .antMatcher("/api/**")
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .csrf().disable()
        .addFilterBefore(new BasicAuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class)
        .authorizeRequests()
        .antMatchers("/api/**").hasRole("API");
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      auth.inMemoryAuthentication().withUser(name).password(password).roles("API");
    }
  }

}
