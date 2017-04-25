#!/usr/bin/python
#coding=utf-8

import sys
import os
import re

invalid_pattern = re.compile('^#.*')
def loadProperties(config) :
	map = {}
	config_file = open(config)
	configure = [ line.strip() for line in config_file if line.strip() and not invalid_pattern.match(line)]
	config_file.close()
	for line in  configure :
		values = [item.strip() for item in line.split('=')]
		map[values[0]] = values[0]+'='+values[1]
	return map

def changeConfigure(configFile, configure) :
	print '-----starting change configure of : %s-----' % configFile
	items = loadProperties(configFile)
	input = open(configFile)
	content = input.read()
	input.close();
	for k,v in items.items() :
		if k in configure :
			content = content.replace(v,configure[k])
			print 'change %s to %s in %s ' % (v, configure[k], configFile)
	writeConfigure(configFile, content)
	print '-----change configure completed of : %s-----' % configFile

def writeConfigure(configFile, configure) :
	output = open(configFile,'w')
	output.write(configure)
	output.close()

def loadConfigFiles(dir) :
	name_pattern = re.compile(r'.*\.properties')
	files = [ os.path.join(dir,file) for file in os.listdir(dir) if name_pattern.match(file)]
	return files

if  __name__ == '__main__' :
	args = sys.argv[1:]
	if len(args) != 2 :
		exit(1)
	configure = loadProperties(args[0])
	files = loadConfigFiles(args[1])
	for file in files :
		changeConfigure(file,configure)

	print 'change %s to directory %s completed' % (args[0], args[1])
	exit(0)

