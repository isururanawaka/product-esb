/*
*  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.esb.generic.inbound.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;

public class GenericInboundTransportTestCase extends ESBIntegrationTest {

	private final String CLASS_JAR="org.wso2.carbon.inbound.endpoint.test-1.0-SNAPSHOT.jar";
	private final String JAR_LOCATION= "/artifacts/ESB/jar";

	private ServerConfigurationManager serverConfigurationManager;

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {

		super.init();
		serverConfigurationManager =
				new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
		OMElement synapse =
				esbUtils.loadResource("/artifacts/ESB/generic/inbound/transport/generic_inbound_transport_config.xml");
		updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));
		//serverConfigurationManager = new ServerConfigurationManager(context);
		//serverConfigurationManager.copyToComponentLib(new File(getClass()
		//		                                                       .getResource(JAR_LOCATION + File.separator +
		//                                                                            CLASS_JAR)
		//		                                                       .toURI()));
		//serverConfigurationManager.restartGracefully();
		//loadESBConfigurationFromClasspath
		// ("/artifacts/ESB/generic/inbound/transport/generic_inbound_transport_config.xml");

	}

	@Test(groups = { "wso2.esb" }, description = "Test Generic Inbound End points")
	public void testGenericInboundEndpoints() throws Exception {
		addInboundEndpoint(addEndpoint1());

	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		super.cleanup();
	}

	private OMElement getArtifactConfig(String fileName) throws Exception {
		String path = "artifacts" + File.separator + "ESB" + File.separator
		              + "generic" + File.separator + "inbound" + File.separator
		              + "transport" + File.separator
		              + fileName;
		try {
			return esbUtils.loadResource(path);
		} catch (FileNotFoundException e) {
			throw new Exception("File Location " + path + " is incorrect ", e);
		} catch (XMLStreamException e) {
			throw new XMLStreamException("XML Stream Exception while reading file stream", e);
		}
	}

	private OMElement addEndpoint1() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
				            "                 name=\"Test\"\n" +
				            "                 sequence=\"main\"\n" +
				            "                 onError=\"inFault\"\n" +
				            "                 class=\"org.wso2.carbon.inbound.endpoint.test.GenericConsumer\"\n" +
				            "                 suspend=\"false\">\n" +
				            "   <parameters>\n" +
				            "      <parameter name=\"interval\">1000</parameter>\n" +
				            "   </parameters>\n" +
				            "</inboundEndpoint>");

		return synapseConfig;
	}
}
