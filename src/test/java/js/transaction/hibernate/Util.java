package js.transaction.hibernate;

import js.transaction.TransactionException;
import js.transaction.TransactionManager;
import js.util.Classes;

import org.hibernate.Session;

final class Util {
	static final String TRANSACTION_IMPL = "js.transaction.hibernate.TransactionManagerImpl";

	static Session getSession(TransactionManager transactionManager) {
		Object adapter = Classes.getFieldValue(transactionManager, "adapter");
		try {
			return Classes.invoke(adapter, "getSession");
		} catch (TransactionException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
