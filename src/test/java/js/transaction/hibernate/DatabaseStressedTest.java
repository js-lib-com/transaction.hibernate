package js.transaction.hibernate;

import java.io.File;

import org.hibernate.Session;
import org.junit.Ignore;

import js.lang.ConfigBuilder;
import js.transaction.Transaction;
import js.transaction.TransactionManager;
import js.util.Classes;
import junit.framework.TestCase;

@Ignore
public class DatabaseStressedTest extends TestCase
{
  private TransactionManager transactionManager;

  @Override
  protected void setUp() throws Exception
  {
    transactionManager = Classes.newInstance(Util.TRANSACTION_IMPL);
    ConfigBuilder builder = new ConfigBuilder(new File("fixture/database-integration-config.xml"));
    Classes.invoke(transactionManager, "config", builder.build());
  }

  public void testStressedHibernateTransactions() throws Exception
  {

    for(int i = 0; i < 1000000; i++) {
      Transaction t = transactionManager.createTransaction(null);
      try {
        Session session = Util.getSession(transactionManager);
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
