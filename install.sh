#!/bin/sh
sh clean.sh
gradle  spider-ecommerce-web:bootRepackage -x test --refresh-dependencies -Denv=dev




