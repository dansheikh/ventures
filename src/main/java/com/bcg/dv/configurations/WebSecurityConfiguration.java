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
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
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
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataDisplayFilter;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.trust.httpclient.TLSProtocolConfigurer;
import org.springframework.security.saml.trust.httpclient.TLSProtocolSocketFactory;
import org.springframework.security.saml.websso.SingleLogoutProfile;
import org.springframework.security.saml.websso.SingleLogoutProfileImpl;
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
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableWebSecurity
@EnableRedisHttpSession
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final Integer PROTOCOL_PORT = 443;

  @Value("${security.saml2.entity-id}")
  private String entityId;

  @Value("${security.saml2.metadata-url}")
  private String metadataUrl;

  @Value("${security.saml2.timeout}")
  private Integer timeout;

  @Value("${server.port}")
  private String port;

  @Value("${server.ssl.key-store}")
  private String keyStore;

  @Value("${server.ssl.key-store-alias}")
  private String keyStoreAlias;

  @Value("${server.ssl.key-store-password}")
  private String keyStorePassword;

  @Value("${server.ssl.key-password")
  private String keyPassword;

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

/*
  @Bean(name = "tlsProtocolConfigurer")
  public TLSProtocolConfigurer tlsProtocolConfigurer() {
    return new TLSProtocolConfigurer();
  }

  @Bean(name = "protocolSocketFactory")
  public ProtocolSocketFactory protocolSocketFactory() {
    return new TLSProtocolSocketFactory(keyManager(), null, "default");
  }

  @Bean(name = "socketFactoryProtocol")
  public Protocol socketFactoryProtocol() {
    return new Protocol("https", protocolSocketFactory(), PROTOCOL_PORT);
  }

  @Bean(name = "socketFactoryInitialization")
  public MethodInvokingFactoryBean socketFactoryInitialization() {
    MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
    methodInvokingFactoryBean.setTargetClass(Protocol.class);
    methodInvokingFactoryBean.setTargetMethod("registerProtocol");
    Object[] args = {"https", socketFactoryProtocol()};
    methodInvokingFactoryBean.setArguments(args);

    return methodInvokingFactoryBean;
  }
*/

  @Bean(name = "samlBootstrap")
  public static SAMLBootstrap samlBootstrap() {
    return new SAMLBootstrap();
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

  @Bean(name = "extendedMetadataProvider")
  public ExtendedMetadataDelegate extendedMetadataProvider(
      @Qualifier("httpClient") HttpClient httpClient,
      @Qualifier("parserPool") StaticBasicParserPool parserPool,
      @Qualifier("extendedMetadata") ExtendedMetadata extendedMetadata)
      throws MetadataProviderException {
    HTTPMetadataProvider httpMetadataProvider = new HTTPMetadataProvider(backgroundTaskTimer,
        httpClient, metadataUrl);
    httpMetadataProvider.setParserPool(parserPool);
    ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(
        httpMetadataProvider, extendedMetadata);
    extendedMetadataDelegate.setMetadataTrustCheck(false);
    extendedMetadataDelegate.setMetadataRequireSignature(false);
    backgroundTaskTimer.purge();

    return extendedMetadataDelegate;
  }

  @Bean(name = "metadata")
  public CachingMetadataManager metadata(
      @Qualifier("extendedMetadataProvider") ExtendedMetadataDelegate extendedMetadataDelegate)
      throws MetadataProviderException {
    List<MetadataProvider> providers = new ArrayList<>();
    providers.add(extendedMetadataDelegate);
    return new CachingMetadataManager(providers);
  }

  @Bean(name = "keyManager")
  public KeyManager keyManager() {
    DefaultResourceLoader loader = new DefaultResourceLoader();
    Resource keystoreResource = loader.getResource(keyStore);
    Map<String, String> passwords = new HashMap<String, String>();
    passwords.put(keyStoreAlias, keyPassword);

    return new JKSKeyManager(keystoreResource, keyStorePassword, passwords, keyStoreAlias);
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

  @Bean(name = "samlContextProvider")
  public SAMLContextProvider samlContextProvider() {
    return new SAMLContextProviderImpl();
  }

  @Bean(name = "samlDefaultLogger")
  public SAMLDefaultLogger samlDefaultLogger() {
    return new SAMLDefaultLogger();
  }

  @Bean(name = "webSSOprofile")
  public WebSSOProfile webSSOProfile() {
    return new WebSSOProfileImpl();
  }

  @Bean(name = "webSSOProfileConsumer")
  public WebSSOProfileConsumer webSSOProfileConsumer() {
    return new WebSSOProfileConsumerImpl();
  }

  @Bean(name = "singleLogoutProfile")
  public SingleLogoutProfile singleLogoutProfile() {
    return new SingleLogoutProfileImpl();
  }

  @Bean(name = "samlEntryPoint")
  public SAMLEntryPoint samlEntryPoint(
      @Qualifier("defaultWebSSOProfileOptions") WebSSOProfileOptions defaultWebSSOProfileOptions,
      @Qualifier("webSSOprofile") WebSSOProfile webSSOProfile) {
    SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
    samlEntryPoint.setWebSSOprofile(webSSOProfile);
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
        samlEntryPoint(defaultWebSSOProfileOptions(), webSSOProfile())));
    chain.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"),
        samlLogoutFilter(successLogoutHandler(), logoutHandler())));
    chain.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/metadata/**"),
        metadataDisplayFilter()));
    chain.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/sso/**"),
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

  @Bean
  public HttpSessionStrategy httpSessionStrategy() {
    return new HeaderHttpSessionStrategy();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.addFilterBefore(
        metadataGeneratorFilter(metadataGenerator(extendedMetadata(), keyManager())),
        ChannelProcessingFilter.class);
    http.addFilterAfter(samlFilter(), BasicAuthenticationFilter.class);

    http.httpBasic()
        .authenticationEntryPoint(samlEntryPoint(defaultWebSSOProfileOptions(), webSSOProfile()));
    http.csrf().disable();

    http.authorizeRequests().antMatchers("/saml/**").permitAll().anyRequest().authenticated().and()
        .requestCache().requestCache(new NullRequestCache());
  }
}
