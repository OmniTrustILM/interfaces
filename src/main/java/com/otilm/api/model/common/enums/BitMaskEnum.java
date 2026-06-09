package com.otilm.api.model.common.enums;

import java.util.EnumSet;

public interface BitMaskEnum {

    int getBit();

    static <E extends Enum<E> & BitMaskEnum> int convertSetToBitMask(EnumSet<E> enums) {
        int bitmask = 0;
        for (E enumEntry : enums) {
            bitmask |= enumEntry.getBit();
        }
        return bitmask;
    }

    static <E extends Enum<E> & BitMaskEnum> EnumSet<E> convertBitMaskToSet(int bitmask, Class<E> enumClass) {
        EnumSet<E> result = EnumSet.noneOf(enumClass);
        for (E constant : enumClass.getEnumConstants()) {
            if ((bitmask & constant.getBit()) != 0) {
                result.add(constant);
            }
        }
        return result;
    }


}
