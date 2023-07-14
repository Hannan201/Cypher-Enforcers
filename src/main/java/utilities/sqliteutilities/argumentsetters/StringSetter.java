package utilities.sqliteutilities.argumentsetters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class allows to set the argument of a SQLite prepared statement to
 * be a String.
 */
public class StringSetter extends ArgumentSetter<String> {

    private static final Logger logger = LoggerFactory.getLogger(StringSetter.class);

    /**
     * Create a new String setter.
     *
     * @param value The String value to be set.
     */
    public StringSetter(String value) {
        super(value);
    }

    /**
     * Set the argument of a prepared statement at a specific index to
     * be a string.
     *
     * @param statement The prepared statement.
     * @param index The index at which the argument needs to be set.
     */
    @Override
    public void setArgument(PreparedStatement statement, int index) {
        try {
            statement.setString(index, value);
        } catch (SQLException e) {
            logger.warn(String.format("Failed to set argument %s at index %d. Cause: ", value, index), e);
            e.printStackTrace();
        }
    }
}
