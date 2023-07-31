package com.Me.ShiftBoard.Util;


import lombok.Data;
@Data
public class Response {

    private String serverError = "";
    private String failureReason = "";

    private Status operationStatus;

    private Object data;

    private String warning="";



    public void setOperationStatus(Status s,Object data){
        if(s.equals(Status.Success))
        {
            this.data = data;

        } else if (s.equals(Status.Failure)) {
            this.failureReason = data.toString();
        }
        else{
            this.serverError = data.toString();
        }

        this.operationStatus = s;
    }
}
