package js.transaction.hibernate;

import js.lang.Config;
import js.transaction.Transaction;
import js.transaction.TransactionManager;
import junit.framework.TestCase;

public class TransactionManagerUnitTest extends TestCase {
	public void testManualTransaction() throws Exception {
		Config config = new Config("test");
		config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		config.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/test");
		config.setProperty("hibernate.connection.password", "test");
		config.setProperty("hibernate.connection.username", "test");
		config.setProperty("hibernate.default_schema", "test");
		config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		config.setProperty("hibernate.show_sql", "false");

		TransactionManager manager = new TransactionManagerImpl();
		manager.config(config);

		Transaction t = manager.createTransaction();
		try {
			// execute transactional working unit
			t.commit();
		} catch (Exception e) {
			t.rollback();
		} finally {
			t.close();
		}
	}
}
