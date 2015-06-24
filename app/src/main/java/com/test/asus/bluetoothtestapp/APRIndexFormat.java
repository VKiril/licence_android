package com.test.asus.bluetoothtestapp;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * A simple formatter to convert bar indexes into sensor names.
 */
public class APRIndexFormat extends Format {
    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        Number num = (Number) obj;

        // using num.intValue() will floor the value, so we add 0.5 to round instead:
        int roundNum = (int) (num.floatValue() + 0.5f);
        switch(roundNum) {
            case 0:
                toAppendTo.append("Azimuth");
                break;
            case 1:
                toAppendTo.append("Pitch");
                break;
            case 2:
                toAppendTo.append("Roll");
                break;
            default:
                toAppendTo.append("Unknown");
        }
        return toAppendTo;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return null;  // We don't use this so just return null for now.
    }


}