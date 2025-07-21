package org.cyprus.util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts plain text to HTML for emails in ADempiere
 */
public class EmailHtmlConverter {

    /**
     * Convert plain text to HTML with basic formatting
     */
    public static String toHtml(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            return "";
        }

        // 1. Escape HTML special characters
        String html = plainText
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");

        // 2. Convert line breaks to <br>
        html = html.replace("\n", "<br>");

        // 3. Make URLs clickable (optional)
        html = linkifyUrls(html);

        // 4. Wrap in minimal HTML structure
        return "<html><body>" + html + "</body></html>";
    }

    /**
     * Convert raw URLs to clickable links
     */
    private static String linkifyUrls(String text) {
        Pattern urlPattern = Pattern.compile(
            "(https?://[\\w./?=#&+-]+)", 
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = urlPattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String url = matcher.group();
            matcher.appendReplacement(sb, "<a href=\"" + url + "\">" + url + "</a>");
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }
}
