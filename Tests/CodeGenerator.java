import java.util.Random;

public class CodeGenerator {

    private static final String ALPHANUMERIC = "123456789abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC = "1234567890";

    public static String makeSegment(String text, int length) {
        Random random = new Random();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i <= length; i++) {
            int index = random.nextInt(text.length());
            result.append(text.charAt(index));
        }

        return result.toString();
    }

    public static String generateCodeNumbers(int length, String delimiter) {
        return makeSegment(NUMERIC, length) + delimiter + makeSegment(NUMERIC, length);
    }

    public static String generateCodeAlphaNumeric(int length, String delimiter) {
        return makeSegment(ALPHANUMERIC, length) + delimiter + makeSegment(ALPHANUMERIC, length);
    }
}
