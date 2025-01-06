package DAOs;

import java.util.List;
import Persistence.JDBCTemplate;

public abstract class AbstractDAO<T> implements IDAO<T> {
	protected final JDBCTemplate jdbcTemplate = JDBCTemplate.getInstance();

	public abstract void add(T obj);
	public abstract void delete(int id);
	public abstract T getOne(int id);
	public abstract List<T> getAll();
	public abstract  void update(T obj);	
}

