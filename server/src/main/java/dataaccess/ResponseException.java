package dataaccess;

public class ResponseException extends DataAccessException {
    final private int statusCode;
    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int status() {
        return statusCode;
    }

}
