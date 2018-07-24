#!/bin/sh
sh clean.sh
gradle  spider-share-main:bootRepackage -x test --refresh-dependencies -Denv=dev




