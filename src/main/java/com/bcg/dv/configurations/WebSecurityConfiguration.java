package com.bcg.dv.configurations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataDisplayFilter;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.websso.WebSSOProfile;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
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

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Value("${security.saml2.oktaMetadataUrl}")
  private String oktaMetadataUrl;

  @Value("${security.saml2.timeout}")
  private Integer timeout;

  @Value("${security.saml2.password}")
  private String password;

  @Value("${security.saml2.username}")
  private String username;

  @Value("${security.saml2.entity-id}")
  private String entityId;

  @Value("${security.saml2.metadata-url}")
  private String metadataUrl;

  @Value("${server.port}")
  private String port;

  @Value("${server.ssl.key-alias}")
  private String keyAlias;

  @Value("${server.ssl.key-store}")
  private String keyStorePath;

  @Value("${server.ssl.key-store-password}")
  private String keyStorePassword;

  private Timer backgroundTaskTimer;
  private MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager;

  @PostConstruct
  public void init() {
    this.backgroundTaskTimer = new Timer(true);
    this.multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
  }

  @PreDestroy
  public void destroy() {
    backgroundTaskTimer.purge();
    backgroundTaskTimer.cancel();
    multiThreadedHttpConnectionManager.shutdown();
  }

  @Bean(name = "parserPool", initMethod = "initialize")
  public StaticBasicParserPool parserPool() {
    return new StaticBasicParserPool();
  }

  @Bean(name = "extendedMetadata")
  public ExtendedMetadata extendedMetadata() {
    ExtendedMetadata extendedMetadata = new ExtendedMetadata();
    extendedMetadata.setIdpDiscoveryEnabled(false);
    extendedMetadata.setSignMetadata(true);

    return extendedMetadata;
  }

  @Bean(name = "httpClient")
  public HttpClient httpClient() {
    return new HttpClient(multiThreadedHttpConnectionManager);
  }

  @Bean(name = "oktaExtendedMetadataProvider")
  public ExtendedMetadataDelegate oktaExtendedMetadataProvider(
      @Qualifier("httpClient") HttpClient httpClient,
      @Qualifier("parserPool") StaticBasicParserPool parserPool,
      @Qualifier("extendedMetadata") ExtendedMetadata extendedMetadata)
      throws MetadataProviderException {
    HTTPMetadataProvider httpMetadataProvider = new HTTPMetadataProvider(backgroundTaskTimer,
        httpClient, oktaMetadataUrl);
    httpMetadataProvider.setParserPool(parserPool);
    ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(
        httpMetadataProvider, extendedMetadata);
    extendedMetadataDelegate.setMetadataTrustCheck(true);
    extendedMetadataDelegate.setMetadataRequireSignature(false);
    backgroundTaskTimer.purge();

    return extendedMetadataDelegate;
  }

  @Bean(name = "metadata")
  public CachingMetadataManager metadata(
      @Qualifier("oktaExtendedMetadataProvider") ExtendedMetadataDelegate extendedMetadataDelegate)
      throws MetadataProviderException {
    List<MetadataProvider> providers = new ArrayList<>();
    providers.add(extendedMetadataDelegate);
    return new CachingMetadataManager(providers);
  }

  @Bean(name = "keyManager")
  public KeyManager keyManager() {
    DefaultResourceLoader loader = new DefaultResourceLoader();
    Resource keystoreResource = loader.getResource("classpath:/saml/samlKeystore.jks");
    Map<String, String> userPasswords = new HashMap<String, String>();
    userPasswords.put(username, password);

    return new JKSKeyManager(keystoreResource, password, userPasswords, username);
  }

  @Bean(name = "metadataGenerator")
  public MetadataGenerator metadataGenerator(
      @Qualifier("extendedMetadata") ExtendedMetadata extendedMetadata,
      @Qualifier("keyManager") KeyManager keyManager) {
    MetadataGenerator metadataGenerator = new MetadataGenerator();
    metadataGenerator.setEntityId(entityId);
    metadataGenerator.setExtendedMetadata(extendedMetadata);
    metadataGenerator.setKeyManager(keyManager);

    return metadataGenerator;
  }

  @Bean(name = "metadataGeneratorFilter")
  public MetadataGeneratorFilter metadataGeneratorFilter(
      @Qualifier("metadataGenerator") MetadataGenerator metadataGenerator) {
    return new MetadataGeneratorFilter(metadataGenerator);
  }

  @Bean(name = "defaultWebSSOProfileOptions")
  public WebSSOProfileOptions defaultWebSSOProfileOptions() {
    WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
    webSSOProfileOptions.setIncludeScoping(false);
    webSSOProfileOptions.setBinding("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");

    return webSSOProfileOptions;
  }

  @Bean(name = "samlEntryPoint")
  public SAMLEntryPoint samlEntryPoint(
      @Qualifier("defaultWebSSOProfileOptions") WebSSOProfileOptions defaultWebSSOProfileOptions) {
    SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
    samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions);

    return samlEntryPoint;
  }

  @Bean(name = "successLogoutHandler")
  public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
    SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
    successLogoutHandler.setDefaultTargetUrl("/");

    return successLogoutHandler;
  }

  @Bean(name = "logoutHandler")
  public SecurityContextLogoutHandler logoutHandler() {
    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    logoutHandler.setInvalidateHttpSession(true);
    logoutHandler.setClearAuthentication(true);

    return logoutHandler;
  }

  @Bean(name = "samlLogoutFilter")
  public SAMLLogoutFilter samlLogoutFilter(
      @Qualifier("successLogoutHandler") SimpleUrlLogoutSuccessHandler successLogoutHandler,
      @Qualifier("logoutHandler") SecurityContextLogoutHandler logoutHandler) {
    return new SAMLLogoutFilter(successLogoutHandler, new LogoutHandler[]{logoutHandler},
        new LogoutHandler[]{logoutHandler});
  }

  @Bean(name = "metadataDisplayFilter")
  public MetadataDisplayFilter metadataDisplayFilter() {
    return new MetadataDisplayFilter();
  }

  @Bean(name = "authenticationManger")
  @Override
  public AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean(name = "authenticationSuccessHandler")
  public SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler() {
    SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    authenticationSuccessHandler.setDefaultTargetUrl("/");

    return authenticationSuccessHandler;
  }

  @Bean(name = "authenticationFailureHandler")
  public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
    SimpleUrlAuthenticationFailureHandler authenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler();
    authenticationFailureHandler.setUseForward(true);
    authenticationFailureHandler.setDefaultFailureUrl("/");

    return authenticationFailureHandler;
  }

  @Bean(name = "samlWebSSOProcessingFilter")
  public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
    SAMLProcessingFilter samlProcessingFilter = new SAMLProcessingFilter();
    samlProcessingFilter.setAuthenticationManager(authenticationManager());
    samlProcessingFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
    samlProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());

    return samlProcessingFilter;
  }

  @Bean(name = "samlLogoutProcessingFilter")
  public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
    return new SAMLLogoutProcessingFilter(successLogoutHandler(), logoutHandler());
  }

  @Bean(name = "samlFilter")
  public FilterChainProxy samlFilter() throws Exception {
    List<SecurityFilterChain> chain = new ArrayList<>();
    chain.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"),
        samlEntryPoint(defaultWebSSOProfileOptions())));
    chain.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"),
        samlLogoutFilter(successLogoutHandler(), logoutHandler())));
    chain.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/metadata/**"),
        metadataDisplayFilter()));
    chain.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"),
        samlWebSSOProcessingFilter()));
    chain.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/singleLogout/**"),
        samlLogoutProcessingFilter()));

    return new FilterChainProxy(chain);
  }

  @Bean(name = "httpRedirectDeflateBinding")
  public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
    return new HTTPRedirectDeflateBinding(parserPool());
  }

  @Bean(name = "samlProcessor")
  public SAMLProcessorImpl samlProcessor() {
    Collection<SAMLBinding> bindings = new ArrayList<>();
    bindings.add(httpRedirectDeflateBinding());

    return new SAMLProcessorImpl(bindings);
  }

  @Bean(name = "webSSOProfileConsumer")
  public WebSSOProfileConsumer webSSOProfileConsumer() {
    return new WebSSOProfileConsumerImpl();
  }

  @Bean(name = "webSSOProfile")
  public WebSSOProfile webSSOProfile() {
    return new WebSSOProfileImpl();
  }

  @Bean
  public SAMLBootstrap samlBootstrap() {
    return new SAMLBootstrap();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.addFilterBefore(
        metadataGeneratorFilter(metadataGenerator(extendedMetadata(), keyManager())),
        ChannelProcessingFilter.class);
    http.addFilterAfter(samlFilter(), BasicAuthenticationFilter.class);

    http.httpBasic().authenticationEntryPoint(samlEntryPoint(defaultWebSSOProfileOptions()));
    http.csrf().disable();

    http.authorizeRequests().antMatchers("/saml/**").permitAll().anyRequest().authenticated();
  }
}
