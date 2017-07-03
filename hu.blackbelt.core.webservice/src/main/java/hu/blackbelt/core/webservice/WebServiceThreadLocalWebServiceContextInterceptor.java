package hu.blackbelt.core.webservice;


import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.context.WebServiceContextImpl;
import org.apache.cxf.jaxws.handler.soap.SOAPMessageContextImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServiceThreadLocalWebServiceContextInterceptor extends AbstractPhaseInterceptor<Message> {
	Logger log = LoggerFactory.getLogger(WebServiceThreadLocalWebServiceContextInterceptor.class);
	Object serv;
	
	public WebServiceThreadLocalWebServiceContextInterceptor(Object serv) {
		super(Phase.RECEIVE);
		this.serv = serv;
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		// Setting up thread local WebService to be able to resolve @Resource annotations
		SOAPMessageContextImpl msg = new SOAPMessageContextImpl(message);
		WebServiceContextImpl ctx = new WebServiceContextImpl();
		ctx.setMessageContext(msg);
	
	}

}
