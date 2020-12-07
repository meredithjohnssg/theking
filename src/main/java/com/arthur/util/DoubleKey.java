package com.arthur.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DoubleKey<K1, K2> {

    private K1 relationship;
    private K2 serial;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubleKey key = (DoubleKey) o;

        if (relationship != null ? !relationship.equals(key.relationship) : key.relationship != null) return false;
        if (serial != null ? !serial.equals(key.serial) : key.serial != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = relationship != null ? relationship.hashCode() : 0;
        result = 31 * result + (serial != null ? serial.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "[" + relationship + ", " + serial + "]";
    }
}
