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

import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.ibb.provider.CloseIQProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.DataPacketProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.OpenIQProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.carbons.Carbon;
import org.jivesoftware.smackx.entitycaps.provider.CapsExtensionProvider;
import org.jivesoftware.smackx.forward.Forwarded;
import org.jivesoftware.smackx.packet.AttentionExtension;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.Nick;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.ping.provider.PingProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.HeadersProvider;
import org.jivesoftware.smackx.provider.HeaderProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.pubsub.provider.AffiliationProvider;
import org.jivesoftware.smackx.pubsub.provider.AffiliationsProvider;
import org.jivesoftware.smackx.pubsub.provider.ConfigEventProvider;
import org.jivesoftware.smackx.pubsub.provider.EventProvider;
import org.jivesoftware.smackx.pubsub.provider.FormNodeProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemsProvider;
import org.jivesoftware.smackx.pubsub.provider.PubSubProvider;
import org.jivesoftware.smackx.pubsub.provider.RetractEventProvider;
import org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionProvider;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionsProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.search.UserSearch;

/**
 * Since dalvik on Android does not allow the loading of META-INF files from the
 * filesystem, you have to register every provider manually.
 *
 * The full list of providers is at:
 * http://fisheye.igniterealtime.org/browse/smack/trunk/build/resources/META-INF/smack.providers?hb=true
 *
 * @author Florian Schmaus fschmaus@gmail.com
 *
 */
public class ConfigureProviderManager {

