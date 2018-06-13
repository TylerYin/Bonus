package com.wytone.po;

import java.io.Serializable;

/**
 * @author Tyler
 */
public class Balance implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long cHongbaoId;
    private Integer balance;
    private Integer send;
    private Integer total;

    public Long getcHongbaoId() {
        return cHongbaoId;
    }

    public void setcHongbaoId(Long cHongbaoId) {
        this.cHongbaoId = cHongbaoId;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getSend() {
        return send;
    }

    public void setSend(Integer send) {
        this.send = send;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}