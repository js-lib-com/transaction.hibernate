package js.transaction.hibernate;

import java.io.File;

import org.hibernate.Session;
import org.junit.Ignore;

import js.lang.ConfigBuilder;
import js.transaction.Transaction;
import js.transaction.TransactionManager;
import js.transaction.hibernate.Util;
import js.util.Classes;
import junit.framework.TestCase;

@Ignore
public class BenchmarkTest extends TestCase
{
  private TransactionManager transactionManager;

  @Override
  protected void setUp() throws Exception
  {
    transactionManager = Classes.newInstance(Util.TRANSACTION_IMPL);
    ConfigBuilder builder = new ConfigBuilder(new File("fixture/benchmark-config.xml"));
    Classes.invoke(transactionManager, "config", builder.build());
  }

  public void testWritableVersusReadOnly()
  {
    final int TEST_COUNT = 1000000;

    executeWriteableTransaction(1);
    executeReadOnlyTransaction(1);

    System.out.println("Start writeable transactions bench.");
    long writeableStart = System.currentTimeMillis();
    for(int i = 0; i < TEST_COUNT; ++i) {
      executeWriteableTransaction(i % 10000);
    }
    System.out.printf("Writeable transactions: %d\n", System.currentTimeMillis() - writeableStart);

    System.out.println("Start read-only transactions bench.");
    long readOnlyStart = System.currentTimeMillis();
    for(int i = 0; i < TEST_COUNT; ++i) {
      executeReadOnlyTransaction(i % 10000);
    }
    System.out.printf("Read-only transactions: %d\n", System.currentTimeMillis() - readOnlyStart);
  }

  private void executeWriteableTransaction(int id)
  {
    Transaction transaction = transactionManager.createTransaction(null);

    Session session = Util.getSession(transactionManager);
    session.get(Person.class, id);

    transaction.commit();
    transaction.close();
  }

  private void executeReadOnlyTransaction(int id)
  {
    Transaction transaction = transactionManager.createReadOnlyTransaction(null);

    Session session = Util.getSession(transactionManager);
    session.get(Person.class, id);

    transaction.close();
  }
}
