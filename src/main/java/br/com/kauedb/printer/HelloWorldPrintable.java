package br.com.kauedb.printer;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class HelloWorldPrintable implements Printable {

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

        if(pageIndex < 0){
            return -1;
        }



        return 0;
    }
}
