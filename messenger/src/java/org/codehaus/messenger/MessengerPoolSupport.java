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

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TopicSession;

/**
 * <p><code>SimpleMessengerPool.java</code> is a useful base class
 * for implementation inheritence.
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision: 1.2 $
 */
public abstract class MessengerPoolSupport implements MessengerPool {

    private SessionFactory factory;

    public MessengerPoolSupport() {
    }

    public MessengerPoolSupport(SessionFactory factory) {
        this.factory = factory;
    }
    
    /**
     * @return
     */
    public SessionFactory getFactory() {
        return factory;
    }

    /**
     * @param factory
     */
    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    public void close() throws JMSException {
        factory.close();
    }
    
    public abstract Messenger getMessenger() throws JMSException;

    /**
     * Factory method to create a new Messenger instances
     * 
     * @return
     * @throws JMSException
     */
    protected Messenger createMessenger() throws JMSException {
        // lets create a new one each time
        Connection connection = factory.getConnection();
        Session session = factory.createSession();
        if (factory.isTopic()) {
            return new TopicMessenger(connection, (TopicSession) session);
        }
        else {
            return new QueueMessenger(connection, (QueueSession) session);
        }
    }
    
}
