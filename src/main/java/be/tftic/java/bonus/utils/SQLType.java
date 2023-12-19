package be.tftic.java.bonus.utils;

import java.sql.Types;

public enum SQLType {
    LONG(Types.BIGINT),
    INT(Types.INTEGER),
    STRING(Types.VARCHAR);


    private final int constantValue;

    SQLType(int constantValue) {
        this.constantValue = constantValue;
    }

    public int getConstantValue() {
        return constantValue;
    }
}
