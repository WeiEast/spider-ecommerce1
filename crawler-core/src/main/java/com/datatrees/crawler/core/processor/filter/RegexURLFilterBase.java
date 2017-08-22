/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.filter;

import java.util.LinkedList;
import java.util.List;

import com.datatrees.crawler.core.domain.config.filter.UrlFilter;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 7:25:43 PMO
 */
public abstract class RegexURLFilterBase implements Filter {

	/** An array of applicable rules */
	private LinkedList<RegexRule> rules = new LinkedList<RegexRule>();

	public RegexURLFilterBase(List<UrlFilter> filters) {
		for (UrlFilter urlFilter : filters) {
			RegexRule rule = createRule(urlFilter);
			if (rule != null) {
			    rules.add(rule);
//				if (rule.accept()) {
//					rules.addLast(rule);
//				} else {
//					rules.addFirst(rule);
//				}
			}
		}
	}

	/**
	 * 
	 * @param urlFilter
	 * @return
	 */
	public abstract RegexRule createRule(UrlFilter urlFilter);

	public String filter(String url) {
		for (RegexRule rule : rules) {
			if (rule.match(url)) {
				return rule.accept() ? url : null;
			}
		}
		;
		return null;
	}

}
