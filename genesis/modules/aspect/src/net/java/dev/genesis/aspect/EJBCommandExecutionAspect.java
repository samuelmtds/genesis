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
package net.java.dev.genesis.aspect;

import java.lang.reflect.InvocationTargetException;

import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import org.codehaus.aspectwerkz.CrossCuttingInfo;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import org.codehaus.aspectwerkz.joinpoint.MethodRtti;
import org.codehaus.aspectwerkz.joinpoint.impl.MethodRttiImpl;

import net.java.dev.genesis.ejb.CommandExecutor;
import net.java.dev.genesis.ejb.CommandExecutorHome;

public class EJBCommandExecutionAspect extends CommandInvocationAspect {

    public EJBCommandExecutionAspect(CrossCuttingInfo ccInfo) {
        super(ccInfo);
    }

    private CommandExecutor getCommandExecutor() {
        try{
            final CommandExecutorHome home = (CommandExecutorHome) PortableRemoteObject.narrow(new InitialContext().lookup(ccInfo
                    .getParameter("jndiName")), CommandExecutorHome.class);
            return home.create();
        }
        catch(final Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * The command execution implemented as a remote EJB call.
     * 
     * @param jp
     *            the join point
     * @return the resulting object
     * @throws Throwable
     */
    public Object commandExecution(final JoinPoint jp) throws Throwable {
        
        final MethodRtti rtti = (MethodRtti)jp.getRtti();
        final CommandResolver obj = (CommandResolver) jp.getTargetInstance();
        try {
            final Class[] classes = rtti.getParameterTypes();
            final String[] classNames = new String[classes.length];

            for(int i = 0; i < classes.length; i++){
                classNames[i] = classes[i].getName();
            }

            final CommandExecutor commandExecutor = getCommandExecutor();

            try {
                final String methodName = ((MethodRttiImpl)rtti).getMethodTuple().getWrapperMethod().getName();
                final Object[] parameterValues = rtti.getParameterValues();
                if(obj.isTransaction(rtti.getMethod())){
                    return commandExecutor.executeTransaction(obj, methodName, classNames, parameterValues);
                }
                return commandExecutor.executeQuery(obj, methodName, classNames, parameterValues);
            } finally {
                commandExecutor.remove();
            }
        } catch(final InvocationTargetException ite) {
            throw ite.getTargetException();
        }
    }
}