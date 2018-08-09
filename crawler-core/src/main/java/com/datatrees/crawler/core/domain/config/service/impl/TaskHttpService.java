/**
 *
 */

package com.datatrees.crawler.core.domain.config.service.impl;

import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.treefinance.crawler.framework.config.annotation.Path;
import com.treefinance.crawler.framework.config.annotation.Tag;

/**
 * TaskHttpClient代替DefautService
 */
@Path(".[@type='task_http']")
@Tag("service")
public class TaskHttpService extends AbstractService {}
