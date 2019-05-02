package main.java.com.github.mkale.tsa.ml.preprocessing;

import static org.apache.lucene.util.RamUsageEstimator.NUM_BYTES_CHAR; 

import java.io.IOException; 
import java.io.Reader; 
import java.io.Serializable; 
import java.io.StringReader; 
import java.util.ArrayList; 
import java.util.HashSet; 
import java.util.List; 
import java.util.Set; 
 
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory; 
import org.apache.lucene.analysis.Analyzer; 
import org.apache.lucene.analysis.CharArraySet; 
import org.apache.lucene.analysis.KeywordMarkerFilter; 
import org.apache.lucene.analysis.LowerCaseFilter; 
import org.apache.lucene.analysis.PorterStemFilter; 
import org.apache.lucene.analysis.StopFilter; 
import org.apache.lucene.analysis.StopwordAnalyzerBase; 
import org.apache.lucene.analysis.TokenFilter; 
import org.apache.lucene.analysis.TokenStream; 
import org.apache.lucene.analysis.Tokenizer; 
import org.apache.lucene.analysis.en.EnglishAnalyzer; 
import org.apache.lucene.analysis.en.EnglishPossessiveFilter; 
import org.apache.lucene.analysis.shingle.ShingleFilter; 
import org.apache.lucene.analysis.standard.StandardAnalyzer; 
import org.apache.lucene.analysis.standard.StandardFilter; 
import org.apache.lucene.analysis.standard.StandardTokenizer; 
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute; 
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute; 
import org.apache.lucene.util.ArrayUtil; 
import org.apache.lucene.util.Version; 
import org.tartarus.snowball.ext.PorterStemmer; 
 
/**
 *
 */ 
public class EnglishTokenizer implements Serializable, TextTokenizer { 
 
 private static final long serialVersionUID = -4307517944955017402L; 
 
 private static final Log LOGGER = LogFactory.getLog(EnglishTokenizer.class); 
 
 private final transient static Version LUCENE_VERSION = Version.LUCENE_36; 
 
 protected List<String> stopWords = null; 
 protected Set<String> stemExclusionsSet = new HashSet<String>(); 
 
 private boolean nGram = false; 
 private int minNGram = 2; 
 private int maxNGram = 2; 
 
 public EnglishTokenizer() { 
 } 
 
 public EnglishTokenizer(int minNGram, int maxNGram) { 
  this.nGram = true; 
  this.minNGram = minNGram; 
  this.maxNGram = maxNGram; 
 } 
 
 public EnglishTokenizer(List<String> stopWords) { 
  this.stopWords = stopWords; 
 } 
 
 public EnglishTokenizer(List<String> stopWords, Set<String> stemExclusionsSet) { 
  this.stopWords = stopWords; 
  this.stemExclusionsSet = stemExclusionsSet; 
 } 
 
 public List<String> tokenize(String text) { 
  List<String> words = new ArrayList<String>(); 
  if (text != null && !text.isEmpty()) { 
   TokenStream tokenStream = this.createTokenStream(text); 
   CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class); 
 
   try { 
    while (tokenStream.incrementToken()) { 
     String term = charTermAttribute.toString(); 
     words.add(term); 
    } 
   } catch (IOException ioe) { 
    LOGGER.error("Unable to analyze text. Cause : " + ioe.getMessage(), ioe); 
   } finally { 
    try { 
     tokenStream.end(); 
     tokenStream.close(); 
    } catch (IOException e) { 
     // Can't do nothing!! 
     LOGGER.error("Unable to close token stream : " + e.getMessage()); 
    } 
   } 
  } 
 
