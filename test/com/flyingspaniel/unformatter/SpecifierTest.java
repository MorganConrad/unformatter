package com.flyingspaniel.unformatter;

import org.junit.*;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class SpecifierTest {

  @Test
  public void testHappyPath() {
    Specifier s = Specifier.next("%0#5.2fxxx");
    assertEquals(8, s.getWidth());
    assertEquals("lead='' flags='0#' width=8 conversion=f", s.toString());
    assertEquals("xxx", s.remainder);

    s = Specifier.next("%.5d%07x%07o%4s more static text");
    assertEquals("lead='' flags='' width=6 conversion=d", s.toString());

    s = Specifier.next("lead%05d%more static text");
    assertEquals("lead='lead' flags='0' width=5 conversion=d", s.toString());

    s = Specifier.next("%%more text");
    assertNull(s);

    s = Specifier.next("%%text%5d");
    assertEquals("lead='%text' flags='' width=5 conversion=d", s.toString());
  }
}
