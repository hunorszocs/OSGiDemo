package hu.blackbelt.core.webservice;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.base.Predicates;
import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.BusFactory;
import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.cxf.transport.http.DestinationRegistry;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.apache.sling.commons.mime.MimeTypeService;
import org.osgi.framework.*;
import org.osgi.service.component.annotations.*;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component(immediate = true, service = JaxRsServiceManager.class)
//@Service
//@Properties(value = {
//        @Property(name = JaxRsServiceManager.PROP_SERVICE_ROOT_URL, value = JaxRsServiceManager.DEFAULT_SERVICE_ROOT_URL),
//        @Property(name = JaxRsServiceManager.PROP_SERVICE_PACKAGE_ROOT, value = JaxRsServiceManager.DEFAULT_SERVICE_PACKAGE_ROOT),
//        @Property(name = JaxRsServiceManager.PROP_REQUEST_LOG, boolValue = JaxRsServiceManager.DEFAULT_REQUEST_LOG),
//        @Property(name = JaxRsServiceManager.PROP_REQUEST_LOG_DETAILS, boolValue = JaxRsServiceManager.DEFAULT_REQUEST_LOG_DETAILS)
//
//})

public class JaxRsServiceManager implements ServiceListener {
    private static final long serialVersionUID = 1L;

    public static final String PROP_SERVICE_ROOT_URL = "root";
    public static final String DEFAULT_SERVICE_ROOT_URL = "/webservices";

    public static final String PROP_SERVICE_PACKAGE_ROOT = "packageRoot";
    public static final String DEFAULT_SERVICE_PACKAGE_ROOT = "hu.blackbelt";

    public static final String PROP_REQUEST_LOG = "request.log";
    public static final boolean DEFAULT_REQUEST_LOG = false;

    public static final String PROP_REQUEST_LOG_DETAILS = "request.log.details";
    public static final boolean DEFAULT_REQUEST_LOG_DETAILS = false;


    private String serviceRootUrl = PROP_SERVICE_ROOT_URL;
    private String packageRoot = PROP_SERVICE_PACKAGE_ROOT;


