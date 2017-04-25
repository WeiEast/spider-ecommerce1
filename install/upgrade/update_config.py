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
	file_configure = items.items()
	for k,v in configure :
		if k in file_configure :
			content = content.replace(file_configure[k],v)
			print 'change %s to %s in %s ' % (file_configure[k],v, configFile)
		else :
			content = '\n'.join((content, v))
			print 'add %s in %s' % (v, configFile)
			
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

def getConfigure(file, configure) :
	file_configure = {}
	items = loadProperties(file)
	for k,v in items.items() :
		for k in configure :
			if k in configure :
				file_configure[k] = configure[k]
	return os.path.basename(file),file_configure

if  __name__ == '__main__' :
	args = sys.argv[1:]
	if len(args) != 3 :
		exit(1)
	configure = loadProperties(args[0])
	original_files = loadConfigFiles(args[2])
	for file in original_files :
		file, file_configure = getConfigure(file, configure)
		if file_configure :
			file = os.path.join(args[1], file)
			changeConfigure(file,configure)

	print 'change %s to directory %s completed' % (args[0], args[1])
	exit(0)
 

