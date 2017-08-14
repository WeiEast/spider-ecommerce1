#!/bin/sh
gradle clean rawdatacentral-api:upload rawdatacentral-domain:upload  rawdatacentral-plugin:upload -x test