    public static void configureProviderManager() {
        ProviderManager pm = ProviderManager.getInstance();

        // The order is the same as in the smack.providers file

        //  Private Data Storage
        pm.addIQProvider("query","jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());
        //  Time
        try {
            pm.addIQProvider("query","jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            System.err.println("Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        //  Roster Exchange
        pm.addExtensionProvider("x","jabber:x:roster", new RosterExchangeProvider());
        //  Message Events
        pm.addExtensionProvider("x","jabber:x:event", new MessageEventProvider());
        //  Chat State
        pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        //  XHTML
        pm.addExtensionProvider("html","http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

        //  Group Chat Invitations
        pm.addExtensionProvider("x","jabber:x:conference", new GroupChatInvitation.Provider());
        //  Service Discovery # Items
        pm.addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
        //  Service Discovery # Info
        pm.addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
        //  Data Forms
        pm.addExtensionProvider("x","jabber:x:data", new DataFormProvider());
        //  MUC User
        pm.addExtensionProvider("x","http://jabber.org/protocol/muc#user", new MUCUserProvider());
        //  MUC Admin
        pm.addIQProvider("query","http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
        //  MUC Owner
        pm.addIQProvider("query","http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
        //  Delayed Delivery
        pm.addExtensionProvider("x","jabber:x:delay", new DelayInformationProvider());
        pm.addExtensionProvider("delay", "urn:xmpp:delay", new DelayInformationProvider());
        //  Version
        try {
            pm.addIQProvider("query","jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            System.err.println("Can't load class for org.jivesoftware.smackx.packet.Version");
        }
        //  VCard
        pm.addIQProvider("vCard","vcard-temp", new VCardProvider());
        //  Offline Message Requests
        pm.addIQProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
        //  Offline Message Indicator
        pm.addExtensionProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
        //  Last Activity
        pm.addIQProvider("query","jabber:iq:last", new LastActivity.Provider());
        //  User Search
        pm.addIQProvider("query","jabber:iq:search", new UserSearch.Provider());
        //  SharedGroupsInfo
        pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

        //  JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses","http://jabber.org/protocol/address", new MultipleAddressesProvider());

        //   FileTransfer
        pm.addIQProvider("si","http://jabber.org/protocol/si", new StreamInitiationProvider());
        pm.addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
        pm.addIQProvider("open","http://jabber.org/protocol/ibb", new OpenIQProvider());
        pm.addIQProvider("data","http://jabber.org/protocol/ibb", new DataPacketProvider());
        pm.addIQProvider("close","http://jabber.org/protocol/ibb", new CloseIQProvider());
        pm.addExtensionProvider("data","http://jabber.org/protocol/ibb", new DataPacketProvider());

        //  Privacy
        pm.addIQProvider("query","jabber:iq:privacy", new PrivacyProvider());

        // SHIM
        pm.addExtensionProvider("headers", "http://jabber.org/protocol/shim", new HeadersProvider());
        pm.addExtensionProvider("header", "http://jabber.org/protocol/shim", new HeaderProvider());

        // PubSub
        pm.addIQProvider("pubsub", "http://jabber.org/protocol/pubsub", new PubSubProvider());
        pm.addExtensionProvider("create", "http://jabber.org/protocol/pubsub", new SimpleNodeProvider());
        pm.addExtensionProvider("items", "http://jabber.org/protocol/pubsub", new ItemsProvider());
        pm.addExtensionProvider("item", "http://jabber.org/protocol/pubsub", new ItemProvider());
        pm.addExtensionProvider("subscriptions", "http://jabber.org/protocol/pubsub", new SubscriptionsProvider());
        pm.addExtensionProvider("subscription", "http://jabber.org/protocol/pubsub", new SubscriptionProvider());
        pm.addExtensionProvider("affiliations", "http://jabber.org/protocol/pubsub", new AffiliationsProvider());
        pm.addExtensionProvider("affiliation", "http://jabber.org/protocol/pubsub", new AffiliationProvider());
        pm.addExtensionProvider("options", "http://jabber.org/protocol/pubsub", new FormNodeProvider());
        // PubSub owner
        pm.addIQProvider("pubsub", "http://jabber.org/protocol/pubsub#owner", new PubSubProvider());
        pm.addExtensionProvider("configure", "http://jabber.org/protocol/pubsub#owner", new FormNodeProvider());
        pm.addExtensionProvider("default", "http://jabber.org/protocol/pubsub#owner", new FormNodeProvider());
        // PubSub event
        pm.addExtensionProvider("event", "http://jabber.org/protocol/pubsub#event", new EventProvider());
        pm.addExtensionProvider("configuration", "http://jabber.org/protocol/pubsub#event", new ConfigEventProvider());
        pm.addExtensionProvider("delete", "http://jabber.org/protocol/pubsub#event", new SimpleNodeProvider());
        pm.addExtensionProvider("options", "http://jabber.org/protocol/pubsub#event", new FormNodeProvider());
        pm.addExtensionProvider("items", "http://jabber.org/protocol/pubsub#event", new ItemsProvider());
        pm.addExtensionProvider("item", "http://jabber.org/protocol/pubsub#event", new ItemProvider());
        pm.addExtensionProvider("retract", "http://jabber.org/protocol/pubsub#event", new RetractEventProvider());
        pm.addExtensionProvider("purge", "http://jabber.org/protocol/pubsub#event", new SimpleNodeProvider());

        // Nick Exchange
        pm.addExtensionProvider("nick", "http://jabber.org/protocol/nick", new Nick.Provider());

        // Attention
        pm.addExtensionProvider("attention", "urn:xmpp:attention:0", new AttentionExtension.Provider());

	// XEP-0297 Stanza Forwarding
	pm.addExtensionProvider("forwarded", "urn:xmpp:forward:0", new Forwarded.Provider());

	// XEP-0280 Message Carbons
	pm.addExtensionProvider("sent", "urn:xmpp:carbons:2", new Carbon.Provider());
	pm.addExtensionProvider("received", "urn:xmpp:carbons:2", new Carbon.Provider());

	// XEP-0199 XMPP Ping
	pm.addIQProvider("ping", "urn:xmpp:ping", new PingProvider());

	// XEP-184 Message Delivery Receipts
	pm.addExtensionProvider("received", "urn:xmpp:receipts", new DeliveryReceipt.Provider());
	pm.addExtensionProvider("request", "urn:xmpp:receipts", new DeliveryReceiptRequest.Provider());

	// XEP-0115 Entity Capabilities
	pm.addExtensionProvider("c", "http://jabber.org/protocol/caps", new CapsExtensionProvider());
    }
}
