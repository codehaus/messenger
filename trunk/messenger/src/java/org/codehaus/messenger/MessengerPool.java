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

/**
 * <p><code>Messenger</code> a facade over a JMS session making JMS easier to
 * use and hiding much of the threading and pooling.
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public interface MessengerPool {

    /** 
     * Returns the underlying JMS connection that any created Messenger instances 
     * will use 
     */
    public Connection getConnection() throws JMSException;

    /** 
     * Closes this connection and any related Messenger instances
     */
    public void close() throws JMSException;
    
    /**
     * Creates a new Messenger instance, using a JMS session from the pool.
     * To return the session back to the pull, call {@link Messenger.close()}
     * 
     * @return a new Messenger from the pool
     * @throws JMSException
     */
    public Messenger getMessenger() throws JMSException;
    
}
