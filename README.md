unformatter
====

Java code to parse an input CharSequence, controlled by a format string from a [java.util.Formatter.](https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html)

Similar in concept to a "scanf", but the format String syntax is different.  **This code is mainly intended to be used with fixed records.**

### Usage

Say you created a String with the following code

    String format = "This is my data: %5d%7x%7o%4s%4.2f values in %%";
    String result = String.format(format, 1234, 0x1234, 01234, "1234", 1234.56);
    
    result = "This is my data:  1234   1234   123412341234.56 values in %"

To read back the information:

    Unformatter unf = new Unformatter(format);
    List<Object> scanned = unf.unformat(result);

scanned will contain the following:

 0. Long 1234
 1. Long 4660 (same as hex  0x1234)
 2. Long 668  (same as octal 01234)
 3. String "1234"
 4. Double 1234.56
 5. CharSequence ""

All parsed values are returned as Longs, Doubles, or Strings as appropriate.  

The last element (5 in the above example) represents any remaining data in the input that was not parsed.  This is useful if you are reading from a CharSequence containing multiple records.

### Supported Format Conversions

**Note:** Since this if for reading fixed formats, all conversions should include a width!

 * d decimal, returned as a Long
 * f floating point, returned as a Double
 * h hex, returned as a Long
 * o octal, returned as a Long
 * s string, returned as a String
 * % not really a conversion, but "%%" in the format is converted to fixed text "%"
 * n not really a conversion, but "%n" in the format is converted to a fixed text newline

### Unsupported Stuff
 
 * any conversions not noted above, including uppercase
 * argument indexing
 * flags  (sorry, no locale-specific grouping for now)

 
 
### API   
  For more, consult the code or the [zipped javadocs](https://github.com/MorganConrad/unformatter/blob/master/javadocs.zip)

#### Unformatter

##### Unformatter(String format)                    
 * constructor, taking a format string
  
##### List unformat(CharSequence input)     
 * parses input, 
 * returns a List contain Longs, Doubles, or Strings for the data.
 * the last element in the List is a CharSequence of any reminaing input.

    
