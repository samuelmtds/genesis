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

import net.java.dev.genesis.command.hibernate.HibernateCommand;
import net.sf.hibernate.FlushMode;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.cfg.Configuration;

import org.codehaus.aspectwerkz.CrossCuttingInfo;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import org.codehaus.aspectwerkz.joinpoint.MethodSignature;

/**
 * @Aspect perJVM
 */
public class LocalCommandExecutionAspect extends CommandInvocationAspect {

    public LocalCommandExecutionAspect(CrossCuttingInfo ccInfo) {
        super(ccInfo);
    }
    
    /**
     * @Around localCommandExecution
     */
    public Object commandExecution(final JoinPoint joinPoint) throws Throwable {
        
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final CommandResolver obj = (CommandResolver)joinPoint.getTargetInstance();
        if(!(obj instanceof HibernateCommand)){
            return joinPoint.proceed();
        }

        final HibernateCommand hibernateCommand = (HibernateCommand) obj;
        final Session session = HibernateHelper.getInstance().createSession();
        final boolean transactional = obj.isTransaction(methodSignature.getMethod());

        Transaction transaction = null;
        Object ret;

        try{
            hibernateCommand.setSession(session);

            if(transactional){
                transaction = session.beginTransaction();
                session.setFlushMode(FlushMode.COMMIT);
            }

            ret = joinPoint.proceed();

            if(transactional){
                transaction.commit();
            }

            return ret;
        }
        catch(Throwable t){
            if(transaction != null){
                try{
                    transaction.rollback();
                } catch(Throwable th){
                    th.printStackTrace();
                }
            }

            throw t;
        } finally{
            hibernateCommand.setSession(null);

            try{
                session.close();
            } catch(Throwable t){
                t.printStackTrace();
            }
        }
    }
}

final class HibernateHelper {
    private static final HibernateHelper instance = new HibernateHelper();
    private SessionFactory factory;

    private HibernateHelper() {
    }

    public static HibernateHelper getInstance() {
        return instance;
    }

    public Session createSession() throws HibernateException {
        if (factory == null) {
            factory = new Configuration().configure().buildSessionFactory();
        }

        return factory.openSession();
    }
}