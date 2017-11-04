package com.nickandjerry.dynamiclayoutinflator.lib;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

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
        Matcher matcher = Pattern.compile("case \"(\\w+)\":").matcher(str);
        StringBuffer output = new StringBuffer();
        while (matcher.find()) {
            String rep =
                    String.format("%s\nview.set%s(value);",
                            matcher.group(), up(matcher.group(1)));
            matcher.appendReplacement(output, rep);
        }
        matcher.appendTail(output);
        System.out.println(output);
    }

    private String up(String group) {
        return group.substring(0, 1).toUpperCase() + group.substring(1);
    }

}