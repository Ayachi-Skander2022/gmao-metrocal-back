package com.metrocal.metrocal.services.PdfCertificate;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.metrocal.metrocal.dto.InterventionResponseDto;
import com.metrocal.metrocal.dto.MesureDto;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class PdfCertificateService {


    private static final Color HEADER_COLOR = new DeviceRgb(13, 110, 253);
    private static final Color LIGHT_BG = new DeviceRgb(248, 249, 250);

    public byte[] generateCalibrationCertificate(InterventionResponseDto dto) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
             Document document = new Document(pdfDoc, PageSize.A4)) {

            document.setMargins(50, 50, 70, 50);

            FontSet fonts = loadFonts();

            addHeader(document, fonts.getRegular(), fonts.getBold(), dto);
            addMainInfoSection(document, fonts.getRegular(), fonts.getBold(), dto);
            addMeasurementsSection(document, fonts.getRegular(), fonts.getBold(), dto);
            addConclusionSection(document, fonts.getRegular(), fonts.getBold());
          addSignature(document, "/static/images/signature.png");

        // Ici, passer pdfDoc et non document
        addFooter(pdfDoc, fonts.getRegular());
        }

        return baos.toByteArray();
    }

    private FontSet loadFonts() throws Exception {
        PdfFont regularFont = loadFontFromResources("/fonts/Roboto-Regular.ttf");
        PdfFont boldFont = loadFontFromResources("/fonts/Roboto-Bold.ttf");
        return new FontSet(regularFont, boldFont);
    }

    private PdfFont loadFontFromResources(String path) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) throw new Exception("Font file not found in resources: " + path);
            byte[] bytes = is.readAllBytes();
            if (bytes.length == 0) throw new Exception("Font file empty: " + path);
            return PdfFontFactory.createFont(bytes, PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
        }
    }

    private static class FontSet {
        private final PdfFont regular;
        private final PdfFont bold;

        public FontSet(PdfFont regular, PdfFont bold) {
            this.regular = regular;
            this.bold = bold;
        }

        public PdfFont getRegular() { return regular; }
        public PdfFont getBold() { return bold; }
    }

    private void addHeader(Document document, PdfFont regular, PdfFont bold, InterventionResponseDto dto) {
        Table headerTable = new Table(new float[]{1, 3});
        headerTable.setWidth(UnitValue.createPercentValue(100));

        try {
            InputStream logoStream = getClass().getResourceAsStream("/static/images/logo.jpg");
            if (logoStream != null) {
                ImageData imageData = ImageDataFactory.create(logoStream.readAllBytes());
                Image logo = new Image(imageData);
                logo.scaleToFit(150, 80);
                headerTable.addCell(new Cell().add(logo)
                        .setBorder(Border.NO_BORDER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE));
            } else {
                headerTable.addCell(new Cell().add(new Paragraph("[LOGO]").setFont(bold))
                        .setBorder(Border.NO_BORDER));
            }
        } catch (Exception e) {
            headerTable.addCell(new Cell().add(new Paragraph("[LOGO]").setFont(bold))
                    .setBorder(Border.NO_BORDER));
        }

        Paragraph companyInfo = new Paragraph()
                .add(new Text("LABORATOIRE DE MÉTROLOGIE – METROCAL\n").setFont(bold).setFontSize(10))
                .add(new Text("13, rue Claude Bernard – Cité Les Jardins – 1002 Tunis – TUNISIE\n").setFont(regular).setFontSize(10))
                .add(new Text("Tél : (216) 71 795 867 / (216) 71 846 122\n").setFont(regular).setFontSize(10))
                .add(new Text("E-mail : metrocal@planet.tn\n").setFont(regular).setFontSize(10))
                .setTextAlignment(TextAlignment.RIGHT);

        headerTable.addCell(new Cell().add(companyInfo)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));

        document.add(headerTable);

        document.add(new Paragraph("CERTIFICAT D'ÉTALONNAGE")
                .setFont(bold)
                .setFontSize(18)
                .setFontColor(HEADER_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20)
                .setMarginBottom(30));

        document.add(new Paragraph()
                .add(new Text("Référence: ").setFont(regular))
                .add(new Text(dto.getId().toString()).setFont(bold))
                .add(new Text(" | Date: ").setFont(regular))
                .add(new Text(dto.getDateIntervention().toString()).setFont(bold))
                .setFontSize(10)
                .setBackgroundColor(LIGHT_BG)
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));
    }

    private void addMainInfoSection(Document document, PdfFont regular, PdfFont bold, InterventionResponseDto dto) {
        document.add(new Paragraph("Informations principales")
                .setFont(bold)
                .setFontSize(14)
                .setMarginBottom(10));

        document.add(new Paragraph("Client : " + dto.getDemande().getClient().getFullName()).setFont(regular).setFontSize(12));
        document.add(new Paragraph("Adresse : " + dto.getDemande().getClient().getAdresse()).setFont(regular).setFontSize(12));
        document.add(new Paragraph("Téléphone : " + dto.getDemande().getClient().getTelephone()).setFont(regular).setFontSize(12));
        document.add(new Paragraph("Email : " + dto.getDemande().getClient().getEmail()).setFont(regular).setFontSize(12));

        document.add(new Paragraph("\nInstrument étalonné :").setFont(bold).setFontSize(14).setMarginTop(15));
        if (dto.getInstrument() != null) {
            document.add(new Paragraph("Nom : " + dto.getInstrument().getNomInstrument()).setFont(regular).setFontSize(12));
            document.add(new Paragraph("Référence : " + dto.getInstrument().getReferenceInstrument()).setFont(regular).setFontSize(12));
            document.add(new Paragraph("Constructeur : " + dto.getInstrument().getConstructeur()).setFont(regular).setFontSize(12));
            document.add(new Paragraph("Type de mesure : " + dto.getInstrument().getTypeMesure()).setFont(regular).setFontSize(12));
            document.add(new Paragraph("Plage de mesure : " + dto.getInstrument().getMinMesure() + " - " + dto.getInstrument().getMaxMesure() + " " + dto.getInstrument().getUniteMesure()).setFont(regular).setFontSize(12));
        } else {
            document.add(new Paragraph("Aucun instrument renseigné.").setFont(regular).setFontSize(12));
        }

        document.add(new Paragraph("\nTechnicien ayant réalisé l'intervention :").setFont(bold).setFontSize(14).setMarginTop(15));
        document.add(new Paragraph(dto.getTechnicien().getFullName()).setFont(regular).setFontSize(12));
        document.add(new Paragraph("Adresse : " + dto.getTechnicien().getAdresse()).setFont(regular).setFontSize(12));
        document.add(new Paragraph("Téléphone : " + dto.getTechnicien().getTelephone()).setFont(regular).setFontSize(12));

        document.add(new Paragraph("\nDétails de l'intervention :").setFont(bold).setFontSize(14).setMarginTop(15));
        document.add(new Paragraph("Durée de l'étalonnage : " + dto.getDureeEtalonnage() + " heures").setFont(regular).setFontSize(12));
        document.add(new Paragraph("Écart global : " + dto.getEcart()).setFont(regular).setFontSize(12));
        document.add(new Paragraph("Type d'étalonnage : " + dto.getDemande().getTypeEtalonnage()).setFont(regular).setFontSize(12));
        document.add(new Paragraph("Statut de l'étalonnage : " + dto.getDemande().getStatutEtalonnage()).setFont(regular).setFontSize(12));
    }

    private void addMeasurementsSection(Document document, PdfFont regular, PdfFont bold, InterventionResponseDto dto) {
        document.add(new Paragraph("\nMesures").setFont(bold).setFontSize(14).setMarginBottom(10));

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3})).useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("Valeur Instrument").setFont(bold)));
        table.addHeaderCell(new Cell().add(new Paragraph("Valeur Étalon").setFont(bold)));
        table.addHeaderCell(new Cell().add(new Paragraph("Écart").setFont(bold)));

        for (MesureDto mesure : dto.getMesures()) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(mesure.getValeurInstrument())).setFont(regular)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(mesure.getValeurEtalon())).setFont(regular)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(mesure.getEcart())).setFont(regular)));
        }

        document.add(table);
    }

    private void addConclusionSection(Document document, PdfFont regular, PdfFont bold) {
        document.add(new Paragraph("\nConclusion").setFont(bold).setFontSize(14).setMarginTop(20));
        document.add(new Paragraph("Ce certificat confirme la conformité des étalonnages réalisés pour l'instrument indiqué. "
                + "Toute anomalie ou remarque doit être signalée au laboratoire dans les plus brefs délais.")
                .setFont(regular)
                .setFontSize(12));
    }


