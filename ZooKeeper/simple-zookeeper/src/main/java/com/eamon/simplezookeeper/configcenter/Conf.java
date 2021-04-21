package com.eamon.simplezookeeper.configcenter;

/**
 * @author eamonzzz
 * @date 2021-03-24 23:15
 */
public class Conf {
    private String data;

    public Conf() {
    }

    public Conf(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
