/*
 * The Genesis Project
 * Copyright (C) 2004  Summa Technologies do Brasil Ltda.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.java.dev.genesis.samples.simplecalc.ui;

import java.io.Serializable;

//Databean
import net.java.dev.genesis.samples.simplecalc.databeans.CalculatorMemory;
//Business Commands
import net.java.dev.genesis.samples.simplecalc.business.DeleteMemoryCommand;
import net.java.dev.genesis.samples.simplecalc.business.CreateMemoryCommand;
import net.java.dev.genesis.samples.simplecalc.business.RestoreMemoryCommand;
import net.java.dev.genesis.samples.simplecalc.business.UpdateMemoryCommand;
import net.java.dev.genesis.samples.simplecalc.business.CalculateCommand;

/**
 * @Form
 */
public class CalculatorForm implements Serializable {

   private int number1;
   private int number2;
   private int result;

   private CalculatorMemory memory= null;
   
   public int getNumber1() {
      return number1;
   }

   public void setNumber1(int number1) {
      this.number1 = number1;
   }

   public int getNumber2() {
      return number2;
   }

   public void setNumber2(int number2) {
      this.number2 = number2;
   }

   /**
    * @Condition hasResult=(result!=0) 
    * @Condition hasOperands=(number1!=0)
    */
   public int getResult() {
      return result;
   }

   public void setResult(int result) {
      this.result = result;
   }

   /**
    * @Action
    * @EnabledWhen $hasResult
    */
   public void store() throws Exception {
	   if (memory == null){
		   CreateMemoryCommand memoryCommand = new CreateMemoryCommand();
		   memory = new CalculatorMemory();
		   memory.setValue(result);
		   memory =  memoryCommand.createMemory(memory);
	   	}
		else{
			UpdateMemoryCommand memoryCommand = new UpdateMemoryCommand();
			memory.setValue(result);
			memoryCommand.updateMemory(memory);
		}
   }

   /**
    * @Action
    */
   public void recall() throws Exception {
	   if (memory != null){
	   RestoreMemoryCommand memoryCommand = new RestoreMemoryCommand();
	   memory = memoryCommand.getMemory(memory.getId());
	   number1=memory.getValue();
	   }
	   else
		   number1=0;
   }

   /**
    * @Action
    */
   public void clear() throws Exception {
   	   if (memory != null){
	   DeleteMemoryCommand memoryCommand = new DeleteMemoryCommand();
	   memoryCommand.deleteMemory(memory);
           memory=null;
	   }
}

   /**
    * @Action
    * @EnabledWhen $hasOperands
    */
   public void compute() throws Exception {
	   CalculateCommand command= new CalculateCommand();
	   result = command.add(number1, number2);
   }

 
}