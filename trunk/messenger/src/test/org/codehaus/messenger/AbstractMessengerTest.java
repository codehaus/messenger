/** 
 * 
 * Copyright 2004 Protique Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 **/ 
package org.codehaus.messenger;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Abstract base class for Messenger tests
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractMessengerTest extends TestCase {

    /** Logger */
    protected Log log = LogFactory.getLog(getClass());

    protected MessengerPool messengerPool;

    protected static boolean verbose = true;
    protected List failures = new ArrayList();

    protected String subject;
    protected String messageText = "This is the text of a message";
    protected long waitTime = 2 * 1000;
    protected volatile boolean receivedMessage;

    public AbstractMessengerTest(String testName) {
        super(testName);
    }

    public void testSend() throws Exception {
        Messenger messenger = messengerPool.getMessenger();
        Destination destination = messenger.createDestination(subject);

        final MessageConsumer consumer = messenger.createConsumer(destination);
        
        flushConsumer(consumer);

        Thread thread = new Thread() {
            public void run() {
                try {
                    receiveTopicMessage(consumer);
                }
                catch (Exception e) {
                    log.error("Caught: " + e, e);
                    failures.add(e);
                }
            }
        };
        thread.start();

        log.info("sleeping to let the receive thread start");

        Thread.sleep(waitTime);

        log.info("sending destination message");

        // lets create another session to send from
        Messenger sendMessenger = messengerPool.getMessenger();
        
        TextMessage message = sendMessenger.createTextMessage(messageText);

        sendMessenger.send(destination, message);

        log.info("sleeping");

        Thread.sleep(waitTime);

        assertTrue("Have received the destination message", receivedMessage);
        assertEquals("Should not have received any exceptions", 0, failures.size());
        
        try {
            consumer.close();
        }
        catch (JMSException e) {
            log.warn("Caught exception closing the consumer: " + e, e);
        }
        
        try {
            messenger.close();
            sendMessenger.close();
        }
        catch (JMSException e) {
            log.warn("Caught exception closing messenger: " + e, e);
        }
    }

    protected void setUp() throws Exception {
        messengerPool = createMessengerPool();
    }

    /**
     * Factory method for derived tests to create specific MessengerPool 
     * implementations
     * 
     * @return
     * @throws Exception
     */
    protected abstract MessengerPool createMessengerPool() throws Exception;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        messengerPool.close();
    }

    protected void flushConsumer(
        MessageConsumer consumer)
        throws Exception {

        log.info("Clearing consumer: " + consumer);

        // lets remove any existing messages
        while (true) {
            Message m = consumer.receiveNoWait();
            if (m != null) {
                log.info("Ignoring message: " + m);
            }
            else {
                break;
            }
        }

        log.info("Cleared consumer: " + consumer);
    }

    protected void receiveTopicMessage(MessageConsumer consumer) throws Exception {

        log.info("Calling receive() on destination");

        TextMessage message = (TextMessage) consumer.receive();

        assertEquals("message text", messageText, message.getText());

        log.info("Found message: " + message.getText());

        receivedMessage = true;
    }

}
