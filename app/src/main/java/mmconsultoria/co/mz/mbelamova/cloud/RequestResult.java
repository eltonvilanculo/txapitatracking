package mmconsultoria.co.mz.mbelamova.cloud;

public enum RequestResult{
    ERR_NETWORK,
    ERR_UNKNOWN,
    ERR_Image_Upload,
    ERR_OPERATION_CANCELED,
    SUCCESSFULL, Location_Updated, Location_Downloaded, no_location, Location_key_exited, Location_key_moved, Location_query_ready, NOTIFICATION_NOT_SENT, ERR_OPERATION_NOT_PERMITED, INVALID_PARAMETERS;


    private Throwable error;

    public RequestResult setError(Throwable error) {
        this.error = error;
        return this;
    }

    public Throwable getError() {
        return error;
    }
}
