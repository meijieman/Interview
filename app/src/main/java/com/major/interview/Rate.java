package com.major.interview;

/**
 * @desc: TODO
 * @author: Major
 * @since: 2018/4/6 23:38
 */
public class Rate {

    private String currency;
    private String equal;

    public Rate(String currency, String equal) {
        this.currency = currency;
        this.equal = equal;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getEqual() {
        return equal;
    }
    public void setEqual(String equal) {
        this.equal = equal;
    }

    @Override
    public String toString() {
        return "Rate{" +
                "currency='" + currency + '\'' +
                ", equal=" + equal +
                '}';
    }
}
