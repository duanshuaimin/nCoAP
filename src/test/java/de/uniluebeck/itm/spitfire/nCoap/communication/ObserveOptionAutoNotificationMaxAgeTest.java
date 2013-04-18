package de.uniluebeck.itm.spitfire.nCoap.communication;

import de.uniluebeck.itm.spitfire.nCoap.application.CoapServerApplication;
import static junit.framework.Assert.*;
import static de.uniluebeck.itm.spitfire.nCoap.message.options.OptionRegistry.OptionName.*;
import static de.uniluebeck.itm.spitfire.nCoap.application.CoapServerApplication.DEFAULT_COAP_SERVER_PORT;

import de.uniluebeck.itm.spitfire.nCoap.communication.utils.ObservableDummyWebService;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapMessage;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapRequest;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapResponse;
import de.uniluebeck.itm.spitfire.nCoap.message.header.Code;
import de.uniluebeck.itm.spitfire.nCoap.message.header.MsgType;
import de.uniluebeck.itm.spitfire.nCoap.message.options.UintOption;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Iterator;
import java.util.SortedMap;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.uniluebeck.itm.spitfire.nCoap.testtools.ByteTestTools.*;


/**
* Tests if the server sends a new notification when Max-Age ends.
*
* @author Stefan Hueske
*/
public class ObserveOptionAutoNotificationMaxAgeTest extends AbstractCoapCommunicationTest{

    //registration requests
    private static CoapRequest coapRequest;

    //notifications
    private static CoapResponse expectedNotification1;
    private static CoapResponse expectedNotification2;

    @Override
    public void createTestScenario() throws Exception {
        //define expected notifications
        expectedNotification1 = new CoapResponse(Code.CONTENT_205);
        expectedNotification1.setPayload("testpayload1".getBytes("UTF-8"));
        expectedNotification1.setMaxAge(5);

        expectedNotification2 = new CoapResponse(Code.CONTENT_205);
        expectedNotification2.setPayload("testpayload2".getBytes("UTF-8"));

        //register webservice on test server
        registerObservableDummyService(0, 2000, 2000);

        //create registration request
        URI targetUri = new URI("coap://localhost:" + testServer.getServerPort() + OBSERVABLE_SERVICE_PATH);
        coapRequest = new CoapRequest(MsgType.CON, Code.GET, targetUri);
        coapRequest.getHeader().setMsgID(1111);
        coapRequest.setToken(new byte[]{0x13, 0x53, 0x34});
        coapRequest.setObserveOptionRequest();

        //registration
        testReceiver.writeMessage(coapRequest, new InetSocketAddress("localhost", testServer.getServerPort()));

        //wait for Max-Age to end and resulting notification
        Thread.sleep(2500);

        //send reset to remove observer
        CoapResponse resetMessage = new CoapResponse(Code.EMPTY);
        resetMessage.getHeader().setMsgType(MsgType.RST);
        resetMessage.setMessageID(2222);
        testReceiver.writeMessage(resetMessage, new InetSocketAddress("localhost", testServer.getServerPort()));

        testReceiver.setReceiveEnabled(false);
    }

//    @BeforeClass
//    public static void init() throws Exception {
//        //init
//        testReceiver.reset();
//        testServer.reset();
//        testReceiver.setReceiveEnabled(true);
//
//        //Wireshark: https://dl.dropbox.com/u/10179177/Screenshot_2013.04.11-21.53.31.png
//
//        //create registration request
//        String requestPath = "/testpath";
//        URI targetUri = new URI("coap://localhost:" +
//                CoapServerApplication.DEFAULT_COAP_SERVER_PORT + requestPath);
//        coapRequest = new CoapRequest(MsgType.CON, Code.GET, targetUri);
//        coapRequest.getHeader().setMsgID(1111);
//        coapRequest.setToken(new byte[]{0x13, 0x53, 0x34});
//        coapRequest.setObserveOptionRequest();
//
//        //create notifications
//        (expectedNotification1 = new CoapResponse(Code.CONTENT_205)).setPayload("testpayload1tt".getBytes("UTF-8"));
//        expectedNotification1.setMaxAge(5);
//        (expectedNotification2 = new CoapResponse(Code.CONTENT_205)).setPayload("testpayload2".getBytes("UTF-8"));
//
//        ObservableDummyWebService observableDummyWebService = new ObservableDummyWebService(requestPath, true, 0, 0);
//        observableDummyWebService.addPreparedResponses(expectedNotification1, expectedNotification2);
//        testServer.registerService(observableDummyWebService);
//
//        //setup testServer
////        testServer.registerDummyService(requestPath);
//        //if both cancellations fail, the notification will be send six times
////        testServer.addResponse(expectedNotification1, expectedNotification2);
//
//
//        //run test sequence
//
//        //registration
//        testReceiver.writeMessage(coapRequest, new InetSocketAddress("localhost", DEFAULT_COAP_SERVER_PORT));
//        //wait for response
//        Thread.sleep(150);
//
//        //wait for Max-Age to end and resulting notification
//        Thread.sleep(5500);
//
//        //send reset to remove observer
//        CoapResponse rstMsg = new CoapResponse(Code.EMPTY);
//        rstMsg.getHeader().setMsgType(MsgType.RST);
//        rstMsg.setMessageID(expectedNotification2.getMessageID());
//        testReceiver.writeMessage(rstMsg, new InetSocketAddress("localhost", DEFAULT_COAP_SERVER_PORT));
//
//        testReceiver.setReceiveEnabled(false);
//    }

