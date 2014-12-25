import java.util.HashMap;
import java.util.Stack;
import java.util.Scanner;

/**
 *  Dec 9: Started, created populateVariables() method.
 *
 *
 */

public class Function
{
   //TODO: Add function name functionality
   //String name;
   //String EQUAL_SIGN = "=";
   
   String expression;
   
   HashMap<Character, Double> variables;
   String OPERATORS = "+-*/^"; //Order matters, keep in PEMDAS order
   String OPENING = "([{";
   String CLOSING = ")]}";
   String NUMBERS = "0123456789.";
   
   public Function(String input)
   {
      expression = input.replaceAll("\\s","");
      populateVariables();
   }
   
   /**
    * Populates variable list and checks for matching parentheses.
    */
   private void populateVariables()
   {
      Stack<Character> checkStack = new Stack<>();
      variables = new HashMap<>();
      
      for(int i = 0; i < expression.length(); ++i)
      {
         Character selected = expression.charAt(i);
         int index;
         
         if(OPENING.indexOf(selected) > -1)
         {
            checkStack.push(selected);
         }
         else if((index = CLOSING.indexOf(selected)) > -1)
         {
            //Check if there are more closing parentheses than opening at any point in time
            if(checkStack.isEmpty())
            {
               throw new NeedOpeningParenthesisException();
            }
            //Check matching parentheses
            if(OPENING.indexOf(checkStack.pop()) != index)
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
      if(!checkStack.isEmpty())
      {
         throw new NeedClosingParenthesisException();
      }
   }
   
   public String variableString()
   {
      String variableString = "";
      for(Character c: variables.keySet())
      {
         variableString += c;
      }
      return variableString;
   }
   
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
   
   public Double getValueOf(Character variable)
   {
      Double value = variables.get(variable);
      if(value == null)
      {
         throw new VariableNotSetException();
      }
      return value;
   }
   
   public Double evaluate()
   {
      Function fxn = substitute();
      
      if(!fxn.variables.isEmpty())
      {
         throw new VariableNotSetException();
      }
      
      fxn = new Function(fxn.evaluateParentheses());
      fxn = new Function(fxn.evaluateOperations(fxn.expression, 0));
      
      return Double.valueOf(fxn.toString());
   }
   
   public Function substitute()
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
   //CURRENT
   private String evaluateParentheses()
   {
      String before, evaluated, after;
      before = after = "";
      evaluated = expression;
      int openIndex = indexOfOpenParenthesis();
      
      if(openIndex > 0)
      {
         int closeIndex = indexOfCloseParenthesis(openIndex);
         before = expression.substring(0, openIndex - 1);
         evaluated = expression.substring(openIndex + 1, closeIndex - 1);
         evaluated = new Function(evaluated).evaluate().toString();
         after = expression.substring(closeIndex + 1, expression.length());
      }
      return before + evaluated + after;
   }
      
   private int indexOfOpenParenthesis()
   {
      int index = expression.indexOf(OPENING.charAt(0));
      int temp = index;
      
      for(int i = 1; i < OPENING.length(); ++i)
      {
          temp = expression.indexOf(OPENING.charAt(i));
          if(temp > 0 && temp < index)
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
      String substring = expression.substring(openIndex);
      Stack<Character> parenStack = new Stack<>();
      
      parenStack.push(openingParen);
      int index = 1; //starts at 1 since openParen is first char
      Character temp;
      while(!parenStack.isEmpty()) 
      {
         Character cursor = substring.charAt(index);
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
      Character operator = OPERATORS.charAt(operatorIndex);
      String[] pieces = input.split(operator.toString());
      int numOfPieces = pieces.length;
      
      //Check if no operators in consecutive pieces, if so set evaluated based on current operator
      if(operatorIndex < OPERATORS.length() && numOfPieces > 1)
      {
         for(int i = 0; i < numOfPieces; ++i)
         {
            pieces[i] = evaluateOperations(pieces[i], operatorIndex + 1);
         }
         Double cursor = Double.valueOf(pieces[0]);
         Double total = cursor;
         for(int i = 1; i < numOfPieces ; ++i)
         {
            cursor = Double.valueOf(pieces[1]);
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
         evaluated = input;
      }
      
      return evaluated;
   }
   
   private int indexOfValueBefore(Character operator)
   {
      return 0;
   }
   
   public String toString()
   {
      return expression;
   }
}