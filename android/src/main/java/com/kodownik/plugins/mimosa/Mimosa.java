package com.kodownik.plugins.mimosa;

import com.getcapacitor.Logger;

public class Mimosa {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
