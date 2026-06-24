package com.dgspos.printagent;

import com.dgspos.printagent.dto.BarcodeRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.ByteArrayInputStream;
import java.util.Base64;

public class BarcodePrintable implements ThermalPrintable {

    private final BarcodeRequest request;
    private final BufferedImage barcodeImage;

    public BarcodePrintable(BarcodeRequest request) throws Exception {
        this.request = request;
        byte[] imageBytes = Base64.getDecoder().decode(request.getBarcodeBase64());
        this.barcodeImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
    }

    @Override
    public int estimateHeight() {
        int barcodeHeight = 60;
        // int textHeight = 20;
        int spacing = 20;

        int singleLabelHeight = barcodeHeight + spacing;
        return singleLabelHeight * request.getQuantity();
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;

        int barcodeWidth = 180;
        int barcodeHeight = 60;

        int y = 10;

        Font font = new Font("SansSerif", Font.PLAIN, 10);
        g2d.setFont(font);

        // Center the barcode
        int barcodeX = ((int) pageFormat.getImageableWidth() - barcodeWidth) / 2;

        for (int i = 0; i < request.getQuantity(); i++) {

            // Barcode
            g2d.drawImage(
                    barcodeImage,
                    barcodeX,
                    y,
                    barcodeWidth,
                    barcodeHeight,
                    null
            );

            y += barcodeHeight + 20;
//
//            // SKU | Product Name
//            g2d.drawString(
//                    request.getSku() + " | " + request.getProductName(),
//                    10,
//                    y
//            );

            // Gap before next label
//            y += 20;
        }

        return PAGE_EXISTS;
    }
}
