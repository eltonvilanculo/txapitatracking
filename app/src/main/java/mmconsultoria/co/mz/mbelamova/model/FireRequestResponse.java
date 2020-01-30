package mmconsultoria.co.mz.mbelamova.model;

public class FireRequestResponse {
    private int errorCode;
    private int code;
    private String serverCode;
    private String serverResponse;
    private String message;
    private long date;

    public FireRequestResponse() {
    }

    public FireRequestResponse(int errorCode, int code, String serverResponse, String message) {
        this.errorCode = errorCode;
        this.code = code;
        this.serverResponse = serverResponse;
        this.message = message;
    }

    public boolean isSucessful() {
        return code == 200 || code == 201;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getServerResponse() {
        return serverResponse;
    }

    public void setServerResponse(String serverResponse) {
        this.serverResponse = serverResponse;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "FireRequestResponse{" +
                "errorCode=" + errorCode +
                ", code=" + code +
                ", serverResponse='" + serverResponse + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public enum Type {
        MPESA, CREDIT_CARD, AVAILABLE_BALANCE
    }
}
