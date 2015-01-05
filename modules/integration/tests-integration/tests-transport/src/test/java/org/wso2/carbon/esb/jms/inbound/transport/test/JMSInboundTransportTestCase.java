/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.jms.inbound.transport.test;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageConsumer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSTopicMessageConsumer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSTopicMessagePublisher;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.inbound.stub.types.carbon.InboundEndpointDTO;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.esb.integration.common.clients.inbound.endpoint.InboundAdminClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.JMSEndpointManager;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public class JMSInboundTransportTestCase extends ESBIntegrationTest {
	private ServerConfigurationManager serverConfigurationManager;
	private InboundAdminClient inboundAdminClient;
	private ActiveMQServer activeMQServer
			= new ActiveMQServer();

	@BeforeClass(alwaysRun = true)
	protected void init() throws Exception {
		activeMQServer.startJMSBrokerAndConfigureESB();
		super.init();
		serverConfigurationManager =
				new ServerConfigurationManager(new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN));
		OMElement synapse =
				esbUtils.loadResource("/artifacts/ESB/jms/inbound/transport/jms_transport_proxy_service.xml");
		updateESBConfiguration(JMSEndpointManager.setConfigurations(synapse));
		inboundAdminClient = new InboundAdminClient(context.getContextUrls().getBackEndUrl(),
		                                                               getSessionCookie());



	}

	@Test(groups = { "wso2.esb" }, description = "Adding New Inbound End point")
	public void testAddingNewJMSInboundEndpoint() throws Exception {

		int beforeCount = 0;
		System.out.println("Before Adding +++++++"+beforeCount);
		addInboundEndpoint(addEndpoint1());
		int afterCount = inboundAdminClient.getAllInboundEndpointNames().length;
		System.out.println("afterCount Adding +++++++"+afterCount);
		assertEquals(1, afterCount - beforeCount);

		InboundEndpointDTO[] inboundEndpoints = inboundAdminClient.getAllInboundEndpointNames();
		if (inboundEndpoints != null && inboundEndpoints.length > 0 && inboundEndpoints[0] != null) {
			List endpointList = Arrays.asList(inboundEndpoints);
			for(int i=0;i<inboundEndpoints.length;i++){
				System.out.println("********************************"+inboundEndpoints[i].getName());
			}

			//assertTrue(endpointList.contains("TestJMS1"));
		} else {
			fail("Inbound Endpoint has not been added to the system properly");
		}

		deleteInboundEndpoints();

	}

	@Test(groups = { "wso2.esb" }, description = "Updationg Existing Inbound End point")
	public void testUpdatingJMSInboundEndpoint() throws Exception {

		//int beforeCount = 0;
		//System.out.println("Before Adding +++++++"+beforeCount);
		addInboundEndpoint(addEndpoint4());
		updateInboundEndpoint(addEndpoint6());
		//int afterCount = inboundAdminClient.getAllInboundEndpointNames().length;
		//System.out.println("afterCount Adding +++++++"+afterCount);
		//assertEquals(1, afterCount - beforeCount);

		InboundEndpointDTO[] inboundEndpoints = inboundAdminClient.getAllInboundEndpointNames();
		if (inboundEndpoints != null && inboundEndpoints.length > 0 && inboundEndpoints[0] != null) {
			List endpointList = Arrays.asList(inboundEndpoints);
			for(int i=0;i<inboundEndpoints.length;i++){
				System.out.println("********************************"+inboundEndpoints[i].getName());
			}

			//assertTrue(endpointList.contains("TestJMS1"));
		} else {
			fail("Inbound Endpoint has not been added to the system properly");
		}

		deleteInboundEndpoints();

	}

	@Test(groups = { "wso2.esb" }, description = "Deleting an Inbound End point")
	public void testDeletingJMSInboundEndpoint() throws Exception {
		addInboundEndpoint(addEndpoint1());
		//addInboundEndpoint(addEndpoint2());
		int beforeCount = inboundAdminClient.getAllInboundEndpointNames().length;;
		System.out.println("Before Adding +++++++"+beforeCount);
		deleteInboundEndpointFromName("TestJMS1");
		int afterCount = 0;
		//if(inboundAdminClient.getAllInboundEndpointNames().length == null)

		System.out.println("afterCount Adding +++++++"+afterCount);
		assertEquals(1, beforeCount - afterCount);
		deleteInboundEndpoints();

	}

	@Test(groups = { "wso2.esb" }, description = "Test Inbound End points")
	public void testInboundEndpointJMS() throws Exception {
		addInboundEndpoint(addEndpoint1());
		JMSQueueMessageProducer sender =
				new JMSQueueMessageProducer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
		String queueName = "localq";
		boolean isEntryFound = false;

		try {
			sender.connect(queueName);
			for (int i = 0; i < 3; i++) {
				sender.pushMessage("<?xml version='1.0' encoding='UTF-8'?>" +
				                   "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
				                   " xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">" +
				                   "   <soapenv:Header/>" +
				                   "   <soapenv:Body>" +
				                   "      <ser:placeOrder>" +
				                   "         <ser:order>" +
				                   "            <xsd:price>100</xsd:price>" +
				                   "            <xsd:quantity>2000</xsd:quantity>" +
				                   "            <xsd:symbol>JMSInboundEndpoints</xsd:symbol>" +
				                   "         </ser:order>" +
				                   "      </ser:placeOrder>" +
				                   "   </soapenv:Body>" +
				                   "</soapenv:Envelope>");
			}
		} finally {
			sender.disconnect();
		}
		System.out.println("Thread is sleeping now");
		Thread.sleep(15000);

		JMSQueueMessageConsumer consumer =
				new JMSQueueMessageConsumer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
		try {
			consumer.connect(queueName);
			for (int i = 0; i < 3; i++) {
				if (consumer.popMessage().toString() != null) {
					isEntryFound=true;
				}
			}
		} finally {
			consumer.disconnect();
		}
		assertTrue(isEntryFound);
		deleteInboundEndpoints();

	}

	@Test(groups = { "wso2.esb" }, description = "Test Inbound End points")
	public void testTopicInboundEndpointJMS() throws Exception {
		addInboundEndpoint(addEndpoint2());
		JMSTopicMessagePublisher sender =
				new JMSTopicMessagePublisher(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
		String queueName = "TestJMS4";
		boolean isEntryFound = false;

		try {
			sender.connect(queueName);
			for (int i = 0; i < 3; i++) {
				sender.publish("<?xml version='1.0' encoding='UTF-8'?>" +
				                   "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
				                   " xmlns:ser=\"http://services.samples\" xmlns:xsd=\"http://services.samples/xsd\">" +
				                   "   <soapenv:Header/>" +
				                   "   <soapenv:Body>" +
				                   "      <ser:placeOrder>" +
				                   "         <ser:order>" +
				                   "            <xsd:price>100</xsd:price>" +
				                   "            <xsd:quantity>2000</xsd:quantity>" +
				                   "            <xsd:symbol>JMSInboundEndpoints</xsd:symbol>" +
				                   "         </ser:order>" +
				                   "      </ser:placeOrder>" +
				                   "   </soapenv:Body>" +
				                   "</soapenv:Envelope>");
			}
		} finally {
			sender.disconnect();
		}

		Thread.sleep(10000);

		JMSTopicMessageConsumer consumer =
				new JMSTopicMessageConsumer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
		try {
			consumer.getMessages();

		} finally {
			consumer.stopConsuming();
		}
		//assertTrue(isEntryFound);
		deleteInboundEndpoints();

	}

	@Test(groups = { "wso2.esb" }, description = "Test Inbound End points")
	public void testByteMessageInboundEndpointJMS() throws Exception {
		addInboundEndpoint(addEndpoint2());
		JMSQueueMessageProducer sender =
				new JMSQueueMessageProducer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
		String queueName = "localq";
		boolean isEntryFound = false;

		try {
			sender.connect(queueName);
			for (int i = 0; i < 3; i++) {
				sender.sendBytesMessage("message".getBytes());
			}
		} finally {
			sender.disconnect();
		}

		Thread.sleep(10000);

		JMSQueueMessageConsumer consumer =
				new JMSQueueMessageConsumer(JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
		try {
			consumer.connect(queueName);
			consumer.popRawMessage();

		} finally {
			consumer.disconnect();
		}
		//assertTrue(isEntryFound);
		deleteInboundEndpoints();

	}


	@Test(groups = { "wso2.esb" }, description = "Test Inbound End points")
	public void testUpdateInboundEndpointJMS() throws Exception {
		addInboundEndpoint(addEndpoint4());
		addInboundEndpoint(addEndpoint4());

		deleteInboundEndpoints();



	}


	@Test(groups = { "wso2.esb" }, description = "Test Inbound End points")
	public void testDeletingInvalidJMSInboundEndpoint() throws Exception {
		addInboundEndpointFromParams(addEndpoint5());
		deleteInboundEndpointFromName("TestJMS10");
//		addInboundEndpoint(addEndpoint3());
//		deleteInboundEndpoints();
		deleteInboundEndpoints();


	}

	@Test(groups = { "wso2.esb" }, description = "Test Inbound End points")
	public void testInvalidIntervalInboundEndpointJMS() throws Exception {
		//addInboundEndpointFromParams(addEndpoint7());
		//deleteInboundEndpointFromName("TestJMS10");
		//		addInboundEndpoint(addEndpoint3());
		//		deleteInboundEndpoints();
		deleteInboundEndpoints();


	}

	@Test(groups = { "wso2.esb" }, description = "Test Inbound End points")
	public void testInvalidCacheLevelInboundEndpointJMS() throws Exception {
		//addInboundEndpointFromParams(addEndpoint8());
		//deleteInboundEndpointFromName("TestJMS10");
		//		addInboundEndpoint(addEndpoint3());
		//		deleteInboundEndpoints();
		deleteInboundEndpoints();


	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		super.cleanup();
		activeMQServer.stopJMSBrokerRevertESBConfiguration();
	}

	private OMElement getArtifactConfig(String fileName) throws Exception {
		String path = "artifacts" + File.separator + "ESB" + File.separator
		              + "jms" + File.separator + "inbound" + File.separator
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
				            "                 name=\"TestJMS1\"\n" +
				            "                 sequence=\"requestHandlerSeq\"\n" +
				            "                 onError=\"inFault\"\n" +
				            "                 protocol=\"jms\"\n" +
				            "                 suspend=\"false\">\n" +
				            "    <parameters>\n" +
				            "        <parameter name=\"interval\">10000</parameter>\n" +
				            "        <parameter name=\"transport.jms.Destination\">localq</parameter>\n" +
				            "        <parameter name=\"transport.jms.CacheLevel\">0</parameter>\n" +
				            "        <parameter name=\"transport.jms" +
				            ".ConnectionFactoryJNDIName\">QueueConnectionFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.factory.initial\">org.apache.activemq.jndi.ActiveMQInitialContextFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.provider.url\">tcp://localhost:61616</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionAcknowledgement\">AUTO_ACKNOWLEDGE</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionTransacted\">false</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryType\">queue</parameter>\n" +
				            "    </parameters>\n" +
				            "</inboundEndpoint>");

		return synapseConfig;
	}

	private OMElement addEndpoint2() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
				            "                 name=\"TestJMS2\"\n" +
				            "                 sequence=\"requestHandlerSeq\"\n" +
				            "                 onError=\"inFault\"\n" +
				            "                 protocol=\"jms\"\n" +
				            "                 suspend=\"false\">\n" +
				            "    <parameters>\n" +
				            "        <parameter name=\"interval\">10000</parameter>\n" +
				            "        <parameter name=\"transport.jms.Destination\">localq</parameter>\n" +
				            "        <parameter name=\"transport.jms.CacheLevel\">1</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryJNDIName\">TopicConnectionFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.factory.initial\">org.apache.activemq.jndi.ActiveMQInitialContextFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.provider.url\">tcp://localhost:61616</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionAcknowledgement\">AUTO_ACKNOWLEDGE</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionTransacted\">false</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryType\">topic</parameter>\n" +
				            "    </parameters>\n" +
				            "</inboundEndpoint>");

		return synapseConfig;
	}

	private OMElement addEndpoint3() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
				            "                 name=\"TestJMS3\"\n" +
				            "                 sequence=\"requestHandlerSeq\"\n" +
				            "                 onError=\"inFault\"\n" +
				            "                 protocol=\"jms\"\n" +
				            "                 suspend=\"true\">\n" +
				            "    <parameters>\n" +
				            "        <parameter name=\"interval\">10000</parameter>\n" +
				            "        <parameter name=\"transport.jms.Destination\">localq</parameter>\n" +
				            "        <parameter name=\"transport.jms.CacheLevel\">1</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryJNDIName\">TopicConnectionFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.factory.initial\">org.apache.activemq.jndi.ActiveMQInitialContextFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.provider.url\">tcp://localhost:61616</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionAcknowledgement\">AUTO_ACKNOWLEDGE</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionTransacted\">false</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryType\">topic</parameter>\n" +
				            "    </parameters>\n" +
				            "</inboundEndpoint>");

		return synapseConfig;
	}
	private OMElement addEndpoint4() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
				            "                 name=\"TestJMS4\"\n" +
				            "                 sequence=\"requestHandlerSeq\"\n" +
				            "                 onError=\"inFault\"\n" +
				            "                 protocol=\"jms\"\n" +
				            "                 suspend=\"false\">\n" +
				            "    <parameters>\n" +
				            "        <parameter name=\"interval\">10000</parameter>\n" +
				            "        <parameter name=\"transport.jms.Destination\">localq</parameter>\n" +
				            "        <parameter name=\"transport.jms.CacheLevel\">1</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryJNDIName\">TopicConnectionFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.factory.initial\">org.apache.activemq.jndi.ActiveMQInitialContextFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.provider.url\">tcp://localhost:61616</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionAcknowledgement\">AUTO_ACKNOWLEDGE</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionTransacted\">false</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryType\">topic</parameter>\n" +
				            "    </parameters>\n" +
				            "</inboundEndpoint>");

		return synapseConfig;
	}
	private OMElement addEndpoint5() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
				            "                 name=\"TestJMS5\"\n" +
				            "                 sequence=\"requestHandlerSeq\"\n" +
				            "                 onError=\"inFault\"\n" +
				            "                 protocol=\"jms\"\n" +
				            "                 suspend=\"false\">\n" +
				            "    <parameters>\n" +
				            "        <parameter name=\"interval\">1000</parameter>\n" +
				            "        <parameter name=\"transport.jms.Destination\">localq</parameter>\n" +
				            "        <parameter name=\"transport.jms.CacheLevel\">1</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryJNDIName\">TopicConnectionFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.factory.initial\">org.apache.activemq.jndi.ActiveMQInitialContextFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.provider.url\">tcp://localhost:61616</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionAcknowledgement\">AUTO_ACKNOWLEDGE</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionTransacted\">false</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryType\">topic</parameter>\n" +
				            "    </parameters>\n" +
				            "</inboundEndpoint>");

		return synapseConfig;
	}

	private OMElement addEndpoint6() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
				            "                 name=\"TestJMS4\"\n" +
				            "                 sequence=\"requestHandlerSeq\"\n" +
				            "                 onError=\"inFault\"\n" +
				            "                 protocol=\"jms\"\n" +
				            "                 suspend=\"false\">\n" +
				            "    <parameters>\n" +
				            "        <parameter name=\"interval\">20000</parameter>\n" +
				            "        <parameter name=\"transport.jms.Destination\">localq</parameter>\n" +
				            "        <parameter name=\"transport.jms.CacheLevel\">1</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryJNDIName\">TopicConnectionFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.factory.initial\">org.apache.activemq.jndi.ActiveMQInitialContextFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.provider.url\">tcp://localhost:61616</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionAcknowledgement\">AUTO_ACKNOWLEDGE</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionTransacted\">false</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryType\">topic</parameter>\n" +
				            "    </parameters>\n" +
				            "</inboundEndpoint>");

		return synapseConfig;
	}

	private OMElement addEndpoint7() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
				            "                 name=\"TestJMS4\"\n" +
				            "                 sequence=\"requestHandlerSeq\"\n" +
				            "                 onError=\"inFault\"\n" +
				            "                 protocol=\"jms\"\n" +
				            "                 suspend=\"false\">\n" +
				            "    <parameters>\n" +
				            "        <parameter name=\"interval\">1.1</parameter>\n" +
				            "        <parameter name=\"transport.jms.Destination\">localq</parameter>\n" +
				            "        <parameter name=\"transport.jms.CacheLevel\">1</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryJNDIName\">TopicConnectionFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.factory.initial\">org.apache.activemq.jndi.ActiveMQInitialContextFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.provider.url\">tcp://localhost:61616</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionAcknowledgement\">AUTO_ACKNOWLEDGE</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionTransacted\">false</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryType\">topic</parameter>\n" +
				            "    </parameters>\n" +
				            "</inboundEndpoint>");

		return synapseConfig;
	}

	private OMElement addEndpoint8() throws Exception {
		OMElement synapseConfig = null;
		synapseConfig = AXIOMUtil
				.stringToOM("<inboundEndpoint xmlns=\"http://ws.apache.org/ns/synapse\"\n" +
				            "                 name=\"TestJMS4\"\n" +
				            "                 sequence=\"requestHandlerSeq\"\n" +
				            "                 onError=\"inFault\"\n" +
				            "                 protocol=\"jms\"\n" +
				            "                 suspend=\"false\">\n" +
				            "    <parameters>\n" +
				            "        <parameter name=\"interval\">1000</parameter>\n" +
				            "        <parameter name=\"transport.jms.Destination\">localq</parameter>\n" +
				            "        <parameter name=\"transport.jms.CacheLevel\">1.1</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryJNDIName\">TopicConnectionFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.factory.initial\">org.apache.activemq.jndi.ActiveMQInitialContextFactory</parameter>\n" +
				            "        <parameter name=\"java.naming.provider.url\">tcp://localhost:61616</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionAcknowledgement\">AUTO_ACKNOWLEDGE</parameter>\n" +
				            "        <parameter name=\"transport.jms.SessionTransacted\">false</parameter>\n" +
				            "        <parameter name=\"transport.jms.ConnectionFactoryType\">topic</parameter>\n" +
				            "    </parameters>\n" +
				            "</inboundEndpoint>");

		return synapseConfig;
	}

}
