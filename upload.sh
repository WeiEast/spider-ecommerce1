#!/bin/sh
gradle clean rawdatacentral-api:upload rawdatacentral-domain:upload  rawdatacentral-common:upload rawdatacentral-plugin:upload -x test


