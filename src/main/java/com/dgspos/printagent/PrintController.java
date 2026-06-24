package com.dgspos.printagent;

import com.dgspos.printagent.dto.BarcodeRequest;
import com.dgspos.printagent.dto.ReceiptRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.print.PrintService;
import java.awt.print.PrinterJob;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PrintController {

    private final PrinterService printerService;

    @PostMapping("/print")
    public void print(@RequestBody ReceiptRequest request) throws Exception {
        printerService.print(request);
    }

    @PostMapping("/barcode")
    public void barcode(@RequestBody BarcodeRequest request) throws Exception {
        printerService.printBarcode(request);
    }

    @GetMapping("/printers")
    public List<String> printers() {
        return Arrays.stream(PrinterJob.lookupPrintServices())
                .map(PrintService::getName)
                .toList();
    }
}