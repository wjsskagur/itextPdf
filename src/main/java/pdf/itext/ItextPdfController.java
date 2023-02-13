package pdf.itext;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;
import java.util.Random;

@Controller
@RequiredArgsConstructor
public class ItextPdfController {
    @Autowired
    private final ItextPdfUtil itextPdfUtil;

    @RequestMapping("/attachment/pdf")
    public void pdfDownload(HttpServletResponse response) {

        // 미리 준비한 DTO 선언
        ItextPdfDto itextPdfDto = new ItextPdfDto();
        // pdf 파일이 저장될 경로 ( Mac 기준 )
        itextPdfDto.setPdfFilePath("/Users/jeon/Documents/JavaProject/Blog/pdf/");

        // pdf 파일이 저장될 경로 ( Windows 기준 )
        // itextPdfDto.setPdfFilePath("C:\\Users\\hyeok\\Desktop\\pdf");

        // pdf 파일명 ( 테스트를 위해 랜덤으로 생성 )
        itextPdfDto.setPdfFileName(new Random().nextInt() + ".pdf");
        // itextPdfDto.setPdfFileName("test.pdf");

        // getHtml 에서 호출될 코드명
        itextPdfDto.setPdfCode("hyeok");

        // ======================= PDF 존재 유무 체크 =======================
        // 없다면 PDF 파일 만들기
        File file = itextPdfUtil.checkPDF(itextPdfDto);
        int fileSize = (int) file.length();
        // ===============================================================


        // ===============================================================
        // 파일 다운로드를 위한 header 설정
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename="+itextPdfDto.getPdfFileName()+";");
        response.setContentLengthLong(fileSize);
        response.setStatus(HttpServletResponse.SC_OK);
        // ===============================================================

        // 파일 다운로드
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        // PDF 파일을 버퍼에 담은 후 다운로드
        try{
            in = new BufferedInputStream(new FileInputStream(file));
            out = new BufferedOutputStream(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            byte[] buffer = new byte[4096];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                Objects.requireNonNull(out).flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
