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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.jms.QueueSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p><code>QueueMessengerSupport</code> is a Messenger
 * implementation for working with queues
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public class QueueMessenger extends MessengerSupport {

    private Log log = LogFactory.getLog(MessengerSupport.class);
    private QueueSession session;
    private boolean durable = false;
    private boolean noLocal = false;
    private String durableName;

    public QueueMessenger(QueueSession session) {
        super(session);
        this.session = session;
    }

    public Destination createDestination(String subject) throws JMSException {
        return session.createQueue(subject);
    }

    public Destination createTemporaryDestination() throws JMSException {
        return session.createTemporaryQueue();
    }

    public void send(Destination destination, Message message)
        throws JMSException {
        QueueSender sender = session.createSender((Queue) destination);
        try {
            sender.send((Queue) destination, message);
        }
        finally {
            try {
                sender.close();
            }
            catch (JMSException e) {
                log.info("Warning: caught exception closing sender: " + e, e);
            }
        }
    }

    public MessageConsumer createConsumer(Destination destination)
        throws JMSException {
        return session.createReceiver((Queue) destination);
    }

    public MessageConsumer createConsumer(
        Destination destination,
        String selector)
        throws JMSException {
        return session.createReceiver((Queue) destination, selector);
    }

    // Properties
    //-------------------------------------------------------------------------    

    /** 
     * @return the QueueSession this Messenger is using
     */
    public QueueSession getTopcSession() throws JMSException {
        return session;
    }

    /**
     * @return
     */
    public boolean isDurable() {
        return durable;
    }

    /**
     * @param durable
     */
    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    /**
     * @return
     */
    public String getDurableName() {
        return durableName;
    }

    /**
     * @param durableName
     */
    public void setDurableName(String durableName) {
        this.durableName = durableName;
    }

    /**
     * @return
     */
    public boolean isNoLocal() {
        return noLocal;
    }

    /**
     * @param noLocal
     */
    public void setNoLocal(boolean noLocal) {
        this.noLocal = noLocal;
    }

}
