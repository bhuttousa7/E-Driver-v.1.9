package com.telogix.telogixcaptain.Pojo;


public class ExcelPojo {
    // variable name should be same as in the json response from php
    private String Status;

    private String Message;

    private com.telogix.telogixcaptain.Pojo.Data[] Data;

    public String getStatus ()
    {
        return Status;
    }

    public void setStatus (String Status)
    {
        this.Status = Status;
    }

    public String getMessage ()
    {
        return Message;
    }

    public void setMessage (String Message)
    {
        this.Message = Message;
    }

    public com.telogix.telogixcaptain.Pojo.Data[] getData ()
    {
        return Data;
    }

    public void setData (com.telogix.telogixcaptain.Pojo.Data[] Data)
    {
        this.Data = Data;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Status = "+Status+", Message = "+Message+", Data = "+Data+"]";
    }

}