    @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC)
    private volatile HttpService osgiHttpService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC)
    private volatile MimeTypeService mimeTypeService;

    private BundleContext context;
    private WebServiceThreadLocalRequestFilter filter;
    private ServiceRegistration filterRegistration;
    private Map<String, HttpServlet> registeredServlets = new ConcurrentHashMap<>();

    private final Logger log = LoggerFactory.getLogger(JaxRsServiceManager.class);

    private Server jaxRsServer;
    private Bus bus;
    private DestinationRegistry destinationRegistry;

    private Map<Class<?>, Object> registeredServices = new ConcurrentHashMap<>();
    private Map<Class<?>, Object> registeredProviders = new ConcurrentHashMap<>();

    @Activate
    protected void activate(BundleContext context) {

        System.out.println(" --- Activating WebServiceRegistrationListener");

        // Setting up properties
        serviceRootUrl = DEFAULT_SERVICE_ROOT_URL;//= PropertiesUtil.toString(context.getProperties().get(PROP_SERVICE_ROOT_URL), DEFAULT_SERVICE_ROOT_URL);
        packageRoot = DEFAULT_SERVICE_PACKAGE_ROOT;//= PropertiesUtil.toString(context.getProperties().get(PROP_SERVICE_PACKAGE_ROOT), DEFAULT_SERVICE_PACKAGE_ROOT);
        try {

            bus = BusFactory.newInstance().createBus();

            /*if (toBoolean(context.getProperties().get(PROP_REQUEST_LOG), DEFAULT_REQUEST_LOG)) {
                bus.getInInterceptors().addAll(loggers(toBoolean(context.getProperties().get(PROP_REQUEST_LOG_DETAILS), DEFAULT_REQUEST_LOG_DETAILS)));
            }*/

            //bus.setExtension(pckClassLoader, ClassLoader.class);
            DestinationFactoryManager dfm = bus.getExtension(DestinationFactoryManager.class);
            destinationRegistry = null;
            try {
                DestinationFactory df = dfm
                        .getDestinationFactory("http://cxf.apache.org/transports/http/configuration");
                if (df instanceof HTTPTransportFactory) {
                    HTTPTransportFactory transportFactory = (HTTPTransportFactory) df;
                    destinationRegistry = transportFactory.getRegistry();
                }
            } catch (BusException e) {
                log.warn("DestinationFactoryManager not found - {}", e.getLocalizedMessage());
                log.debug("Exception: ", e);
            }

            if (destinationRegistry != null) {
                HttpContext httpContext = new HttpContext() {

                    @Override
                    public boolean handleSecurity(HttpServletRequest request,
                                                  HttpServletResponse response) throws IOException {
                        return true;
                    }

                    // this context provides no resources, always call the servlet
                    @Override
                    public URL getResource(String name) {
                        return null;
                    }

                    @Override
                    public String getMimeType(String name) {
                        MimeTypeService mts = JaxRsServiceManager.this.mimeTypeService;
                        return (mts != null) ? mts.getMimeType(name) : null;
                    }
                };

                final CXFNonSpringServlet cxfServlet = new CXFNonSpringServlet(destinationRegistry, false);
                registeredServlets.put(serviceRootUrl, cxfServlet);

                try {
                    System.out.println(" --- Registering CXF Root servlet: " + serviceRootUrl);
                    Dictionary props;
                    props = new Hashtable();
                    props.put("alias", serviceRootUrl);

                    osgiHttpService.registerServlet(serviceRootUrl, cxfServlet, props, httpContext);

                    System.out.println(" --- Registering WebServiceThreadLocalRequestFilter");
                    // Registering threadLocalRequest servlet filter also
                    Dictionary<String, Comparable> filterProps = new Hashtable<String, Comparable>();
                    filterProps.put("pattern", serviceRootUrl + "/.*");
                    filterProps.put("alias", serviceRootUrl);

                    filterProps.put("init.message", "WebServiceThreadLocalRequestFilter!");
                    filterProps.put("service.ranking", "1");
                    filter = new WebServiceThreadLocalRequestFilter();
                    filter.setWebServiceRegistrationListener(this);

                    filterRegistration = context.registerService(Filter.class.getName(), filter, filterProps);

                } catch (ServletException e) {
                    log.error("Error on servlet registration: {} - {}", serviceRootUrl, e.getLocalizedMessage());
                    log.debug("Exception: ", e);
                } catch (NamespaceException e) {
                    log.error("Could not register servlet to URL: {}  (Another servlet on URL?) - {}", serviceRootUrl, e.getLocalizedMessage());
                    log.debug("Exception: ", e);
                }

            }

        } catch (Throwable th) {
            log.error("activate() - {}", th.getLocalizedMessage());
            log.debug("Exception: ", th);
        } finally {
        }

        // The listener can process changes from here
        this.context = context;

        // If this started later we search for all references to register
        try {
            ServiceReference[] refs = this.context.getAllServiceReferences(null, "(objectClass=" + packageRoot + "*)");

            if (refs != null) {
                for (ServiceReference ref : refs) {
                    registerService(ref);
                }
            }

        } catch (InvalidSyntaxException e) {
            log.error("Cannot get sercive references");
        }

        restartJaxRsServer();
        System.out.println(" --- Adding OSGi service listener");
        context.addServiceListener(this);
    }

    @Deactivate
    protected void deactivate(BundleContext context) {
        // Remove OSGi service listener
        try {
            System.out.println(" --- Remove OSGi service listener");
            context.removeServiceListener(this);
        } catch (Exception e) {
            log.error("Could not get unregister service listener - {}", e.getLocalizedMessage());
            log.debug("Exception: ", e);
        }

        if (bus == null) {
            return;
        }

        // Remove ThreadLocalRequest filter
        if (filterRegistration != null) {
            System.out.println(" --- Unregistering WebServiceThreadLocalRequestFilter");
            try {
                context.ungetService(filterRegistration.getReference());
            } catch (Exception e) {
                log.error("Could not unregister WebServiceThreadLocalRequestFilter - {}", e.getLocalizedMessage());
                log.debug("Exception: ", e);
            }
        }

        // For safety we remove all HTTPServlet
        for (String serv : registeredServlets.keySet()) {
            try {
                System.out.println(" --- Unregistering servlet: " + serv);
                osgiHttpService.unregister(serv);
            } catch (Exception e) {
                log.error("Could not remove: {} - {}", serv, e.getLocalizedMessage());
                log.debug("Exception: ", e);
            }
        }
        registeredServlets.clear();
        stopJaxRsServer();
        destinationRegistry = null;
        bus.shutdown(true);
        bus = null;
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        if (context == null) {
            return;
        }
        ServiceReference sr = event.getServiceReference();
        switch (event.getType()) {
            case ServiceEvent.REGISTERED: {
                if (registerService(sr)) {
                    restartJaxRsServer();
                }
                ;
            }
            break;

            case ServiceEvent.UNREGISTERING: {
                if (unregisterService(sr)) {
                    restartJaxRsServer();
                }
            }
            break;

            default:
                break;
        }
    }

    private WebServiceType getWebserviceType(ServiceReference sr) {
        WebServiceType type = WebServiceType.NONE;

        try {
            if (isJaxRsService(sr)) {
                type = WebServiceType.JAXRS;
            }
        } catch (Throwable th) {
            log.warn("Error getting webservice type - {}", th.getLocalizedMessage());
            log.debug("Exception: ", th);
        }
        return type;
    }

    private String getJaxrsPath(ServiceReference reference) {
        Object instance = context.getService(reference);
        Set<Class<?>> classesWithPath = ReflectionUtils.getAllSuperTypes(instance.getClass(), Predicates.and(ReflectionUtils.withAnnotation(Path.class)));
        for (Class<?> c : classesWithPath) {
            Path p = c.getAnnotation(Path.class);
            return p.value();
        }
        return "";
    }

    private boolean isJaxRsService(ServiceReference reference) {
        Object instance = null;
        try {
            instance = context.getService(reference);
        } catch (Throwable th) {
        }
        if (instance == null) {
            return false;
        }
        Set<Class<?>> classesWithPath = ReflectionUtils.getAllSuperTypes(instance.getClass(), Predicates.and(ReflectionUtils.withAnnotation(Path.class)));

        if (classesWithPath != null && classesWithPath.size() > 0) {
            return true;
        }
        return false;
    }

    private boolean isJaxRsProvider(ServiceReference reference) {
        Object instance = null;
        try {
            instance = context.getService(reference);
        } catch (Throwable th) {
        }
        if (instance == null) {
            return false;
        }
        Set<Class<?>> classesWithPath = ReflectionUtils.getAllSuperTypes(instance.getClass(), Predicates.and(ReflectionUtils.withAnnotation(Provider.class)));

        if (classesWithPath != null && classesWithPath.size() > 0) {
            return true;
        }
        return false;
    }

    private boolean unregisterService(ServiceReference sr) {
        synchronized (bus) {
            try {
                WebServiceType type = getWebserviceType(sr);
                if (type == WebServiceType.NONE) {
                    // Testing for @Provider
                    Object instance = context.getService(sr);
                    if (isJaxRsProvider(sr)) {
                        if (!instance.getClass().getName().startsWith(packageRoot)) {
                            return false;
                        }
                        System.out.println(" --- Provider unregistration: " + context.getService(sr).getClass().getName());
                        if (registeredProviders.containsKey(instance.getClass())) {
                            registeredProviders.remove(instance.getClass());
                            return true;
                        }
                    }
                    return false;
                }
                Object instance = context.getService(sr);
                if (!instance.getClass().getName().startsWith(packageRoot)) {
                    return false;
                }
                System.out.println(" --- Service unregistration: " + context.getService(sr).getClass().getName());
                if (type == WebServiceType.JAXRS) {
                    if (registeredServices.containsKey(instance.getClass())) {
                        registeredServices.remove(instance.getClass());
                        return true;
                    }
                }
            } catch (Throwable th) {
                log.warn("Error on unregister webservice", th.getLocalizedMessage());
                log.debug("Exception: ", th);
            }
        }
        return false;
    }


    private boolean registerService(ServiceReference sr) {
        synchronized (bus) {
            try {
                WebServiceType type = getWebserviceType(sr);
                if (type == WebServiceType.NONE) {
                    // Testing for @Provider
                    Object instance = context.getService(sr);
                    if (isJaxRsProvider(sr)) {
                        if (!instance.getClass().getName().startsWith(packageRoot)) {
                            return false;
                        }
                        System.out.println(" --- Provider registration: " + context.getService(sr).getClass().getName());
                        if (!registeredProviders.containsKey(instance.getClass())) {
                            registeredProviders.put(instance.getClass(), instance);
                            return true;
                        }
                    }
                    return false;
                }
                Object instance = context.getService(sr);
                if (!instance.getClass().getName().startsWith(packageRoot)) {
                    return false;
                }
                System.out.println(" --- Service registration: " + context.getService(sr).getClass().getName() + " Path: " + getJaxrsPath(sr));
                if (!registeredServices.containsKey(instance.getClass())) {
                    registeredServices.put(instance.getClass(), instance);
                    return true;
                }
                return false;
            } catch (Throwable th) {
                log.warn("Error on register webservice - {}", th.getLocalizedMessage());
                log.debug("Exception: ", th);
            }
        }
        return false;
    }

    private JAXRSServerFactoryBean createJAXRSServerFactoryBean() {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setBus(bus);

        sf.setAddress("/");

        Map<Object, Object> extensionMappings = new HashMap<Object, Object>();
        extensionMappings.put("xml", "application/xml");
        extensionMappings.put("json", "application/json");


        // But we have to able to define the XSLT file for the proveder, we need
        // to extend the loader service for it to be flexible.  More info:
        // http://sberyozkin.blogspot.hu/2009/05/mvc-xml-way-with-cxf-jax-rs.html
        // http://cxf.apache.org/docs/jax-rs-advanced-xml.html

        // extensionMappings.put("html", "text/html");
        // It can be configured with this way:
        //  <map id="outTemplates">
        //      <entry key="application/xml" value="classpath:/WEB-INF/templates/book-xml.xsl"/>
        //      <entry key="text/html" value="classpath:/WEB-INF/templates/book-html.xsl"/>
        //      <entry key="application/json" value="classpath:/WEB-INF/templates/book-json.xsl"/>
        //  </map>
        //
        //	  <bean id="uriResolver" class="org.apache.cxf.systest.jaxrs.URIResolverImpl"/>
        //
        //	  <bean id="xsltProvider" class="org.apache.cxf.jaxrs.provider.XSLTJaxbProvider">
        //	      <property name="outMediaTemplates" ref="outTemplates"/>
        //	      <property name="resolver" ref="uriResolver"/>
        //  </bean>

        sf.setExtensionMappings(extensionMappings);

        List<Object> providers = new ArrayList<Object>();
        providers.add(new JAXBElementProvider());

        // Jackson JSON Provider
        JacksonJaxbJsonProvider jp = new JacksonJaxbJsonProvider();

        // This is hack, because the interface does not work in first time, so we emulate it
        // http://stackoverflow.com/questions/10860142/appengine-java-jersey-jackson-jaxbannotationintrospector-noclassdeffounderror
        // But that solution is not correct fpr this problem, because xc cause other problem (reason: JAXB annotations)
        try {
            jp.writeTo(new Long(1), Long.class, Long.class, new Annotation[]{}, MediaType.APPLICATION_JSON_TYPE, null, new ByteArrayOutputStream());
        } catch (Throwable e) {
        }
        providers.add(jp);

        // Adding providers
        providers.addAll(registeredProviders.values());

        sf.setProviders(providers);

        BindingFactoryManager manager = sf.getBus().getExtension(BindingFactoryManager.class);
        JAXRSBindingFactory factory = new JAXRSBindingFactory();
        factory.setBus(sf.getBus());
        manager.unregisterBindingFactory(JAXRSBindingFactory.JAXRS_BINDING_ID);
        manager.registerBindingFactory(JAXRSBindingFactory.JAXRS_BINDING_ID, factory);

        sf.setResourceClasses(new ArrayList<Class<?>>(registeredServices.keySet()));
        //sf.setServiceBeanObjects(registeredServices.values());
        for (Class<?> c : registeredServices.keySet()) {
            sf.setResourceProvider(c, new SingletonResourceProvider(registeredServices.get(c)));
        }
        return sf;
    }

    private void stopJaxRsServer() {
        if (jaxRsServer != null) {
            jaxRsServer.stop();
            jaxRsServer.destroy();
        }
        for (String path : destinationRegistry.getDestinationsPaths()) {
            // clean up the destination in case the destination itself can
            // no longer access the registry later
            AbstractHTTPDestination dest = destinationRegistry.getDestinationForPath(path);
            destinationRegistry.removeDestination(path);
            dest.releaseRegistry();
        }
        for (AbstractHTTPDestination destination : destinationRegistry.getDestinations()) {
            destination.releaseRegistry();
        }
        jaxRsServer = null;
    }

    private void restartJaxRsServer() {
        if (registeredServices.size() == 0) {
            return;
        }
        if (jaxRsServer != null) {
            stopJaxRsServer();
        }
        jaxRsServer = createJAXRSServerFactoryBean().create();
    }

    public Server getServiceByRequest(HttpServletRequest request) {
        String pathInfo = request.getPathInfo() == null ? request.getServletPath() : request.getPathInfo();
        return jaxRsServer;
    }
}
