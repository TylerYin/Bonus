
package com.wytone.constants;


/**
 * Title: ShareState
 * Description: 微信分享state
 * @author Tyler
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
