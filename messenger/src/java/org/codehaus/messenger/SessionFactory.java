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

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p><code>SessionFactory</code> is a Factory of JMS Session objects.
 * It can be configured with a JMS Connection object to use or can use 
 * a JMS ConnectionFactory instance to create the JMS Connection lazily</p>
 *
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision: 1.2 $
 */
public class SessionFactory {

    /** Logger */
    private static final Log log = LogFactory.getLog(SessionFactory.class);

    /** Should we create a connection per messenger or just session per messager */
    private boolean shareConnections = false;
    
    /** The JMS connection used to create JMS sessions */
    private Connection connection;

    /** The JMS ConnectionFactory used to create JMS Connection instances */
    private ConnectionFactory connectionFactory;

    /** JMS acknowlege mode used on each session */
    private int acknowlegeMode = Session.AUTO_ACKNOWLEDGE;

    /** whether JMS sessions should be transacted */
    private boolean transacted;

    /** the optional username used when creating a new JMS connection via a JMS ConnectionFactory */
    private String username;

    /** the optional password used when creating a new JMS connection via a JMS ConnectionFactory */
    private String password;

    /** the properties used to create the connection */
    protected Properties properties;

    /** Whether to use a Topic or Queue connection/session */
    private boolean topic = true;

    /** The client ID for the connection */
    private String clientID;
    
    public SessionFactory() {
    }
    
    public SessionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public SessionFactory(ConnectionFactory connectionFactory, boolean topic) {
        this.connectionFactory = connectionFactory;
        this.topic = topic;
    }
    
    /** Creates a new Session instance */
    public Session createSession(Connection connection) throws JMSException {
        if ( log.isDebugEnabled() ) {
            log.debug( 
                "Creating a JMS session in transacted mode: " 
                + isTransacted() + " with ack mode: " + getAcknowledgeMode() 
            );
        }
        
        if (topic) {
            TopicConnection topicConnection = (TopicConnection) connection;
            return topicConnection.createTopicSession(isTransacted(), getAcknowledgeMode());
        }
        else {
            QueueConnection queueConnection = (QueueConnection) connection;
            return queueConnection.createQueueSession(isTransacted(), getAcknowledgeMode());
        }
    }

    /** Creates a new Session instance */
    public Session createSession() throws JMSException {
        Connection connection = getConnection();
        if (topic) {
            TopicConnection topicConnection = (TopicConnection) connection;
            return topicConnection.createTopicSession(isTransacted(), getAcknowledgeMode());
        }
        else {
            QueueConnection queueConnection = (QueueConnection) connection;
            return queueConnection.createQueueSession(isTransacted(), getAcknowledgeMode());
        }
    }

    /** Factory method used to create a connection */
    public Connection createConnection() throws JMSException {
        ConnectionFactory factory = getConnectionFactory();
        if (factory == null) {
            throw new JMSException("No ConnectionFactory configured. Cannot create a JMS Session");
        }
        if (log.isDebugEnabled()) {
            log.debug("About to create a connection from: " + factory);
        }
        if (topic) {
            return createTopicConnection((TopicConnectionFactory) factory);
        }
        else {
            return createQueueConnection((QueueConnectionFactory) factory);
        }
    }

    /** Closes the JMS Connection that this object is using, if any */
    public synchronized void close() throws JMSException {
        if (connection != null) {
            Connection tmp = connection;
            connection = null;
            tmp.close();
        }
    }


    // Properties
    //-------------------------------------------------------------------------    
    
    /** 
     * If we are sharing JMS connections across Messenger instances
     * then this method will lazily create a connection and share it for
     * all other sessions - otherwise we'll create a new connection each time
     * 
     * @return the JMS connection used to create new sessions 
     */
    public Connection getConnection() throws JMSException {
        if (!shareConnections || connection == null) {
            setConnection(createConnection());
            connection.start();
        }
        return connection;
    }

    public void setConnection(Connection connection) throws JMSException {
        this.connection = connection;
        // change the topic flag if the wrong topic/queue type
        if (topic) {
            if (!(connection instanceof TopicConnection)) {
                setTopic(false);
            }
        }
        else {
            if (!(connection instanceof QueueConnection)) {
                setTopic(true);
            }
        }
        
        // assign a clientID if one is set
        if (clientID != null) {
            connection.setClientID(clientID);                
        }
    }

