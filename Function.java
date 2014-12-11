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
   String OPERATORS = "+-*/^";
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
      Function evaluation = substitute();
      
      if(!evaluation.variables.isEmpty())
      {
         throw new VariableNotSetException();
      }
            
      evaluation = evaluation.evaluateParentheses();
      evaluation = evaluation.evaluateMult();
      evaluation = evaluation.evaluateAdd();
      
      return new value;
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
   private Function evaluateParentheses()
   {
      if(expression.indexOf
   }
            
      evaluation = evaluation.evaluateParentheses();
      evaluation = evaluation.evaluateMult();
      evaluation = evaluation.evaluateAdd();
}