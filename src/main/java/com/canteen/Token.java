package com.canteen;

public class Token {

    private int tokenId;
    private int orderId;
    private int tokenNumber;
    private String tokenStatus;

    public Token(int tokenId, int orderId, int tokenNumber, String tokenStatus) {
        this.tokenId = tokenId;
        this.orderId = orderId;
        this.tokenNumber = tokenNumber;
        this.tokenStatus = tokenStatus;
    }

    public Token(int orderId, int tokenNumber) {
        this.orderId = orderId;
        this.tokenNumber = tokenNumber;
        this.tokenStatus = "Waiting";
    }

    public int getTokenId() {
        return tokenId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public String getTokenStatus() {
        return tokenStatus;
    }

    public void setTokenStatus(String tokenStatus) {
        this.tokenStatus = tokenStatus;
    }
}