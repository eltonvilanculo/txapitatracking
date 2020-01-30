package mmconsultoria.co.mz.mbelamova.mpesaapi;

public class Result {
    private String conversationId;
    private String resultCode;
    private String transactionId;
    private String status;

    /**
     * @return the conversationId
     */
    public String getConversationId() {
        return conversationId;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return the resultCode
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * @param resultCode the resultCode to set
     */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * @param conversationId the conversationId to set
     */
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public String toString() {
        return "Result{" + "resultCode" + resultCode + "converstionId" + conversationId + "transactionId"
                + transactionId + "status" + status + "}";
    }
}