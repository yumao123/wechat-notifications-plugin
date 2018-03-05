package com.qy.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Marvin on 16/10/8.
 */
public class WechatServiceImpl implements WechatService {

    private Logger logger = LoggerFactory.getLogger(WechatService.class);

    private String buildurl;

    private String corpid;

    private String corpsecret;

    private String agentid;

    private String memberIds;

    private String file;

    private String link;

    private String reportFile;

    private String reportLink;

    private boolean onStart;

    private boolean onSuccess;

    private boolean onFailed;

    private TaskListener listener;

    private AbstractBuild build;

    private String token = "";

    private String proName;

    private String displayName;

    private String currentTime = new SimpleDateFormat("E yyyy.MM.dd hh:mm:ss").format(new Date());

    public WechatServiceImpl(String buildurl, String corpid, String corpsecret, String agentid, String memberIds, String file,
                             String link, String reportFile, String reportLink, boolean onStart, boolean onSuccess, boolean onFailed, TaskListener listener,
                             AbstractBuild build) {
        this.buildurl = buildurl;
        this.corpid = corpid;
        this.corpsecret = corpsecret;
        this.agentid = agentid;
        this.memberIds = memberIds;
        this.file = file;
        this.link = link;
        this.reportFile = reportFile;
        this.reportLink = reportLink;
        this.onStart = onStart;
        this.onSuccess = onSuccess;
        this.onFailed = onFailed;
        this.listener = listener;
        this.build = build;

        proName = build.getProject().getDisplayName();
        displayName = build.getDisplayName();
    }

    /**
     * 获取测试报告路径
     * 现在测试报告路径以打包的ID分辨,所以这里通过替换$BUILD_ID生成报告路径
     * @return
     */
    private String getReportPath(){
        return reportLink.endsWith("/") ?
                reportLink + reportFile.trim().replace("$BUILD_ID", build.getId()) :
                reportLink + "/" + reportFile.trim().replace("$BUILD_ID", build.getId());
    }

    /**
     * 获取下载路径
     * 现在安卓端打包的后缀都是年月日,所以在这里通过替换$DATE生成文件路径
     * @return
     */
    private String getDownloadPath(){
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return link.endsWith("/") ?
                link + file.trim().replace("$DATE", date) :
                link + "/" + file.trim().replace("$DATE", date);
    }

    @Override
    public void start() {
        if (onStart) {
            sendLinkMessage(
                    String.format(Messages.WechatNotifier_startContent(), proName, displayName));
        }
    }

    @Override
    public void success() {
        if (onSuccess) {
            sendLinkMessage(
                    String.format(Messages.WechatNotifier_successContent(),
                            currentTime, build.getProject().getDisplayName(), displayName, build.getBuildStatusSummary().message, build.getDurationString(),
                            getDownloadPath(), getReportPath()));
        }
    }

    @Override
    public void failed() {
        if (onFailed) {
            sendLinkMessage(
                    String.format(Messages.WechatNotifier_failedContent(),
                            currentTime, build.getProject().getDisplayName(),displayName, build.getBuildStatusSummary().message, build.getDurationString()));
        }
    }

    /**
     * 群发消息
     * @param msg
     */
    private void sendLinkMessage(String msg) {
        if (this.token.equals("")){
            updateToken();
        }
        HttpClient client = getHttpClient();
        PostMethod post = new PostMethod(String.format(Messages.WechatNotifier_sendMsgApi(), token));

        TextMessage textMessage = new TextMessage(memberIds, "", "", "text", agentid, "0", new Text(msg));
        String body = JSON.toJSONString(textMessage);

        try {
            logger.info("Send msg:" + body);
            listener.getLogger().println("Send msg:" + body);
            post.setRequestEntity(new StringRequestEntity(body, "application/json", "UTF-8"));
            client.executeMethod(post);
            logger.info(post.getResponseBodyAsString());
        } catch (IOException e) {
            listener.getLogger().println("build request error: " + e);
            logger.error("send msg error", e);
        }
        post.releaseConnection();
    }

    /**
     * 更新access_token
     */
    private void updateToken(){
        HttpClient client = getHttpClient();
        GetMethod get = new GetMethod(String.format(Messages.WechatNotifier_updateTokenApi(), corpid, corpsecret));
        try {
            client.executeMethod(get);
            logger.info(get.getResponseBodyAsString());
            listener.getLogger().println(get.getResponseBodyAsString());
            String responseBody = get.getResponseBodyAsString();
            JSONObject object = JSONObject.parseObject(responseBody);
            this.token = (String)object.get("access_token");
        }catch (IOException e){
            listener.getLogger().println("build request error: " + e);
            logger.error("get token error", e);
        }
    }

    private HttpClient getHttpClient() {
        HttpClient client = new HttpClient();
        return client;
    }
}
