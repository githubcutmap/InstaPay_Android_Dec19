package gramtarang.instamoney.aeps;

public class AepsReport {
    private String orderId;
    private String timeStamp;
    private String agentId;
    private String status;
    private String message;
    private String customerName;
    private String transType;
    private String TransId;
    private String commision;
    private String amount;

    public AepsReport(String orderId, String timeStamp, String agentId, String status, String message, String customerName, String transType, String transId, String commision, String amount) {
        this.orderId = "orderID:"+orderId;
        this.timeStamp = timeStamp;
        this.agentId = agentId;
        this.status = status;
        this.message = message;
        this.customerName = customerName;
        this.transType = transType;
        TransId = transId;
        this.commision = commision;
        this.amount = amount;
    }



    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTransId() {
        return TransId;
    }

    public void setTransId(String transId) {
        TransId = transId;
    }

    public String getCommision() {
        return commision;
    }

    public void setCommision(String commision) {
        this.commision = commision;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


}
