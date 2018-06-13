package com.wytone.util;

import java.security.MessageDigest;

/**
 * ClassName:Md5Utils <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:   TODO ADD REASON. <br/>
 * Date:     2015-1-15 下午1:27:25 <br/>
 *
 * @author 822
 * @see
 * @since JDK 1.6
 */
public class Md5Utils {
    public static String md5(String s) throws Exception {
        if (s.trim() == null) {
            return "null";
        }
        MessageDigest messagedigest = MessageDigest.getInstance("MD5");
        byte[] abyte0 = s.getBytes("utf-8");
        byte[] abyte1 = messagedigest.digest(abyte0);
        return bytes2Hex(abyte1).toUpperCase();
    }

    private static String bytes2Hex(byte[] b) {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < b.length; ++i) {
            String s1 = Integer.toHexString(b[i] & 0xFF);
            if (s1.length() == 1) {
                sb = sb.append("0");
            }
            sb = sb.append(s1);
        }
        return sb.toString();
    }

    /**
     * <p>Description: 16位的MD5值</p>
     * <p>Company: caimei365</p>
     *
     * @param s
     * @return
     * @throws Exception
     * @author dmeng
     * @date 2015年12月17日 下午5:28:49
     */
    public static String md5To16Bit(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(s.getBytes());
        byte[] b = md.digest();
        int i;
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0) {
                i += 256;
            }

            if (i < 16) {
                buf.append("0");
            }

            buf.append(Integer.toHexString(i));
        }
        return buf.toString().substring(8, 24);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Md5Utils.md5To16Bit("act_name=发红包测试&client_ip=127.0.0.1&max_value=132&mch_billno=100257913453645825&mch_id=10025791&min_value=132&nick_name=采美365网&nonce_str=A1ED1E437160452FAE18356985D38F43&re_openid=oVTYvt4xhmqGKSI3owBO1TdJqtYo&remark=快来抢！&send_name=采美365网&total_amount=132&total_num=1&wishing=恭喜发财,大吉大利&wxappid=wxea43a0f9ebce9e66&key=CaimeiWxpayasdklfj8sdf27sdf3DcVd"));
    }
}
