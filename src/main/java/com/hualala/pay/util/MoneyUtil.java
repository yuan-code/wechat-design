package com.hualala.pay.util;

import java.math.BigDecimal;


public class MoneyUtil {

    /**
     * 元转分
     *
     * @param yuan
     * @return
     */
    public static Integer Yuan2Fen(BigDecimal yuan) {
        return yuan.movePointRight(2).intValue();
    }

    /**
     * 分转元
     *
     * @param fen
     * @return
     */
    public static BigDecimal Fen2Yuan(Integer fen) {
        return new BigDecimal(fen.toString()).movePointLeft(2);
    }
}
