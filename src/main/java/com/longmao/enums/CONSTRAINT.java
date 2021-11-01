package com.longmao.enums;

public enum CONSTRAINT {
    GREATER_THAN_OR_EQUAL_TO_ZERO("greater_than_or_equal_to_zero"),
    LESS_THAN_OT_EQUAL_TO_ZERO("less_than_or_equal_to_zero"),
    NO_CONSTRAINT("no_constraint");

    private String constraint;

    CONSTRAINT(String constraint){
        this.constraint = constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String getConstraint() {
        return constraint;
    }
}
