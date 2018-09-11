/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.bank.service.normalizer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.service.domain.SubmitMessage;
import com.datatrees.spider.share.service.normalizers.SubmitNormalizer;
import com.datatrees.spider.bank.domain.model.EBankExtractResult;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月4日 下午5:33:54
 */
@Service
public class EbankSubmitDataNormalizer implements SubmitNormalizer {

    @Override
    public boolean normalize(SubmitMessage message) {
        if (message.getExtractMessage().getResultType().equals(ResultType.EBANKBILL)) {
            EBankExtractResult result = (EBankExtractResult) message.getResult();
            Set<Map.Entry<String, Object>> entrySet = message.getPageExtractObject().entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                if (entry.getValue() instanceof Collection) {
                    for (Map map : (Collection<Map>) entry.getValue()) {
                        this.ebankDNormalize(map, result);
                    }
                } else if (entry.getValue() instanceof Map) {
                    Map map = (Map) entry.getValue();
                    this.ebankDNormalize(map, result);
                }
            }
            return true;
        }
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void ebankDNormalize(Map map, EBankExtractResult result) {
        map.put("BankId", result.getBankId());
        map.put("UUID", result.getUniqueMd5());
        map.put("PageExtractId", result.getPageExtractId());
    }

}
