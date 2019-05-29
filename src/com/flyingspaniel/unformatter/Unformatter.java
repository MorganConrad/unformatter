package com.flyingspaniel.unformatter;

import java.util.ArrayList;
import java.util.List;


/**
 * Class to parse input based upon a java.util.Formatter format String
 */
public class Unformatter {

  public final String urFormat;  // for debugging
  protected final boolean verifyFixedText;
  protected final ArrayList<Parser> parsers;
  protected final String trailingFixedText;

  /**
   * Constructor, passed a format string as used by Formatter
   * @param format  as used by a Formatter, e.g. "Duke's Age: %d"
   * @param verifyFixedText  if true, fixed strings (e.g. "Duke's Age: ") will be verified vs. the input.
   */
  public Unformatter(String format, boolean verifyFixedText) {
    this.urFormat = format;
    this.verifyFixedText = verifyFixedText;
    this.parsers = new ArrayList();

    Specifier nextSpec = Specifier.next(format);
    while (nextSpec != null) {
      parsers.add(specToParser(nextSpec));
      format = nextSpec.getRemainder();
      nextSpec = Specifier.next(format);
    }

    trailingFixedText = escapePercentSignsAndNewlines(format);
  }


  /**
   * Common Constructor with verifyFixedText = true
   * @param format as used by a Formatter, e.g. "Duke's Age: %d"
   */
  public Unformatter(String format) {
    this(format, true);
  }


  /**
   * Scan the input
   * @param input the text to be scanned
   * @return List containing Strings, Doubles, or Longs.  Fixed text is included.
   *         The final element is a String containing any remaining input that was not processed.
   */
  public List<Object> unformat(final CharSequence input) {
    CharSequence remainingInput = input;
    ArrayList<Object> result = new ArrayList<>(parsers.size());

    for (Parser p : parsers) {
      result.add(p.parse(remainingInput));
      remainingInput = p.getRemainingInput();
    }
    remainingInput = Parser.skipFixedText(this.trailingFixedText, remainingInput, this.verifyFixedText);

    result.add(remainingInput);
    return result;
  }


  Parser specToParser(Specifier spec) {
    switch(spec.getConversion()) {
      case "d" : return new Parser._Long(spec, verifyFixedText, 10);
      case "f" : return new Parser._Double(spec, verifyFixedText);
      case "o" : return new Parser._Long(spec, verifyFixedText, 8);
      case "s" : return new Parser._String(spec, verifyFixedText);
      case "x" : return new Parser._Long(spec, verifyFixedText, 16);
      default  : throw new IllegalArgumentException(spec.toString());
    }
  }

  static String escapePercentSignsAndNewlines(String input) {
    return input.replaceAll("\\%n", "\n")
                 .replaceAll("\\%\\%", "%");

  }

}