    /** Returns the JMS ConnectionFactory used to create a new connection */
    public ConnectionFactory getConnectionFactory() throws JMSException {
        if (connectionFactory == null) {
            setConnectionFactory(createConnectionFactory());
        }
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        // change the topic flag if the wrong topic/queue type
        if (topic) {
            if (!(connectionFactory instanceof TopicConnectionFactory)) {
                setTopic(false);
            }
        }
        else {
            if (!(connectionFactory instanceof QueueConnectionFactory)) {
                setTopic(true);
            }
        }
    }

    /** Returns true if sessions created by this factory should be transacted */
    public boolean isTransacted() {
        return transacted;
    }

    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }

    /**
     * Are we sharing connections across messenger isntances or do we create
     * a new connection for each Messenger (which is the default)
     * 
     * @return
     */
    public boolean isShareConnections() {
        return shareConnections;
    }
    
    /**
     * This method allows connections to be shared across messenger instances.
     * The default is to create a new connection for each Messenger.
     * 
     * @param shareConnections
     */
    public void setShareConnections(boolean shareConnections) {
        this.shareConnections = shareConnections;
    }
    
    /** Returns the JMS acknowledge mode used by the JMS sessions created by this session */
    public int getAcknowledgeMode() {
        return acknowlegeMode;
    }

    public void setAcknowledgeMode(int acknowlegeMode) {
        this.acknowlegeMode = acknowlegeMode;
    }
    
    /**
     * A String based setter method to allow this property to be defined
     * easily from within the digester XML file.
     * 
     * @param value is either "auto", "client" or "dups_ok"
     * @throws IllegalArgumentException if the value is not one of the correct values 
     */
    public void setAcknowledge(String value) {
    	if (value != null) {
    		if (value.equalsIgnoreCase("auto")) {
    			setAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
    		}
    		else if (value.equalsIgnoreCase("client")) {
    			setAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
    		}
    		else if (value.equalsIgnoreCase("dups_ok")) {
    			setAcknowledgeMode(Session.DUPS_OK_ACKNOWLEDGE);
    		}
    		else {
    			throw new IllegalArgumentException(
					"Value: " + value 
					+ " is invalid. Must be 'auto', 'client' or 'dups_ok'"
				);
    		}
    	}
    }

    /** Returns the optional username used when creating a new JMS connection via a JMS ConnectionFactory */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /** Returns the optional password used when creating a new JMS connection via a JMS ConnectionFactory */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /** Returns the Properties that can be used to configure the connection creation */
    public Properties getProperties() {
        if (properties == null) {
            properties = createProperties();
        }
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void addProperty(String name, String value) {
        getProperties().setProperty(name, value);
    }

    /** @return whether to use a Topic or Queue connection/session */
    public boolean isTopic() {
        return topic;
    }

    /** Sets whether to use a Topic or Queue connection/session */
    public void setTopic(boolean topic) {
        this.topic = topic;
    }

    /**
     * Returns the clientID used on the current connection.
     * @return String
     */
    public String getClientID() {
        return clientID;
    }

    /**
     * Sets the clientID used on the current connection.
     * @param clientID The clientID to set
     */
    public void setClientID(String clientID) {
        this.clientID = clientID;
    }


    // Implementation methods
    //-------------------------------------------------------------------------    
    protected QueueConnection createQueueConnection(QueueConnectionFactory queueConnectionFactory)
        throws JMSException {
        if (username != null || password != null) {
            return queueConnectionFactory.createQueueConnection(username, password);
        }
        else {
            return queueConnectionFactory.createQueueConnection();
        }
    }

    protected TopicConnection createTopicConnection(TopicConnectionFactory topicConnectionFactory)
        throws JMSException {
        if (username != null || password != null) {
            return topicConnectionFactory.createTopicConnection(username, password);
        }
        else {
            return topicConnectionFactory.createTopicConnection();
        }
    }

    /** Factory method used to create a connection factory. 
      * Derived classes may wish to use JNDI to load the ConnectionFactory
      */
    protected ConnectionFactory createConnectionFactory() throws JMSException {
        return null;
    }

    /** Factory method used to create the initial JNDI context properties.
      * Derived classes may wish to overload this method to provide different properties
      */
    protected Properties createProperties() {
        try {
            return new Properties(System.getProperties());
        }
        catch (Throwable e) {
            // security exceptoin
            return new Properties();
        }
    }
}
