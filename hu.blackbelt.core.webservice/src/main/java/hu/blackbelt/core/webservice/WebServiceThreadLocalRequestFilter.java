package hu.blackbelt.core.webservice;

import org.apache.cxf.endpoint.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A filter puts the request to ThreadLocal variable and gives static method tho retreive
 */
public class WebServiceThreadLocalRequestFilter implements Filter {
	private final Logger LOG = LoggerFactory.getLogger(WebServiceThreadLocalRequestFilter.class);

	private static JaxRsServiceManager listener;

	public void setWebServiceRegistrationListener(JaxRsServiceManager listener) {
		this.listener = listener;
	}
	
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
	ServletException {
		//LOG.info("Do filtering on URL: "+((HttpServletRequest)request).getRequestURL().toString());
		Server server = null;
		if (listener != null) {
			server = listener.getServiceByRequest((HttpServletRequest)request);
		}
		ThreadLocalRequestContext.getThreadLocal().set(new ThreadLocalRequestContext((HttpServletRequest)request, (HttpServletResponse)response, server));
		chain.doFilter(request, response);
	}


	@Override
	public void init(FilterConfig config) throws ServletException {
		LOG.info("init()");
	}
	
	@Override
	public void destroy() {
	}
	
	public static HttpServletRequest getThreadLocalRequest() {
		return ThreadLocalRequestContext.getRequestContext().getRequest();
	}

	public static HttpServletResponse getThreadLocalResponse() {
		return ThreadLocalRequestContext.getRequestContext().getResponse();
	}
	
	public static Server getWebServiceServer() {
		return ThreadLocalRequestContext.getRequestContext().getServer();
	}

}
