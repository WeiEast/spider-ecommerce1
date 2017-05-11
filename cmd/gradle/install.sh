#!/bin/sh
gradle clean build installApp -x test --refresh-dependencies -Denv=daily
