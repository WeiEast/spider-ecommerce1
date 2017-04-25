package com.datatrees.rawdatacentral.collector.chain;

import java.util.ArrayList;
import java.util.List;

import com.datatrees.rawdatacentral.collector.chain.search.AddPagingUrlLinkFilter;
import com.datatrees.rawdatacentral.collector.chain.search.ResponseStatusFilter;
import com.datatrees.rawdatacentral.collector.chain.search.RecordNetworkTrafficFilter;
import com.datatrees.rawdatacentral.collector.chain.search.RetryRequestFilter;
import com.datatrees.rawdatacentral.collector.chain.search.SleepModeFilter;
import com.datatrees.rawdatacentral.collector.chain.urlHandler.AddParserTemplateUrlFilter;
import com.datatrees.rawdatacentral.collector.chain.urlHandler.DuplicateRemoveFilter;
import com.datatrees.rawdatacentral.collector.chain.urlHandler.EcommerceTitleFilter;
import com.datatrees.rawdatacentral.collector.chain.urlHandler.MailBillOutboxFilter;
import com.datatrees.rawdatacentral.collector.chain.urlHandler.MailBillReceiveTimeFilter;
import com.datatrees.rawdatacentral.collector.chain.urlHandler.MailBillSenderFilter;
import com.datatrees.rawdatacentral.collector.chain.urlHandler.MailBillillegalSubjectFilter;


/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午2:36:45
 */
public enum FilterListFactory {

    LINKNODE {
        List<Filter> filterList = new ArrayList<Filter>();
        {
            filterList.add(new MailBillReceiveTimeFilter());
            filterList.add(new MailBillillegalSubjectFilter());
            filterList.add(new MailBillOutboxFilter());
            filterList.add(new MailBillSenderFilter());

            filterList.add(new DuplicateRemoveFilter());
            filterList.add(new AddParserTemplateUrlFilter());
            filterList.add(new EcommerceTitleFilter());
        }

        @Override
        public List<Filter> getFilterList() {
            return filterList;
        }

    },
    SEARCH {
        List<Filter> filterList = new ArrayList<Filter>();
        {
            filterList.add(new RecordNetworkTrafficFilter());
            filterList.add(new ResponseStatusFilter());
            filterList.add(new AddPagingUrlLinkFilter());
            filterList.add(new RetryRequestFilter());
            filterList.add(new SleepModeFilter());
        }

        @Override
        public List<Filter> getFilterList() {
            return filterList;
        }
    };
    public abstract List<Filter> getFilterList();

}
