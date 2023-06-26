import data.Token;
import models.Code;
import org.junit.jupiter.api.Test;
import models.Account;
import data.Database;
import models.User;
import org.sqlite.SQLiteConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoadingDataTests {
    @Test
    void userLoginLoaded() {
        Database.setConnectionSource("./Tests/test.db");

        Token token = Database.authenticateUser("Hannan", "12345");
        assertNotNull(token);

        assertTrue(Database.checkUsername("haNNan"));

        Database.logUserOut(token);
        Database.disconnect();
    }

    @Test
    void userAccountLoaded() {
        Database.setConnectionSource("./Tests/test.db");

        Token token = Database.authenticateUser("Hannan", "12345");
        assertNotNull(token);

        User user = Database.getUser(token);
        assertNotNull(user);
        assertEquals(user.getUsername(), "hannan");

        List<Account> accounts = Database.getAccounts(token);
        assertEquals(accounts.size(), 1);
        Account account = accounts.get(0);
        assertEquals(account.getName(), "Joe");
        assertEquals(account.getSocialMediaType(), "1234");

        List<Code> codes = Database.getCodes(token, account.getID());
        assertEquals(codes.size(), 3);
        for (Code c : codes) {
            assertEquals(c.getCode(), "1234");
        }

        Database.logUserOut(token);
        Database.disconnect();

        try {
            Files.deleteIfExists(Path.of("./Tests/test.db"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}