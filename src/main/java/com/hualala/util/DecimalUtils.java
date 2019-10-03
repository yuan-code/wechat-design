package com.hualala.util;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudapeng
 * Date: 2016/11/15
 */
public class DecimalUtils {

    /**
     * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精
     * 确的浮点数运算，包括加减乘除和四舍五入。
     */
    //默认除法运算精度
    private static final int DEF_DIV_SCALE = 8;

    //加减乘除运算
    public static BigDecimal bigAdd(BigDecimal arg1, BigDecimal arg2){

        BigDecimal args = BigDecimal.ZERO;
        args = arg1.add(arg2).setScale(DEF_DIV_SCALE,BigDecimal.ROUND_HALF_UP);
        return args;
    }

    public static BigDecimal bigSub(BigDecimal arg1 ,BigDecimal arg2){

        BigDecimal args = BigDecimal.ZERO;
        args = arg1.subtract(arg2).setScale(DEF_DIV_SCALE,BigDecimal.ROUND_HALF_UP);
        return args;
    }

    public static BigDecimal bigMul(BigDecimal arg1, BigDecimal arg2){
        return arg1.multiply(arg2).setScale(DEF_DIV_SCALE,BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal bigMul(double arg1, double arg2){
        return bigMul(BigDecimal.valueOf(arg1), BigDecimal.valueOf(arg2));
    }

    public static BigDecimal bigDiv(BigDecimal arg1, BigDecimal arg2){

        BigDecimal args = BigDecimal.ZERO;
        if(arg1.compareTo(BigDecimal.ZERO) == 0 || arg2.compareTo(BigDecimal.ZERO) == 0){
            return args;
        }else{
            args = arg1.divide(arg2, DEF_DIV_SCALE ,BigDecimal.ROUND_HALF_UP);
            return args;
        }
    }
}
