#!/bin/bash

svnfetch() {
    REV="${3:-HEAD}"
    echo "Fetching from ${1} to ${2} at revision ${REV}"
    cd $SRC_DIR
    if ! [ -f "${2}/.svn/entries" ]; then
	mkdir "${2}"
	cd "${2}"
	svn co --non-interactive --trust-server-cert "${1}" -r "${REV}" "."
    else
	cd "${2}"
	svn cleanup
	svn up -r "${REV}"
    fi
}

gitfetch() {
    echo "Fetching ${2} branch from ${1} to ${3} via git"
    cd $SRC_DIR
    if ! [ -f "${3}/.git/config" ]; then
	git clone "${1}" "${3}"
	cd "${3}"
	git checkout "${2}"
    else
	cd "${3}"
	git fetch
	git checkout "${2}"
    fi

    if [ $? -ne 0 ]; then
	exit
    fi
}

hgfetch() {
(
  echo "Fetching ${2} branch from ${1} to ${3} via mercurial"
  cd src
  if [ -e "${2}/.hg" ] ; then
      cd ${2}
      hg pull
  else
      hg clone "${1}" "${2}"
  fi
  hg up -r ${3}
)
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
    echo "## Step 15: fetching sources"
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
	execute gitfetch "$SMACK_REPO" "$SMACK_BRANCH" "smack"
    fi

    if ! $UPDATE_REMOTE ; then
	echo "Won't update or fetch third party resources"
	wait
	return
    fi

    execute svnfetch "http://svn.apache.org/repos/asf/qpid/trunk/qpid/java/management/common/src/main/" "qpid" 
    execute svnfetch "http://svn.apache.org/repos/asf/harmony/enhanced/java/trunk/classlib/modules/auth/src/main/java/common/" "harmony" 
    execute svnfetch "https://dnsjava.svn.sourceforge.net/svnroot/dnsjava/trunk" "dnsjava" 
    execute gitfetch "git://kenai.com/jbosh~origin" "master" "jbosh" 
    # jldap doesn't compile with the latest version (missing deps?), therefore it's a fixed version for now
    #  execute gitfetch "git://git.openldap.org/openldap-jldap.git" "master" "novell-openldap-jldap"
    wait
}

copyfolder() {
  cd ${ASMACK_BASE}
  (
    cd "${1}"
    tar -cSsp --exclude-vcs "${3}"
  ) | (
    cd "${2}"
    tar -xSsp
  )
  wait
}

createbuildsrc() {
  echo "## Step 20: creating build/src"
  cd "${ASMACK_BASE}"
  rm -rf build/src
  mkdir -p build/src/trunk

  execute copyfolder "src/smack/source/" "build/src/trunk" "." 
  execute copyfolder "src/qpid/java" "build/src/trunk" "org/apache/qpid/management/common/sasl" 
  execute copyfolder "src/novell-openldap-jldap" "build/src/trunk" "." 
  execute copyfolder "src/dnsjava"  "build/src/trunk" "org" 
  execute copyfolder "src/harmony" "build/src/trunk" "." 
  execute copyfolder "src/jbosh/src/main/java" "build/src/trunk" "." 
  if $BUILD_JINGLE ; then
    execute copyfolder "src/smack/jingle/extension/source/" "build/src/trunk" "." 
  fi
  wait
  # custom overwrites some files from smack, so this has to be done as last
  copyfolder "src/custom" "build/src/trunk" "." 
}

patchsrc() {
  echo "## Step 25: patch build/src"
  cd "${ASMACK_BASE}"
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
  buildandroid 
  if [ $? -ne 0 ]; then
      exit
  fi
}

