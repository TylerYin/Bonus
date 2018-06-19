package com.bonus.form;

/**
 * <p>Title: BaseForm</p>
 * <p>Description: 基础参数</p>
 *
 * @author Tyler
 * @date 2015年11月20日 上午10:03:28
 */
public class BaseForm {
    /**
     * 第几页
     */
    private Integer index = 1;

    /**
     * 一页的大小
     */
    private Integer pageSize = 15;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
