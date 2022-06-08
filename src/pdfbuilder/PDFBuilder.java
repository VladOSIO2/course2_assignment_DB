package pdfbuilder;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PDFBuilder {
    private static final String FONT_REGULAR = "src\\utility\\VelaSans-Regular.ttf";
    private static final String FONT_BOLD = "src\\utility\\VelaSans-Bold.ttf";

    static {
        FontFactory.register(FONT_REGULAR);
        FontFactory.register(FONT_BOLD);
    }

    private static final Font TITLE_FONT = FontFactory.getFont(FONT_BOLD, "Cp1251", true, 14);
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FONT_BOLD, "Cp1251", true, 10);
    private static final Font TABLE_FONT = FontFactory.getFont(FONT_REGULAR, "Cp1251", true, 10);

    public static String build(
            String filename, String title,
            List<String> header, List<String> values,
            int[] relativeWidths
    ) {

        String datetime = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String fileFixedName = filename.replaceAll("\\s+", "_").replaceAll(":;", "")
                + "_" + datetime + ".pdf";
        File file = new File(fileFixedName);

        try {
//            creating document
            boolean isCreated = file.createNewFile();
            if (!isCreated) {
                return "Файл " + fileFixedName + " не створено: файл вже існує";
            }
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));

//            filling document
            document.open();
            document.add(pdfTitle(title));
            document.add(pdfTitle(" "));
            PdfPTable table = new PdfPTable(header.size());
            fillTableHeader(table, header);
            fillTableValues(table, values);
            table.setWidths(relativeWidths);
            document.add(table);
            document.close();
        } catch (DocumentException | IOException e) {
            return "Файл не створено:\n" + e.getMessage();
        }

        return "Файл " + fileFixedName + " створено: " + file.getAbsolutePath();
    }

    private static Paragraph pdfTitle(String s) {
        Paragraph p = new Paragraph();
        p.setFont(TITLE_FONT);
        p.add(s);
        return p;
    }

    private static void fillTableHeader(PdfPTable table, List<String> header) {
        for (String column : header) {
            Phrase phrase = new Phrase();
            phrase.setFont(TABLE_HEADER_FONT);
            phrase.add(column);
            PdfPCell cell = new PdfPCell();
            cell.setPhrase(phrase);
            table.addCell(cell);
        }
    }

    private static void fillTableValues(PdfPTable table, List<String> values) {
        for (String value : values) {
            Phrase phrase = new Phrase();
            phrase.setFont(TABLE_FONT);
            phrase.add(value);
            table.addCell(phrase);
        }
    }
}
