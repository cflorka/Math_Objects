import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;


public class FunctionTest {
   Function one, two, three, four, five, six, seven, eight, nine;

   /** Fixture initialization (common initialization
    *  for all tests). **/
   @Before public void setUp() {
      one = new Function("");
      two = new Function("{y*[x^2]+(x+1)+3}");
      three = new Function("[y*x]^2+(z+1)^X");
      four = new Function("{y*x^2+(x*1)}/3");
      five = new Function("(x+y)");
      six = new Function("x-y");
      seven = new Function("x*y");
      eight = new Function("x/y");
      nine = new Function("x^y");
   }


   //TODO Make more rigorous.
   /** Test parentheses checking **/
   @Test public void parenthesesTest()
   {
      assertFalse(mismatched(""));
      assertFalse(needOpening(""));
      assertFalse(needClosing(""));
      
      assertFalse(mismatched("x^2+(x+1)+3"));
      assertFalse(needOpening("x^2+(x+1)+3"));
      assertFalse(needClosing("x^2+(x+1)+3"));
      
      assertFalse(mismatched("{y[x^2]+(x+1)+3}"));
      assertFalse(needOpening("{y[x^2]+(x+1)+3}"));
      assertFalse(needClosing("{y[x^2]+(x+1)+3}"));
      
      assertFalse(mismatched("[yx]^2+(z+1)^X"));
      assertFalse(needOpening("[yx]^2+(z+1)^X"));
      assertFalse(needClosing("[yx]^2+(z+1)^X"));
      
      assertFalse(mismatched("x^2+(x+1)+3"));
      assertFalse(needOpening("x^2+(x+1)+3"));
      assertFalse(needClosing("x^2+(x+1)+3"));
      
      assertTrue(mismatched("(]"));
      assertTrue(mismatched("(}"));
      assertTrue(mismatched("[)"));
      assertTrue(mismatched("[}"));
      assertTrue(mismatched("{)"));
      assertTrue(mismatched("{]"));
      
      assertTrue(needOpening(")"));
      assertTrue(needOpening("]"));
      assertTrue(needOpening("}"));
      
      assertTrue(needClosing("("));
      assertTrue(needClosing("["));
      assertTrue(needClosing("{"));
   }
   
   private boolean mismatched(String input)
   {
      boolean thrown = false;
      try
      {
         new Function(input);
      }
      catch(MismatchedParenthesisException exception)
      {
         thrown = true;
      }
      return thrown;
   }
   
   private boolean needOpening(String input)
   {
      boolean thrown = false;
      try
      {
         new Function(input);
      }
      catch(NeedOpeningParenthesisException exception)
      {
         thrown = true;
      }
      return thrown;
   }
   
   private boolean needClosing(String input)
   {
      boolean thrown = false;
      try
      {
         new Function(input);
      }
      catch(NeedClosingParenthesisException exception)
      {
         thrown = true;
      }
      return thrown;
   }
   
   /** Test variable population **/
   @Test public void variableTest()
   {
      assertEquals(one.variableString(), "");
      assertEquals(orderString(two.variableString()), "xy");
      assertEquals(orderString(three.variableString()), "Xxyz");
      assertEquals(orderString(four.variableString()), "xy");
   }
   
   private static String orderString(String string)
   {
      char[] charArray = string.toCharArray();
      Arrays.sort(charArray);
      return new String(charArray);
   }
   
   /** Test setValueOf/getValueOf methods **/
   @Test public void setGetTest()
   {
      assertTrue(noSuchVariable(one, 'a'));
      assertFalse(noSuchVariable(two, 'x'));
      two.setValueOf('x', 0.0);
      assertEquals(two.getValueOf('x'), 0.0, 0.000);
      two.setValueOf('x', 2.5);
      assertEquals(two.getValueOf('x'), 2.5, 0.000);
      assertTrue(variableNotSet(two, 'y'));
   }
   
   private boolean noSuchVariable(Function fxn, Character var)
   {
      boolean thrown = false;
      try
      {
         fxn.setValueOf(var, 0.0);
      }
      catch(NoSuchVariableException exception)
      {
         thrown = true;
      }
      return thrown;      
   }
   
   private boolean variableNotSet(Function fxn, Character var)
   {
      boolean thrown = false;
      try
      {
         fxn.getValueOf(var);
      }
      catch(VariableNotSetException exception)
      {
         thrown = true;
      }
      return thrown;      
   }
   
   @Test public void evaluateTest()
   {
      setXY(two);
      assertEquals(two.evaluate(), 6.0, 0.0);/*
      two = new Function("{y*[x^2]+(x+1)+3}");
      three = new Function("[y*x]^2+(z+1)^X");
      four = new Function("{y*x^2+(x*1)}/3");
      setXY(five);
      assertEquals(five.evaluate(), 2.0, 0.0);
      setXY(six);
      assertEquals(six.evaluate(), 0.0, 0.0);
      setXY(seven);
      assertEquals(seven.evaluate(), 1.0, 0.0);
      setXY(eight);
      assertEquals(eight.evaluate(), 1.0, 0.0);
      setXY(nine);
      assertEquals(nine.evaluate(), 1.0, 0.0);*/
   }
   
   private void setXY(Function fxn)
   {
      fxn.setValueOf('x', 1.0);
      fxn.setValueOf('y', 1.0);
   }
}