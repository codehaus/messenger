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
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

/**
 * <p><code>MessengerSupport</code> an abstract
 * base class for implementation in heritence
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision: 1.2 $
 */
public abstract class MessengerSupport implements Messenger {

    private Connection connection;
    private Session session;

    public MessengerSupport(Connection connection, Session session) {
        this.connection = connection;
        this.session = session;
    }

    public Connection getConnection() {
        return connection;
    }
    
    public Session getSession() throws JMSException {
        return session;
    }

    // Message factory methods
    //-------------------------------------------------------------------------    

    public BytesMessage createBytesMessage() throws JMSException {
        return session.createBytesMessage();
    }

    public MapMessage createMapMessage() throws JMSException {
        return session.createMapMessage();
    }

    public Message createMessage() throws JMSException {
        return session.createMessage();
    }

    public ObjectMessage createObjectMessage() throws JMSException {
        return session.createObjectMessage();
    }

    public ObjectMessage createObjectMessage(Serializable object)
        throws JMSException {
        return session.createObjectMessage(object);
    }

    public StreamMessage createStreamMessage() throws JMSException {
        return session.createStreamMessage();
    }

    public TextMessage createTextMessage() throws JMSException {
        return session.createTextMessage();
    }

    public TextMessage createTextMessage(String text) throws JMSException {
        return session.createTextMessage(text);
    }

    // Transaction related methods
    //-------------------------------------------------------------------------    
    public void commit() throws JMSException {
        session.commit();
    }

    public void rollback() throws JMSException {
        session.rollback();
    }

    public void recover() throws JMSException {
        session.recover();
    }

    public void close() throws JMSException {
        session.close();
    }
}
