#!/bin/bash

echo "## Step 00: initialize"
(
  if ! [ -d src ]; then
    mkdir src || ( echo "can't create source folder" ; exit 1 )
  fi
  if ! [ -d build ]; then
    mkdir build
    mkdir build/src
    mkdir build/src/trunk
  fi
)

fetch() {

echo "## Step 10: fetch smack"
(
  cd src
  if ! [ -d smack-trunk ]; then
    svn co http://svn.igniterealtime.org/svn/repos/smack/trunk/source/ smack-trunk || (
      echo "could not fetch smack/trunk"
      exit 11
    )
  else
    (
      cd smack-trunk
      svn cleanup && svn up
    )
  fi
)

echo "## Step 11: fetch qpid"
(
  cd src
  if ! [ -d qpid-trunk ]; then
    svn co http://svn.apache.org/repos/asf/qpid/trunk/qpid/java/management/common/src/main/ qpid-trunk
  else
    cd qpid-trunk
    svn cleanup && svn up
  fi
)

echo "## Step 12: fetch harmony"
(
  cd src
  if ! [ -d harmony-trunk ]; then
    svn co http://svn.apache.org/repos/asf/harmony/enhanced/classlib/trunk/modules/auth/src/main/java/common/ harmony-trunk
  else
    cd harmony-trunk
    svn cleanup && svn up
  fi
)

echo "## Step 13: fetch dnsjava"
(
  cd src
  if ! [ -d dnsjava ]; then
    svn co https://dnsjava.svn.sourceforge.net/svnroot/dnsjava/trunk dnsjava
  else
    cd dnsjava
    svn cleanup && svn up
  fi
)

}

buildsrc() {
  echo "## Step 20: creating build/src"
  rm -rf build/src
  mkdir build/src
  mkdir build/src/trunk
  (
    cd src/smack-trunk/
    tar -cSsp --exclude-vcs .
  ) | (
    cd build/src/trunk/
    tar -xSsp
  )
  (
    cd src/qpid-trunk/java
    tar -cSsp --exclude-vcs org/apache/qpid/management/common/sasl/
  ) | (
    cd build/src/trunk/
    tar -xSsp
  )
  (
    cd src/novell-openldap-jldap
    tar -cSsp --exclude-vcs .
  ) | (
    cd build/src/trunk/
    tar -xSsp
  )
  (
    cd src/dnsjava
    tar -cSsp --exclude-vcs org
  ) | (
    cd build/src/trunk/
    tar -xSsp
  )
  (
    cd src/harmony-trunk/
    tar -cSsp --exclude-vcs .
  ) | (
    cd build/src/trunk/
    tar -xSsp
  )
  (
    cd src/custom/
    tar -cSsp --exclude-vcs .
  ) | (
    cd build/src/trunk/
    tar -xSsp
  )

}

patchsrc() {
  echo "## Step 21: patch build/src"
  (
    cd build/src/trunk/
    for PATCH in `(cd ../../../patch ; find -maxdepth 1 -type f ; cd trunk ; find -maxdepth 1 -type f)|sort` ; do
      if echo $PATCH | grep '\.sh$'; then
        if [ -f ../../../patch/$PATCH ]; then ../../../patch/$PATCH ; fi
        if [ -f ../../../patch/trunk/$PATCH ]; then ../../../patch/trunk/$PATCH ; fi
      fi
      if echo $PATCH | grep '\.patch$'; then
        if [ -f ../../../patch/$PATCH ]; then patch -p0 < ../../../patch/$PATCH ; fi
        if [ -f ../../../patch/trunk/$PATCH ]; then patch -p0 < ../../../patch/trunk/$PATCH ; fi
      fi
    done
  )
}

build() {
  echo "## Step 30: compile"
  ant
}

fetch
buildsrc
patchsrc
build

