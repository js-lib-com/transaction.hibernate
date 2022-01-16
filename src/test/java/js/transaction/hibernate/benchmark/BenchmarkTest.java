package js.transaction.hibernate.benchmark;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import js.transaction.Transaction;
import js.transaction.hibernate.Person;
import js.transaction.hibernate.TransactionManagerImpl;

public class BenchmarkTest
{
  private final int TEST_COUNT = 1000000;

  private TransactionManagerImpl transactionManager;

  @Before
  public void beforeTest() throws Exception
  {
    transactionManager = new TransactionManagerImpl();
  }

  @Test
  public void writableVersusReadOnly()
  {
    executeWriteableTransaction(1);
    executeReadOnlyTransaction(1);

    System.out.println("Start writeable transactions benchmark.");
    long writeableStart = System.currentTimeMillis();
    for(int i = 0; i < TEST_COUNT; ++i) {
      executeWriteableTransaction(i % 10000);
    }
    System.out.printf("Ellapsed time for %,d writeable transactions: %,d msec\n", TEST_COUNT, System.currentTimeMillis() - writeableStart);

    System.out.println("Start read-only transactions bench.");
    long readOnlyStart = System.currentTimeMillis();
    for(int i = 0; i < TEST_COUNT; ++i) {
      executeReadOnlyTransaction(i % 10000);
    }
    System.out.printf("Ellapsed time for %,d read-only transactions: %,d msec\n", TEST_COUNT, System.currentTimeMillis() - readOnlyStart);
  }

  private void executeWriteableTransaction(int id)
  {
    Transaction transaction = transactionManager.createTransaction(null);
    Session session = transactionManager.getSession();
    session.get(Person.class, id);
    transaction.commit();
    transaction.close();
  }

  private void executeReadOnlyTransaction(int id)
  {
    Transaction transaction = transactionManager.createReadOnlyTransaction(null);
    Session session = transactionManager.getSession();
    session.get(Person.class, id);
    transaction.close();
  }
}
