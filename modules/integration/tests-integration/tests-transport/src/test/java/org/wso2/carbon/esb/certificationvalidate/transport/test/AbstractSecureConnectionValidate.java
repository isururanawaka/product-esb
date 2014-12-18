package org.wso2.carbon.esb.certificationvalidate.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import java.io.*;

/**
 * Created by vijithae on 12/14/14.
 */
public class AbstractSecureConnectionValidate {

	public void createTomcat() throws LifecycleException {
		 final Tomcat tomcat = new Tomcat();
		 String separator = File.separator;


		tomcat.getService().setContainer(tomcat.getEngine());
		tomcat.setPort(8080);
		tomcat.setBaseDir(".");

		// Configure the standard host
		StandardHost stdHost = (StandardHost) tomcat.getHost();
		stdHost.setAppBase(".");
		stdHost.setAutoDeploy(true);
		stdHost.setDeployOnStartup(true);
		stdHost.setUnpackWARs(true);
		tomcat.setHost(stdHost);

		//deploy Service
		Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());

		Tomcat.addServlet(ctx, "hello", new HttpServlet() {
			protected void service(HttpServletRequest req, HttpServletResponse resp)
					throws ServletException, IOException {

				String body = null;
				StringBuilder stringBuilder = new StringBuilder();
				BufferedReader bufferedReader = null;

				try {
					InputStream inputStream = req.getInputStream();
					if (inputStream != null) {
						bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
						char[] charBuffer = new char[128];
						int bytesRead = -1;
						while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
							stringBuilder.append(charBuffer, 0, bytesRead);
						}
					} else {
						stringBuilder.append("");
					}
				} catch (IOException ex) {
					throw ex;
				} finally {
					if (bufferedReader != null) {
						try {
							bufferedReader.close();
						} catch (IOException ex) {
							throw ex;
						}
					}
				}
				try {
					OMElement putRequest = AXIOMUtil
							.stringToOM(
									"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
									"   <soapenv:Header/>\n" +
									"         <response>\n" +
									"         \tWSO2\n" +
									"         </response>\n" +
									"   <soapenv:Body/>\n" +
									"</soapenv:Envelope>");
					body = stringBuilder.toString();
					PrintWriter w = resp.getWriter();
					resp.setContentType("application/xml");
					//resp.setCharacterEncoding("UTF-8");
					w.write(putRequest.toString());
					w.flush();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				}


			}
		});
		ctx.addServletMapping("/*", "hello");

		PrivilegedCarbonContext.startTenantFlow();
		PrivilegedCarbonContext ctx1 = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		ctx1.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
		ctx1.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
//		System.setProperty("javax.net.ssl.keyStore",  ("/Users/vijithae/developments/wso2-products/wso2esb-4.8" +
//		                                                       ".1/repository/resources/security/MyCompany.jks"));
//		System.setProperty("javax.net.ssl.keyStorePassword", "wso2carbon");
//
//		//Set key store, this must contain the user private key
//		//here we have use both trust store and key store as the same key store
//		// But you can use a separate key store for key store an trust store.
//		System.setProperty("javax.net.ssl.keyStore",  ("/Users/vijithae/developments/wso2-products/wso2esb-4.8" +
//		                                                       ".1/repository/resources/security/MyCompany.jks"));
//		System.setProperty("javax.net.ssl.keyStorePassword", "wso2carbon");


		// Setting up the connector
		Connector connector = new Connector();
		connector.setPort(8443);
		connector.setProtocol("HTTP/1.1");
		connector.setProperty("SSLEnabled", "true");
		connector.setProperty("maxThreads", "150");
		connector.setScheme("https");
		connector.setSecure(true);
		connector.setProperty("clientAuth", "false");
		connector.setProperty("sslProtocol", "TLS");
		connector
				.setProperty("keystoreFile", "/Users/vijithae/developments/wso2-products/wso2esb-4.8.1/repository/resources/security/MyCompany.jks"
						);
		connector.setProperty("keystorePass", "wso2carbon");

		tomcat.getService().addConnector(connector);

		// Starting Tomcat in new thread
		new Thread(new Runnable() {
			public void run() {
				try {
					tomcat.start();
					tomcat.getServer().await();
				} catch (LifecycleException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

}
