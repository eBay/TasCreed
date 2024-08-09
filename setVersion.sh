#!/bin/bash

set_version()
{
  ov=`mvn help:evaluate -Dexpression=project.version -f $1 -q -DforceStdout`
  replace_version $1 $2 $ov
}

replace_version()
{
  backup="$1.versionsBackup"
  cp $1 $backup
  sed "s/$3/$2/g" $backup > $1
}

commit_verion()
{
  backupPom="$1.versionsBackup"
  rm $backupPom
}

revert_verion()
{
  backupPom="$1.versionsBackup"
  mv $backupPom $1
}

update_child_app()
{
  cd $1
  ./setVersion.sh $2
  cd ..
}

VALIDATED=0

if [ $# -lt 1 ] ;then
    echo "you need input a parameter as the new version you want to set"
else
    NEW_VERSION=${1}
    echo "Please confirm to update version to $NEW_VERSION"
    read -r -p "Are You Sure? [Y/n] " input
    case $input in
        [yY][eE][sS]|[yY])
            echo "Yes"
            VALIDATED=1
            ;;
        [nN][oO]|[nN])
            echo "No"
            ;;
        *)
        echo "Invalid input..."
        ;;
    esac
fi

if [ $VALIDATED -eq 0 ] ;then
    echo "version not updated"
    exit 1;
fi

BACKUP=true

OLD_VERSION=`mvn help:evaluate -Dexpression=project.version -q -DforceStdout`

VERSION_FILE=src/main/resources/tumbler.yaml

echo "start to update version from $OLD_VERSION to $NEW_VERSION"

mvn versions:set -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=$BACKUP

replace_version tumbler-core/tumbler-infra/$VERSION_FILE $NEW_VERSION $OLD_VERSION

CONFIRMED=0

echo "Version updated to $NEW_VERSION, please double check and confirm with it."
read -r -p "Are you sure? [Y/n] " input
case $input in
    [yY][eE][sS]|[yY])
        echo "Yes"
        CONFIRMED=1
        ;;
    [nN][oO]|[nN])
        echo "No"
        ;;
    *)
    echo "Invalid input..."
    ;;
esac

if [ $CONFIRMED -eq 1 ] ;then
    echo "Confirmed to update to $NEW_VERSION, will remove all the backup files ..."

    mvn versions:commit

    commit_verion tumbler-core/tumbler-infra/$VERSION_FILE

    echo "version update finished."
else
    echo "Will revert back to $OLD_VERSION ..."

    mvn versions:revert

    revert_verion tumbler-core/tumbler-infra/$VERSION_FILE

    echo "version update reverted."
fi