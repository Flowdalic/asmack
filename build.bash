#!/bin/bash

echo "## Step 00: initialize"
(
  if ! [ -d build ]; then
    mkdir build
    mkdir build/src
    mkdir build/src/trunk
  fi
)

fetch() {
(
  echo "Fetching from ${1} to ${2}"
  cd src
  if ! [ -f "${2}/.svn/entries" ]; then
    mkdir "${2}"
    cd "${2}"
    svn co --non-interactive --trust-server-cert "${1}" "."
  else
    cd "${2}"
    svn cleanup
    svn up
  fi
)
}

gitfetch() {
(
  echo "Fetching ${3} branch from ${1} to ${2} via git"
  cd src
  if ! [ -f "${2}/.git/config" ]; then
    git clone "${1}" "${2}"
    git checkout origin/"${3}"
  else
    cd "${2}"
    git pull
    git checkout origin/"${3}"
  fi
)
}

fetchall() {
  gitfetch "git://github.com/Flowdalic/smack.git" "smack" "smack_extended"
  fetch "http://svn.apache.org/repos/asf/qpid/trunk/qpid/java/management/common/src/main/" "qpid"
  fetch "http://svn.apache.org/repos/asf/harmony/enhanced/java/trunk/classlib/modules/auth/src/main/java/common/" "harmony"
  fetch "https://dnsjava.svn.sourceforge.net/svnroot/dnsjava/trunk" "dnsjava"
  gitfetch "git://kenai.com/jbosh~origin" "jbosh" "master"
#  gitfetch "git://git.openldap.org/openldap-jldap.git" "novell-openldap-jldap" "master"
}

copyfolder() {
(
  (
    cd "${1}"
    tar -cSsp --exclude-vcs "${3}"
  ) | (
    cd "${2}"
    tar -xSsp
  )
)
}

buildsrc() {
  echo "## Step 20: creating build/src"
  rm -rf build/src
  mkdir build/src
  mkdir build/src/trunk
  copyfolder "src/smack/source/" "build/src/trunk" "."
  copyfolder "src/qpid/java" "build/src/trunk" "org/apache/qpid/management/common/sasl"
  copyfolder "src/novell-openldap-jldap" "build/src/trunk" "."
  copyfolder "src/dnsjava"  "build/src/trunk" "org"
  copyfolder "src/harmony" "build/src/trunk" "."
  copyfolder "src/custom" "build/src/trunk" "."
  copyfolder "src/jbosh/src/main/java" "build/src/trunk" "."
}

patchsrc() {
  echo "## Step 21: patch build/src"
  (
    cd build/src/trunk/
    for PATCH in `(cd "../../../${1}" ; find -maxdepth 1 -type f)|sort` ; do
      if echo $PATCH | grep '\.sh$'; then
        if [ -f "../../../${1}/$PATCH" ]; then "../../../${1}/$PATCH" || exit 1 ; fi
      fi
      if echo $PATCH | grep '\.patch$'; then
        if [ -f "../../../${1}/$PATCH" ]; then patch -p0 < "../../../${1}/$PATCH" || exit 1 ; fi
      fi
    done
  )
}

build() {
  echo "## Step 30: compile"
  ant -Dbuild.all=true
  buildandroid 
}

buildandroid() {
    sdklocation=$(grep sdk-location local.properties| cut -d= -f2)
    if [ -z "$sdklocation" ] ; then
	echo "Android SDK not found. Don't build android version"
	return
    fi
    for f in $sdklocation/platforms/* ; do
	version=`basename $f`
	ant -Dandroid.version=${version}  -Djar.suffix="$1" compile-android
    done
}

buildcustom() {
  for dir in `find patch -maxdepth 1 -mindepth 1 -type d`; do
    buildsrc
    patchsrc "patch"
    patchsrc "${dir}"
    custom=`echo ${dir}|sed 's:patch/:-:'`
    ant -Djar.suffix="$custom"
    buildandroid `echo ${dir}|sed 's:patch/:-:'`
  done
}

fetchall
buildsrc
patchsrc "patch"
build
buildcustom

if which advzip; then
  find build/*.jar -exec advzip -z4 '{}' ';'
  find build/*.zip -exec advzip -z4 '{}' ';'
fi
