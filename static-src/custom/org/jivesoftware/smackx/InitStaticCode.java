/**
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.smackx;

import android.content.Context;

/**
 * Since dalvik on Android does not allow the loading of META-INF files from the
 * filesystem, the static blocks of some classes have to be inited manually.
 *
 * The full list can be found here:
 * http://fisheye.igniterealtime.org/browse/smack/trunk/build/resources/META-INF/smack-config.xml?hb=true
 *
 * @author Florian Schmaus fschmaus@gmail.com
 *
 */
public class InitStaticCode {

    public static void initStaticCode(Context ctx) {
	    // This has the be the application class loader,
	    // *not* the system class loader
	    ClassLoader appClassLoader = ctx.getClassLoader();

	    try {
		    Class.forName(org.jivesoftware.smackx.ServiceDiscoveryManager.class.getName(), true, appClassLoader);
		    Class.forName(org.jivesoftware.smack.PrivacyListManager.class.getName(), true, appClassLoader);
		    Class.forName(org.jivesoftware.smackx.XHTMLManager.class.getName(), true, appClassLoader);
		    Class.forName(org.jivesoftware.smackx.muc.MultiUserChat.class.getName(), true, appClassLoader);
		    Class.forName(org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager.class.getName(), true, appClassLoader);
		    Class.forName(org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager.class.getName(), true, appClassLoader);
		    Class.forName(org.jivesoftware.smackx.filetransfer.FileTransferManager.class.getName(), true, appClassLoader);
		    Class.forName(org.jivesoftware.smackx.LastActivityManager.class.getName(), true, appClassLoader);
		    Class.forName(org.jivesoftware.smack.ReconnectionManager.class.getName(), true, appClassLoader);
		    Class.forName(org.jivesoftware.smackx.commands.AdHocCommandManager.class.getName(), true, appClassLoader);
	    } catch (ClassNotFoundException e) {
		    throw new IllegalStateException("Could not init static class blocks", e);
	    }
    }
}
