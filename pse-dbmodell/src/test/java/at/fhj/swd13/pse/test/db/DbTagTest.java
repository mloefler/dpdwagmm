package at.fhj.swd13.pse.test.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import at.fhj.swd13.pse.db.ConstraintViolationException;
import at.fhj.swd13.pse.db.DbContext;
import at.fhj.swd13.pse.db.DbContextProvider;
import at.fhj.swd13.pse.db.DbContextProviderImpl;
import at.fhj.swd13.pse.db.dao.TagDAO;
import at.fhj.swd13.pse.db.entity.Tag;

public class DbTagTest {

	private DbContextProvider contextProvider;

	@Before
	public void setup() {
		contextProvider = new DbContextProviderImpl();
	}

	@Test
	public void insertRemoveCycle() throws Exception {

		Tag t = new Tag("Ballroom", "Alles was mit Tanzen zu tun hat.");

		try (DbContext dbContext = contextProvider.getDbContext()) {

			TagDAO tagDAO = dbContext.getTagDAO();

			tagDAO.insert(t);


			dbContext.commit();

			assertFalse(t.getTagId() == 0);
		}

		try (DbContext dbContext = contextProvider.getDbContext()) {

			TagDAO tagDAO = dbContext.getTagDAO();

			Tag x = tagDAO.getById(t.getTagId());

			assertEquals(t.getTagId(), x.getTagId());
			assertEquals(t.getToken(), x.getToken());
		}

		try (DbContext dbContext = contextProvider.getDbContext()) {

			TagDAO tagDAO = dbContext.getTagDAO();

			tagDAO.remove(t.getTagId());
			
			dbContext.commit();
		}

		try (DbContext dbContext = contextProvider.getDbContext()) {

			TagDAO tagDAO = dbContext.getTagDAO();

			assertNull(tagDAO.getById(t.getTagId()));
		}
	}
	
	@Test
	public void insertDuplicate() throws Exception {

		Tag t = new Tag("Ballroom", "Alles was mit Tanzen zu tun hat.");

		try (DbContext dbContext = contextProvider.getDbContext()) {

			TagDAO tagDAO = dbContext.getTagDAO();

			tagDAO.insert(t);


			dbContext.commit();

			assertFalse(t.getTagId() == 0);
		}
		
		Tag t2 = new Tag("Ballroom", "Alles was mit Tanzen zu tun hat.");

		try (DbContext dbContext = contextProvider.getDbContext()) {
			TagDAO tagDAO = dbContext.getTagDAO();

			tagDAO.insert(t2);


			dbContext.commit();
			fail("Expected ConstraintViolation (duplicte key)");
		}
		catch( ConstraintViolationException e ) {
			
		}

		try (DbContext dbContext = contextProvider.getDbContext()) {

			TagDAO tagDAO = dbContext.getTagDAO();

			tagDAO.remove(t.getTagId());
			
			dbContext.commit();
		}		
	}	
}
