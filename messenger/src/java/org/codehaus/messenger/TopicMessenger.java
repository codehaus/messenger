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
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p><code>TopicMessengerSupport</code> is a Messenger implementation
 * for working with Topics
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public class TopicMessenger extends MessengerSupport {

    private Log log = LogFactory.getLog(MessengerSupport.class);
    private TopicSession session;
    private boolean durable = false;
    private boolean noLocal = false;
    private String durableName;

    public TopicMessenger(TopicSession session) {
        super(session);
        this.session = session;
    }

    public Destination createDestination(String subject) throws JMSException {
        return session.createTopic(subject);
    }

    public Destination createTemporaryDestination() throws JMSException {
        return session.createTemporaryTopic();
    }

    public void send(Destination destination, Message message)
        throws JMSException {
            
        /** @todo we should add a more customizable way of creating publishers */
            
        TopicPublisher publisher = session.createPublisher((Topic) destination);
        try {
            publisher.publish((Topic) destination, message);
        }
        finally {
            try {
                publisher.close();
            }
            catch (JMSException e) {
                log.info(
                    "Warning: caught exception closing publisher: " + e,
                    e);
            }
        }
    }

    public MessageConsumer createConsumer(Destination destination)
        throws JMSException {
        if (isDurable()) {
            return session.createDurableSubscriber(
                (Topic) destination,
                getDurableName());
        }
        else {
            return session.createSubscriber((Topic) destination);
        }
    }

    public MessageConsumer createConsumer(
        Destination destination,
        String selector)
        throws JMSException {
        if (isDurable()) {
            return session.createDurableSubscriber(
                (Topic) destination,
                getDurableName(),
                selector,
                isNoLocal());
        }
        else {
            return session.createSubscriber(
                (Topic) destination,
                selector,
                isNoLocal());
        }
    }

    // Properties
    //-------------------------------------------------------------------------    

    /** 
     * @return the TopicSession this Messenger is using
     */
    public TopicSession getTopcSession() throws JMSException {
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
