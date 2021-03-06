package com.qy.wechat.module;

/**
 * Created by Administrator on 2018/3/2.
 */
public class CardMessage {

    private String touser;

    private String toparty;

    private String totag;

    private String msgtype;

    private String agentid;

    private String safe;

    private TextCard textcard;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getToparty() {
        return toparty;
    }

    public void setToparty(String toparty) {
        this.toparty = toparty;
    }

    public String getTotag() {
        return totag;
    }

    public void setTotag(String totag) {
        this.totag = totag;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }

    public String getSafe() {
        return safe;
    }

    public void setSafe(String safe) {
        this.safe = safe;
    }

    public TextCard getTextcard() {
        return textcard;
    }

    public void setTextcard(TextCard textcard) {
        this.textcard = textcard;
    }

    public CardMessage(String touser, String toparty, String totag, String msgtype, String agentid, String safe, TextCard textcard) {
        this.touser = touser;
        this.toparty = toparty;
        this.totag = totag;
        this.msgtype = msgtype;
        this.agentid = agentid;
        this.safe = safe;
        this.textcard = textcard;
    }
}
