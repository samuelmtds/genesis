package net.java.dev.genesis.mockobjects;

import java.util.Map;
import net.java.dev.genesis.script.Script;
import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptExpression;
import net.java.dev.genesis.script.ScriptFactory;

public class MockScriptFactory implements ScriptFactory {
   public Script newScript() {
      return new Script() {
         public ScriptContext newContext(Object root) {
            return new MockScriptContext();
         }

         public ScriptExpression compile(String expression) {
            return new MockScriptExpression();
         }
      };
   }
}

class MockScriptContext extends ScriptContext {
   public Object lookup(String name) {
      return null;
   }

   public void declare(String name, Object value) {
   }

   public void undeclare(String name) {
   }

   public void registerFunctions(String prefix, Class functionClass) {
   }

   public Map getContextMap() {
      return null;
   }

   protected Object doEval(ScriptExpression expr) {
      return null;
   }

   protected ScriptExpression newScriptExpression(String expression) {
      return new MockScriptExpression();
   }
}

class MockScriptExpression extends ScriptExpression {
   public MockScriptExpression() {
      super(null);
   }
}