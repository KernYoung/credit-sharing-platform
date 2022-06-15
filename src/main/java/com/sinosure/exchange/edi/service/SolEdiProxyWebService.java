package com.sinosure.exchange.edi.service;


import org.springframework.beans.factory.annotation.Value;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 3.3.7
 * 2020-07-15T18:18:39.533+08:00
 * Generated source version: 3.3.7
 *
 */
@WebServiceClient(name = "SolEdiProxyWebService",
                 wsdlLocation = "http://10.0.132.12:80/ediserver/ws_services/SolEdiProxyWebService?wsdl",
//                  wsdlLocation = "http://10.0.105.3:8081/ediserver/ws_services/SolEdiProxyWebService?wsdl",
                  targetNamespace = "http://service.edi.exchange.sinosure.com")
public class SolEdiProxyWebService extends Service {
    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://service.edi.exchange.sinosure.com", "SolEdiProxyWebService");
    public final static QName SolEdiProxyWebServiceHttpPort = new QName("http://service.edi.exchange.sinosure.com", "SolEdiProxyWebServiceHttpPort");
    public final static String Edi3Server = "http://10.0.132.12:80/ediserver/gateway.do";
//    public final static String Edi3Server = "http://10.0.105.3:8081/ediserver/gateway.do";
    static {
        URL url = null;
        try {
            //132 测试
            //105生产
            url = new URL("http://10.0.132.12:80/ediserver/ws_services/SolEdiProxyWebService?wsdl");
//            url = new URL("http://10.0.105.3:8081/ediserver/ws_services/SolEdiProxyWebService?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(SolEdiProxyWebService.class.getName())
                .log(java.util.logging.Level.INFO,
                     "Can not initialize the default wsdl from {0}", "http://10.0.132.12:80/ediserver/ws_services/SolEdiProxyWebService?wsdl");
//             "Can not initialize the default wsdl from {0}", "http://10.0.105.3:8081/ediserver/ws_services/SolEdiProxyWebService?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public SolEdiProxyWebService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public SolEdiProxyWebService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SolEdiProxyWebService() {
        super(WSDL_LOCATION, SERVICE);
    }

    public SolEdiProxyWebService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public SolEdiProxyWebService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public SolEdiProxyWebService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }




    /**
     *
     * @return
     *     returns SolEdiProxyWebServicePortType
     */
    @WebEndpoint(name = "SolEdiProxyWebServiceHttpPort")
    public SolEdiProxyWebServicePortType getSolEdiProxyWebServiceHttpPort() {
        return super.getPort(SolEdiProxyWebServiceHttpPort, SolEdiProxyWebServicePortType.class);
    }

    /**
     *
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SolEdiProxyWebServicePortType
     */
    @WebEndpoint(name = "SolEdiProxyWebServiceHttpPort")
    public SolEdiProxyWebServicePortType getSolEdiProxyWebServiceHttpPort(WebServiceFeature... features) {
        return super.getPort(SolEdiProxyWebServiceHttpPort, SolEdiProxyWebServicePortType.class, features);
    }

}
