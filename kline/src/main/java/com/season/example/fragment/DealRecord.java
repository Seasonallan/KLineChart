package com.season.example.fragment;

/**
 * 成交记录 RecyclerView适配器里面用的
 * */
public class DealRecord {

    /**
     * id : 1806734
     * time : 1.598013570229571E9
     * price : 343
     * amount : 0.1
     * type : sell
     */

    private int id = -1;
    private Integer time;
    private String price;
    private Double amount;
    private int direction;//1买 2卖
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
