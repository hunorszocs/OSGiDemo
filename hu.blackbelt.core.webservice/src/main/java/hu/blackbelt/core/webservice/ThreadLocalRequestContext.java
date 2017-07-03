//$URL: http://feanor:8050/svn/test/trunk/DevTest/apache-sling/adaptto/sling-cxf-integration/helloworld-application/src/main/java/adaptto/slingcxf/server/util/RequestContext.java $
//$Id: RequestContext.java 677 2011-09-09 15:26:10Z PRO-VISION\SSeifert $
package hu.blackbelt.core.webservice;

import org.apache.cxf.endpoint.Server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Manages request/response context of incoming SOAP server HTTP requests using
 * a ThreadLocal.
 */
class ThreadLocalRequestContext {

    private static ThreadLocal<ThreadLocalRequestContext> mThreadLocal = new ThreadLocal<ThreadLocalRequestContext>();

    private final HttpServletRequest mRequest;
    private final HttpServletResponse mResponse;
    private final Server mServer;
    

    ThreadLocalRequestContext(HttpServletRequest pRequest, HttpServletResponse pResponse, Server pServer) {
        mRequest = pRequest;
        mResponse = pResponse;
        mServer = pServer;
    }

    /**
     * @return Request
     */
    public HttpServletRequest getRequest() {
        return mRequest;
    }

    /**
     * @return Response
     */
    public HttpServletResponse getResponse() {
        return mResponse;
    }

    /**
     * Return endpoint server handling this request
     * @return
     */
    public Server getServer() {
    	return mServer;
    }
    
    
    /**
     * @return Context for current SOAP server request
     */
    public static ThreadLocalRequestContext getRequestContext() {
        return mThreadLocal.get();
    }

    static ThreadLocal<ThreadLocalRequestContext> getThreadLocal() {
        return mThreadLocal;
    }


}
