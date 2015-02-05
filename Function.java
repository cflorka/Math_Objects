import java.util.HashMap;
import java.util.Stack;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.Arrays;

/** A class for mathematical functions.
 *  Can substitute Double values for variables and then be evaluated to a Double.
 *  This class is immutable.
 *
 *  @author Clint Florka
 *  @version v0.1
 */


public class Function
{
   //TODO: Add function name functionality
   //String name;
   
   String expression;
   
   HashMap<Character, Double> variables;
   String OPERATORS = "+-*/^"; //Order matters, keep in PEMDAS order
   String OPENING = "([{";
   String CLOSING = ")]}";
   String NUMBERS = "0123456789.";
   //String EQUAL_SIGN = "=";
   
   /** Constructor taking one String argument.
    *  Removes whitespace and populates variable list and checks for matching parentheses.
    *  Can contain multiple variables (e.g. 'x' and 'y').
    *  Note: Does not check for juxtaposition of variables or values and this will cause an
    *  exception for the evaluate() method.
    *
    *  @param input  the String representation of the function, not null
    *
    *  @throws MismatchedParenthesisException if the parentheses don't match
    *  @throws NeedClosingParenthesisException if there is an extra opening parenthesis
    *  @throws NeedOpeningParenthesisException if there is an extra closing parenthesis            
    */
   public Function(String input)
   {
      expression = input.replaceAll("\\s","");
      populateVariables();
   }
   
   private void populateVariables()
   {
      Stack<Character> parenStack = new Stack<>();
      variables = new HashMap<>();
      
      for(int i = 0; i < expression.length(); ++i)
      {
         Character selected = expression.charAt(i);
         int index;
         
         if(OPENING.indexOf(selected) > -1)
         {
            parenStack.push(selected);
         }
         else if((index = CLOSING.indexOf(selected)) > -1)
         {
            //Check if there are more closing parentheses than opening at any point in time
            if(parenStack.isEmpty())
            {
               throw new NeedOpeningParenthesisException();
            }
            //Check matching parentheses
            if(OPENING.indexOf(parenStack.pop()) != index)
            {
               throw new MismatchedParenthesisException();
            };
         }
         else if(OPERATORS.indexOf(selected) < 0 &&
                 NUMBERS.indexOf(selected) < 0 &&
                 !variables.keySet().contains(selected))
         {
            variables.put(selected, null);
         }
      }
      //Check if there are no left over opening parentheses
      if(!parenStack.isEmpty())
      {
         throw new NeedClosingParenthesisException();
      }
   }
   
   /** Returns a string containing all the variables. Used for testing.
    *
    *  @returns a String containing all the variables
    */
   public String variableString()
   {
      String variableString = "";
      for(Character c: variables.keySet())
      {
         variableString += c;
      }
      return variableString;
   }
   
   /** Sets the given variable to the given value.
    *
    *  @param variable  the Character representation of the variable to be set, not null
    *  @param value  the Double value to set the variable to, not null
    *  @throws NoSuchVariableException if the given variable isn't in the function
    */
   public void setValueOf(Character variable, Double value)
   {
      if(variables.keySet().contains(variable))
      {
         variables.put(variable, value);
      }
      else
      {
         throw new NoSuchVariableException();
      }
   }
   
   /** Returns the Double value of the given variable.
    *
    *  @param variable  the Character representation of the variable to lookup
    *                   the value of, not null
    *  @returns value the Double value that has been set for the given variable, not null
    *  @throws VariableNotSetException if the variable has not yet been set
    */
   public Double getValueOf(Character variable)
   {
      Double value = variables.get(variable);
      if(value == null)
      {
         throw new VariableNotSetException();
      }
      return value;
   }
   
   //TODO make easier to use recursively
   /** Returns the Double value of the function evaluated with the values set for the variables.
    *  
    *  @returns the Double value of the function evaluated with the values set for the variables
    *  @throws VariableNotSetException if there are any variables whose values haven't been set
    */
   public Double evaluate()
   {
      Function fxn = substitute();
      String evaluated;
      
      if(!fxn.variables.isEmpty())
      {
         throw new VariableNotSetException();
      }
      
      evaluated = fxn.evaluateParentheses();
      evaluated = evaluateOperations(evaluated, 0);
      return Double.valueOf(evaluated);
   }
      
   private Function substitute()
   {
      String substituted = expression;
      Double value;
      for(Character x : variables.keySet())
      {
         if( (value = variables.get(x)) != null)
         {
            substituted = substituted.replaceAll(x.toString(), value.toString());
         }
      }
      return new Function(substituted);
   }
   
