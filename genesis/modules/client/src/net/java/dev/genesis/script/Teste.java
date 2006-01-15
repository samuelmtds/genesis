package net.java.dev.genesis.script;

import net.java.dev.genesis.script.bsf.BSFScriptFactory;

public class Teste {
   public void teste(Object o) {
      System.out.println(o.getClass());
   }
   
   public static void main(String[] args) {
      ScriptFactory f = new BSFScriptFactory("javascript");

      ScriptContext ctx = f.newScript().newContext(new Teste());

      ctx.eval("form.teste(Integer(1))");
      long b, a=System.currentTimeMillis();
      for (int i = 0; i < 10000; i++) {
         ctx.eval("genesis.isEmpty('a')");
      }
      b =System.currentTimeMillis();
      System.out.println("Time: " + (b-a));
   }
}
