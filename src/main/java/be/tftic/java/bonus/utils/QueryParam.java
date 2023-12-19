package be.tftic.java.bonus.utils;

public record QueryParam(
        int position,
        SQLType type,
        Object value
){}
