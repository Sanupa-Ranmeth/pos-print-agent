package com.dgspos.printagent;

import com.dgspos.printagent.dto.ReceiptRequest;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.util.Arrays;

@Service
public class PrinterService {

    public void print(ReceiptRequest receipt) throws Exception {

        PrinterJob printerJob = PrinterJob.getPrinterJob();

        PrintService printer = Arrays.stream(PrinterJob.lookupPrintServices())
                        .filter(p -> p.getName().equals("Star TSP100 Cutter (TSP143)"))
                        .findFirst().orElseThrow(() -> new RuntimeException("Printer not found"));

        printerJob.setPrintService(printer);

        // -------- CREATING NEW PAGE FORMAT ----------------
        Paper paper = new Paper();

        // 3 inch
        double width = 3 * 72;

        ReceiptPrintable printable = new ReceiptPrintable(receipt);
        double height = printable.estimateHeight();

        paper.setSize(width, height);
        paper.setImageableArea(0, 0, width, height);

        PageFormat pf = new PageFormat();
        pf.setPaper(paper);

        printerJob.setPrintable(
                printable,
                pf
        );

        printerJob.print();

    }
}
