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

import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** 
 * Test case for queues
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public class TestQueues extends AbstractMessengerTest {

    public static Test suite() {
        return new TestSuite(TestQueues.class);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].startsWith("-v")) {
                verbose = true;
            }
        }
        TestRunner.run(suite());
    }

    public TestQueues(String testName) {
        super(testName);

        subject = "jms/Queue";
    }

    protected MessengerPool createMessengerPool() throws Exception {
        return createMessengerPool("JmsQueueConnectionFactory", false);
    }
    
    protected MessengerPool createMessengerPool(String name, boolean topic) throws Exception {
        Hashtable properties = new Hashtable();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
        properties.put(Context.PROVIDER_URL, "rmi://localhost:1099/");

        Context context = new InitialContext(properties);

        SessionFactory factory =
            new SessionFactory((ConnectionFactory) context.lookup(name), topic);

        return new DefaultMessengerPool(factory);
    }
}
