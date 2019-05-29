package com.flyingspaniel.unformatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class representing a format specifier: %[argument_index$][flags][width][.precision]conversion
 *
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html">java.util.Formatter</a>
 *
 */
public class Specifier {

  static final String REGEX =
    "(?<!\\%)\\%([-\\#+ 0,(]*)([1-9]?)(\\.?[0-9]?)([dfosxbhcegat])";
  //    not%   %    flags       width     .width   conversion

  static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.DOTALL);


  public final Matcher matcher;
  public final int width;                // 0 flags no conversion
  public final String leadingFixedText;  // any fixed text preceeding the %
  public final String remainder;         // all text following the conversion character


  protected Specifier(Matcher matcher, String leadingFixedText, String remainder) {
    this.matcher = matcher;
    this.leadingFixedText = Unformatter.escapePercentSignsAndNewlines(leadingFixedText);
    this.remainder = remainder;
    this.width = calcWidth();
  }

  public static Specifier next(String format) {
    Matcher matcher = PATTERN.matcher(format);

    if (!matcher.find()) {  // end of the format, no "real" specifiers left
      return null;
    }

    else {
      String leadingFixedText = format.substring(0, matcher.start());
      String remainder = format.substring(matcher.end());
      return new Specifier(matcher, leadingFixedText, remainder);
    }

  }

  /**
   * Return flags, e.g. ",".  These are generally ignored
   * @return String, not-null, may be empty
   */
  public String getFlags() {
    return matcher.group(1);
  }

  /**
   * The width of the conversion. 0 means no conversion
   * @return int, character width to be read
   */
  public int getWidth() {
    return width;
  }

  /**
   * Type of conversion: one of dfosx (lowercase)
   * @return String, lowercase, one character long
   */
  public String getConversion() {
    return matcher.group(4);
  }

  /**
   * Return all of the incoming format string left over (after the conversion character)
   * @return String, not-null, may be empty
   */
  public String getRemainder() {
    return remainder;
  }


  @Override
  public String toString() {
    return "lead='" + leadingFixedText + "' flags='" + getFlags() + "' width=" + width + " conversion=" + getConversion();
  }



  public static long robustParseLong(String s, int radix) {
    s = s.trim();
    return s.isEmpty() ? 0L : Long.parseLong(s, radix);
  }

  public static int robustParseInt(String s) {
    return (int)robustParseLong(s, 10);
  }



  protected int calcWidth() {
    int width = robustParseInt(matcher.group(2));
    if (matcher.group(3).length() > 1)
      width += robustParseInt(matcher.group(3).substring(1)) + 1;  // +1 for the decimal point

    return width;
  }
}
