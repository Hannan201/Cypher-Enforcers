package cypher.enforcers.data.implementations;

import cypher.enforcers.code.Code;
import cypher.enforcers.data.spis.CodeDAO;
import cypher.enforcers.data.spis.CodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cypher.enforcers.models.Account;
import cypher.enforcers.annotations.SimpleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 Implementation for the code Repository. Behaves as a collection of
 accounts to help ease on making any changes.
 */
public class CodeRepositoryImpl implements CodeRepository {

    // Logger for the code repository.
    private static final Logger logger = LoggerFactory.getLogger(CodeRepositoryImpl.class);

    // To make updates to the database.
    @SimpleService
    private CodeDAO codeDAO;

    /**
     * Create a new code.
     *
     * @param code The code to add.
     * @return True if the code was added, otherwise false.
     */
    @Override
    public boolean create(Code code) {
        logger.trace("Attempting to create code {} for account with ID {}.", code.getCode(), code.getAccountID());

        boolean result = codeDAO.addCode(code);

        if (result) {
            logger.trace("Code {} created for account with ID {}", code.getCode(), code.getAccountID());
        } else {
            logger.warn("Unable to create code {} for account with ID {}.", code.getCode(), code.getAccountID());
        }

        return result;
    }

    /**
     * Read all codes for an account.
     *
     * @param account Account to retrieve the codes for.
     * @return List of codes. Empty list is returned if no codes
     * are present.
     */
    @Override
    public List<Code> readAll(Account account) {
        logger.trace("Attempting to get all codes for account with ID {}.", account.getID());

        List<Code> result = codeDAO.getCodes(account.getID());

        if (result == null) {
            logger.warn("No accounts found for account with ID {}.", account.getID());
            return new ArrayList<>();
        }

        logger.trace("Accounts retrieved for account with ID {}.", account.getID());
        return result;
    }

    /**
     * Read a code by its ID.
     *
     * @param codeID ID of the code.
     * @return An Optional containing the code. Null otherwise.
     */
    @Override
    public Optional<Code> read(long codeID) {
        logger.trace("Attempting to get code with ID {}.", codeID);

        Optional<Code> result = codeDAO.getCode(codeID);

        if (result.isPresent()) {
            logger.trace("Code found.");
        } else {
            logger.warn("Failed to get code with ID {}.", codeID);
        }

        return result;
    }

    /**
     * Update a code.
     *
     * @param code The code to update.
     * @return True if successfully updated, false otherwise.
     */
    @Override
    public boolean update(Code code) {
        logger.trace("Attempting to update code with ID {} to {}.", code.getId(), code.getCode());

        boolean result = codeDAO.updateCode(code);

        if (result) {
            logger.trace("Update code successfully.");
        } else {
            logger.warn("Failed to update code with ID {} to {}.", code.getId(), code.getCode());
        }

        return result;
    }

    /**
     * Delete a code.
     *
     * @param code The code to delete.
     * @return True if the code was deleted successfully, false otherwise.
     */
    @Override
    public boolean delete(Code code) {
        logger.trace("Attempting to delete code with ID {}.", code.getId());

        boolean result = codeDAO.removeCode(code);

        if (result) {
            logger.trace("Deleted code successfully.");
        } else {
            logger.warn("Failed to delete code with ID {}.", code.getId());
        }

        return result;
    }

    /**
     * Delete all codes for an account.
     *
     * @param account The accounts to delete the codes for.
     * @return True if all codes were deleted successfully, false
     * otherwise.
     */
    @Override
    public boolean deleteAll(Account account) {
        logger.trace("Attempting to delete all codes for account with ID {}.", account.getID());

        boolean result = codeDAO.clearAllCodes(account);

        if (result) {
            logger.trace("Deleted all codes successfully.");
        } else {
            logger.warn("Failed to delete all codes for account with ID {}.", account.getID());
        }

        return result;
    }
}