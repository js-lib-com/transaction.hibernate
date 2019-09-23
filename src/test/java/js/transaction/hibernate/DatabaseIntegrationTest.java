package js.transaction.hibernate;

import java.io.FileInputStream;

import js.lang.Config;
import js.lang.ConfigBuilder;
import js.transaction.Transaction;
import js.transaction.TransactionException;
import js.transaction.TransactionManager;
import js.transaction.WorkingUnit;
import js.util.Classes;
import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseIntegrationTest extends TestCase {
	private TransactionManager transactionManager;

	@Override
	protected void setUp() throws Exception {
		transactionManager = Classes.newInstance(Util.TRANSACTION_IMPL);
		ConfigBuilder builder = new ConfigBuilder(new FileInputStream("fixture/database-integration-config.xml"));
		Config config = builder.build();
		Classes.invoke(transactionManager, "config", config);
	}

	public void testExecuteHibernateTransaction() throws Exception {
		Transaction transaction = transactionManager.createTransaction(null);

		Person person = new Person();
		Session session = Util.getSession(transactionManager);
		session.saveOrUpdate(person);

		transaction.commit();
		transaction.close();
	}

	public void testExecuteHibernateConcurentTransaction() throws Exception {
		class TransactionThread extends Thread {
			@SuppressWarnings("unused")
			private int id;

			public TransactionThread(int id) {
				this.id = id;
			}

			public void run() {
				for (int i = 0; i < 4; i++) {
					Transaction t = transactionManager.createTransaction(null);
					try {
						Session session = Util.getSession(transactionManager);
						Person person = new Person();
						session.save(person);
						t.commit();
					} catch (Exception e) {
						t.rollback();
					} finally {
						t.close();
					}
				}
			}
		}

		final int COUNT = 10;
		Thread[] threads = new Thread[COUNT];
		for (int i = 0; i < COUNT; i++) {
			threads[i] = new TransactionThread(i);
			threads[i].start();
		}
		for (int i = 0; i < COUNT; i++) {
			threads[i].join();
		}
	}

	public void testExecuteConcurentNativeHibernate() throws Exception {
		// if MAX_SIZE > MAX_STATEMENTS some threads block
		final String MAX_SIZE = "60";
		final String MAX_STATEMENTS = "50";

		Configuration cfg = new Configuration();
		cfg.setProperty("hibernate.connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider");
		cfg.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		cfg.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/test");
		cfg.setProperty("hibernate.default_schema", "test");
		cfg.setProperty("hibernate.connection.username", "test");
		cfg.setProperty("hibernate.connection.password", "test");
		cfg.setProperty("hibernate.c3p0.min_size", "5");
		cfg.setProperty("hibernate.c3p0.max_size", MAX_SIZE);
		cfg.setProperty("hibernate.c3p0.max_statements", MAX_STATEMENTS);
		cfg.setProperty("hibernate.c3p0.timeout", "1800");
		cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		cfg.setProperty("hibernate.show_sql", "false");
		cfg.addResource("js/transaction/hibernate/person.hbm");

		final SessionFactory sessionFactory = cfg.buildSessionFactory();

		class TransactionThread extends Thread {
			@SuppressWarnings("unused")
			private int id;

			public TransactionThread(int id) {
				this.id = id;
			}

			public void run() {
				for (int i = 0; i < 4; i++) {
					Session session = sessionFactory.openSession();
					org.hibernate.Transaction t = null;
					try {
						t = session.beginTransaction();
						Person person = new Person();
						session.save(person);
						t.commit();
					} catch (Exception e) {
						if (t != null)
							t.rollback();
					} finally {
						session.close();
					}
				}
			}
		}

		final int COUNT = 10;
		Thread[] threads = new Thread[COUNT];
		for (int i = 0; i < COUNT; i++) {
			threads[i] = new TransactionThread(i);
			threads[i].start();
		}
		for (int i = 0; i < COUNT; i++) {
			threads[i].join();
		}
	}

	public void testNestedTransactions() {
		Transaction outerTransaction = transactionManager.createTransaction(null);

		transactionManager.exec(new WorkingUnit<Session, Void>() {
			@Override
			public Void exec(Session session, Object... args) throws Exception {
				Transaction innerTransaction = transactionManager.createTransaction(null);
				Person person = new Person();
				session.saveOrUpdate(person);
				innerTransaction.commit();
				innerTransaction.close();
				return null;
			}
		});

		outerTransaction.commit();
		outerTransaction.close();
	}

	public void testErrorOnUsingNativeInterfaceOutOfTransactionBoundaries() {
		try {
			Person person = new Person();
			Session session = Util.getSession(transactionManager);
			session.saveOrUpdate(person);
			fail("Exception expected when try to use hibernate session outside transaction boundaries.");
		} catch (TransactionException e) {
		}

		Transaction transaction = transactionManager.createTransaction(null);
		try {
			transaction.close();
			Person person = new Person();
			Session session = Util.getSession(transactionManager);
			session.saveOrUpdate(person);
			fail("Exception expected when try to use session after transaction close.");
		} catch (TransactionException e) {
		}
	}
}