private void addSignature(Document document, String signatureImagePath) throws IOException, java.io.IOException {
    InputStream sigStream = getClass().getResourceAsStream(signatureImagePath);
    if (sigStream != null) {
        ImageData sigData = ImageDataFactory.create(sigStream.readAllBytes());
        Image signature = new Image(sigData);
        
        // Ajuster la taille automatiquement en conservant le ratio
        float maxWidth = 300; // largeur maximale
        float maxHeight = 150; // hauteur maximale
        if (signature.getImageWidth() > maxWidth || signature.getImageHeight() > maxHeight) {
            signature.scaleToFit(maxWidth, maxHeight);
        }
        
        // Centrer l'image
        signature.setHorizontalAlignment(HorizontalAlignment.CENTER);
        signature.setMarginTop(20);
        document.add(signature);

        // Ajouter une ligne pour la signature
        LineSeparator line = new LineSeparator(new SolidLine());
        line.setWidth(UnitValue.createPercentValue(30)); // largeur ligne
        line.setMarginTop(5);
        line.setHorizontalAlignment(HorizontalAlignment.CENTER);
        document.add(line);

        // Ajouter le nom et le titre du signataire en dessous
        Paragraph signataire = new Paragraph()
                .add(new Text("Signataire Responsable Qualité").setFontSize(10))
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(5);
        document.add(signataire);
    }
}


  private void addFooter(PdfDocument pdfDoc, PdfFont regular) {
    int numberOfPages = pdfDoc.getNumberOfPages();
    for (int i = 1; i <= numberOfPages; i++) {
        PdfPage page = pdfDoc.getPage(i);
        Rectangle pageSize = page.getPageSize();
        PdfCanvas pdfCanvas = new PdfCanvas(page);
        Canvas canvas = new Canvas(pdfCanvas, pageSize);

        canvas.showTextAligned(
            new Paragraph("© 2025 Laboratoire de Métrologie – METROCAL")
                .setFont(regular)
                .setFontSize(10),
            pageSize.getWidth() / 2, // X : centré
            pageSize.getBottom() + 20, // Y : 20 points au dessus du bas
            TextAlignment.CENTER
        );
        canvas.close();
    }


}


}
