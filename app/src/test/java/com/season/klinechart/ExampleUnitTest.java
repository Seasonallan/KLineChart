package com.season.klinechart;

import org.junit.Test;

import java.text.DecimalFormat;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);

        DecimalFormat dfRMB = new DecimalFormat("#,#0.00");

        System.out.println(""+ dfRMB.format(2.002));

    }
}