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

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * <p>
 * <code>DefaultMessengerPool</code> is a default implementation of a
 * MessengerPool which uses a SessionFactory to lazily create or lookup the
 * connection factory or connection object.
 * 
 * Messenger instances are pooled using Jakarta Commons Pool. You can pass in 
 * your own implementation or configuration of ObjectPool into this object on
 * construction, or let it create one with the defaults or use a GenericObjectPool
 * using the GenericObjectPool's Config object.
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan </a>
 * @version $Revision: 1.1 $
 */
public class DefaultMessengerPool extends MessengerPoolSupport implements PoolableObjectFactory  {

    private ObjectPool channel;

    public DefaultMessengerPool() {
    }

    public DefaultMessengerPool(SessionFactory factory) {
        super(factory);
        this.channel = new GenericObjectPool(this);
    }

    public DefaultMessengerPool(SessionFactory factory, GenericObjectPool.Config poolConfig) {
        super(factory);
        this.channel = new GenericObjectPool(this, poolConfig);
    }

    public DefaultMessengerPool(SessionFactory factory, ObjectPool channel) {
        super(factory);
        this.channel = channel;
    }

    public Messenger getMessenger() throws JMSException {
        Messenger messenger = null;
            try {
                messenger = (Messenger) channel.borrowObject();
            }
            catch (Exception e1) {
                // ignore exception
            }
        if (messenger == null) {
            messenger = makePooledMessenger();
        }
        return messenger;
    }

    protected Messenger makePooledMessenger() throws JMSException {
        return new PooledMessenger(createMessenger());
    }

    protected void addMessengerToPool(PooledMessenger messenger) {
        while (true) {
            try {
                channel.returnObject(messenger);
                return;
            }
            catch (Exception e) {
                // lets ignore and try again
            }
        }
    }

    protected class PooledMessenger implements Messenger {

        private Messenger delegate;

        public PooledMessenger(Messenger delegate) {
            this.delegate = delegate;
        }

        public void realClose() throws JMSException {
            delegate.close();
        }

        public void close() throws JMSException {
            addMessengerToPool(this);
        }

        public void commit() throws JMSException {
            delegate.commit();
        }

        public BytesMessage createBytesMessage() throws JMSException {
            return delegate.createBytesMessage();
        }

        public MessageConsumer createConsumer(Destination destination) throws JMSException {
            return delegate.createConsumer(destination);
        }

        public MessageConsumer createConsumer(Destination destination, String selector) throws JMSException {
            return delegate.createConsumer(destination, selector);
        }

        public Destination createDestination(String subject) throws JMSException {
            return delegate.createDestination(subject);
        }

        public MapMessage createMapMessage() throws JMSException {
            return delegate.createMapMessage();
        }

        public Message createMessage() throws JMSException {
            return delegate.createMessage();
        }

        public ObjectMessage createObjectMessage() throws JMSException {
            return delegate.createObjectMessage();
        }

        public ObjectMessage createObjectMessage(Serializable object) throws JMSException {
            return delegate.createObjectMessage(object);
        }

        public StreamMessage createStreamMessage() throws JMSException {
            return delegate.createStreamMessage();
        }

        public Destination createTemporaryDestination() throws JMSException {
            return delegate.createTemporaryDestination();
        }

        public TextMessage createTextMessage() throws JMSException {
            return delegate.createTextMessage();
        }

        public TextMessage createTextMessage(String text) throws JMSException {
            return delegate.createTextMessage(text);
        }

        public Session getSession() throws JMSException {
            return delegate.getSession();
        }

        public void recover() throws JMSException {
            delegate.recover();
        }

        public void rollback() throws JMSException {
            delegate.rollback();
        }

        public void send(Destination destination, Message message) throws JMSException {
            delegate.send(destination, message);
        }
    }

    // PoolableObjectFactory interface
    //-------------------------------------------------------------------------
    public Object makeObject() throws Exception {
        return makePooledMessenger();
    }

    public void destroyObject(Object object) throws Exception {
        PooledMessenger messenger = (PooledMessenger) object;
        messenger.realClose();
    }

    public boolean validateObject(Object object) {
        return true;
    }

    public void activateObject(Object object) throws Exception {
    }

    public void passivateObject(Object object) throws Exception {
    }
}