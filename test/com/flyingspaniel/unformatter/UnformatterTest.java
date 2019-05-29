package com.flyingspaniel.unformatter;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UnformatterTest {

  @Test
  public void testHappyPath() {

    String format = "This is my data: %5d%7x%7o%4s%4.2f values in %%";
    String formatted = String.format(format, 1234, 0x1234, 01234, "1234", 1234.56 );
    Unformatter unf = new Unformatter(format);
    List<Object> scanned = unf.unformat(formatted  + " extraAtEnd");

    int idx = 0;
    assertEquals(1234L, ((Long)scanned.get(idx++)).longValue());
    assertEquals(Long.valueOf(0x1234), scanned.get(idx++));
    assertEquals(Long.valueOf(01234), scanned.get(idx++));
    assertEquals("1234", scanned.get(idx++));
    assertEquals(Double.valueOf(1234.56), scanned.get(idx++));
    assertEquals(" extraAtEnd", scanned.get(idx++));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalConversion() {
    Unformatter unf = new Unformatter("%5e");
  }


  @Test
  public void testNoConversionsAndEscapes() {
    String formatJustStaticText = "just %%static %ntext";
    Unformatter unf = new Unformatter(formatJustStaticText);
    assertEquals("just %static \ntext", unf.trailingFixedText);
    List<Object> scanned = unf.unformat("just %static \ntexthere is more");
    assertEquals("[here is more]", scanned.toString());
  }

  @Test(expected = IllegalStateException.class)
  public void testVerifyFixedText() {
    String format = "static text%5d";
    Unformatter unf = new Unformatter(format);
    unf.unformat("STATIC TEXT12345");
  }

}
