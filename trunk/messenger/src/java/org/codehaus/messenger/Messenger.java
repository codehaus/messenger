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

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

/**
 * <p><code>Messenger</code> a facade over a JMS session making JMS easier to
 * use and hiding much of the threading and pooling.
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public interface Messenger {

    /** 
     * @returns the destination for the given subject name 
     */
    public Destination createDestination(String subject) throws JMSException;

    /** 
     * @return a new temporary destination 
     */
    public Destination createTemporaryDestination() throws JMSException;

    /** 
     * Sends a message on the given destination 
     */
    public void send(Destination destination, Message message)
        throws JMSException;

//    /** 
//     * Sends a message on the given destination and blocks until a response is returned 
//     */
//    public Message call(Destination destination, Message message)
//        throws JMSException;
//
//    /** 
//     * Sends a message on the given destination and blocks until a response is returned or the given timeout period expires 
//     */
//    public Message call(
//        Destination destination,
//        Message message,
//        long timeoutMillis)
//        throws JMSException;

    /** Creates a MessageConsumer for the given JMS Desintation
     */
    public MessageConsumer createConsumer(Destination destination)
        throws JMSException;

    /** Creates a MessageConsumer for the given JMS Desintation and JMS selector
     */
    public MessageConsumer createConsumer(
        Destination destination,
        String selector)
        throws JMSException;

    // Properties
    //-------------------------------------------------------------------------    

    /** 
     * Returns the underlying JMS session this messenger will use
     */
    public Session getSession() throws JMSException;


    // Message factory methods
    //-------------------------------------------------------------------------    

    public BytesMessage createBytesMessage() throws JMSException;

    public MapMessage createMapMessage() throws JMSException;

    public Message createMessage() throws JMSException;

    public ObjectMessage createObjectMessage() throws JMSException;

    public ObjectMessage createObjectMessage(Serializable object)
        throws JMSException;

    public StreamMessage createStreamMessage() throws JMSException;

    public TextMessage createTextMessage() throws JMSException;

    public TextMessage createTextMessage(String text) throws JMSException;

    // Transaction related methods
    //-------------------------------------------------------------------------    
  
    /** 
     * Commits all messages done in this thread and releases any locks 
     */
    public void commit() throws JMSException;

    /** 
     * Rolls back any messages done in this thread and releases any locks 
     */
    public void rollback() throws JMSException;

    public void recover() throws JMSException;
    

    /** 
     * Closes this session and returns it to the pool 
     */
    public void close() throws JMSException;
}
