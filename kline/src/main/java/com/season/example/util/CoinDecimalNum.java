package com.season.example.util;

public enum CoinDecimalNum {
    ETH(8, 8),
    BTC(8, 2),
    USDT(8, 8),
    XRP(8, 8),
    EOS(8, 2),
    LTC(8, 2),
    TRX(8, 2),
    XMR(8, 2),
    NEO(8, 8),
    ADA(8, 8),
    DASH(8, 2),
    BCH(8, 2),
    BSV(8, 2),
    ETC(8, 2),
    LINK(8, 2),
    AITD(8, 8),
    ADAUSDT(6, 6),
    AITDUSDT(8, 8),
    BCHUSDT(2, 4),
    BSVUSDT(2, 4),
    BTCUSDT(2, 6),
    DASHUSDT(2, 4),
    EOSUSDT(4, 4),
    ETCUSDT(4, 4),
    ETHUSDT(2, 4),
    LINKUSDT(4, 2),
    LTCUSDT(2, 4),
    NEOUSDT(2, 4),
    TRXUSDT(6, 2),
    XMRUSDT(2, 4),
    XRPUSDT(5, 2),
    ADABTC(8, 2),
    DASHBTC(6, 4),
    EOSBTC(8, 2),
    ETHBTC(6, 4),
    LTCBTC(6, 4),
    NEOBTC(6, 4),
    TRXBTC(10, 2),
    XMRBTC(6, 4),
    XRPBTC(8, 0),
    ADAETH(6, 4),
    EOSETH(8, 2),
    LINKETH(8, 2),
    TRXETH(8, 2),
    XMRETH(6, 4);

    private int priceDecimal;//价格小数位
    private int numberDecimal;//数量小数位

    CoinDecimalNum(int priceDecimal, int numberDecimal) {
        this.priceDecimal = priceDecimal;
        this.numberDecimal = numberDecimal;
    }

    public int getPriceDecimal() {
        return priceDecimal;
    }

    public void setPriceDecimal(int priceDecimal) {
        this.priceDecimal = priceDecimal;
    }

    public int getNumberDecimal() {
        return numberDecimal;
    }

    public void setNumberDecimal(int numberDecimal) {
        this.numberDecimal = numberDecimal;
    }
}
