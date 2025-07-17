package org.example.vitraya_sso_token.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserData {
    @JsonProperty("createdat")
    private String createdat;

    @JsonProperty("isnewuser")
    private boolean isnewuser;

    @JsonProperty("lastlogintype")
    private String lastlogintype;

    private String cellnumber;
    private String blocked;
    private String userid;
    private String email;

    public UserData() {}

    public String getCreatedat() {
        return createdat;
    }

    public void setCreatedat(String createdat) {
        this.createdat = createdat;
    }

    public boolean isIsnewuser() {
        return isnewuser;
    }

    public void setIsnewuser(boolean isnewuser) {
        this.isnewuser = isnewuser;
    }

    public String getLastlogintype() {
        return lastlogintype;
    }

    public void setLastlogintype(String lastlogintype) {
        this.lastlogintype = lastlogintype;
    }

    public String getCellnumber() {
        return cellnumber;
    }

    public void setCellnumber(String cellnumber) {
        this.cellnumber = cellnumber;
    }

    public String getBlocked() {
        return blocked;
    }

    public void setBlocked(String blocked) {
        this.blocked = blocked;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}