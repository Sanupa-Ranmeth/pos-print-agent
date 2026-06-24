package com.dgspos.printagent;

import com.dgspos.printagent.dto.Company;
import com.dgspos.printagent.dto.Item;
import com.dgspos.printagent.dto.ReceiptRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.ByteArrayInputStream;
import java.util.Base64;

public class ReceiptPrintable implements ThermalPrintable {

    private final ReceiptRequest receipt;

    private static final int LEFT_PADDING = 10;
    private static final int RIGHT_PADDING = 10;
    private static final int DIVIDER_MARGIN = 4;

    public ReceiptPrintable(ReceiptRequest receipt) {
        this.receipt = receipt;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g = (Graphics2D) graphics;

        g.translate(
                pageFormat.getImageableX(),
                pageFormat.getImageableY()
        );

        g.setColor(Color.BLACK);

        int y = 20;

        int width = (int) pageFormat.getImageableWidth();
        int contentWidth = width - LEFT_PADDING - RIGHT_PADDING;

        Company company = receipt.getCompany();

        // =====================================================
        // LOGO
        // =====================================================

        if (company != null &&
                company.getLogoBase64() != null &&
                !company.getLogoBase64().isBlank()) {

            try {

                String base64 = company.getLogoBase64();

                if (base64.contains(",")) {
                    base64 = base64.substring(base64.indexOf(",") + 1);
                }

                byte[] bytes = Base64.getDecoder().decode(base64);

                BufferedImage logo =
                        ImageIO.read(new ByteArrayInputStream(bytes));

                if (logo != null) {

                    int logoWidth = 140;

                    int logoHeight =
                            (logo.getHeight() * logoWidth)
                                    / logo.getWidth();

                    int x = (width - logoWidth) / 2;

                    g.drawImage(
                            logo,
                            x,
                            y,
                            logoWidth,
                            logoHeight,
                            null
                    );

                    y += logoHeight + 10;
                }

            } catch (Exception ignored) {
            }
        }

        // =====================================================
        // COMPANY DETAILS
        // =====================================================

//        g.setFont(new Font("Arial", Font.BOLD, 14));
//        drawCentered(g, company.getName(), width, y);
//
//        y += 15;

        g.setFont(new Font("Arial", Font.PLAIN, 9));
        drawCentered(g, company.getPhone(), width, y);

        y += 9;

        // smaller font so address usually fits on one line
        g.setFont(new Font("Arial", Font.PLAIN, 7));
        drawCentered(g, company.getAddress(), width, y);

        y += 8;

        y += DIVIDER_MARGIN;
        divider(g, width, y);
        y += DIVIDER_MARGIN;

        y += 8;

        // =====================================================
        // INVOICE
        // =====================================================

        g.setFont(new Font("Arial", Font.PLAIN, 10));

        g.drawString(
                "Invoice: " + receipt.getInvoiceNumber(),
                LEFT_PADDING,
                y
        );

        y += 4;

        // y += DIVIDER_MARGIN;
        divider(g, width, y);
        y += DIVIDER_MARGIN;

        y += 10;

        // =====================================================
        // ITEM HEADER
        // =====================================================

        g.setFont(new Font("Arial", Font.BOLD, 10));

        FontMetrics fm = g.getFontMetrics();

        g.drawString("Item", LEFT_PADDING, y);
        g.drawString("Qty", width - 75, y);
        g.drawString(
                "Total",
                width - RIGHT_PADDING - fm.stringWidth("Total"),
                y
        );

        y += 5;

        // y += DIVIDER_MARGIN;
        divider(g, width, y);
        y += DIVIDER_MARGIN;

        y += 8;

        // =====================================================
        // ITEMS
        // =====================================================

        g.setFont(new Font("Arial", Font.PLAIN, 10));

        for (Item item : receipt.getItems()) {

            String qty = String.valueOf(item.getQuantity());
            String total = String.format("%.2f", item.getTotal());

            String itemName = item.getName();

            if (itemName != null && itemName.length() > 22) {
                itemName = itemName.substring(0, 19) + "...";
            }

            g.drawString(
                    itemName,
                    LEFT_PADDING,
                    y
            );

            g.drawString(
                    qty,
                    width - 75,
                    y
            );

            fm = g.getFontMetrics();

            g.drawString(
                    total,
                    width - RIGHT_PADDING - fm.stringWidth(total),
                    y
            );

            y += 11;

            g.setFont(new Font("Arial", Font.PLAIN, 8));

            g.drawString(
                    item.getQuantity()
                            + " x "
                            + String.format("%.2f", item.getPrice()),
                    LEFT_PADDING + 10,
                    y
            );

            y += 10;

            g.setFont(new Font("Arial", Font.PLAIN, 10));
        }

        y += DIVIDER_MARGIN;
        divider(g, width, y);
        y += DIVIDER_MARGIN;

        y += 10;

        // =====================================================
        // TOTALS
        // =====================================================

        drawAmountLine(
                g,
                "Subtotal",
                receipt.getSubtotal(),
                width,
                y
        );

        y += 12;

        drawAmountLine(
                g,
                "Discount",
                receipt.getDiscount(),
                width,
                y
        );

        y += 6;

        // y += DIVIDER_MARGIN;
        divider(g, width, y);
        y += DIVIDER_MARGIN;

        y += 12;

        g.setFont(new Font("Arial", Font.BOLD, 12));

        drawAmountLine(
                g,
                "TOTAL",
                receipt.getTotal(),
                width,
                y
        );

        y += 15;

        // =====================================================
        // PAYMENT
        // =====================================================

        g.setFont(new Font("Arial", Font.PLAIN, 10));

        drawLabelValue(
                g,
                "Payment",
                receipt.getPaymentMethod(),
                width,
                y
        );

        y += 12;

        drawLabelValue(
                g,
                "Paid",
                String.format("%.2f", receipt.getPaidAmount()),
                width,
                y
        );

        y += 12;

        drawLabelValue(
                g,
                "Change",
                String.format("%.2f", receipt.getChangeAmount()),
                width,
                y
        );

        y += 8;

        // =====================================================
        // BARCODE
        // =====================================================

        if (receipt.getBarcodeBase64() != null &&
                !receipt.getBarcodeBase64().isBlank()) {

            try {

                String base64 = receipt.getBarcodeBase64();

                if (base64.contains(",")) {
                    base64 = base64.substring(
                            base64.indexOf(",") + 1
                    );
                }

                byte[] bytes =
                        Base64.getDecoder().decode(base64);

                BufferedImage barcode =
                        ImageIO.read(
                                new ByteArrayInputStream(bytes)
                        );

                if (barcode != null) {
                    int barcodeWidth = contentWidth - 30;
                    int barcodeHeight = 60;

                    // Preserves the aspect ratio of the barcode
//                    int barcodeWidth = contentWidth - 80;
//
//                    int barcodeHeight =
//                            (barcode.getHeight() * barcodeWidth)
//                                    / barcode.getWidth();

                    int barcodeX =
                            LEFT_PADDING +
                                    (contentWidth - barcodeWidth) / 2;

                    g.drawImage(
                            barcode,
                            barcodeX,
                            y,
                            barcodeWidth,
                            barcodeHeight,
                            null
                    );

                    y += barcodeHeight + 4;
                }

            } catch (Exception ignored) {
            }
        }

        // y += DIVIDER_MARGIN;
        divider(g, width, y);
        y += DIVIDER_MARGIN;

        y += 8;

        // =====================================================
        // EXCHANGE POLICY
        // =====================================================

        g.setFont(new Font("Arial", Font.BOLD, 11));

        drawCentered(
                g,
                "- SAREES ARE NOT EXCHANGEABLE -",
                width,
                y
        );

        y += 8;

        g.setFont(new Font("Arial", Font.PLAIN, 8));

        y = drawWrappedCentered(
                g,
                "Batik items may be exchanged within 3 days.",
                contentWidth,
                y
        );

        y += 4;

        y = drawWrappedCentered(
                g,
                "No bill is required for exchanges provided the original item tag remains attached.",
                contentWidth,
                y
        );

        y += 6;

        divider(g, width, y);
        y += DIVIDER_MARGIN;

        y += 10;

        // =====================================================
        // THANK YOU
        // =====================================================

        g.setFont(new Font("Arial", Font.BOLD, 11));

        drawCentered(
                g,
                "THANK YOU FOR SHOPPING",
                width,
                y
        );

        y += 8;

        g.setFont(new Font("Arial", Font.PLAIN, 8));

        drawWrappedCentered(
                g,
                company.getFooterText(),
                contentWidth,
                y
        );

        return PAGE_EXISTS;
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private void divider(
            Graphics2D g,
            int width,
            int y
    ) {

        g.drawLine(
                LEFT_PADDING,
                y,
                width - RIGHT_PADDING,
                y
        );
    }

    private void drawCentered(
            Graphics2D g,
            String text,
            int width,
            int y
    ) {

        if (text == null || text.isBlank()) {
            return;
        }

        FontMetrics fm = g.getFontMetrics();

        int x =
                (width - fm.stringWidth(text)) / 2;

        g.drawString(text, x, y);
    }

    private void drawAmountLine(
            Graphics2D g,
            String label,
            double amount,
            int width,
            int y
    ) {

        String value =
                String.format("%.2f", amount);

        FontMetrics fm = g.getFontMetrics();

        g.drawString(
                label,
                LEFT_PADDING,
                y
        );

        g.drawString(
                value,
                width - RIGHT_PADDING - fm.stringWidth(value),
                y
        );
    }

    private void drawLabelValue(
            Graphics2D g,
            String label,
            String value,
            int width,
            int y
    ) {

        FontMetrics fm = g.getFontMetrics();

        g.drawString(
                label,
                LEFT_PADDING,
                y
        );

        g.drawString(
                value,
                width - RIGHT_PADDING - fm.stringWidth(value),
                y
        );
    }

    private int drawWrappedCentered(
            Graphics2D g,
            String text,
            int width,
            int y
    ) {

        if (text == null || text.isBlank()) {
            return y;
        }

        FontMetrics fm = g.getFontMetrics();

        String[] words = text.split("\\s+");

        StringBuilder line = new StringBuilder();

        for (String word : words) {

            String testLine =
                    line.length() == 0
                            ? word
                            : line + " " + word;

            if (fm.stringWidth(testLine) > width) {

                drawCentered(
                        g,
                        line.toString(),
                        width + LEFT_PADDING + RIGHT_PADDING,
                        y
                );

                y += fm.getHeight();

                line = new StringBuilder(word);

            } else {

                line = new StringBuilder(testLine);
            }
        }

        if (!line.isEmpty()) {

            drawCentered(
                    g,
                    line.toString(),
                    width + LEFT_PADDING + RIGHT_PADDING,
                    y
            );

            y += fm.getHeight();
        }

        return y;
    }

    public int estimateHeight() {

        int y = 20;

        Company company = receipt.getCompany();

        // logo
        if (company != null &&
                company.getLogoBase64() != null &&
                !company.getLogoBase64().isBlank()) {

            y += 90;
        }

        // company details
        // y += 15; // name
        y += 10; // phone
        y += 10; // address

        y += 10; // divider

        // invoice
        y += 20;

        // item header
        y += 25;

        // items
        for (Item item : receipt.getItems()) {

            y += 14; // item row
            y += 12; // qty x price
            y += 4;
        }

        y += 20; // divider + spacing

        // totals
        y += 15;
        y += 15;
        y += 20;

        // payment
        y += 15;
        y += 15;
        y += 20;

        // barcode
        if (receipt.getBarcodeBase64() != null &&
                !receipt.getBarcodeBase64().isBlank()) {

            y += 50;
        }

        // exchange policy
        y += 35;

        // thank you + footer
        y += 40;

        if (company != null &&
                company.getFooterText() != null) {

            int footerLines =
                    Math.max(
                            1,
                            company.getFooterText().length() / 30
                    );

            y += footerLines * 10;
        }

        return y + 40;
    }
}