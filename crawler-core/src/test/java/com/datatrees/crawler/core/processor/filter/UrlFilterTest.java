/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.filter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import com.datatrees.crawler.core.domain.config.filter.FilterType;
import com.datatrees.crawler.core.domain.config.filter.UrlFilter;
import com.datatrees.crawler.core.processor.filter.URLRegexFilter;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 7:39:02 PM
 */
public class UrlFilterTest {

	@Test
	public void testContained() {

		UrlFilter blackfilter = new UrlFilter();
		blackfilter.setFilter(".*kenitra\\.biz/category.*");
		blackfilter.setType(FilterType.BLACK_LIST.getValue());

		UrlFilter whiteFilter = new UrlFilter();
		whiteFilter.setFilter(".*kenitra\\.biz.*");
		whiteFilter.setType(FilterType.WHITE_LIST.getValue());

		URLRegexFilter fl = new URLRegexFilter(Arrays.asList(whiteFilter,
				blackfilter));

		String result = fl.filter("http://kenitra.biz/category/animes/");
		System.out.println(result);
	}

	@Test
	public void testMix() {

		UrlFilter blackfilter = new UrlFilter();
		blackfilter.setFilter("link/show");
		blackfilter.setType(FilterType.BLACK_LIST.getValue());

		UrlFilter whiteFilter = new UrlFilter();
		whiteFilter.setFilter("link/play");
		whiteFilter.setType(FilterType.WHITE_LIST.getValue());

		URLRegexFilter fl = new URLRegexFilter(Arrays.asList(blackfilter,
				whiteFilter));

		String result = fl.filter("solarmovie/link/xxx");
		assertNull(result);
		result = fl.filter("solarmovie/link/show/aa");
		assertNull(result);
		result = fl.filter("solarmovie/link/play/aa");
		assertNotNull(result);
	}

	@Test
	public void testBlackistRegexFilter() {
		UrlFilter filter = new UrlFilter();
		filter.setFilter(".*kenitra\\.biz/category.*");
		filter.setType(FilterType.BLACK_LIST.getValue());

		URLRegexFilter fl = new URLRegexFilter(Arrays.asList(filter));
		String result = fl.filter("http://kenitra.biz/category/animes/");
		System.out.println(result);
		result = fl.filter("solarmovie/link/show/aa");
		assertNull(result);
	}

	@Ignore
	@Test
	public void testWhiteListRegexFilter() {
		UrlFilter filter = new UrlFilter();
		filter.setFilter("link/show");
		filter.setType(FilterType.WHITE_LIST.getValue());

		URLRegexFilter fl = new URLRegexFilter(Arrays.asList(filter));
		String result = fl.filter("solarmovie/link/xxx");
		assertNull(result);

		result = fl.filter("solarmovie/link/show/aa");
		assertNotNull(result);
	}

}
