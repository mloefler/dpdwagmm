package at.fhj.swd13.pse.db.dao;

import java.util.List;

import javax.persistence.Query;

import at.fhj.swd13.pse.db.DAOBase;
import at.fhj.swd13.pse.db.DbContext;
import at.fhj.swd13.pse.db.entity.Tag;

public class TagDAOImpl extends DAOBase implements TagDAO {

	public TagDAOImpl(DbContext dbContext) {

		super(dbContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.fhj.swd13.pse.db.dao.TagDAO#insert(at.fhj.swd13.pse.db.entity.Tag)
	 */
	public void insert(Tag tag) {

		dbContext.persist(tag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhj.swd13.pse.db.TagDAO#getById(int)
	 */
	@Override
	public Tag getById(int tagId) {

		final Query q = dbContext.createNamedQuery("Tag.findById");
		q.setParameter("id", tagId);

		return fetchSingle(q);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhj.swd13.pse.db.dao.TagDAO#getByTokenLike(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Tag> getByTokenLike(String beginning) {

		final Query q = dbContext.createNamedQuery("Tag.findByTokenLike");
		q.setParameter("token", beginning + "%");

		return q.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.fhj.swd13.pse.db.TagDAO#remove(int)
	 */
	public void remove(int tagId) {

		final Query q = dbContext.createNamedQuery("Tag.deleteById");
		q.setParameter("id", tagId);

		q.executeUpdate();
	}
}
