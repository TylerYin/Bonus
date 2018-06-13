
package com.wytone.constants;


/**
 * <p>Title: ShareState</p>
 * <p>Description: 微信分享state</p>
 * @author userwyh
 */
public enum ShareState {
	/**
	 * luckymoney
	 */
	LUCKYMONEY("luckymoney");

	private String desc;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	ShareState(String desc) {
		this.desc = desc;
	}
}
