#!/bin/sh
rootPath=`pwd`

echo "$rootPath/rawdatacentral-api"
cd $rootPath/rawdatacentral-api
gradle upload

echo "$rootPath/rawdatacentral-domain"
cd $rootPath/rawdatacentral-domain
gradle upload

cd $rootPath



