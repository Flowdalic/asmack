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

Can be found @ http://asmack.freakempire.de/ 

Make sure to [*read the
README*](https://github.com/Flowdalic/asmack/blob/master/README.asmack)
for every release.

Support
=======
[![Flattr this git repo](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=Flowdalic&url=https://github.com/flowdalic/asmack&title=asmack&language=&tags=github&category=software)

Compiling aSmack on Linux
==========================

1. copy local.properties.example to local.properties and set the
Android SDK path (e.g. sdk-location=/opt/android-sdk-update-manager/
on a gentoo system)

2. Run build.bash

aSmack uses a [special fork of
smack](https://github.com/Flowdalic/smack). You can read about the
results you will get when using one of the various branches provides
in the
[README](https://github.com/Flowdalic/smack/blob/master/README.markdown).

Compiling aSmack on OSX (tested on 10.8)
========================================
- install ftp://gnu.mirror.iweb.com/gnu/findutils
- install ftp://gnu.mirror.iweb.com/gnu/coreutils
- install ftp://gnu.mirror.iweb.com/gnu/tar
- install ftp://gnu.mirror.iweb.com/gnu/bash
- install ftp://gnu.mirror.iweb.com/gnu/sed

each with ./configure && make && sudo make install

- either 1 or 2

1
- softlink /usr/local/bin/find 
- softlink /usr/local/bin/cp
- softlink /usr/local/bin/tar
- softlink /usr/local/bin/bash
- softlink /usr/local/bin/sed

2

edit /etc/paths, such that /usr/local/bin
is in the first place. this might sound extreme,
however it is not such a bad idea, as essentially
you install updated versions of existing programs
into /usr/local.


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

Contact
=======

Join ##smack @ freenode

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

