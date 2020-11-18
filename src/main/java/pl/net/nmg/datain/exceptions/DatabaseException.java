package pl.net.nmg.datain.exceptions;

/**
 *
 * @author greg
 */
public class DatabaseException extends Exception {

    public static final int UNKNOWN = 0;
    public static final int NOT_AVAILABLE = 1;
    public static final int TABLE_CREATION_ERROR = 2;
    public static final int NOT_CONFIGURED = 3;

    private int code = UNKNOWN;
    private String message;

    public DatabaseException(int code) {
        this.code = code;
        switch (code) {
            case DatabaseException.NOT_CONFIGURED:
                message = "database not configured";
            case DatabaseException.TABLE_CREATION_ERROR:
                message = "table creation error";
                break;
            case DatabaseException.NOT_AVAILABLE:
                message = "not available";
                break;
            case DatabaseException.UNKNOWN:
                message = "unknown error";
                break;
            default:
                message = "unknown error";
                break;
        }
    }

    public DatabaseException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

}
