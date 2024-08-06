package com.family.common.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 异常日志表
 * @TableName exception_log
 */
@TableName(value ="exception_log")
@Data
public class ExceptionLog implements Serializable {
    /**
     * 主键ID，自动递增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 异常信息
     */
    private String exceptionMessage;

    /**
     * 异常堆栈信息
     */
    private String exceptionStackTrace;

    /**
     * 异常发生的时间
     */
    private LocalDateTime timestamp;

    /**
     * 请求的端点URL
     */
    private String endpoint;

    /**
     * HTTP请求方法
     */
    private String httpMethod;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 请求头信息
     */
    private String requestHeaders;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 客户端IP地址
     */
    private String clientIp;

    /**
     * 引发异常的类
     */
    private String exceptionClass;

    /**
     * 引发异常的方法
     */
    private String exceptionMethod;

    /**
     * 用户ID
     */
    private Long userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ExceptionLog other = (ExceptionLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getExceptionMessage() == null ? other.getExceptionMessage() == null : this.getExceptionMessage().equals(other.getExceptionMessage()))
            && (this.getExceptionStackTrace() == null ? other.getExceptionStackTrace() == null : this.getExceptionStackTrace().equals(other.getExceptionStackTrace()))
            && (this.getTimestamp() == null ? other.getTimestamp() == null : this.getTimestamp().equals(other.getTimestamp()))
            && (this.getEndpoint() == null ? other.getEndpoint() == null : this.getEndpoint().equals(other.getEndpoint()))
            && (this.getHttpMethod() == null ? other.getHttpMethod() == null : this.getHttpMethod().equals(other.getHttpMethod()))
            && (this.getRequestParams() == null ? other.getRequestParams() == null : this.getRequestParams().equals(other.getRequestParams()))
            && (this.getRequestHeaders() == null ? other.getRequestHeaders() == null : this.getRequestHeaders().equals(other.getRequestHeaders()))
            && (this.getSessionId() == null ? other.getSessionId() == null : this.getSessionId().equals(other.getSessionId()))
            && (this.getUserAgent() == null ? other.getUserAgent() == null : this.getUserAgent().equals(other.getUserAgent()))
            && (this.getClientIp() == null ? other.getClientIp() == null : this.getClientIp().equals(other.getClientIp()))
            && (this.getExceptionClass() == null ? other.getExceptionClass() == null : this.getExceptionClass().equals(other.getExceptionClass()))
            && (this.getExceptionMethod() == null ? other.getExceptionMethod() == null : this.getExceptionMethod().equals(other.getExceptionMethod()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getExceptionMessage() == null) ? 0 : getExceptionMessage().hashCode());
        result = prime * result + ((getExceptionStackTrace() == null) ? 0 : getExceptionStackTrace().hashCode());
        result = prime * result + ((getTimestamp() == null) ? 0 : getTimestamp().hashCode());
        result = prime * result + ((getEndpoint() == null) ? 0 : getEndpoint().hashCode());
        result = prime * result + ((getHttpMethod() == null) ? 0 : getHttpMethod().hashCode());
        result = prime * result + ((getRequestParams() == null) ? 0 : getRequestParams().hashCode());
        result = prime * result + ((getRequestHeaders() == null) ? 0 : getRequestHeaders().hashCode());
        result = prime * result + ((getSessionId() == null) ? 0 : getSessionId().hashCode());
        result = prime * result + ((getUserAgent() == null) ? 0 : getUserAgent().hashCode());
        result = prime * result + ((getClientIp() == null) ? 0 : getClientIp().hashCode());
        result = prime * result + ((getExceptionClass() == null) ? 0 : getExceptionClass().hashCode());
        result = prime * result + ((getExceptionMethod() == null) ? 0 : getExceptionMethod().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", exceptionMessage=").append(exceptionMessage);
        sb.append(", exceptionStackTrace=").append(exceptionStackTrace);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", endpoint=").append(endpoint);
        sb.append(", httpMethod=").append(httpMethod);
        sb.append(", requestParams=").append(requestParams);
        sb.append(", requestHeaders=").append(requestHeaders);
        sb.append(", sessionId=").append(sessionId);
        sb.append(", userAgent=").append(userAgent);
        sb.append(", clientIp=").append(clientIp);
        sb.append(", exceptionClass=").append(exceptionClass);
        sb.append(", exceptionMethod=").append(exceptionMethod);
        sb.append(", userId=").append(userId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}