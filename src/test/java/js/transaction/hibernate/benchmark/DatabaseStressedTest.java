package js.transaction.hibernate.benchmark;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import js.lang.ConfigBuilder;
import js.transaction.Transaction;
import js.transaction.hibernate.Person;
import js.transaction.hibernate.TransactionManagerImpl;

public class DatabaseStressedTest
{
  private TransactionManagerImpl transactionManager;

  @Before
  public void beforeTest() throws Exception
  {
    transactionManager = new TransactionManagerImpl();
    ConfigBuilder builder = new ConfigBuilder(getClass().getResourceAsStream("/integration-config.xml"));
    transactionManager.config(builder.build());
  }

  @Test
  public void stressedWriteTransactions() throws Exception
  {
    for(int i = 0; i < 1000000; i++) {
      Transaction t = transactionManager.createTransaction(null);
      try {
        Session session = transactionManager.getSession();
        Person person = new Person();
        session.save(person);
        t.commit();
      }
      catch(Exception e) {
        t.rollback();
      }
      finally {
        t.close();
      }
    }
  }
}