buildandroid() {
    local sdklocation
    local version
    local sdks
    sdklocation=$(grep sdk-location local.properties| cut -d= -f2)
    if [ -z "$sdklocation" ] ; then
	echo "Android SDK not found. Don't build android version"
	return
    fi
    for f in ${sdklocation/\$\{user.home\}/$HOME}/platforms/* ; do
	version=`basename $f`
	if [ ${version#android-} -gt 5 ] ; then
	    echo "Building for ${version}"
	    sdks="${sdks} ${version}\n"
	fi

    done
    if [ -z "${sdks}" ] ; then
	echo "No SDKs found"
	exit 1
    fi
    if echo -e ${sdks} | \
	xargs -I{} -n 1 $XARGS_ARGS ant -Dandroid.version={} -Djar.suffix="${1}" compile-android ; then
	exit 1
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
    local custom
    custom=$(echo ${dir} | sed 's:patch/:-:')
    ant -Djar.suffix="${custom}" $JINGLE_ARGS
    buildandroid "${custom}"
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
		XARGS_ARGS="-t"
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
		PARALLEL_BUILD=true
		;;
	    h)
		echo "$0 -d -c -u -j -r <repo> -b <branch>"
		echo "-d: Enable debug"
		echo "-j: Build jingle code"
		echo "-c: Apply custom patchs from patch directory"
		echo "-u: DON'T update remote third party resources"
		echo "-r <repo>: Git repository (can be local or remote) for underlying smack repository"
		echo "-b <branch>: Git branch used to build aSmack from underlying smack repository"
		echo "-p use parallel build where possible"
		exit
		;;
	esac
    done
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
    find build \( -name '*.jar' -or -name '*.zip' \) -print0 | xargs -0 rm -f
}

cleanup() {
    echo "## Deleting all temporary files"
    rm -rf build
    rm -rf src
}

copystaticsrc() {
    cp -ur static-src/* src/
}

cmdExists() {
    command -v $1 &> /dev/null
    return $?
}

prettyPrintSeconds() {
    local ttime
    if (( $1 > 59 )); then
	ttime=$(printf "%dm %ds\n" $(($1/60%60)) $(($1%60)) )
    else
	ttime=$(printf "%ds\n" $(($1)) )
    fi
    echo "Execution took $ttime"
}

execute() { 
    if [ -n "$BACKGROUND" ]; then
	"$@" &
    else
	"$@"
    fi
}

setdefaults() {
# Default configuration
    SMACK_REPO=git://github.com/Flowdalic/smack.git
    SMACK_BRANCH=origin/master
    SMACK_LOCAL=false
    UPDATE_REMOTE=true
    BUILD_CUSTOM=false
    BUILD_JINGLE=false
    JINGLE_ARGS=""
    PARALLEL_BUILD=false
    ASMACK_BASE=$(pwd)
    SRC_DIR=$ASMACK_BASE/src
    STARTTIME=$(date -u "+%s")
}

parseconfig() {
    if [ -f ${ASMACK_BASE}/config ]; then
	source ${ASMACK_BASE}/config
    fi
}

setconfig() {
    if [ ${PARALLEL_BUILD} == "true" ]; then
	XARGS_ARGS="${XARGS_ARGS} -P4"
	BACKGROUND="true"
    else
	XARGS_ARGS=""
	BACKGROUND=""
    fi

    if islocalrepo $SMACK_REPO ; then
	SMACK_LOCAL=true
	SMACK_REPO=`readlink -f $SMACK_REPO`
    fi
}

printconfig() {
    echo "Smack git repository $SMACK_REPO with branch $SMACK_BRANCH"
    echo -e "SMACK_LOCAL:$SMACK_LOCAL\tUPDATE_REMOTE:$UPDATE_REMOTE\tBUILD_CUSTOM:$BUILD_CUSTOM\tBUILD_JINGLE:$BUILD_JINGLE"
    echo -e "PARALLEL_BUILD:$PARALLEL_BUILD\tBASE:$ASMACK_BASE"
}

setdefaults
parseopts $@
parseconfig
setconfig
printconfig

initialize
copystaticsrc
testsmackgit
fetchall
createbuildsrc
patchsrc "patch"
if $BUILD_JINGLE ; then
  patchsrc "jingle"
  JINGLE_ARGS="-Djingle=lib/jstun.jar"
fi
build

if $BUILD_CUSTOM; then
    buildcustom
fi

if cmdExists advzip ; then
  echo "advzip found, compressing files"
  find build \( -name '*.jar' -or -name '*.zip' \) -print0 | xargs -n 1 -0 $XARGS_ARGS advzip -z4 
else
  echo "Could not find the advzip command."
  echo "advzip will further reduce the size of the generated jar and zip files,"
  echo "consider installing advzip"
fi

STOPTIME=$(date -u "+%s")
RUNTIME=$(( $STOPTIME - $STARTTIME ))
prettyPrintSeconds $RUNTIME
printconfig
