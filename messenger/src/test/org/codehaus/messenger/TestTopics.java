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

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** 
 * Test case for topics
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public class TestTopics extends TestQueues {

    public static Test suite() {
        return new TestSuite(TestTopics.class);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].startsWith("-v")) {
                verbose = true;
            }
        }
        TestRunner.run(suite());
    }

    public TestTopics(String testName) {
        super(testName);

        subject = "jms/Topic";
    }

    protected MessengerPool createMessengerPool() throws Exception {
        return createMessengerPool("JmsTopicConnectionFactory", true);
    }
}
