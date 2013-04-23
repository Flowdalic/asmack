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
    echo "Fetching ${2} branch/commit from ${1} to ${3} via git"
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

createVersionTag() {
    # Skip this step is no version tag is given
    [[ -z $VERSION_TAG ]] && return

    local v
    cat <<EOF  > $TAG_FILE
#!/bin/bash

# This file contains the version information of the components that
# were used to build this aSmack version

declare -g -A COMPONENT_VERSIONS
EOF

    for d in $(ls $SRC_DIR) ; do
	cd $SRC_DIR

	# Don't record the components version for static-src
	for static in $(ls ${ASMACK_BASE}/static-src) ; do
	    # Don't record the version if it's from the static sources
	    [ $d == $static ] && continue
	done

	if [[ -d $d/.git ]] ; then
	    v=$(cd $d && git rev-parse HEAD)
	    key=$d
	    COMPONENT_VERSIONS["$d"]=$v
	elif [[ -d $d/.svn ]] ; then
	    v=$(cd $d && svn info |grep Revision |cut -f 2 -d ' ')
	    key=$d
	    COMPONENT_VERSIONS["$d"]=$v
	fi
    done

    if $SMACK_LOCAL ; then
	cd $SMACK_REPO
	v=$(git rev-parse HEAD)
	COMPONENT_VERSIONS[smack]=$v
    fi

    cd ${ASMACK_BASE}
    v=$(git rev-parse HEAD)
    COMPONENT_VERSIONS[asmack]=$v

    for i in "${!COMPONENT_VERSIONS[@]}" ; do
	echo "COMPONENT_VERSIONS[$i]=${COMPONENT_VERSIONS[$i]}" >> $TAG_FILE
    done
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
    cd ${ASMACK_BASE}/build/src/trunk/
    for PATCH in `(cd "../../../${1}" ; find -maxdepth 1 -type f)|sort` ; do
	echo $PATCH
	if [[ $PATCH == *.sh ]]; then
	    "../../../${1}/$PATCH" || exit 1
	elif [[ $PATCH == *.patch ]]; then
	    patch -p0 < "../../../${1}/$PATCH" || exit 1
	fi
    done
}

build() {
  echo "## Step 30: compile"
  buildandroid
  if [ $? -ne 0 ]; then
      exit 1
  fi
}