   private String evaluateParentheses()
   {
      return evaluateParentheses(expression);
   }
   
   //Needs to check for independant parens 
   private String evaluateParentheses(String input)
   {
      String before, evaluated, after;
      before = after = "";
      evaluated = input;
      int openIndex = indexOfOpenParenthesis();
      
      if(openIndex >= 0)
      {
         int closeIndex = indexOfCloseParenthesis(openIndex);
         before = expression.substring(0, openIndex);
         if(openIndex > 0)
         {
            String preOper, operator;
            
            preOper = before.substring(0, openIndex - 2);
            preOper = new Function(preOper).evaluate().toString();
            
            before = preOper + ((Character)before.charAt(openIndex - 1)).toString(); //TODO seems needlessly complex
         }
         evaluated = expression.substring(openIndex + 1, closeIndex - 1);
         evaluated = new Function(evaluated).evaluate().toString();
         int length = expression.length();
         if(closeIndex < length)
         {
            after = expression.substring(closeIndex);
            
            if(after.length() > 0)
            {
               String postOper, operator;
               
               postOper = after.substring(1);
               postOper = new Function(postOper).evaluate().toString();
               after = after.substring(0, 1) + postOper;
            }
         }
      }
      return before + evaluated + after;
   }
      
   private int indexOfOpenParenthesis()
   {
      int index = expression.indexOf(OPENING.charAt(0));
      int temp = index;
      
      for(int i = 0; i < OPENING.length(); ++i)
      {
          temp = expression.indexOf(OPENING.charAt(i));
          if(temp >= 0 && (temp < index || index < 0))
          {
              index = temp;
          }
      }
      return index;
   }
   
   private int indexOfCloseParenthesis(int openIndex)
   {
      Character openingParen = expression.charAt(openIndex);
      Character closingParen = CLOSING.charAt(OPENING.indexOf(openingParen));
      Stack<Character> parenStack = new Stack<>();
      
      parenStack.push(openingParen);
      int index = openIndex + 1; //starts at 1 since openParen is first char
      Character temp, cursor;
      while(!parenStack.isEmpty()) 
      {
         cursor = expression.charAt(index);
         if(cursor.equals(openingParen))
         {
            parenStack.push(openingParen);
         }
         else if(cursor.equals(closingParen))
         {
            parenStack.pop();
         }
         ++index;
      }
      return index;
   }
      
   private String evaluateOperations(String input, int operatorIndex)
   {
      String evaluated;
      
      if(operatorIndex < OPERATORS.length())
      {
         Character operator = OPERATORS.charAt(operatorIndex);
         
         String[] pieces = Pattern.compile(operator.toString(), Pattern.LITERAL).split(input);
         int numOfPieces = pieces.length;
         
         for(int i = 0; i < numOfPieces; ++i)
         {
            //checks for negative numbers when splitting on minus operator
            if(operator.equals('-'))
            {
               if(pieces[i].equals(""))
               {
                  pieces[i] = "-" + pieces[i + 1];
                  pieces[i + 1] = "0.0";
                  System.out.println(pieces[i]);
               }
               else if(OPERATORS.indexOf(pieces[i].charAt(pieces[i].length() - 1)) >= 0)
               { 
                  pieces[i] = pieces[i] + "-" + pieces[i + 1];
                  pieces[i + 1] = "0.0";
               }
            }
            pieces[i] = evaluateOperations(pieces[i], operatorIndex + 1);
         }
         if(numOfPieces > 1)
         {
            Double cursor = Double.valueOf(pieces[0]);
            Double total = cursor;
            for(int i = 1; i < numOfPieces ; ++i)
            {
               cursor = Double.valueOf(pieces[i]);
               switch(operator)
               {
                  case '+':
                     total += cursor;
                     break;
                  case '-':
                     total -= cursor;
                     break;
                  case '*':
                     total *= cursor;
                     break;
                  case '/':
                     total /= cursor;
                     break;
                  case '^':
                     total = Math.pow(total, cursor);
                     break;
                }
            }
            evaluated = total.toString();
         }
         else
         {
            evaluated = pieces[0];
         }
      }
      else
      {
         evaluated = input;
      }
      
      return evaluated;
   }
      
   /** Returns String representation of this.
    *
    *  @returns this.expression
    */
   public String toString()
   {
      return expression;
   }
}