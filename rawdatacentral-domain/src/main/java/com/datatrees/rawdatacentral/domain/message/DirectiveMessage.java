package com.datatrees.rawdatacentral.domain.message;

import com.datatrees.rawdatacentral.domain.enums.DirectiveEnum;

/**
 * 操作指令
 * Created by zhouxinghai on 2017/4/25.
 */
public class DirectiveMessage extends TaskMessage {

    /**
     * 指令代码
     */
    private String directiveCode;

    /**
     * 指令名称
     */
    private String directiveName;

    /**
     * 设置指令
     *
     * @param directiveEnum 指令
     */
    public void setDirective(DirectiveEnum directiveEnum) {
        this.directiveCode = directiveEnum.getCode();
        this.directiveName = directiveEnum.getName();
    }


    public String getDirectiveCode() {
        return directiveCode;
    }

    public void setDirectiveCode(String directiveCode) {
        this.directiveCode = directiveCode;
    }

    public String getDirectiveName() {
        return directiveName;
    }

    public void setDirectiveName(String directiveName) {
        this.directiveName = directiveName;
    }
}