buildandroid() {
    local sdklocation
    local version
    local sdks
    local minSdkVer=8

    cd $ASMACK_BASE

    if [ ! -f local.properties ] ; then
	echo "Could not find local.properties file"
	echo "See local.properties.example"
	exit 1
    fi

    sdklocation=$(grep sdk-location local.properties| cut -d= -f2)
    if [ -z "$sdklocation" ] ; then
	echo "Android SDK not found. Don't build android version"
	exit 1
    fi
    for f in ${sdklocation/\$\{user.home\}/$HOME}/platforms/* ; do
	version=`basename $f`
	if [[ "$version" != android-* ]] ; then
	    echo "$sdklocation contains no Android SDKs"
	    exit 1
	fi
	if [[ ${version#android-} -ge $minSdkVer ]] ; then
	    if [ -n $BUILD_ANDROID_VERSIONS ] ; then
		for build_version in $BUILD_ANDROID_VERSIONS ; do
		    [ ${version#android-} != $build_version ] && continue 2
		done
	    fi
	    echo "Building for ${version}"
	    sdks="${sdks} ${version}\n"
	fi

    done

    if [ -z "${sdks}" ] ; then
	echo "No SDKs of a suitable minimal API (${minSdkVer}) version found"
	exit 1
    fi

    local asmack_suffix
    if [[ -n ${VERSION_TAG} ]] && [[ -n ${1} ]] ; then
	asmack_suffix="${1}-${VERSION_TAG}"
    elif [[ -n ${VERSION_TAG} ]] ; then
	asmack_suffix="-${VERSION_TAG}"
    else
	asmack_suffix="${1}"
    fi
    if ! echo -e ${sdks} \
	| xargs -I{} -n 1 $XARGS_ARGS ant \
		-Dandroid.version={} \
		-Djar.suffix="${asmack_suffix}" \
		compile-android ; then
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
    while getopts a:b:r:t:cdhjpux OPTION "$@"; do
	case $OPTION in
	    a)
		BUILD_ANDROID_VERSIONS="${OPTARG}"
		;;
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
	    t)
		VERSION_TAG="${OPTARG}"
		;;
	    x)
		PUBLISH_RELEASE=true
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
		echo "-t <version>: Create a new version tag. You should build aSmack before calling this"
		echo "-x: Publish the release"
		echo "-a <SDK Version(s)>: Build only for the given Android SDK versions"
		exit
		;;
	esac
    done
}

prepareRelease() {
    if [[ -z ${VERSION_TAG} ]]; then
	echo "Version tag is not set. Not going to prepare a release"
	return
    fi

    if [ -d $RELEASE_DIR ] ; then
	rm -rf $RELEASE_DIR
    fi
    mkdir -p $RELEASE_DIR

    mv ${ASMACK_BASE}/build/*.{jar,zip} ${RELEASE_DIR}/
    cp $TAG_FILE ${RELEASE_DIR}/
    cp ${ASMACK_BASE}/CHANGELOG ${RELEASE_DIR}

    if [ -n $GPG_KEY ] ; then
	find $RELEASE_DIR -maxdepth 1 -and \( -name '*.jar' -or -name '*.zip' \) -print0 \
	    | xargs -n 1 -0 $XARGS_ARGS gpg --local-user $GPG_KEY --detach-sign
    fi

    find $RELEASE_DIR -maxdepth 1 -and \( -name '*.jar' -or -name '*.zip' \) -print0 \
	| xargs -I{} -n 1 -0 $XARGS_ARGS sh -c 'md5sum {} > {}.md5'

    local release_readme
    release_readme=${RELEASE_DIR}/README

    sed \
	-e "s/\$VERSION_TAG/${VERSION_TAG}/" \
	-e "s/\$BUILD_DATE/${BUILD_DATE}/" \
	README.asmack > $release_readme

    # Pretty print the component versions at the end of README
    # Note that there is an exclamation mark at the beginning of the
    # associative array to access the keys
    for i in "${!COMPONENT_VERSIONS[@]}" ; do
	local tabs
	if [[ ${#i} -le 6 ]] ; then
	    tabs="\t\t"
	else
	    tabs="\t"
	fi
	echo -e "${i}:${tabs}${COMPONENT_VERSIONS[$i]}" >> $release_readme
    done
}

publishRelease() {
    if [[ -z ${VERSION_TAG} ]]; then
	echo "Version tag is not set. Not going to prepare a release"
	return
    fi

    if [[ -z ${PUBLISH_RELEASE} ]]; then
	echo "User doesn't want to publish this release"
	return
    fi

    if [[ -z $PUBLISH_HOST || -z $PUBLISH_DIR ]]; then
	echo "WARNING: Not going to publish this release as either $PUBLISH_HOST or $PUBLISH_DIR is not set"
	return
    fi

    cd ${ASMACK_RELEASES}
    cat <<EOF | sftp $PUBLISH_HOST
rm ${PUBLISH_DIR}/${VERSION_TAG}/*
mkdir ${PUBLISH_DIR}/${VERSION_TAG}
put -r $VERSION_TAG $PUBLISH_DIR
EOF
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
    rm -rf src/custom
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
    # Default configuration, can be changed with script arguments
    SMACK_REPO=git://github.com/Flowdalic/smack.git
    SMACK_BRANCH=master
    SMACK_LOCAL=false
    UPDATE_REMOTE=true
    BUILD_CUSTOM=false
    BUILD_JINGLE=false
    JINGLE_ARGS=""
    PARALLEL_BUILD=false
    VERSION_TAG=""
    PUBLISH_RELEASE=""
    PUBLISH_HOST=""
    PUBLISH_DIR=""
    BUILD_ANDROID_VERSIONS=""

    # Often used variables
    ASMACK_BASE=$(pwd)
    ASMACK_RELEASES=${ASMACK_BASE}/releases
    SRC_DIR=${ASMACK_BASE}/src
    VERSION_TAG_DIR=${ASMACK_BASE}/version-tags
    STARTTIME=$(date -u "+%s")
    BUILD_DATE=$(date)
    # Declare an associative array that is in global scope ('-g')
    declare -g -A COMPONENT_VERSIONS
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

    if islocalrepo $SMACK_REPO; then
	SMACK_LOCAL=true
	SMACK_REPO=`readlink -f $SMACK_REPO`
    fi

    if [[ -n ${VERSION_TAG} ]]; then
	if ! grep ${VERSION_TAG} CHANGELOG; then
	    echo "Could not find the tag in the CHANGELOG file. Please write a short summary of changes"
	    exit 1
	fi
	if ! git diff --exit-code; then
	    echo "Unstaged changes found, please stages your changes"
	    exit 1
	fi
	if ! git diff --cached --exit-code; then
	    echo "Staged, but uncommited changes found, please commit"
	    exit 1
	fi
	RELEASE_DIR=${ASMACK_RELEASES}/${VERSION_TAG}
	TAG_FILE=${VERSION_TAG_DIR}/${VERSION_TAG}.tag
    fi
}

printconfig() {
    echo "Smack git repository $SMACK_REPO with branch $SMACK_BRANCH"
    echo -e "SMACK_LOCAL:$SMACK_LOCAL\tUPDATE_REMOTE:$UPDATE_REMOTE\tBUILD_CUSTOM:$BUILD_CUSTOM\tBUILD_JINGLE:$BUILD_JINGLE"
    echo -e "PARALLEL_BUILD:$PARALLEL_BUILD\tBASE:$ASMACK_BASE"
}

checkPrerequisites() {
    if [[ $BASH_VERSION < 4 ]] ; then
	echo "aSmack's build.bash needs at least bash version 4"
	exit 1
    fi

    if ! tar --version |grep GNU &> /dev/null ; then
	echo "aSmack's build.bash needs GNU tar"
	exit 1
    fi
}

# Main

setdefaults
parseopts $@
checkPrerequisites
parseconfig
setconfig
printconfig
initialize
copystaticsrc
testsmackgit
fetchall
createVersionTag
createbuildsrc
patchsrc "patch"
if $BUILD_JINGLE ; then
  patchsrc "jingle"
  JINGLE_ARGS="-Djingle=lib/jstun.jar"
fi
build

if $BUILD_CUSTOM ; then
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

prepareRelease
publishRelease

STOPTIME=$(date -u "+%s")
RUNTIME=$(( $STOPTIME - $STARTTIME ))
prettyPrintSeconds $RUNTIME
printconfig