  return words; 
 } 
 
 protected TokenStream createTokenStream(String text) { 
  Set<?> luceneStopWords = this.stopWords == null ? EnglishAnalyzer.getDefaultStopSet() : StopFilter.makeStopSet(LUCENE_VERSION, stopWords); 
  Analyzer analyzer = new EnglishSpecialAnalyzer(LUCENE_VERSION, luceneStopWords, this.stemExclusionsSet); 
 
  TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(text)); 
  if (this.nGram) { 
   tokenStream = new ShingleFilter(tokenStream, this.minNGram, this.maxNGram); 
  } 
 
  return tokenStream; 
 } 
 
 public void setNGram(int minNGram, int maxNGram) { 
  this.nGram = true; 
  this.minNGram = minNGram; 
  this.maxNGram = maxNGram; 
 } 
 
 public boolean isnGram() { 
  return nGram; 
 } 
 
 public void setnGram(boolean nGram) { 
  this.nGram = nGram; 
 } 
 
 public int getMinNGram() { 
  return minNGram; 
 } 
 
 public void setMinNGram(int minNGram) { 
  this.minNGram = minNGram; 
 } 
 
 public int getMaxNGram() { 
  return maxNGram; 
 } 
 
 public void setMaxNGram(int maxNGram) { 
  this.maxNGram = maxNGram; 
 } 
 
 public List<String> getStopWords() { 
  return stopWords; 
 } 
 
 public void setStopWords(List<String> stopWords) { 
  this.stopWords = stopWords; 
 } 
 
 public void setStemExclusionsSet(Set<String> stemExclusionsSet) { 
  this.stemExclusionsSet = stemExclusionsSet; 
 } 
 
 public Set<String> getStemExclusionsSet() { 
  return stemExclusionsSet; 
 } 
 
 @SuppressWarnings("unused") 
 private static class EnglishSpecialAnalyzer extends StopwordAnalyzerBase { 
  private final Set<?> stemExclusionSet; 
 
  /**
   * Returns an unmodifiable instance of the default stop words set. 
   *  
   * @return default stop words set. 
   */ 
  public static Set<?> getDefaultStopSet() { 
   return DefaultSetHolder.DEFAULT_STOP_SET; 
  } 
 
  /**
   * Atomically loads the DEFAULT_STOP_SET in a lazy fashion once the 
   * outer class accesses the static final set the first time.; 
   */ 
  private static class DefaultSetHolder { 
   static final Set<?> DEFAULT_STOP_SET = StandardAnalyzer.STOP_WORDS_SET; 
  } 
 
  /**
   * Builds an analyzer with the default stop words: 
   * {@link #getDefaultStopSet}. 
   */ 
  public EnglishSpecialAnalyzer(Version matchVersion) { 
   this(matchVersion, DefaultSetHolder.DEFAULT_STOP_SET); 
  } 
 
  /**
   * Builds an analyzer with the given stop words. 
   *  
   * @param matchVersion 
   *            lucene compatibility version 
   * @param stopwords 
   *            a stopword set 
   */ 
  public EnglishSpecialAnalyzer(Version matchVersion, Set<?> stopwords) { 
   this(matchVersion, stopwords, CharArraySet.EMPTY_SET); 
  } 
 
  /**
   * Builds an analyzer with the given stop words. If a non-empty stem 
   * exclusion set is provided this analyzer will add a 
   * {@link KeywordMarkerFilter} before stemming. 
   *  
   * @param matchVersion 
   *            lucene compatibility version 
   * @param stopwords 
   *            a stopword set 
   * @param stemExclusionSet 
   *            a set of terms not to be stemmed 
   */ 
  public EnglishSpecialAnalyzer(Version matchVersion, Set<?> stopwords, Set<?> stemExclusionSet) { 
   super(matchVersion, stopwords); 
   this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(matchVersion, stemExclusionSet)); 
  } 
 
  /**
   * Creates a 
   * {@link org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents} 
   * which tokenizes all the text in the provided {@link Reader}. 
   *  
   * @return A 
   *         {@link org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents} 
   *         built from an {@link StandardTokenizer} filtered with 
   *         {@link StandardFilter}, {@link LowerCaseFilter}, 
   *         {@link StopFilter} , {@link KeywordMarkerFilter} if a stem 
   *         exclusion set is provided and {@link PorterStemFilter}. 
   */ 
  @SuppressWarnings("deprecation") 
  @Override 
  protected TokenStreamComponents createComponents(String fieldName, Reader reader) { 
   final Tokenizer source = new StandardTokenizer(matchVersion, reader); 
   TokenStream result = new StandardFilter(matchVersion, source); 
   // prior to this we get the classic behavior, standardfilter does it 
   // for us. 
   if (matchVersion.onOrAfter(Version.LUCENE_31)) 
    result = new EnglishPossessiveFilter(result); 
   result = new LowerCaseFilter(matchVersion, result); 
   result = new StopFilter(matchVersion, result, stopwords); 
   if (!stemExclusionSet.isEmpty()) 
    result = new KeywordMarkerFilter(result, stemExclusionSet); 
   result = new PorterSpecialStemFilter(result); 
   return new TokenStreamComponents(source, result); 
  } 
 } 
 
 private static class PorterSpecialStemFilter extends TokenFilter { 
  private final PorterSpecialStemmer stemmer = new PorterSpecialStemmer(); 
  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class); 
  private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class); 
 
  public PorterSpecialStemFilter(TokenStream in) { 
   super(in); 
  } 
 
  @Override 
  public final boolean incrementToken() throws IOException { 
   if (!input.incrementToken()) 
    return false; 
 
   if ((!keywordAttr.isKeyword()) && stemmer.stem(termAtt.buffer(), 0, termAtt.length())) 
    termAtt.copyBuffer(stemmer.getResultBuffer(), 0, stemmer.getResultLength()); 
   return true; 
  } 
 } 
 
 @SuppressWarnings("unused") 
 private static class PorterSpecialStemmer { 
  private char[] b; 
  private int i, /* offset into b */ 
  j, k, k0; 
  private boolean dirty = false; 
  private static final int INITIAL_SIZE = 50; 
 
  public PorterSpecialStemmer() { 
   b = new char[INITIAL_SIZE]; 
   i = 0; 
  } 
 
  /**
   * reset() resets the stemmer so it can stem another word. If you invoke 
   * the stemmer by calling add(char) and then stem(), you must call 
   * reset() before starting another word. 
   */ 
  public void reset() { 
   i = 0; 
   dirty = false; 
  } 
 
  /**
   * Add a character to the word being stemmed. When you are finished 
   * adding characters, you can call stem(void) to process the word. 
   */ 
  public void add(char ch) { 
   if (b.length <= i) { 
    b = ArrayUtil.grow(b, i + 1); 
   } 
   b[i++] = ch; 
  } 
 
  /**
   * After a word has been stemmed, it can be retrieved by toString(), or 
   * a reference to the internal buffer can be retrieved by 
   * getResultBuffer and getResultLength (which is generally more 
   * efficient.) 
   */ 
  @Override 
  public String toString() { 
   return new String(b, 0, i); 
  } 
 
  /**
   * Returns the length of the word resulting from the stemming process. 
   */ 
  public int getResultLength() { 
   return i; 
  } 
 
  /**
   * Returns a reference to a character buffer containing the results of 
   * the stemming process. You also need to consult getResultLength() to 
   * determine the length of the result. 
   */ 
  public char[] getResultBuffer() { 
   return b; 
  } 
 
  /* cons(i) is true <=> b[i] is a consonant. */ 
 
  private final boolean cons(int i) { 
   switch (b[i]) { 
   case 'a': 
   case 'e': 
   case 'i': 
   case 'o': 
   case 'u': 
    return false; 
   case 'y': 
    return (i == k0) ? true : !cons(i - 1); 
   default: 
    return true; 
   } 
  } 
 
  /*
   * m() measures the number of consonant sequences between k0 and j. if c 
   * is a consonant sequence and v a vowel sequence, and <..> indicates 
   * arbitrary presence, 
   *  
   * <c><v> gives 0 <c>vc<v> gives 1 <c>vcvc<v> gives 2 <c>vcvcvc<v> gives 
   * 3 .... 
   */ 
 
  private final int m() { 
   int n = 0; 
   int i = k0; 
   while (true) { 
    if (i > j) 
     return n; 
    if (!cons(i)) 
     break; 
    i++; 
   } 
   i++; 
   while (true) { 
    while (true) { 
     if (i > j) 
      return n; 
     if (cons(i)) 
      break; 
     i++; 
    } 
    i++; 
    n++; 
    while (true) { 
     if (i > j) 
      return n; 
     if (!cons(i)) 
      break; 
     i++; 
    } 
    i++; 
   } 
  } 
 
  /* vowelinstem() is true <=> k0,...j contains a vowel */ 
 
  private final boolean vowelinstem() { 
   int i; 
   for (i = k0; i <= j; i++) 
    if (!cons(i)) 
     return true; 
   return false; 
  } 
 
  /* doublec(j) is true <=> j,(j-1) contain a double consonant. */ 
 
  private final boolean doublec(int j) { 
   if (j < k0 + 1) 
    return false; 
   if (b[j] != b[j - 1]) 
    return false; 
   return cons(j); 
  } 
 
  /*
   * cvc(i) is true <=> i-2,i-1,i has the form consonant - vowel - 
   * consonant and also if the second c is not w,x or y. this is used when 
   * trying to restore an e at the end of a short word. e.g. 
   *  
   * cav(e), lov(e), hop(e), crim(e), but snow, box, tray. 
   */ 
 
  private final boolean cvc(int i) { 
   if (i < k0 + 2 || !cons(i) || cons(i - 1) || !cons(i - 2)) 
    return false; 
   else { 
    int ch = b[i]; 
    if (ch == 'w' || ch == 'x' || ch == 'y') 
     return false; 
   } 
   return true; 
  } 
 
  private final boolean ends(String s) { 
   int l = s.length(); 
   int o = k - l + 1; 
   if (o < k0) 
    return false; 
   for (int i = 0; i < l; i++) { 
    final char ch = s.charAt(i); 
    /*
     * LUCENE-3335: ch == 0 check added to prevent hotspot crash: no 
     * endings that are stripped contain this. 
     */ 
    if (ch == 0 || b[o + i] != ch) 
     return false; 
   } 
   j = k - l; 
   return true; 
  } 
 
  /*
   * setto(s) sets (j+1),...k to the characters in the string s, 
   * readjusting k. 
   */ 
 
  void setto(String s) { 
   int l = s.length(); 
   int o = j + 1; 
   for (int i = 0; i < l; i++) 
    b[o + i] = s.charAt(i); 
   k = j + l; 
   dirty = true; 
  } 
 
  /* r(s) is used further down. */ 
 
  void r(String s) { 
   if (m() > 0) 
    setto(s); 
  } 
 
  /*
   * step1() gets rid of plurals and -ed or -ing. e.g. 
   *  
   * caresses -> caress ponies -> poni ties -> ti caress -> caress cats -> 
   * cat 
   *  
   * feed -> feed agreed -> agree disabled -> disable 
   *  
   * matting -> mat mating -> mate meeting -> meet milling -> mill messing 
   * -> mess 
   *  
   * meetings -> meet 
   */ 
 
  private final void step1() { 
   if (b[k] == 's') { 
    if (ends("sses")) 
     k -= 2; 
    else if (ends("ies")) 
     setto("i"); 
    else if (b[k - 1] != 's') 
     k--; 
   } 
   if (ends("eed")) { 
    if (m() > 0) 
     k--; 
   } else if ((ends("ed") || ends("ing")) && vowelinstem()) { 
    k = j; 
    if (ends("at")) 
     setto("ate"); 
    else if (ends("bl")) 
     setto("ble"); 
    else if (ends("iz")) 
     setto("ize"); 
    else if (doublec(k)) { 
     int ch = b[k--]; 
     if (ch == 'l' || ch == 's' || ch == 'z') 
      k++; 
    } else if (m() == 1 && cvc(k)) 
     setto("e"); 
   } 
  } 
 
  /*
   * step2() turns terminal y to i when there is another vowel in the 
   * stem. 
   */ 
 
  private final void step2() { 
   if (ends("y") && vowelinstem()) { 
    b[k] = 'i'; 
    dirty = true; 
   } 
  } 
 
  /*
   * step3() maps double suffices to single ones. so -ization ( = -ize 
   * plus -ation) maps to -ize etc. note that the string before the suffix 
   * must give m() > 0. 
   */ 
 
  private final void step3() { 
   if (k == k0) 
    return; /* For Bug 1 */ 
   switch (b[k - 1]) { 
   case 'a': 
    if (ends("ational")) { 
     r("ate"); 
     break; 
    } 
    if (ends("tional")) { 
     r("tion"); 
     break; 
    } 
    break; 
   case 'c': 
    if (ends("enci")) { 
     r("ence"); 
     break; 
    } 
    if (ends("anci")) { 
     r("ance"); 
     break; 
    } 
    break; 
   case 'e': 
    if (ends("izer")) { 
     r("ize"); 
     break; 
    } 
    break; 
   case 'l': 
    if (ends("bli")) { 
     r("ble"); 
     break; 
    } 
    if (ends("alli")) { 
     r("al"); 
     break; 
    } 
    if (ends("entli")) { 
     r("ent"); 
     break; 
    } 
    if (ends("eli")) { 
     r("e"); 
     break; 
    } 
    if (ends("ousli")) { 
     r("ous"); 
     break; 
    } 
    break; 
   case 'o': 
    if (ends("ization")) { 
     r("ize"); 
     break; 
    } 
    if (ends("ation")) { 
     r("ate"); 
     break; 
    } 
    if (ends("ator")) { 
     r("ate"); 
     break; 
    } 
    break; 
   case 's': 
    if (ends("alism")) { 
     r("al"); 
     break; 
    } 
    if (ends("iveness")) { 
     r("ive"); 
     break; 
    } 
    if (ends("fulness")) { 
     r("ful"); 
     break; 
    } 
    if (ends("ousness")) { 
     r("ous"); 
     break; 
    } 
    break; 
   case 't': 
    if (ends("aliti")) { 
     r("al"); 
     break; 
    } 
    if (ends("iviti")) { 
     r("ive"); 
     break; 
    } 
    if (ends("biliti")) { 
     r("ble"); 
     break; 
    } 
    break; 
   case 'g': 
    if (ends("logi")) { 
     r("log"); 
     break; 
    } 
   } 
  } 
 
  /* step4() deals with -ic-, -full, -ness etc. similar strategy to step3. */ 
 
  private final void step4() { 
   switch (b[k]) { 
   case 'e': 
    if (ends("icate")) { 
     r("ic"); 
     break; 
    } 
    if (ends("ative")) { 
     r(""); 
     break; 
    } 
    if (ends("alize")) { 
     r("al"); 
     break; 
    } 
    break; 
   case 'i': 
    if (ends("iciti")) { 
     r("ic"); 
     break; 
    } 
    break; 
   case 'l': 
    if (ends("ical")) { 
     r("ic"); 
     break; 
    } 
    if (ends("ful")) { 
     r(""); 
     break; 
    } 
    break; 
   case 's': 
    if (ends("ness")) { 
     r(""); 
     break; 
    } 
    break; 
   } 
  } 
 
  /* step5() takes off -ant, -ence etc., in context <c>vcvc<v>. */ 
 
  private final void step5() { 
   if (k == k0) 
    return; /* for Bug 1 */ 
   switch (b[k - 1]) { 
   case 'a': 
    if (ends("al")) 
     break; 
    return; 
   case 'c': 
    if (ends("ance")) 
     break; 
    if (ends("ence")) 
     break; 
    return; 
   case 'e': 
    if (ends("er")) 
     break; 
    return; 
   case 'i': 
    if (ends("ic")) 
     break; 
    return; 
   case 'l': 
    if (ends("able")) 
     break; 
    if (ends("ible")) 
     break; 
    return; 
   case 'n': 
    if (ends("ant")) 
     break; 
    if (ends("ement")) 
     break; 
    if (ends("ment")) 
     break; 
    /* element etc. not stripped before the m */ 
    if (ends("ent")) 
     break; 
    return; 
   case 'o': 
    if (ends("ion") && j >= 0 && (b[j] == 's' || b[j] == 't')) 
     break; 
    /* j >= 0 fixes Bug 2 */ 
    if (ends("ou")) 
     break; 
    return; 
    /* takes care of -ous */ 
   case 's': 
    if (ends("ism")) 
     break; 
    return; 
   case 't': 
    if (ends("ate")) 
     break; 
    if (ends("iti")) 
     break; 
    return; 
   case 'u': 
    if (ends("ous")) 
     break; 
    return; 
   case 'v': 
    if (ends("ive")) 
     break; 
    return; 
   case 'z': 
    if (ends("ize")) 
     break; 
    return; 
   default: 
    return; 
   } 
   if (m() > 1) 
    k = j; 
  } 
 
  /* step6() removes a final -e if m() > 1. */ 
 
  private final void step6() { 
   j = k; 
   if (b[k] == 'e') { 
    int a = m(); 
    if (a > 1 || a == 1 && !cvc(k - 1)) 
     k--; 
   } 
   if (b[k] == 'l' && doublec(k) && m() > 1) 
    k--; 
  } 
 
  /**
   * Stem a word provided as a String. Returns the result as a String. 
   */ 
  public String stem(String s) { 
   if (stem(s.toCharArray(), s.length())) 
    return toString(); 
   else 
    return s; 
  } 
 
  /**
   * Stem a word contained in a char[]. Returns true if the stemming 
   * process resulted in a word different from the input. You can retrieve 
   * the result with getResultLength()/getResultBuffer() or toString(). 
   */ 
  public boolean stem(char[] word) { 
   return stem(word, word.length); 
  } 
 
  /**
   * Stem a word contained in a portion of a char[] array. Returns true if 
   * the stemming process resulted in a word different from the input. You 
   * can retrieve the result with getResultLength()/getResultBuffer() or 
   * toString(). 
   */ 
  public boolean stem(char[] wordBuffer, int offset, int wordLen) { 
   reset(); 
   if (b.length < wordLen) { 
    b = new char[ArrayUtil.oversize(wordLen, NUM_BYTES_CHAR)]; 
   } 
   System.arraycopy(wordBuffer, offset, b, 0, wordLen); 
   i = wordLen; 
   return stem(0); 
  } 
 
  /**
   * Stem a word contained in a leading portion of a char[] array. Returns 
   * true if the stemming process resulted in a word different from the 
   * input. You can retrieve the result with 
   * getResultLength()/getResultBuffer() or toString(). 
   */ 
  public boolean stem(char[] word, int wordLen) { 
   return stem(word, 0, wordLen); 
  } 
 
  /**
   * Stem the word placed into the Stemmer buffer through calls to add(). 
   * Returns true if the stemming process resulted in a word different 
   * from the input. You can retrieve the result with 
   * getResultLength()/getResultBuffer() or toString(). 
   */ 
  public boolean stem() { 
   return stem(0); 
  } 
 
  public boolean stem(int i0) { 
   k = i - 1; 
   k0 = i0; 
   if (k > k0 + 1) { 
    step1(); 
    step2(); 
    step3(); 
    step4(); 
    step5(); 
    step6(); 
   } 
   // Also, a word is considered dirty if we lopped off letters 
   // Thanks to Ifigenia Vairelles for pointing this out. 
   if (i != k + 1) 
    dirty = true; 
   i = k + 1; 
   return dirty; 
  } 
 } 
 
}