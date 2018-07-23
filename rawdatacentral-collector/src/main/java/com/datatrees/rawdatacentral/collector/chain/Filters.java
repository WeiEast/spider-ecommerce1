package com.datatrees.rawdatacentral.collector.chain;

import java.util.ArrayList;
import java.util.List;

import com.datatrees.rawdatacentral.collector.chain.search.*;
import com.datatrees.rawdatacentral.collector.chain.urlHandler.*;

/**
 * @author Jerry
 * @since 21:01 2018/4/17
 */
public enum Filters {
    LINKNODE {
        @Override
        List<Filter> getFilters() {
            List<Filter> filterList = new ArrayList<>();
            filterList.add(new MailBillReceiveTimeFilter());
            filterList.add(new MailBillIllegalSubjectFilter());
            filterList.add(new MailBillOutboxFilter());
            filterList.add(new MailBillSenderFilter());

            filterList.add(new DuplicateRemoveFilter());
            filterList.add(new AddParserTemplateUrlFilter());
            filterList.add(new EcommerceTitleFilter());

            return filterList;
        }
    },
    SEARCH {
        @Override
        List<Filter> getFilters() {
            List<Filter> filterList = new ArrayList<>();

            filterList.add(new RecordNetworkTrafficFilter());
            filterList.add(new ResponseStatusFilter());
            filterList.add(new AddPagingUrlLinkFilter());
            filterList.add(new RetryRequestFilter());
            filterList.add(new SleepModeFilter());

            return filterList;
        }
    };

    private List<Filter> filters;

    Filters() {
        this.filters = getFilters();
    }

    abstract List<Filter> getFilters();

    public void doFilter(Context context) {
        new FilterChain(filters).doFilter(context);
    }

}
