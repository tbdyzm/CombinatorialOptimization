package com.longmao;

import java.util.LinkedList;
import java.util.List;

/**
 * @Description TODO
 * @Author zimu young
 * Date 2021/8/17 20:49
 * Version 1.0
 **/
public class Main {
    public List<String> a = new LinkedList<>();

    public void print(){
        String[] a = new String[3];
        a[0] = "hello";
        a[1] = "world";
        a[2] = "java";
        this.a.add(a[0]);
        this.a.add(a[1]);
        this.a.add(a[2]);
        System.out.println(this.a);
        System.out.println(this.a.indexOf("hello"));
        this.a.remove("hello");
        System.out.println(this.a.indexOf("world"));
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.print();
    }
}
