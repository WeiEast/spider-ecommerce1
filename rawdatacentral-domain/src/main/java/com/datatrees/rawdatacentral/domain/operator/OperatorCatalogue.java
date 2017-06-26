package com.datatrees.rawdatacentral.domain.operator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouxinghai on 2017/6/23.
 */
public class OperatorCatalogue implements Serializable{

    /**
     * 运营商:移动,电信,联通
     */
    private String               catalogue;

    /**
     * 相关站点目录
     */
    private List<OperatorConfig> list = new ArrayList<>();

    public OperatorCatalogue(String catalogue, List<OperatorConfig> list) {
        this.catalogue = catalogue;
        this.list = list;
    }

    public String getCatalogue() {
        return catalogue;
    }

    public void setCatalogue(String catalogue) {
        this.catalogue = catalogue;
    }

    public List<OperatorConfig> getList() {
        return list;
    }

    public void setList(List<OperatorConfig> list) {
        this.list = list;
    }
}
