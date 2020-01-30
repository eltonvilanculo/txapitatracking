package mmconsultoria.co.mz.mbelamova.mpesaapi;

import com.google.gson.annotations.SerializedName;

public class RequestPayment {
    @SerializedName("input_Amount")
    private String balance;
    @SerializedName("input_TransactionReference")
    private String transactionRef;
    @SerializedName("input_CustomerMSISDN")
    private String clientSSID;
    @SerializedName("input_ServiceProviderCode")
    private String providerCode;
    @SerializedName("input_ThirdPartyReference")
    private String thirdPartRef;

    @SerializedName("output_ConversationID")
    private String responseConversationId;
    @SerializedName("output_ResponseCode")
    private String responseCode;
    @SerializedName("output_TransactionID")
    private String responseTransactionId;
    @SerializedName("output_ResponseDesc")
    private String responseDescription;

    /**
     * @return the conversationId
     */
    public String getResponseConversationId() {
        return responseConversationId;
    }

    /**
     * @param thirdPartRef the thirdPartRef to set
     */
    public void setThirdPartRef(String thirdPartRef) {
        this.thirdPartRef = thirdPartRef;
    }

    /**
     * @return the thirdPartRef
     */
    public String getThirdPartRef() {
        return thirdPartRef;
    }

    /**
     * @return the responseDescription
     */
    public String getResponseDescription() {
        return responseDescription;
    }

    /**
     * @param responseDescription the responseDescription to set
     */
    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    /**
     * @return the transactionId
     */
    public String getResponseTransactionId() {
        return responseTransactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setResponseTransactionId(String transactionId) {
        this.responseTransactionId = transactionId;
    }

    /**
     * @return the responseCode
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * @param responseCode the responseCode to set
     */
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * @param conversationId the conversationId to set
     */
    public void setResponseConversationId(String conversationId) {
        this.responseConversationId = conversationId;
    }

    /**
     * @param providerCode the providerCode to set
     */
    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    /**
     * @return the providerCode
     */
    public String getProviderCode() {
        return providerCode;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(String balance) {
        this.balance = balance;
    }

    /**
     * @return the balance
     */
    public String getBalance() {
        return balance;
    }

    /**
     * @param clientSSID the clientSSID to set
     */
    public void setClientSSID(String clientSSID) {
        this.clientSSID = clientSSID;
    }

    /**
     * @return the clientSSID
     */
    public String getClientSSID() {
        return clientSSID;
    }

    /**
     * @param transactionReference the transactionReference to set
     */
    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    /**
     * @return the transactionReference
     */
    public String getTransactionRef() {
        return transactionRef;
    }

    @Override
    public String toString() {
        return "Response: {" + "conversationId: " + responseConversationId + "responseCode: " + responseCode
                + "transactionId: " + responseTransactionId + "responseDescription: " + responseDescription + "}";
    }
}