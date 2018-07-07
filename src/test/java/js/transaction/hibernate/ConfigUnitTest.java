package js.transaction.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import js.transaction.TransactionContext;
import js.util.Classes;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
@SuppressWarnings("unused")
public class ConfigUnitTest
{
  @Test
  public void testHibernateAdapterConfig() throws ClassNotFoundException
  {
    String[] confs = new String[]
    {
        "hibernate-jsconfig.xml", "hibernate-mapping-resource-jsconfig.xml"
    };
    for(String conf : confs) {
      TransactionContext database = null;// Factory.getInstance(TransactionContext.class);
      assertNotNull(database);
      assertEquals(database.getClass(), Classes.forName("js.db.HibernateAdapter"));

      SessionFactory sessionFactory = Classes.getFieldValue(database, "sessionFactory");
      assertNotNull(sessionFactory);
      assertEquals(2, sessionFactory.getStatistics().getEntityNames().length);

      if(conf.equals("hibernate-jsconfig.xml")) {
        ClassMetadata classMetadata = sessionFactory.getClassMetadata(Driver2.class);
        assertNotNull(classMetadata);
        assertEquals("js.test.core.Driver2", classMetadata.getEntityName());
      }
      else {
        ClassMetadata classMetadata = sessionFactory.getClassMetadata(Driver1.class);
        assertNotNull(classMetadata);
        assertEquals("js.test.core.Driver1", classMetadata.getEntityName());
      }
    }
  }

  private static class Driver1
  {
    private int id;
    private String name;

    public String getName()
    {
      return this.name;
    }
  }

  private static class Driver2
  {
    private int id;
    private String name;

    public String getName()
    {
      return this.name;
    }
  }
}
