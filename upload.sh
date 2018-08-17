#!/bin/sh
gradle clean spider-share-domain:upload  spider-share-api:upload spider-share-common:upload  -x test
gradle spider-operator-domain:upload  spider-operator-api:upload -x test
gradle spider-bank-domain:upload  spider-bank-api:upload -x test
gradle spider-ecommerce-domain:upload  spider-ecommerce-api:upload -x test
gradle spider-extra-domain:upload  spider-extra-api:upload -x test


