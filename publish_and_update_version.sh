#!/bin/bash
set -e

function get_property
{
    grep "^$2=" "$1" | cut -d'=' -f2
}

function print_usage
{
	echo "Usage: sh $0 <next_library_version>

This script creates a git tag for the current version, \
uploads artifacts to the maven repo and \
updates the library version, with the given param.
"
}


#########################################################################


# VALIDATE USAGE
if [ "$1" = "" ]
then
  print_usage
  exit
fi

# VALIDATE VERSION PARAM
current_version=$(get_property gradle.properties VERSION)
if [ $current_version = "$1" ]
then
	echo "The given new version is the same as current version!"
	echo "You should give me the next version of the library"
	exit
fi

# VALIDATE BRANCH
current_branch=$(git rev-parse --symbolic-full-name --abbrev-ref HEAD)
if [ $current_branch != "master" ]
then
	echo "You gotta be on master branch!"
	echo "Currently in $current_branch"
	exit
fi

# VALIDATE LOCAL CHANGES
if git status --porcelain | grep .; then
    echo "There are local changes not committed."
    echo "It's advised to commit them before publishing new version..."
    exit
fi


#########################################################################

echo "Uploading archives..."
./gradlew network-monitor:clean network-monitor:build network-monitor:uploadArchives

echo "Creating tag $current_version and pushing to origin..."
git tag $current_version
git push origin $current_version

echo "Bumping library version and pushing to master"
CONFIG_FILE=gradle.properties
TARGET_KEY=VERSION
REPLACEMENT_VALUE=$1
sed -i '' "s/\($TARGET_KEY *= *\).*/\1$REPLACEMENT_VALUE/" $CONFIG_FILE
git add gradle.properties
git commit -m "Bump version to $1" gradle.properties
git push origin master

echo "Done"
