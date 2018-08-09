package com.datatrees.crawler.core.domain.config.service;

import java.io.Serializable;

import com.datatrees.common.util.json.annotation.Description;
import com.datatrees.crawler.core.domain.config.service.impl.GrabService;
import com.datatrees.crawler.core.domain.config.service.impl.PluginService;
import com.datatrees.crawler.core.domain.config.service.impl.TaskHttpService;
import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.xml.AbstractBeanDefinition;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Jan 9, 2014 7:16:53 PM
 */
@Description(value = "serviceType", keys = {"Plugin_Service", "Grab_Service", "Task_Service"}, types = {PluginService.class, GrabService.class,
        TaskHttpService.class})
public abstract class AbstractService extends AbstractBeanDefinition implements Serializable {

    /**
     *
     */
    private static final long        serialVersionUID = -1249145375986336790L;

    private              ServiceType serviceType;

    @Attr("type")
    public ServiceType getServiceType() {
        return serviceType;
    }

    @Node("@type")
    public void setServiceType(String serviceType) {
        this.serviceType = ServiceType.getServiceType(serviceType);
    }

}
