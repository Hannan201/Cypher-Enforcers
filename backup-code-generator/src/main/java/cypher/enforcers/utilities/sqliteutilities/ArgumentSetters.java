package cypher.enforcers.utilities.sqliteutilities;

import cypher.enforcers.data.entities.CodeEntity;
import cypher.enforcers.data.entities.AccountEntity;
import cypher.enforcers.data.entities.UserEntity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * This class is used to define how different types of objects should be
 * set when being used as placeholders in an SQL query.
 */
public class ArgumentSetters {

    /**
     * Private constructor for the argument setters to set certain types
     * of arguments for a prepared SQL statement.
     * No reason to make an instance of this object, instead use the static
     * methods provided.
     * <br>
     * Mainly here to avoid warnings.
     */
    private ArgumentSetters() {

    }

    /** How a user should be added to the database. */
    private static final BiConsumer<PreparedStatement, UserEntity> FOR_USER = (statement, user) -> {
        try {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    /** How an account should be added to the database. */
    private static final BiConsumer<PreparedStatement, AccountEntity> FOR_ACCOUNT = (statement, account) -> {
        try {
            statement.setLong(1, account.getUserId());
            statement.setString(2, account.getName());
            statement.setString(3, account.getSocialMediaType());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    /** How a code should be added to the database. */
    private static final BiConsumer<PreparedStatement, CodeEntity> FOR_CODE = (statement, code) -> {
        try {
            statement.setLong(1, code.getAccountID());
            statement.setString(2, code.getCode());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    /** How an integer should be added to the database. */
    private static final TriConsumer<PreparedStatement, Integer, Object> FOR_INTEGER = (statement, index, value) -> {
        try {
            statement.setInt(index, (Integer) value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    /** How a long should be added to the database. */
    private static final TriConsumer<PreparedStatement, Integer, Object> FOR_LONG = (statement, index, value) -> {
        try {
            statement.setLong(index, (Long) value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    /**
     * How a long should be added to the database, if it's the only
     * argument being passed in.
     */
    private static final BiConsumer<PreparedStatement, Long> FOR_ONE_LONG = (statement, value) -> {
        try {
            statement.setLong(1, value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    /** How a string should be added to the database. */
    private static final TriConsumer<PreparedStatement, Integer, Object> FOR_STRING = (statement, index, value) -> {
        try {
            statement.setString(index, (String) value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    /**
     * Maps the type of object to how it should be set for the
     * placeholders.
     */
    private static final Map<Class<?>, BiConsumer<PreparedStatement, ?>> OBJECT_TYPE_TO_SETTER =
            Map.ofEntries(
                    Map.entry(UserEntity.class, FOR_USER),
                    Map.entry(AccountEntity.class, FOR_ACCOUNT),
                    Map.entry(Long.class, FOR_ONE_LONG),
                    Map.entry(CodeEntity.class, FOR_CODE)
            );

    /**
     * Maps the type of object to which index it should be added.
     * Since the data being added might not all belong to one class.
     */
    private static final Map<Class<?>, TriConsumer<PreparedStatement, Integer, Object>> SINGLE_TYPE_TO_SETTER =
            Map.ofEntries(
                    Map.entry(Integer.class, FOR_INTEGER),
                    Map.entry(Long.class, FOR_LONG),
                    Map.entry(String.class, FOR_STRING)
            );

    /**
     * Get the argument setter when related data in one class is being
     * used for the placeholder.
     *
     * @param clazz The socialMediaType of object being added.
     * @return A BiConsumer that knows how to add this object for the
     * SQL query.
     */
    public static BiConsumer<PreparedStatement, ?> getObjectSetter(Class<?> clazz) {
        return OBJECT_TYPE_TO_SETTER.get(clazz);
    }

    /**
     * Get the argument setter for that takes in an index to where
     * the object should be added.
     *
     * @param clazz The socialMediaType of object being added.
     * @return A TriConsumer that knows how to add the argument to the
     * placeholder with the index being specified.
     */
    public static TriConsumer<PreparedStatement, Integer, Object> getSetter(Class<?> clazz) {
        return SINGLE_TYPE_TO_SETTER.get(clazz);
    }
}