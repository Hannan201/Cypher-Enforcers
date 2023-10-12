package cypher.enforcers.utilities.sqliteutilities;

import cypher.enforcers.utilities.Utilities;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cypher.enforcers.utilities.sqliteutilities.argumentsetters.ArgumentSetter;
import cypher.enforcers.utilities.sqliteutilities.retrievers.IntegerRetriever;
import javafx.util.Callback;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.*;

/*
 This class acts as a utility class to provide a more clean interface
 to interact with a SQLite database.
 */
public class SQLiteHelper {

    private static final Logger logger = LoggerFactory.getLogger(SQLiteHelper.class);

    // Connection to the database.
    private Connection connection;

    /**
     * Create a new SQLite helper.
     *
     * @param path Path to the SQLite file.
     */
    public SQLiteHelper(String path) {
        connect(path);
    }

    /**
     * Connect to a SQLite database.
     *
     * @param path Path to the file.
     */
    public void connect(String path) {
        try {
            Utilities.copyResourceFileIf(path);

            SQLiteConfig configurations = new SQLiteConfig();
            configurations.enforceForeignKeys(true);
            connection = DriverManager.getConnection(
                    "jdbc:sqlite:" +
                            Utilities.getJarParentDirectory() +
                            File.separator +
                            FilenameUtils.getName(
                                    Utilities.loadFileByURL(path).getPath()
                            ),
                    configurations.toProperties()
            );
        } catch (SQLException e) {
            logger.warn("Connection to database failed. No new changes will be saved. Cause: ", e);
            return;
        }

        logger.info("Database connected.");
    }

    /**
     * Check if there's a connection failure.
     *
     * @return True if there is a failure, false otherwise.
     */
    public boolean connectionFailure() {
        try {
            if (connection == null || connection.isClosed()) {
                logger.warn("Database connection is down.");
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.warn("Failed to check connection status. No new changes will be saved. Cause: ", e);
        }

        return true;
    }

    /**
     * Disconnect from the SQLite database.
     */
    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.error("Failed to close connection to database. Cause: ", e);
            return;
        }

        logger.info("Closed connection to database.");
    }

    /**
     * Check if the number of placeholders in an SQL query is equal to
     * an integer.
     *
     * @param query The SQL query.
     * @param count The integer.
     * @return True if the number of placeholders is not equal to the
     * integer, false otherwise.
     */
    private boolean noMatch(String query, int count) {
        return (query.length() - query.replace("?", "").length()) != count;
    }

    /**
     * Execute a prepared SQL query with placeholders (arguments
     * are added from left to right) with a callback function
     * to determine what to do with the result set. This function
     * acts as the base function, almost all other functions act on top
     * of this function with the parameters slightly adjusted.
     *
     * @param query The SQL query.
     * @param flag The flag on whether to return any generated keys or not.
     *             This can either be Statement.NO_GENERATED_KEYS or
     *             Statement.RETURN_GENERATED_KEYS.
     * @param callback A callback function to decide what to do with the result set.
     *                 Null if nothing needs to be done with the result set.
     * @param arguments Arguments to add (from left to right) to the SQL query
     *                  based on the data type of the argument.
     * @param <Q> The data type to return once the result set has been
     *           processed.
     * @return Resulting object after the statement has been executed and
     * processed.
     */
    private <Q> Q execute(String query, int flag, Callback<ResultSet, Q> callback, ArgumentSetter<?> ... arguments) {
        if (noMatch(query, arguments.length)) {
            logger.warn("Number of placeholders in statement does not match number of arguments provided. Aborting request.");
            return null;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                query, flag
        )) {
            for (int i = 0; i < arguments.length; i++) {
                arguments[i].setArgument(statement, i + 1);
            }

            if (callback == null) {
                statement.executeUpdate();
                return null;
            } else if (flag == Statement.NO_GENERATED_KEYS) {
                return callback.call(statement.executeQuery());
            }

            return callback.call(statement.executeQuery());
        } catch (SQLException e) {
            logger.warn(String.format("Failed to execute query: %s. Cause: ", query), e);
        }

        return null;
    }

    /**
     * Execute a prepared SQL query with placeholders (arguments are added
     * from left to right) to make an update to the database.
     *
     * @param query The SQL query.
     * @param arguments Arguments to add (from left to right) to the SQL query
     *                  based on the data type of the argument.
     */
    public void executeUpdate(String query, ArgumentSetter<?> ... arguments) {
        execute(query, Statement.NO_GENERATED_KEYS, null, arguments);
    }

    /**
     * Execute a SELECT prepared SQL statement with a placeholder
     * with a callback function to determine what needs to be done
     * with the result set.
     *
     * @param query The SELECT SQL query.
     * @param setter Argument to add the SQL query based on the data type.
     * @param callback Callback function to determine what needs to be
     *                 done with the result set.
     * @param <Q> The type of data to return once the result set has been
     *           processed.
     * @return Resulting object after the statement has been executed and
     * processed.
     */
    public <Q> Q executeSelect(String query, ArgumentSetter<?> setter, Callback<ResultSet, Q> callback) {
        return execute(query, Statement.NO_GENERATED_KEYS, callback, setter);
    }

    /**
     * Execute a prepared SQL query with placeholders (arguments are added
     * from left to right) to make an update to the database but also
     * return a generated integer key that was created because of the
     * statement.
     *
     * @param query The SQL query.
     * @param key Name of key that was generated.
     * @param arguments Arguments to add (from left to right) to the SQL query
     *                  based on the data type of the argument.
     * @return The integer key if present, null otherwise.
     */
    public Integer executeUpdateWithKey(String query, String key, ArgumentSetter<?> ... arguments) {
        return execute(query, Statement.RETURN_GENERATED_KEYS, new IntegerRetriever(key), arguments);
    }
}