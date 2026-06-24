package com.dgspos.printagent;

import java.awt.print.Printable;

public interface ThermalPrintable extends Printable {
    int estimateHeight();
}
