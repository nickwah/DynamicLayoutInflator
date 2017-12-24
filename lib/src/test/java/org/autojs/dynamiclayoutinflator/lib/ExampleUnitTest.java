package org.autojs.dynamiclayoutinflator.lib;

import org.junit.Test;

import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\Stardust\\Desktop\\1.txt");
        byte[] bytes = new byte[fileInputStream.available()];
        fileInputStream.read(bytes);
        String str = new String(bytes);
        Matcher matcher = Pattern.compile("android:(\\w+)").matcher(str);
        StringBuffer output = new StringBuffer();
        while (matcher.find()) {
            String p = matcher.group(1);
            output.append("case \"").append(p).append("\":\nview.set")
                    .append(up(p)).append("(Boolean.valueOf(value));\n");
        }
        System.out.println(output);
    }

    private String up(String group) {
        return group.substring(0, 1).toUpperCase() + group.substring(1);
    }

}