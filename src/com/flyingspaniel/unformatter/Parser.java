package com.flyingspaniel.unformatter;

import static com.flyingspaniel.unformatter.Specifier.robustParseLong;

/**
 * Classes for parsing input based upon a {@link com.flyingspaniel.unformatter.Specifier}
 *
 * @param <T>  type returned from parse, e.g. Double, String, Long
 */
public abstract class Parser<T> {

  protected final boolean verifyFixedText;

  protected final Specifier spec;
  protected CharSequence remainingInput;

  /**
   * Parse the input, returning a T
   * @param input  to be parsed
   * @return T
   */
  abstract public T parse(CharSequence input);

  /**
   * Constructor
   * @param spec  our Specifier, may be null for a fixed string
   * @param verifyFixedText if false, we don't confirm input of fixed text.
   */
  protected Parser(Specifier spec, boolean verifyFixedText) {
    this.spec = spec;
    this.verifyFixedText = verifyFixedText;
  }

  /**
   * Return any remaining input leftover after we parse it.
   * @return  CharSequence, non-null, may be empty
   */
  public CharSequence getRemainingInput() { return remainingInput; }


  public static CharSequence skipFixedText(String expect, CharSequence input, boolean verify) {
    if (expect.isEmpty())
      return input;

    String actualFixedText = input.subSequence(0, expect.length()).toString();
    if (verify && !expect.equals(actualFixedText)) {
      throw new IllegalStateException("expected " + expect + " but got " + actualFixedText);
    }

    return input.subSequence(expect.length(), input.length());
  }


  protected CharSequence skipLeadingFixedText(CharSequence input) {
    return skipFixedText(spec.leadingFixedText, input, this.verifyFixedText);
  }

  /* inner subclasses follow */

  /**
   * Parses a double (f conversion)
   */
  public static class _Double extends Parser<Double> {

    _Double(Specifier spec, boolean verifyFixedText) {
      super(spec, verifyFixedText);
    }

    public Double parse(CharSequence input) {
      input = skipLeadingFixedText(input);
      String myInput = input.subSequence(0, spec.width).toString();
      remainingInput = input.subSequence(spec.width, input.length());
      return Double.parseDouble(myInput.trim());
    }
  }


  /**
   * Parses a decimal (d,o, or x conversion) to a Long
   */
  public static class _Long extends Parser<Long> {

    final int radix;

    _Long(Specifier spec, boolean verifyFixedText, int radix) {
      super(spec, verifyFixedText);
      this.radix = radix;
    }

    public Long parse(CharSequence input) {
      input = skipLeadingFixedText(input);
      String myInput = input.subSequence(0, spec.width).toString();
      remainingInput = input.subSequence(spec.width, input.length());
      return robustParseLong(myInput, radix);
    }
  }


  /**
   * Parses a String (s conversion)
   */
  public static class _String extends Parser<String> {
    _String(Specifier spec, boolean verifyFixedText) {
      super(spec, verifyFixedText );
    }

    public String parse(CharSequence input) {
      input = skipLeadingFixedText(input);
      String myInput = input.subSequence(0, spec.width).toString();
      remainingInput = input.subSequence(spec.width, input.length());
      return myInput;
    }
  }


}
