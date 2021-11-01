package com.longmao.enums;

public enum EQUATION {
    GREATER_THAN_OR_EQUAL("greater_than_or_equal"),
    EQUAL("equal"),
    LESS_THAN_OR_EQUAL("less_than_or_equal");

    private String equation;

    EQUATION(String equation){
        this.equation = equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public String getEquation() {
        return equation;
    }
}
