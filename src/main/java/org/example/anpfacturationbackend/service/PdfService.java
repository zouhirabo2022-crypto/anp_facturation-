package org.example.anpfacturationbackend.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.ColumnText;
import org.example.anpfacturationbackend.entity.Facture;
import org.example.anpfacturationbackend.entity.LigneFacture;
import org.example.anpfacturationbackend.enums.StatutFacture;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

@Service
public class PdfService {

    public byte[] generateFacturePdf(Facture facture) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, out);

        document.open();

        if (facture.getStatut() == StatutFacture.BROUILLON) {
            PdfContentByte canvas = writer.getDirectContentUnder();
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                    new Phrase("BROUILLON", new Font(Font.HELVETICA, 52, Font.BOLD, Color.LIGHT_GRAY)),
                    297.5f, 421, 45);
        }

        // Header Layout using Table
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 1});

        // Left: Company Info
        PdfPCell companyCell = new PdfPCell();
        companyCell.setBorder(Rectangle.NO_BORDER);
        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.DARK_GRAY);
        companyCell.addElement(new Paragraph("Agence Nationale des Ports", companyFont));
        companyCell.addElement(new Paragraph("Port de Jorf Lasfar", FontFactory.getFont(FontFactory.HELVETICA, 12)));
        companyCell.addElement(new Paragraph("Direction d'Exploitation", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        headerTable.addCell(companyCell);

        // Right: Invoice Info
        PdfPCell invoiceInfoCell = new PdfPCell();
        invoiceInfoCell.setBorder(Rectangle.NO_BORDER);
        invoiceInfoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        Paragraph title = new Paragraph("FACTURE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, new Color(41, 128, 185))); // Blue color
        title.setAlignment(Element.ALIGN_RIGHT);
        invoiceInfoCell.addElement(title);

        Paragraph ref = new Paragraph("Réf: " + facture.getNumero(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        ref.setAlignment(Element.ALIGN_RIGHT);
        invoiceInfoCell.addElement(ref);

        Paragraph date = new Paragraph("Date: " + facture.getDate(), FontFactory.getFont(FontFactory.HELVETICA, 12));
        date.setAlignment(Element.ALIGN_RIGHT);
        invoiceInfoCell.addElement(date);

        headerTable.addCell(invoiceInfoCell);
        document.add(headerTable);

        document.add(new Paragraph("\n"));

        // Client Info Box
        PdfPTable clientTable = new PdfPTable(1);
        clientTable.setWidthPercentage(50);
        clientTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        PdfPCell clientCell = new PdfPCell();
        clientCell.setPadding(10);
        clientCell.setBackgroundColor(new Color(245, 245, 245)); // Light gray
        clientCell.setBorderColor(Color.LIGHT_GRAY);
        
        clientCell.addElement(new Paragraph("Client:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        clientCell.addElement(new Paragraph(facture.getClient().getNom() + " " +
                (facture.getClient().getPrenom() != null ? facture.getClient().getPrenom() : ""), 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        clientCell.addElement(new Paragraph(facture.getClient().getAdresse()));
        
        if (facture.getClient().getIce() != null)
            clientCell.addElement(new Paragraph("ICE: " + facture.getClient().getIce(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
        if (facture.getClient().getIfClient() != null)
            clientCell.addElement(new Paragraph("IF: " + facture.getClient().getIfClient(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
        if (facture.getClient().getRc() != null)
            clientCell.addElement(new Paragraph("RC: " + facture.getClient().getRc(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
            
        clientTable.addCell(clientCell);
        document.add(clientTable);
        
        document.add(new Paragraph("\n\n"));

        // Table
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 1, 1.5f, 1, 1, 1.5f}); // Adjust column widths
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        Stream.of("Désignation", "Qté", "P.U", "TR (%)", "TVA (%)", "Total HT")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(new Color(230, 230, 230));
                    header.setBorderWidth(1);
                    header.setPadding(6);
                    header.setPhrase(new Phrase(columnTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });

        for (LigneFacture ligne : facture.getLignes()) {
            PdfPCell cellDesc = new PdfPCell(new Phrase(ligne.getPrestation() != null ? ligne.getPrestation().getLibelle() : "N/A", FontFactory.getFont(FontFactory.HELVETICA, 10)));
            cellDesc.setPadding(4);
            table.addCell(cellDesc);

            addRightAlignedCell(table, formatValue(ligne.getQuantite()));
            addRightAlignedCell(table, formatValue(ligne.getPrixUnitaire()));
            addRightAlignedCell(table, formatValue(ligne.getTauxTr()));
            addRightAlignedCell(table, formatValue(ligne.getTauxTva()));
            addRightAlignedCell(table, formatValue(ligne.getMontantHt()));
        }

        document.add(table);

        // Summary
        PdfPTable summary = new PdfPTable(2);
        summary.setWidthPercentage(40);
        summary.setHorizontalAlignment(Element.ALIGN_RIGHT);

        addSummaryRow(summary, "Total HT:", formatValue(facture.getMontantHt()) + " DH");
        addSummaryRow(summary, "Total TR:", formatValue(facture.getMontantTr()) + " DH");
        addSummaryRow(summary, "Total TVA:", formatValue(facture.getMontantTva()) + " DH");
        addSummaryRow(summary, "TOTAL TTC:", formatValue(facture.getMontantTtc()) + " DH");

        document.add(summary);

        document.close();
        return out.toByteArray();
    }

    private String formatValue(Double value) {
        if (value == null)
            return "0.00";
        return String.format("%.2f", value);
    }

    private void addSummaryRow(PdfPTable table, String label, String value) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Phrase(value));
        cellValue.setBorder(Rectangle.NO_BORDER);
        cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellValue);
    }

    private void addRightAlignedCell(PdfPTable table, String value) {
        PdfPCell cell = new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(4);
        table.addCell(cell);
    }
}
