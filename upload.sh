#!/bin/sh
gradle clean spider-share-domain:upload  spider-share-api:upload spider-share-common:upload  -x test
gradle spider-operator-domain:upload  spider-operator-api:upload -x test


