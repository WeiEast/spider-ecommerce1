#!/bin/sh
sh clean.sh
gradle  rawdatacentral-main:bootRepackage -x test --refresh-dependencies -Denv=dev




