package com.season.example.util;

public class CoinCodeDecimalUtil {

    public static String getDecimalFormatPrice(String coinCode){
        StringBuilder sb = new StringBuilder();
        sb.append("########0.");
        for (int i = 0; i < getCoinPriceDecimal(coinCode); i++) {
            sb.append("0");
        }
        return sb.toString();
    }

    public static String getDecimalFormatNumber(String coinCode){
        StringBuilder sb = new StringBuilder();
        sb.append("########0.");
        for (int i = 0; i < getCoinNumberDecimal(coinCode); i++) {
            sb.append("0");
        }
        return sb.toString();
    }


    //获取币种价格保留小数位
    public static int getCoinPriceDecimal(String coinCode) {
        int decimal = 2;//默认两位
        String coinCodeNew = coinCode.replaceAll("_", "");

        CoinDecimalNum[] coinDecimalNums = CoinDecimalNum.values();

        for (int i = 0; i < coinDecimalNums.length; i++) {
            if(coinCodeNew.equals(coinDecimalNums[i].name())) {
                decimal = coinDecimalNums[i].getPriceDecimal();
                break;
            }
        }
        return decimal;
    }

    //获取币种数量保留小数位
    public static int getCoinNumberDecimal(String coinCode) {
        int decimal = 2;//默认两位
        String coinCodeNew = coinCode.replaceAll("_", "");

        CoinDecimalNum[] coinDecimalNums = CoinDecimalNum.values();

        for (int i = 0; i < coinDecimalNums.length; i++) {
            if(coinCodeNew.equals(coinDecimalNums[i].name())) {
                decimal = coinDecimalNums[i].getNumberDecimal();
                break;
            }
        }
        return decimal;
    }
}
