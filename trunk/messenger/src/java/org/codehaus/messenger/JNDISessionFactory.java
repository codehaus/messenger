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
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** 
 * <p><code>JNDISessionFactory</code> is a Factory of JMS Session objects
 * which looks up the ConnectionFactory object from JNDI.</p>
 *
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public class JNDISessionFactory extends SessionFactory {

    /** Logger */
    private static final Log log = LogFactory.getLog( JNDISessionFactory.class );
    
    /** the initial JNDI context used to lookup ConnectionFactory objects */
    public Context context;

    /** the name used to lookup the ConnectionFactory */
    private String lookupName = "TopicConnectionFactory";


    // Properties
    //-------------------------------------------------------------------------

    /** The JNDI Name of the ConnectionFactory */
    public String getLookupName() {
        return lookupName;
    }

    /** Sets the JNDI Name of the ConnectionFactory */
    public void setLookupName(String lookupName) {
        this.lookupName = lookupName;
    }

    /** Returns the JNDI Context used to lookup JMS ConnectionFactory objects */
    public Context getContext() throws NamingException {
        if ( context == null ) {
            context = createContext();
        }
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /** Factory method used to create a connection factory.
      * Lookup the ConnectionFactory in JNDI
      */
    protected ConnectionFactory createConnectionFactory() throws JMSException {
        try {
            if ( log.isInfoEnabled() ) {
                log.info( "Looking up: " + getLookupName() + " in JNDI" );
            }
            return (ConnectionFactory) getContext().lookup(getLookupName());
        }
        catch (NamingException e) {
            JMSException jmsException = new JMSException( "Failed to lookup: " + getLookupName() + " using JNDI. " + e );
            jmsException.setLinkedException(e);
            throw jmsException;
        }
    }

    /** Re-implemented from SessionFactory. Method used to create a connection */
    public Connection createConnection() throws JMSException {
        ConnectionFactory factory = getConnectionFactory();

        if ( factory == null ) {
            throw new JMSException( "No ConnectionFactory configured. Cannot create a JMS Session" );
        }

        if ( isTopic() ) {
            return createTopicConnection((TopicConnectionFactory) factory);
        }
        else {
            return createQueueConnection((QueueConnectionFactory) factory);
        }
    }

    /** Re-implemented from SessionFactory. Creates a new Session instance */
    public Session createSession(Connection connection) throws JMSException {

        if ( isTopic() ) {
            TopicConnection topicConnection = (TopicConnection) connection;
            return topicConnection.createTopicSession( isTransacted(), getAcknowledgeMode() );
        }
        else {
            QueueConnection queueConnection = (QueueConnection) connection;
            return queueConnection.createQueueSession( isTransacted(), getAcknowledgeMode() );
        }
    }

    /** Factory method used to create a connection factory.
      * Derived classes may wish to use JNDI to load the ConnectionFactory
      */
    protected Context createContext() throws NamingException {
        if ( properties != null ) {
            return new InitialContext( properties );
        }
        else {
            return new InitialContext();
        }
    }
}