    @Test
    public void testReceiverReceived2Messages() {
        String message = "Receiver did not receive 2 messages";
        assertEquals(message, 2, testReceiver.getReceivedMessages().values().size());
    }

    @Test
    public void testReceivedMessageArrivedIn2secDelay() {
        SortedMap<Long, CoapMessage> receivedMessages = testReceiver.getReceivedMessages();
        Iterator<Long> timeKeys = receivedMessages.keySet().iterator();
        Long msg1time = timeKeys.next();
        Long msg2time = timeKeys.next();
        long delay = msg2time - msg1time;

        String message = "Scheduled Max-Age notification did not arrive after 5 seconds";
        assertTrue(message, Math.abs(2000 - delay) < 200); //200ms tolerance
    }

    @Test
    public void testObserveOptionIsSetProperly() {
        SortedMap<Long, CoapMessage> receivedMessages = testReceiver.getReceivedMessages();
        Iterator<Long> timeKeys = receivedMessages.keySet().iterator();
        CoapMessage recNotification1 = receivedMessages.get(timeKeys.next());
        CoapMessage recNotification2 = receivedMessages.get(timeKeys.next());

        Long observe1 = ((UintOption)recNotification1.getOption(OBSERVE_RESPONSE).get(0)).getDecodedValue();
        Long observe2 = ((UintOption)recNotification2.getOption(OBSERVE_RESPONSE).get(0)).getDecodedValue();

        String message = String.format("ObserveOption sequence is not set properly (1st: %d, 2nd: %d)",
                observe1, observe2);
        assertTrue(message, observe1 < observe2);
    }

    @Test
    public void testMessageType() {
        SortedMap<Long, CoapMessage> receivedMessages = testReceiver.getReceivedMessages();
        Iterator<Long> timeKeys = receivedMessages.keySet().iterator();
        CoapMessage recNotification1 = receivedMessages.get(timeKeys.next());
        CoapMessage recNotification2 = receivedMessages.get(timeKeys.next());

        String message = "1st notification should be ACK";
        assertEquals(message, MsgType.ACK, recNotification1.getMessageType());
        message = "2nd notification should be CON";
        assertEquals(message, MsgType.CON, recNotification2.getMessageType());
    }

    @Test
    public void testMessagePayload() {
        SortedMap<Long, CoapMessage> receivedMessages = testReceiver.getReceivedMessages();
        Iterator<Long> timeKeys = receivedMessages.keySet().iterator();
        CoapMessage recNotification1 = receivedMessages.get(timeKeys.next());
        CoapMessage recNotification2 = receivedMessages.get(timeKeys.next());

        String message = "1st notifications payload does not match";
        assertEquals(message, expectedNotification1.getPayload(), recNotification1.getPayload());
        message = "2nd notifications payload does not match";
        assertEquals(message, expectedNotification2.getPayload(), recNotification2.getPayload());
    }
}
