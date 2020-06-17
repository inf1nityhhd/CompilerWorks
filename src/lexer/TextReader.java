package lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextReader {
    private String content = "";
    private File file;
    public static Pattern SPLITER_PATTERN = Pattern.compile("\\s*|\t|\r|\n");

    public TextReader(File file) {
        this.file = file;
    }

    public String read() {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuffer sb = new StringBuffer();
            String temp = "";
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
            br.close();
            content = sb.toString();
            cleanContent();
        } catch (Exception e) {
            System.out.println("指定路径的文件不存在！");
        }
        return content;
    }

    public void cleanContent() {
        Matcher m = SPLITER_PATTERN.matcher(content);
        content = m.replaceAll("");
    }

    public String cleanContent(String text) {
        Matcher m = SPLITER_PATTERN.matcher(text);
        return m.replaceAll("");
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
