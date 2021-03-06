package at.fhj.swd13.pse.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.RollbackException;

import org.eclipse.persistence.exceptions.DatabaseException;

import at.fhj.swd13.pse.db.dao.CommunityDAO;
import at.fhj.swd13.pse.db.dao.CommunityDAOImpl;
import at.fhj.swd13.pse.db.dao.PersonDAO;
import at.fhj.swd13.pse.db.dao.PersonDAOImpl;
import at.fhj.swd13.pse.db.dao.TagDAO;
import at.fhj.swd13.pse.db.dao.TagDAOImpl;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

/**
 * Implementaition fo a db session using jpa/ mysql
 *
 */
public class DbContextImpl implements AutoCloseable, DbContext {

	private static final String PERSISTENCE_UNIT_NAME = "pseDbModell";

	private static EntityManagerFactory factory;

	private final EntityManager entityManager;

	private EntityTransaction transaction;

	/**
	 * 'Create' a new session with the database and begin the transaction
	 */
	public DbContextImpl() {
		assertStatics();

		entityManager = factory.createEntityManager();

		transaction = beginTransaction();
		transaction.begin();

	}

	/* (non-Javadoc)
	 * @see at.fhj.swd13.pse.db.DbContext#clearCache()
	 */
	public void clearCache() {
		
		entityManager.getEntityManagerFactory().getCache().evictAll();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhjoanneum.swd13.pse.db.DbContext#persist(java.lang.Object)
	 */
	@Override
	public void persist(final Object target) {

		entityManager.persist(target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhj.swd13.pse.db.DbContext#remove(java.lang.Object)
	 */
	public void remove(final Object target) {

		entityManager.remove(target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhjoanneum.swd13.pse.db.DbContext#commit()
	 */
	@Override
	public void commit() {

		if (transaction == null || !transaction.isActive()) {

			throw new IllegalStateException("no transaction open");
		}

		try {

			transaction.commit();

		} catch (RollbackException e) {

			if (e.getCause() != null && e.getCause().getClass() == DatabaseException.class) {

				final DatabaseException dbx = (DatabaseException) e.getCause();

				if (dbx.getInternalException() != null
						&& dbx.getInternalException().getClass() == MySQLIntegrityConstraintViolationException.class) {

					throw new ConstraintViolationException("Person already exists", e);
				}

				throw e;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhjoanneum.swd13.pse.db.DbContext#rollback()
	 */
	@Override
	public void rollback() {

		if (transaction == null || !transaction.isActive()) {

			throw new IllegalStateException("no transaction open");
		}

		transaction.rollback();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhjoanneum.swd13.pse.db.DbContext#createQuery(java.lang.String)
	 */
	@Override
	public Query createQuery(final String queryString) {

		return entityManager.createQuery(queryString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhj.swd13.pse.db.DbContext#createNamedQuery(java.lang.String)
	 */
	@Override
	public Query createNamedQuery(final String queryName) {

		return entityManager.createNamedQuery(queryName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhjoanneum.swd13.pse.db.DbContext#getEntityManager()
	 */
	@Override
	public EntityManager getEntityManager() {

		return entityManager;
	}

	private EntityTransaction beginTransaction() {

		return entityManager.getTransaction();
	}

	private void assertStatics() {

		if (null == factory) {
			factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhj.swd13.pse.db.DbContext#getPersonDAO()
	 */
	public PersonDAO getPersonDAO() {

		return new PersonDAOImpl(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhj.swd13.pse.db.DbContext#getTagDAO()
	 */
	public TagDAO getTagDAO() {

		return new TagDAOImpl(this);
	}

	
	/* (non-Javadoc)
	 * @see at.fhj.swd13.pse.db.DbContext#getCommunityDAO()
	 */
	public CommunityDAO getCommunityDAO() {
		
		return new CommunityDAOImpl( this );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhjoanneum.swd13.pse.db.DbContext#close()
	 */
	@Override
	public void close() throws Exception { 

		if (transaction != null) {

			if (transaction.isActive()) {
				transaction.rollback();
			}

			transaction = null; 
		}

		entityManager.close();
	}
}
