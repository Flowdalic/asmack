aSmack - buildsystem for Smack on Android
=========================================

*This repository doesn't contain much code, it's a build environment!*

Tracking trunk can be hard. Doing massive changes on top of trunk can
be near impossible. We are mixing 6 open source projects to provide a
working xmpp library for Android. All trunk-based.

This repository contains a source fetching, patching and building
script.  As well as all the minor changes to make an Android version
fly.  See the patches/ folder for a detailed list of changes and
scripts.

Compiled JARs
=============

Make sure to [*read the
README*](https://github.com/Flowdalic/asmack/blob/master/README.asmack)
for every release! Or else aSmack won't work for you. 95% of the
problems people experiencing with aSmack come from the fact that they
didn't read the README.

The JARs can be found @ http://asmack.freakempire.de/

Support & Contact
=================

The best way to get support is IRC: Join ##smack @ freenode. But keep
in mind that this is IRC, it may take a while till you get an
answer. Up to a few days. So make sure to idle around. :)

You can also use upstream's discussion forum:
http://community.igniterealtime.org/community/developers/smack

Compiling aSmack
==========================

Linux: https://github.com/Flowdalic/asmack/wiki/Build-Instructions-for-Linux

Mac OS X: https://github.com/Flowdalic/asmack/wiki/Build-Instructions-on-Mac-OS-X

Note that building on Mac OS X is a user contribution and not offically supported.

Connecting to Openfire
==========================

https://github.com/Flowdalic/asmack/wiki/Connecting-to-Openfire


Apps that use this fork of aSmack
=================================
- [GTalkSMS](http://code.google.com/p/gtalksms/) uses many features of Smack and XMPP on Android:
    - File Transfer
    - DNS SRV
    - MUC
    - Entity Caps
    - and many more 

- [yaxim](https://github.com/ge0rg/yaxim)
- your app?

Contribution
============

If possible, please base patches on smack, not on aSmack. You can use
the 'upstream' branch from [smack @
github](https://github.com/Flowdalic/smack). Only in some cases the
'master' branch should be used.

If your code follows [Smack's contributor guidelines](
http://community.igniterealtime.org/docs/DOC-1984), is good documented
and comes with some testcases, then it's possible to commit it
upstream. Simply join ##smack @ freenode and ask for a code review.

Contributors
============

We do not keep a seperate CONTRIBUTORS file, and we discourage @author
tags.  However you're free to add your full name to every git commit,
and we will preserver this. Let us know if you've helped on
non-technical stuff and we'll find a way to give you the deserved
credit.

Licences / Used libraries
=========================

We only accept Apache and BSD-like licences.
We are currently using code from

 * Apache Harmony (sasl/xml) (Apache Licence)
 * smack (xmpp) (Apache Licence)
 * novell-openldap-jldap (sasl) ([OpenLDAP Licence][1])
 * Apache qpid (sasl) (Apache Licence)
 * jbosh (BOSH) (Apache Licence)
 * dnsjava (dns srv lookups) (BSD)
 * custom code (various glue stuff) (WTFPL | BSD | Apache)

This should work for just about every project. Contact us if you have
problems with the licence.

  [1]: http://www.openldap.org/devel/cvsweb.cgi/~checkout~/LICENSE?rev=1.23.2.1&hideattic=1&sortbydate=0  "OpenLDAP Licence"


Flattr
======

[![Flattr this git repo](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=Flowdalic&url=https://github.com/flowdalic/asmack&title=asmack&language=&tags=github&category=software)