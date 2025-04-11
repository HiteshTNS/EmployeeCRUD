package org.tns.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardResponse<T> {

    private int responseCode;
    private String responseDiscription; // spelling kept as you wrote
    private T data;

    public StandardResponse() {
    }

    public StandardResponse(int responseCode, String responseDiscription, T data) {
        this.responseCode = responseCode;
        this.responseDiscription = responseDiscription;
        this.data = data;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDiscription() {
        return responseDiscription;
    }

    public void setResponseDiscription(String responseDiscription) {
        this.responseDiscription = responseDiscription;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
