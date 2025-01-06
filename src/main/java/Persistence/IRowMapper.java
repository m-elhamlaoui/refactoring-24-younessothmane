package Persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IRowMapper<T> {
    public T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
