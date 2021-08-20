package com.longmao.dto;

public enum OBJECTIVE {
    MAX("max"),
    MIN("min");

    private String objective;

    OBJECTIVE(String objective){
        this.objective = objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getObjective() {
        return objective;
    }
}
