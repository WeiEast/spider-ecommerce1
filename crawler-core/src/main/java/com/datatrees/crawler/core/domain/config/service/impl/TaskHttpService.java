/**
 *
 */

package com.datatrees.crawler.core.domain.config.service.impl;

import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 * TaskHttpClient代替DefautService
 */
@Path(".[@type='task_http']")
@Tag("service")
public class TaskHttpService extends AbstractService {}
