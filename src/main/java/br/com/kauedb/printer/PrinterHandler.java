package br.com.kauedb.printer;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import java.awt.print.PrinterJob;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PrinterHandler {

    public static void main(String[] args) {
//        final PrinterJob job = PrinterJob.getPrinterJob();
//
//        boolean doPrint = job.printDialog();
//
//        System.out.println("doPrint = " + doPrint);

        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        System.out.println("Number of print services: " + printServices.length);

        for (PrintService printer : printServices)
            System.out.println("Printer: " + printer.getName());

//        DocFlavor[] docFalvor = prnSvc.getSupportedDocFlavors();
//        for (int i = 0; i < docFalvor.length; i++) {
//            System.out.println(docFalvor[i].getMimeType());
//        }

        final DocFlavor flavor = DocFlavor.INPUT_STREAM.TEXT_PLAIN_HOST;
        final PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(MediaSizeName.A);
        final PrintService[] pservices = PrintServiceLookup.lookupPrintServices(flavor, aset);
        if (pservices.length > 0) {
            final DocPrintJob pj = pservices[0].createPrintJob();
            try {
                final FileInputStream fis = new FileInputStream("test.txt");
                final Doc doc = new SimpleDoc(fis, flavor, null);
                pj.print(doc, aset);
            } catch (FileNotFoundException fe) {
            } catch (PrintException e) {
            }
        }

    }
}
