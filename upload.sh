#!/bin/sh
gradle clean spider-share-domain:upload  spider-operator-domain:upload rawdatacentral-domain:upload  -x test
gradle spider-share-api:upload rawdatacentral-api:upload  clean spider-operator-api:upload -x test


