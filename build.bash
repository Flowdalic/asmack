#!/bin/bash
#set -x

fetch() {
    echo "Fetching from ${1} to ${2}"
    cd $SRC_DIR
    if ! [ -f "${2}/.svn/entries" ]; then
	mkdir "${2}"
	cd "${2}"
	svn co --non-interactive --trust-server-cert "${1}" "."
    else
	cd "${2}"
	svn cleanup
	svn up
    fi
}

gitfetch() {
    echo "Fetching ${2} branch from ${1} to ${3} via git"
    cd $SRC_DIR
    if ! [ -f "${3}/.git/config" ]; then
	git clone "${1}" "${3}"
	cd "${3}"
	git checkout origin/"${2}"
    else
	cd "${3}"
	git fetch
	git checkout origin/"${2}"
    fi

    if [ $? -ne 0 ]; then
	exit
    fi
}

testsmackgit() {
    cd $SRC_DIR
    if [ -f .used-smack-git-repo ] && [ $(cat .used-smack-git-repo) != $SMACK_REPO ] ; then
	    echo "Used smack repository has changed!"
	    echo "Old: $(cat .used-smack-git-repo) New: ${SMACK_REPO}."
	    echo "Deleting old local copy"
	    rm -rf smack
    fi
    echo "${SMACK_REPO}" > .used-smack-git-repo
}

fetchall() {
    if $SMACK_LOCAL ; then
	# always clean the local copy first
	rm -rf ${SRC_DIR}/smack
	mkdir ${SRC_DIR}/smack
	cd $SMACK_REPO
	git archive $SMACK_BRANCH | tar -x -C ${SRC_DIR}/smack
	if [ $? -ne 0 ]; then
	    exit
	fi
    else
	gitfetch "$SMACK_REPO" "$SMACK_BRANCH" "smack"
    fi

    if ! $UPDATE_REMOTE ; then
	echo "Won't update or fetch third party resources"
	return
    fi

    fetch "http://svn.apache.org/repos/asf/qpid/trunk/qpid/java/management/common/src/main/" "qpid"
    fetch "http://svn.apache.org/repos/asf/harmony/enhanced/java/trunk/classlib/modules/auth/src/main/java/common/" "harmony"
    fetch "https://dnsjava.svn.sourceforge.net/svnroot/dnsjava/trunk" "dnsjava"
    gitfetch "git://kenai.com/jbosh~origin" "master" "jbosh"
    # jldap doesn't compile with the latest version (missing deps?), therefore it's a fixed version for now
    #  gitfetch "git://git.openldap.org/openldap-jldap.git" "master" "novell-openldap-jldap"
}

copyfolder() {
(
  cd ${WD}
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
  cd "${WD}"
  rm -rf build/src
  mkdir -p build/src/trunk

  copyfolder "src/smack/source/" "build/src/trunk" "."
  copyfolder "src/qpid/java" "build/src/trunk" "org/apache/qpid/management/common/sasl"
  copyfolder "src/novell-openldap-jldap" "build/src/trunk" "."
  copyfolder "src/dnsjava"  "build/src/trunk" "org"
  copyfolder "src/harmony" "build/src/trunk" "."
  copyfolder "src/custom" "build/src/trunk" "."
  copyfolder "src/jbosh/src/main/java" "build/src/trunk" "."
  if $BUILD_JINGLE ; then
    copyfolder "src/smack/jingle/extension/source/" "build/src/trunk" "."
  fi
}

patchsrc() {
  echo "## Step 21: patch build/src"
  cd "${WD}"
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
  ant -Dbuild.all=true $JINGLE_ARGS
  if [ $? -ne 0 ]; then
      exit
  fi
}

buildcustom() {
  for dir in `find patch -maxdepth 1 -mindepth 1 -type d`; do
    buildsrc
    patchsrc "patch"
    if $BUILD_JINGLE ; then
      patchsrc "jingle"
      JINGLE_ARGS="-Djingle=lib/jstun.jar"
    fi
    patchsrc "${dir}"
    ant -Djar.suffix=`echo ${dir}|sed 's:patch/:-:'` $JINGLE_ARGS
  done
}

parseopts() {
    while getopts b:r:cdhjpu OPTION "$@"; do
	case $OPTION in
	    r)
		SMACK_REPO="${OPTARG}"
		;;
	    b)
		SMACK_BRANCH="${OPTARG}"
		;;
	    d)
		set -x
		;;
	    j)
		BUILD_JINGLE=true
		;;
	    u)
		UPDATE_REMOTE=false
		;;
	    c)
		BUILD_CUSTOM=true
		;;
	    p)
		XARGS_ARGS="-P4"
		;;
	    h)
		echo "$0 -d -c -u -j -r <repo> -b <branch>"
		echo "-d: Enable debug"
		echo "-j: Build jingle code"
		echo "-c: Apply custom patchs from patch directory"
		echo "-u: DON'T update remote third party resources"
		echo "-r <repo>: Git repository (can be local or remote) for underlying smack repository"
		echo "-b <branch>: Git branch used to build aSmack from underlying smack repository"
		echo "-p use parallel build"
		exit
		;;
	esac
    done

    if islocalrepo $SMACK_REPO ; then
	SMACK_LOCAL=true
	SMACK_REPO=`readlink -f $SMACK_REPO`
    fi
}

islocalrepo() {
    local R="^(git|ssh)"
    if [[ $1 =~ $R ]]; then
	return 1
    else
	return 0
    fi
}

initialize() {
    echo "## Step 00: initialize"
    if ! [ -d build/src/trunk ]; then
	mkdir -p build/src/trunk
    fi
    if [ ! -d src/ ]; then
	mkdir src
    fi
    rm build/*.jar
    rm build/*.zip
}

copystaticsrc() {
    cp -ur static-src/* src/
}

# Default configuration
SMACK_REPO=git://github.com/Flowdalic/smack.git
SMACK_BRANCH=master
SMACK_LOCAL=false
UPDATE_REMOTE=true
BUILD_CUSTOM=false
BUILD_JINGLE=false
JINGLE_ARGS=""
XARGS_ARGS=""
SRC_DIR=$(pwd)/src
WD=$(pwd)

parseopts $@
echo "Using Smack git repository $SMACK_REPO with branch $SMACK_BRANCH"
echo "SMACK_LOCAL: $SMACK_LOCAL UPDATE_REMOTE: $UPDATE_REMOTE BUILD_CUSTOM: $BUILD_CUSTOM BUILD_JINGLE: $BUILD_JINGLE"
initialize
copystaticsrc
testsmackgit
fetchall
buildsrc
patchsrc "patch"
if $BUILD_JINGLE ; then
  patchsrc "jingle"
  JINGLE_ARGS="-Djingle=lib/jstun.jar"
fi
build

if $BUILD_CUSTOM ; then
    buildcustom
fi

if which advzip; then
  find build \( -name '*.jar' -or -name '*.zip' \) -print0 | xargs -n 1 -0 $XARGS_ARGS advzip -z4 
fi
