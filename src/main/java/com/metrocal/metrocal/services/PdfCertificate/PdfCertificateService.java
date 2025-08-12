package com.metrocal.metrocal.services.PdfCertificate;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
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


      private static final String LOGO_URL = "https://example.com/path/to/logo.png";

    private static final Color HEADER_COLOR = new DeviceRgb(13, 110, 253);
    private static final Color LIGHT_BG = new DeviceRgb(248, 249, 250);

    public byte[] generateCalibrationCertificate(InterventionResponseDto dto) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
         Document document = new Document(pdfDoc, PageSize.A4)) {  // <- format portrait

    document.setMargins(50, 50, 70, 50);

            FontSet fonts = loadFonts();

            addHeader(document, fonts.getRegular(), fonts.getBold(), dto);
            addMainInfoSection(document, fonts.getRegular(), fonts.getBold(), dto);
            addMeasurementsSection(document, fonts.getRegular(), fonts.getBold(), dto);
            addConclusionSection(document, fonts.getRegular(), fonts.getBold(), dto);
            addFooter(document, fonts.getRegular());

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
            ImageData imageData = ImageDataFactory.create(LOGO_URL);
            Image logo = new Image(imageData);
            logo.scaleToFit(100, 60);
            headerTable.addCell(new Cell().add(logo)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
        } catch (Exception e) {
            headerTable.addCell(new Cell().add(new Paragraph("[LOGO]").setFont(bold))
                    .setBorder(Border.NO_BORDER));
        }

        Paragraph companyInfo = new Paragraph()
                .add(new Text("LABORATOIRE DE MÉTROLOGIE\n").setFont(bold).setFontSize(14))
                .add(new Text("Agrément N°: 1234/2023\n").setFont(regular).setFontSize(10))
                .add(new Text("Adresse: 123 Rue Principale, Ville\n").setFont(regular).setFontSize(10))
                .add(new Text("Tél: +123 456 789 - Email: contact@labo.com").setFont(regular).setFontSize(10))
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

        // Client
        document.add(new Paragraph("Client : " + dto.getDemande().getClient().getFullName())
                .setFont(regular)
                .setFontSize(12));

        // Adresse client
        document.add(new Paragraph("Adresse : " + dto.getDemande().getClient().getAdresse())
                .setFont(regular)
                .setFontSize(12));

        // Téléphone client
        document.add(new Paragraph("Téléphone : " + dto.getDemande().getClient().getTelephone())
                .setFont(regular)
                .setFontSize(12));

        // Email client
        document.add(new Paragraph("Email : " + dto.getDemande().getClient().getEmail())
                .setFont(regular)
                .setFontSize(12));

        document.add(new Paragraph("\nInstrument étalonné :")
                .setFont(bold)
                .setFontSize(14)
                .setMarginTop(15));

        if (dto.getInstrument() != null) {
            document.add(new Paragraph("Nom : " + dto.getInstrument().getNomInstrument())
                    .setFont(regular).setFontSize(12));
            document.add(new Paragraph("Référence : " + dto.getInstrument().getReferenceInstrument())
                    .setFont(regular).setFontSize(12));
            document.add(new Paragraph("Constructeur : " + dto.getInstrument().getConstructeur())
                    .setFont(regular).setFontSize(12));
            document.add(new Paragraph("Type de mesure : " + dto.getInstrument().getTypeMesure())
                    .setFont(regular).setFontSize(12));
            document.add(new Paragraph("Plage de mesure : " + dto.getInstrument().getMinMesure() + " - " + dto.getInstrument().getMaxMesure() + " " + dto.getInstrument().getUniteMesure())
                    .setFont(regular).setFontSize(12));
        } else {
            document.add(new Paragraph("Aucun instrument renseigné.")
                    .setFont(regular)
                    .setFontSize(12));
        }

        // Technicien
        document.add(new Paragraph("\nTechnicien ayant réalisé l'intervention :")
                .setFont(bold)
                .setFontSize(14)
                .setMarginTop(15));

        document.add(new Paragraph(dto.getTechnicien().getFullName())
                .setFont(regular)
                .setFontSize(12));
        document.add(new Paragraph("Adresse : " + dto.getTechnicien().getAdresse())
                .setFont(regular)
                .setFontSize(12));
        document.add(new Paragraph("Téléphone : " + dto.getTechnicien().getTelephone())
                .setFont(regular)
                .setFontSize(12));

        document.add(new Paragraph("\nDétails de l'intervention :")
                .setFont(bold)
                .setFontSize(14)
                .setMarginTop(15));

        document.add(new Paragraph("Durée de l'étalonnage : " + dto.getDureeEtalonnage() + " heures")
                .setFont(regular)
                .setFontSize(12));
        document.add(new Paragraph("Écart global : " + dto.getEcart())
                .setFont(regular)
                .setFontSize(12));
        document.add(new Paragraph("Type d'étalonnage : " + dto.getDemande().getTypeEtalonnage())
                .setFont(regular)
                .setFontSize(12));
        document.add(new Paragraph("Statut de l'étalonnage : " + dto.getDemande().getStatutEtalonnage())
                .setFont(regular)
                .setFontSize(12));
    }

    private void addMeasurementsSection(Document document, PdfFont regular, PdfFont bold, InterventionResponseDto dto) {
        document.add(new Paragraph("\nMesures")
                .setFont(bold)
                .setFontSize(14)
                .setMarginBottom(10));

        // Créer un tableau avec colonnes: Valeur Instrument | Valeur Étalon | Écart
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3})).useAllAvailableWidth();

        // Entêtes
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

    private void addConclusionSection(Document document, PdfFont regular, PdfFont bold, InterventionResponseDto dto) {
        document.add(new Paragraph("\nConclusion")
                .setFont(bold)
                .setFontSize(14)
                .setMarginTop(20));

        document.add(new Paragraph("Ce certificat confirme la conformité des étalonnages réalisés pour l'instrument indiqué. "
                + "Toute anomalie ou remarque doit être signalée au laboratoire dans les plus brefs délais.")
                .setFont(regular)
                .setFontSize(12));
    }

    private void addFooter(Document document, PdfFont regular) {
        document.add(new Paragraph("\n© 2025 Laboratoire de Métrologie")
                .setFont(regular)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(40));
    }
    


}
