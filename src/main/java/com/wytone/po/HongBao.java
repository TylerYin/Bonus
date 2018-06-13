package com.wytone.po;

import java.io.Serializable;
import java.util.Date;

/**
 * 红包
 *
 * @author Tyler
 */
public class HongBao implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long hongBaoId;

    /**
     * 红包发送时间
     */
    private Date addTime;

    /**
     * 金额
     */
    private Integer amount;

    /**
     * 订单号
     */
    private String billNo;

    /**
     * 微信返回的内容
     */
    private String remark;

    /**
     * 用户openid
     */
    private String openid;

    /**
     * 发送结果
     */
    private Integer result;

    /**
     * 手机号
     */
    private String mobile;

    public Long getHongBaoId() {
        return hongBaoId;
    }

    public void setHongBaoId(Long hongBaoId) {
        this.hongBaoId = hongBaoId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}