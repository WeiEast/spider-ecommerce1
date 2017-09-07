#! /usr/bin/python

import json
import sys
import re
import urllib
import urllib2

class RedirectHandler(urllib2.HTTPRedirectHandler):
    def http_error_301(self, req, fp, code, msg, headers):
        result = urllib2.HTTPRedirectHandler.http_error_301(
	    self, req, fp, code, msg, headers)
        result.location = headers['Location']
	return result

    def http_error_302(self, req, fp, code, msg, headers):
        result = urllib2.HTTPRedirectHandler.http_error_302(self, req, fp, code, msg, headers)
	result.location = headers['Location'] 
	return result

def extract_url(pagecontent):
    m = re.search('<iframe.*src="?([^\s"]*)', pagecontent)
    playbox_url = 'http://zhibo.bz' + m.group(1)
    if playbox_url.find('letv.php') != -1:
        id = re.search('id=(.*)', playbox_url);
	return 'http://live.gslb.letv.com/gslb?stream_id=%s&tag=live&ext=m3u8&sign=live_web&format=2' % id.group(1);
    elif playbox_url.find('qq.php') != -1:
	request = urllib2.Request(playbox_url)
	opener = urllib2.build_opener(RedirectHandler())
	f = opener.open(request)
	location = f.location
	start = location.find('id=')
	end = location.find('&', start)
	id = location[start+len('id='):end]

	return 'http://v.qq.com/iframe/live_player.html?cnlid=' + id + '&width=100%&height=520' 
    elif playbox_url.find('pptv.php') != -1:
	id = re.search('id=(.*)', playbox_url);
	return 'http://pub.pptv.com/player/iframe/index.html#w=750&h=520&id=%s' % id.group(1)
    else:
        return playbox_url
    

if __name__ == "__main__":
    json_data = json.loads(sys.stdin.readline())
    url = extract_url(json_data["pagecontent"])
    print '{"field": "%s"}' % url