package com.gsvn.hrmservice.common.util;

import org.docx4j.Docx4J;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import com.gsvn.hrmservice.model.dto.PayrollReportItem;
import com.gsvn.hrmservice.model.dto.response.PayrollReportResponse;
import com.gsvn.hrmservice.model.entity.LeaveRequest;
import com.gsvn.hrmservice.model.entity.Payroll;
import com.gsvn.hrmservice.model.enums.LeaveType;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class WordGeneratorUtil {
    public byte[] createLeaveRequestDoc(LeaveRequest lr) {
        DateTimeFormatter dmyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean isResignation = LeaveType.RESIGNATION.equals(lr.getLeaveType());

        try (XWPFDocument document = new XWPFDocument()) {
            // --- 1. Cấu hình trang ---
            CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
            CTPageMar pageMar = sectPr.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(1440L));
            pageMar.setRight(BigInteger.valueOf(1440L));
            pageMar.setTop(BigInteger.valueOf(1440L));
            pageMar.setBottom(BigInteger.valueOf(1440L));

            // --- 2. Quốc hiệu ---
            XWPFParagraph header = document.createParagraph();
            header.setAlignment(ParagraphAlignment.CENTER);
            addText(header, "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", 13, true);
            header.createRun().addBreak();
            addText(header, "Độc lập - Tự do - Hạnh phúc", 14, true);
            header.createRun().addBreak();
            addText(header, "---------------", 12, false);

            // --- 3. Ngày tháng năm tạo đơn (Căn lề phải, nằm trên tiêu đề) ---
            XWPFParagraph topDate = document.createParagraph();
            topDate.setAlignment(ParagraphAlignment.RIGHT);
            topDate.setSpacingBefore(200);
            XWPFRun topDateRun = topDate.createRun();
            topDateRun.setFontFamily("Times New Roman");
            topDateRun.setFontSize(12);
            topDateRun.setItalic(true);
            OffsetDateTime createdAt = lr.getCreatedAt() != null ? lr.getCreatedAt() : OffsetDateTime.now();
            topDateRun.setText("Ngày " + createdAt.getDayOfMonth() +
                    " tháng " + createdAt.getMonthValue() +
                    " năm " + createdAt.getYear());

            // --- 4. Tiêu đề ---
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            title.setSpacingBefore(100); // Giảm bớt khoảng cách vì đã có dòng ngày tháng phía trên
            String titleText = isResignation ? "ĐƠN XIN THÔI VIỆC" : "ĐƠN XIN NGHỈ PHÉP";
            addText(title, titleText, 16, true);

            // --- 5. Nội dung chính ---
            XWPFParagraph body = document.createParagraph();
            body.setSpacingBefore(300);
            body.setSpacingAfter(200);

            XWPFRun bodyRun = body.createRun();
            bodyRun.setFontFamily("Times New Roman");
            bodyRun.setFontSize(13);

            bodyRun.setText("Kính gửi: Ban Giám đốc và Phòng Nhân sự công ty");
            bodyRun.addBreak();
            bodyRun.setText("Tôi tên là: " + (lr.getStaffName() != null ? lr.getStaffName() : "...................."));
            bodyRun.addBreak();
            bodyRun.setText("Mã nhân viên: " + (lr.getStaffId() != null ? lr.getStaffId() : "...................."));
            bodyRun.addBreak();
            bodyRun.setText("Lý do: " + (lr.getReason() != null ? lr.getReason() : "...................."));
            bodyRun.addBreak();

            if (isResignation) {
                String effDate = lr.getEffectiveDate() != null ? lr.getEffectiveDate().format(dmyFormatter) : "..../..../....";
                bodyRun.setText("Ngày chính thức thôi việc: " + effDate);
            } else {
                String from = lr.getStartDate() != null ? lr.getStartDate().format(dmyFormatter) : "..../..../....";
                String to = lr.getEndDate() != null ? lr.getEndDate().format(dmyFormatter) : "..../..../....";
                bodyRun.setText("Thời gian nghỉ: Từ ngày " + from + " đến ngày " + to);
            }
            bodyRun.addBreak();
            bodyRun.setText("Rất mong nhận được sự xem xét và phê duyệt của Ban lãnh đạo.");
            bodyRun.addBreak();

            // --- 6. Phần Chữ ký & Ý kiến ---
            XWPFTable footerTable = document.createTable(1, 2);
            footerTable.setWidth("100%");
            footerTable.getCTTbl().getTblPr().unsetTblBorders();

            // --- Trái: Ý kiến ban quản lý ---
            XWPFTableCell leftCell = footerTable.getRow(0).getCell(0);
            XWPFParagraph pLeft = leftCell.getParagraphs().get(0);
            pLeft.setAlignment(ParagraphAlignment.CENTER);
            // Để trống 1 dòng cho cân với bên ngày tháng của người làm đơn
            pLeft.createRun().setText("");
            pLeft.createRun().addBreak();
            addText(pLeft, "Ý KIẾN BAN QUẢN LÝ", 12, true);
            pLeft.createRun().addBreak();

            // Trạng thái (Dịch & Màu)
            XWPFRun statusRun = pLeft.createRun();
            statusRun.setFontFamily("Times New Roman");
            statusRun.setFontSize(12);
            statusRun.setItalic(true);
            statusRun.setBold(true);

            String statusViet = "CHƯA DUYỆT";
            String colorHex = "000000";
            if (lr.getStatus() != null) {
                switch (lr.getStatus()) {
                    case APPROVED: statusViet = "ĐÃ DUYỆT"; colorHex = "008000"; break;
                    case REJECTED: statusViet = "TỪ CHỐI"; colorHex = "FF0000"; break;
                    case PENDING: statusViet = "ĐANG CHỜ"; colorHex = "FFA500"; break;
                }
            }
            statusRun.setText(statusViet);
            statusRun.setColor(colorHex);

            for(int i=0; i<3; i++) pLeft.createRun().addBreak();
            addText(pLeft, lr.getApprovedName() != null ? lr.getApprovedName() : "....................", 12, true);

            // --- Phải: Người làm đơn ---
            XWPFTableCell rightCell = footerTable.getRow(0).getCell(1);
            XWPFParagraph pRight = rightCell.getParagraphs().get(0);
            pRight.setAlignment(ParagraphAlignment.CENTER);

            // Dòng ngày tháng tại phần chữ ký (giữ lại hoặc bỏ tùy ý, thường đơn VN có 2 chỗ ghi ngày)
            XWPFRun footerDateRun = pRight.createRun();
            footerDateRun.setFontFamily("Times New Roman");
            footerDateRun.setFontSize(11);
            footerDateRun.setItalic(true);
            footerDateRun.setText("Ngày " + createdAt.getDayOfMonth() + " tháng " + createdAt.getMonthValue() + " năm " + createdAt.getYear());
            footerDateRun.addBreak();

            addText(pRight, "NGƯỜI LÀM ĐƠN", 12, true);
            for(int i=0; i<4; i++) pRight.createRun().addBreak();
            addText(pRight, lr.getStaffName() != null ? lr.getStaffName() : "....................", 12, true);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo file Word: " + e.getMessage());
        }
    }
    public byte[] convertDocxToPdf(byte[] docxBytes) {
        try (ByteArrayInputStream docxStream = new ByteArrayInputStream(docxBytes);
             ByteArrayOutputStream pdfStream = new ByteArrayOutputStream()) {

            WordprocessingMLPackage wordMLPackage = Docx4J.load(docxStream);

            Mapper fontMapper = new IdentityPlusMapper();
            wordMLPackage.setFontMapper(fontMapper);

            String fontPath = "/usr/share/fonts/truetype/times.ttf";

            PhysicalFont timenew = PhysicalFonts.get("Times New Roman");
            if (timenew == null) {

                PhysicalFonts.discoverPhysicalFonts();

            }

            fontMapper.put("Times New Roman", PhysicalFonts.get("Times New Roman"));
            // --------------------------------------

            Docx4J.toPDF(wordMLPackage, pdfStream);

            return pdfStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi chuyển đổi Word sang PDF: " + e.getMessage(), e);
        }
    }
    private void addText(XWPFParagraph paragraph, String text, int size, boolean isBold) {
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontFamily("Times New Roman");
        run.setFontSize(size);
        run.setBold(isBold);
    }

    public byte[] createPayrollDoc(Payroll payroll) {
        Map<String, Object> data = new HashMap<>();

        data.put("salaryPeriod", payroll.getSalaryPeriod());
        data.put("fullName",payroll.getStaffName());
        data.put("staffId", payroll.getStaffId());
        data.put("positionName", payroll.getPositionName());
        data.put("status", payroll.getStatus());

        data.put("baseSalary", payroll.getBaseSalary());
        data.put("workingDays", payroll.getWorkingDays());
        data.put("totalBonus", payroll.getTotalBonus());
        data.put("totalDeduction", payroll.getTotalDeduction());
        data.put("finalSalary", payroll.getFinalSalary());
        data.put("note", payroll.getNote());

        data.put("approverName", payroll.getApprovedName());
        data.put("printDate", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        try (XWPFDocument document = new XWPFDocument()) {
            DecimalFormat df = new DecimalFormat("#,###");

            // 1. Tiêu đề đơn giản
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun rTitle = title.createRun();
            rTitle.setBold(true);
            rTitle.setFontSize(16);
            rTitle.setFontFamily("Times New Roman");
            rTitle.setText("PHIẾU THANH TOÁN LƯƠNG");
            rTitle.addBreak();

            XWPFRun rPeriod = title.createRun();
            rPeriod.setFontSize(12);
            rPeriod.setFontFamily("Times New Roman");
            rPeriod.setText("Kỳ lương: " + data.getOrDefault("salaryPeriod", ""));

            // 2. Thông tin nhân viên (Viết dạng dòng cho đơn giản)
            XWPFParagraph info = document.createParagraph();
            info.setSpacingBefore(200);
            XWPFRun rInfo = info.createRun();
            rInfo.setFontFamily("Times New Roman");
            rInfo.setText("Nhân viên: " + data.get("fullName") + " - Mã NV: " + data.get("staffId"));
            rInfo.addBreak();
            rInfo.setText("Chức vụ: " + data.get("positionName"));

            // 3. Bảng lương
            XWPFTable table = document.createTable(5, 2); // 5 hàng, 2 cột
            table.setWidth("100%");

            // Dòng 1: Lương cơ bản
            table.getRow(0).getCell(0).setText("Lương cơ bản");
            table.getRow(0).getCell(1).setText(df.format(Double.parseDouble(data.get("baseSalary").toString())) + " VNĐ");

            // Dòng 2: Ngày công
            table.getRow(1).getCell(0).setText("Ngày công thực tế");
            table.getRow(1).getCell(1).setText(data.get("workingDays") + " ngày");

            // Dòng 3: Thưởng
            table.getRow(2).getCell(0).setText("Thưởng");
            table.getRow(2).getCell(1).setText(df.format(Double.parseDouble(data.get("totalBonus").toString())) + " VNĐ");

            // Dòng 4: Khấu trừ
            table.getRow(3).getCell(0).setText("Khấu trừ");
            table.getRow(3).getCell(1).setText("-" + df.format(Double.parseDouble(data.get("totalDeduction").toString())) + " VNĐ");

            // Dòng 5: Thực lĩnh
            XWPFTableRow lastRow = table.getRow(4);
            XWPFRun rTotalLabel = lastRow.getCell(0).getParagraphs().get(0).createRun();
            rTotalLabel.setBold(true);
            rTotalLabel.setText("THỰC LĨNH");

            XWPFRun rTotalVal = lastRow.getCell(1).getParagraphs().get(0).createRun();
            rTotalVal.setBold(true);
            rTotalVal.setText(df.format(Double.parseDouble(data.get("finalSalary").toString())) + " VNĐ");


            XWPFParagraph moneyTextPara = document.createParagraph();
            moneyTextPara.setSpacingBefore(100); // Khoảng cách nhỏ với bảng phía trên
            moneyTextPara.setIndentationLeft(200); // Lùi đầu dòng một chút cho đẹp

            XWPFRun rMoneyText = moneyTextPara.createRun();
            rMoneyText.setFontFamily("Times New Roman");
            rMoneyText.setItalic(true); // In nghiêng theo quy tắc kế toán
            rMoneyText.setFontSize(12);


            BigDecimal finalAmount = new BigDecimal(data.get("finalSalary").toString());
            String amountInWords = MoneyToVietnamese.toVietnamese(finalAmount);

            rMoneyText.setText("Bằng chữ: " + amountInWords);

            XWPFParagraph datePara = document.createParagraph();
            datePara.setAlignment(ParagraphAlignment.RIGHT);
            XWPFRun rDate = datePara.createRun();
            rDate.setFontFamily("Times New Roman");
            rDate.setItalic(true);
            rDate.setFontSize(11);
            rDate.setText("Ngày in: " + data.getOrDefault("printDate", ""));
            // --- 4. Bảng chữ ký 3 cột (Người duyệt | Người nhận | Phòng nhân sự) ---
            XWPFTable footerTable = document.createTable(1, 3);
            footerTable.setWidth("100%");
            footerTable.getCTTbl().getTblPr().unsetTblBorders(); // Ẩn viền bảng

            // CỘT 1: NGƯỜI PHÊ DUYỆT
            XWPFTableCell cell1 = footerTable.getRow(0).getCell(0);
            XWPFParagraph p1 = cell1.getParagraphs().get(0);
            p1.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun r1 = p1.createRun();
            r1.setFontFamily("Times New Roman");
            r1.setBold(true);
            r1.setText("NGƯỜI PHÊ DUYỆT");
            for(int i=0; i<4; i++) p1.createRun().addBreak();
            XWPFRun r1Name = p1.createRun();
            r1Name.setFontFamily("Times New Roman");
            r1Name.setBold(true);
            r1Name.setText((String) data.getOrDefault("approverName", "...................."));

            // CỘT 2: NGƯỜI NHẬN TIỀN
            XWPFTableCell cell2 = footerTable.getRow(0).getCell(1);
            XWPFParagraph p2 = cell2.getParagraphs().get(0);
            p2.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun r2 = p2.createRun();
            r2.setFontFamily("Times New Roman");
            r2.setBold(true);
            r2.setText("NGƯỜI NHẬN TIỀN");
            for(int i=0; i<4; i++) p2.createRun().addBreak();
            XWPFRun r2Name = p2.createRun();
            r2Name.setFontFamily("Times New Roman");
            r2Name.setBold(true);
            r2Name.setText((String) data.getOrDefault("fullName", "...................."));

            // CỘT 3: PHÒNG NHÂN SỰ
            XWPFTableCell cell3 = footerTable.getRow(0).getCell(2);
            XWPFParagraph p3 = cell3.getParagraphs().get(0);
            p3.setAlignment(ParagraphAlignment.CENTER);



            XWPFRun r3 = p3.createRun();
            r3.setFontFamily("Times New Roman");
            r3.setBold(true);
            r3.setText("PHÒNG NHÂN SỰ");
            for(int i=0; i<4; i++) p3.createRun().addBreak();


            // Ghi ra mảng byte
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo Word: " + e.getMessage());
        }
    }
    public byte[] createPayrollReportDoc(PayrollReportResponse report) {
        try (XWPFDocument document = new XWPFDocument()) {

            CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz pageSize = sectPr.addNewPgSz();
            pageSize.setW(java.math.BigInteger.valueOf(15840L));
            pageSize.setH(java.math.BigInteger.valueOf(12240L));
            pageSize.setOrient(org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation.LANDSCAPE);

            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar pageMar = sectPr.addNewPgMar();
            pageMar.setLeft(java.math.BigInteger.valueOf(720L));
            pageMar.setRight(java.math.BigInteger.valueOf(720L));
            pageMar.setTop(java.math.BigInteger.valueOf(720L));
            pageMar.setBottom(java.math.BigInteger.valueOf(720L));

            // --- 2. XỬ LÝ TIÊU ĐỀ (Đổi YYYY-MM sang MM/YYYY) ---
            String period = report.getReportPeriod(); // Giả sử service trả về "Bảng lương chi tiết tháng 2026-05"
            String titleText = period.toUpperCase();

            if (period.contains("-")) {
                // Tìm chuỗi dạng xxxx-xx (ví dụ 2026-05)
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d{4})-(\\d{2})");
                java.util.regex.Matcher matcher = pattern.matcher(period);
                if (matcher.find()) {
                    String year = matcher.group(1);
                    String month = matcher.group(2);
                    String newDate = month + "/" + year;
                    titleText = period.replace(matcher.group(0), newDate).toUpperCase();
                }
            }

            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText(titleText);
            titleRun.setBold(true);
            titleRun.setFontFamily("Times New Roman");
            titleRun.setFontSize(16);

            XWPFTable table = document.createTable(1, 7);
            table.setWidth("100%");

            String[] headers = {"STT", "Mã NV", "Họ tên", "Lương cứng", "Thưởng", "Khấu trừ", "Thực lĩnh"};
            XWPFTableRow headerRow = table.getRow(0);
            for (int i = 0; i < headers.length; i++) {
                XWPFTableCell cell = headerRow.getCell(i);
                cell.setColor("EFEFEF");
                XWPFParagraph p = cell.getParagraphs().get(0);
                p.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun r = p.createRun();
                r.setText(headers[i]);
                r.setBold(true);
                r.setFontFamily("Times New Roman");
            }

            java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");

            for (PayrollReportItem item : report.getItems()) {
                XWPFTableRow row = table.createRow();

                // STT
                XWPFParagraph p0 = row.getCell(0).getParagraphs().get(0);
                p0.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun r0 = p0.createRun();
                r0.setText(String.valueOf(item.getStt()));
                r0.setFontFamily("Times New Roman");

                // Mã NV
                XWPFParagraph p1 = row.getCell(1).getParagraphs().get(0);
                p1.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun r1 = p1.createRun();
                r1.setText(String.valueOf(item.getStaffId()));
                r1.setFontFamily("Times New Roman");

                // Họ tên
                XWPFParagraph p2 = row.getCell(2).getParagraphs().get(0);
                XWPFRun r2 = p2.createRun();
                r2.setText(item.getStaffName());
                r2.setFontFamily("Times New Roman");

                // Lương (Phải)
                XWPFParagraph p3 = row.getCell(3).getParagraphs().get(0);
                p3.setAlignment(ParagraphAlignment.RIGHT);
                XWPFRun r3 = p3.createRun();
                r3.setText(df.format(item.getBaseSalary()));
                r3.setFontFamily("Times New Roman");

                // Thưởng (Phải)
                XWPFParagraph p4 = row.getCell(4).getParagraphs().get(0);
                p4.setAlignment(ParagraphAlignment.RIGHT);
                XWPFRun r4 = p4.createRun();
                r4.setText(df.format(item.getTotalBonus()));
                r4.setFontFamily("Times New Roman");

                // Khấu trừ (Phải)
                XWPFParagraph p5 = row.getCell(5).getParagraphs().get(0);
                p5.setAlignment(ParagraphAlignment.RIGHT);
                XWPFRun r5 = p5.createRun();
                r5.setText(df.format(item.getTotalDeduction()));
                r5.setFontFamily("Times New Roman");

                // Thực lĩnh (Phải)
                XWPFParagraph p6 = row.getCell(6).getParagraphs().get(0);
                p6.setAlignment(ParagraphAlignment.RIGHT);
                XWPFRun r6 = p6.createRun();
                r6.setText(df.format(item.getFinalSalary()));
                r6.setFontFamily("Times New Roman");
            }

            // --- 5. DÒNG TỔNG CỘNG ---
            XWPFTableRow footerRow = table.createRow();
            XWPFTableCell footerTitleCell = footerRow.getCell(2);
            XWPFParagraph pFooterTitle = footerTitleCell.getParagraphs().get(0);
            XWPFRun rFooterTitle = pFooterTitle.createRun();
            rFooterTitle.setText("TỔNG CỘNG");
            rFooterTitle.setBold(true);
            rFooterTitle.setFontFamily("Times New Roman");

            PayrollReportItem sum = report.getSummary();
            java.math.BigDecimal[] totals = {
                    sum.getBaseSalary(), sum.getTotalBonus(), sum.getTotalDeduction(), sum.getFinalSalary()
            };

            for (int i = 0; i < totals.length; i++) {
                XWPFTableCell cell = footerRow.getCell(i + 3);
                XWPFParagraph p = cell.getParagraphs().get(0);
                p.setAlignment(ParagraphAlignment.RIGHT);
                XWPFRun r = p.createRun();
                r.setText(df.format(totals[i]));
                r.setBold(true);
                r.setFontFamily("Times New Roman");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo báo cáo PDF: " + e.getMessage());
        }
    }
}