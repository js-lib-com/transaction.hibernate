package js.transaction.hibernate;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import js.lang.BugError;
import js.lang.Config;
import js.transaction.Immutable;
import js.transaction.Mutable;
import js.transaction.Transaction;
import js.transaction.TransactionContext;
import js.transaction.TransactionManager;
import js.util.Classes;

public class DaoRule<I> implements TestRule, TransactionContext
{
  private final TransactionManager manager;
  private final ThreadLocal<Object> sessionStorage = new ThreadLocal<>();
  private final I dao;
  private final boolean immutableClass;

  @SuppressWarnings("unchecked")
  public DaoRule(Class<?> implementationClass, String configResource)
  {
    // for unknown reason compiler does not accept Class<? extends I> when create DAO rule instance
    
    manager = new TransactionManagerImpl();

    Config config = new Config("data-source");
    config.setAttribute("config", configResource);
    try {
      manager.config(config);
    }
    catch(Exception e) {
      throw new BugError(e);
    }

    ClassLoader classLoader = implementationClass.getClassLoader();
    Class<?>[] interfaces = implementationClass.getInterfaces();
    I instance = (I)Classes.newInstance(implementationClass, new SessionManagerImpl(this));
    InvocationHandler handler = new TransactionalProxy(instance);
    dao = (I)Proxy.newProxyInstance(classLoader, interfaces, handler);

    Annotation annotation = implementationClass.getAnnotation(Immutable.class);
    for(Class<?> interfaceClass : interfaces) {
      if(annotation != null) {
        break;
      }
      annotation = interfaceClass.getAnnotation(Immutable.class);
    }
    immutableClass = annotation != null;
  }

  public I getDaoInstance()
  {
    return dao;
  }

  @Override
  public Statement apply(final Statement base, Description description)
  {
    return new Statement()
    {
      @Override
      public void evaluate() throws Throwable
      {
        base.evaluate();
      }
    };
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getSession()
  {
    return (T)sessionStorage.get();
  }

  private class TransactionalProxy implements InvocationHandler
  {
    private final Object instance;

    public TransactionalProxy(Object instance)
    {
      this.instance = instance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
      boolean immutableMethod = immutableClass;
      if(getAnnotation(method, Immutable.class) != null) {
        immutableMethod = true;
      }
      if(getAnnotation(method, Mutable.class) != null) {
        immutableMethod = false;
      }

      Transaction transaction = immutableMethod ? manager.createReadOnlyTransaction() : manager.createTransaction();
      sessionStorage.set(transaction.getSession());

      try {
        Object value = method.invoke(instance, args);
        if(!immutableMethod) {
          transaction.commit();
        }
        return value;
      }
      catch(Throwable throwable) {
        if(!immutableMethod) {
          transaction.rollback();
        }
        throw throwable;
      }
      finally {
        transaction.close();
        sessionStorage.remove();
      }
    }

    private Annotation getAnnotation(Method interfaceMethod, Class<? extends Annotation> annotationClass) throws NoSuchMethodException, SecurityException
    {
      Annotation annotation = interfaceMethod.getAnnotation(annotationClass);
      if(annotation != null) {
        return annotation;
      }
      Class<?> implementationClass = instance.getClass();
      Method implementationMethod = implementationClass.getDeclaredMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());
      return implementationMethod.getAnnotation(annotationClass);
    }
  }
}
