package com.family.common.util;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.family.common.domain.ExceptionLog;
import com.family.common.mapper.ExceptionLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;
import java.util.Enumeration;

/**
 * <p>
 * 异常日志工具类，用于将异常信息记录到数据库中。
 * 通过自动装配的ExceptionLogMapper实例，实现异常日志的持久化。
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-24
 */
@Component
public class ExceptionLogUtil {

    private static ExceptionLogMapper staticExceptionLogMapper;

    @Autowired
    private ExceptionLogMapper exceptionLogMapper;

    /**
     * 在实例初始化后调用此方法，用于设置静态变量staticExceptionLogMapper的值。
     * 此方法利用@PostConstruct注解确保在所有依赖注入完成后调用。
     * 主要目的是为了在静态上下文中使用exceptionLogMapper的功能，
     * 因为静态方法或属性不能直接依赖于通过依赖注入获得的实例。
     */
    @PostConstruct
    public void init() {
        staticExceptionLogMapper = this.exceptionLogMapper;
    }

    /**
     * 保存异常日志到数据库。
     * 此方法封装了异常信息和请求信息，创建了一个异常日志对象，并将其插入到数据库中。
     * 这有助于问题追踪和故障排查。
     *
     * @param e 异常对象，包含了发生的异常信息。
     */
    public static void saveExceptionLog(Exception e) {
        // 从请求上下文中获取HttpServletRequest对象，用于获取请求相关信息。
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 获取异常的详细信息。
        String exceptionMessage = e.getMessage();
        String exceptionStackTrace = getStackTraceAsString(e);
        // 获取当前时间，作为异常发生的时间戳。
        LocalDateTime timestamp = LocalDateTime.now();
        // 获取请求的URI，用于标识请求的具体资源。
        String endpoint = request.getRequestURI();
        // 获取请求的方法，例如GET、POST等。
        String httpMethod = request.getMethod();
        // 获取请求的参数，用于了解请求的具体内容。
        String requestParams = getRequestParams(request);
        // 获取请求的头部信息，例如Content-Type等。
        String requestHeaders = getRequestHeaders(request);
        // 获取会话ID，用于关联请求和会话。
        String sessionId = getSessionId(request);
        // 获取用户代理信息，用于识别请求的来源和环境。
        String userAgent = request.getHeader("User-Agent");
        // 获取客户端的IP地址，用于定位和追踪。
        String clientIp = request.getRemoteAddr();
        // 获取异常发生的类和方法名，用于精确定位问题。
        String exceptionClass = e.getStackTrace()[0].getClassName();
        String exceptionMethod = e.getStackTrace()[0].getMethodName();
        // 获取当前操作的用户ID，用于关联异常和用户行为。
        Long userId = getCurrentUserId();

        // 如果会话ID为空，则设置为"无会话ID"，确保后续处理不会因为空值出错。
        if (sessionId == null) {
            sessionId = "无会话ID";
        }
        // 如果请求参数为空，则设置为"无请求参数"，确保后续处理不会因为空值出错。
        if (requestParams.isEmpty()) {
            requestParams = "无请求参数";
        }

        // 创建异常日志对象，并填充之前获取的信息。
        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setExceptionMessage(exceptionMessage);
        exceptionLog.setExceptionStackTrace(exceptionStackTrace);
        exceptionLog.setCreatedTime(timestamp);
        exceptionLog.setEndpoint(endpoint);
        exceptionLog.setHttpMethod(httpMethod);
        exceptionLog.setRequestParams(requestParams);
        exceptionLog.setRequestHeaders(requestHeaders);
        exceptionLog.setSessionId(sessionId);
        exceptionLog.setUserAgent(userAgent);
        exceptionLog.setClientIp(clientIp);
        exceptionLog.setExceptionClass(exceptionClass);
        exceptionLog.setExceptionMethod(exceptionMethod);
        exceptionLog.setUserId(userId);

        // 使用静态的异常日志映射器，将异常日志插入到数据库中。
        staticExceptionLogMapper.insert(exceptionLog);
    }

    /**
     * 将异常的堆栈跟踪转换为字符串格式。
     * 此方法用于获取异常发生时的堆栈跟踪，并以字符串形式组织这些信息，方便日志记录或错误处理。
     *
     * @param e 异常对象，包含需要转换为字符串的堆栈跟踪。
     * @return 包含异常堆栈跟踪的字符串。
     */
    private static String getStackTraceAsString(Exception e) {
        // 使用StringBuilder来构建最终的字符串结果，以提高性能。
        StringBuilder result = new StringBuilder();
        // 遍历异常的堆栈跟踪元素。
        for (StackTraceElement element : e.getStackTrace()) {
            // 将每个堆栈跟踪元素转换为字符串并追加到结果中，每个元素后添加换行符。
            result.append(element.toString()).append("\n");
        }
        // 返回包含所有堆栈跟踪元素的字符串。
        return result.toString();
    }


    /**
     * 获取请求参数的字符串表示。
     * 该方法用于遍历HttpServletRequest中的所有参数，并以键值对的形式拼接成字符串。
     * 参数之间使用"&"符号分隔。此方法不包括请求体中的参数。
     *
     * @param request HttpServletRequest对象，代表一个HTTP请求。
     * @return 参数字符串，包含请求中的所有参数及其值。
     */
    private static String getRequestParams(HttpServletRequest request) {
        // 初始化一个StringBuilder用于拼接参数字符串
        StringBuilder params = new StringBuilder();
        // 获取请求中的所有参数名
        Enumeration<String> parameterNames = request.getParameterNames();
        // 遍历所有参数名
        while (parameterNames.hasMoreElements()) {
            // 获取当前参数名
            String paramName = parameterNames.nextElement();
            // 将参数名、等号、参数值追加到params字符串中，并以"&"符号连接下一个参数
            params.append(paramName).append("=").append(request.getParameter(paramName)).append("&");
        }
        // 如果params字符串不为空，删除最后一个多余的"&"符号
        if (params.length() > 0) {
            params.deleteCharAt(params.length() - 1);
        }
        // 返回拼接好的参数字符串
        return params.toString();
    }


    /**
     * 获取请求的所有头部信息。
     *
     * 该方法通过遍历HttpServletRequest对象中的头部信息，将所有头部名称和对应的值拼接成一个字符串。
     * 这对于调试或者记录请求头部信息非常有用，例如在日志中记录请求的全部头部。
     *
     * @param request HttpServletRequest对象，代表一个HTTP请求。
     * @return 返回一个字符串，包含请求的所有头部信息。每行代表一个头部，格式为"头部名称: 头部值"。
     */
    private static String getRequestHeaders(HttpServletRequest request) {
        // 初始化一个StringBuilder用于拼接头部信息。
        StringBuilder headers = new StringBuilder();

        // 获取请求的所有头部名称。
        Enumeration<String> headerNames = request.getHeaderNames();

        // 遍历所有头部名称。
        while (headerNames.hasMoreElements()) {
            // 获取当前头部的名称。
            String headerName = headerNames.nextElement();
            // 将头部名称和对应的值拼接到headers字符串中，并换行。
            headers.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
        }

        // 返回拼接好的头部信息字符串。
        return headers.toString();
    }


    /**
     * 获取当前请求的会话ID。
     *
     * 此方法尝试从当前的HttpServletRequest中获取会话ID。它首先尝试获取当前请求的会话，如果没有则不创建新的会话，
     * 这样做可以避免不必要的会话创建。如果存在当前会话，则返回会话ID；如果不存在，则返回null。
     *
     * @param request 当前的HttpServletRequest对象，从中获取会话ID。
     * @return 当前请求的会话ID，如果不存在会话则返回null。
     */
    private static String getSessionId(HttpServletRequest request) {
        // 尝试获取当前请求的会话，但不创建新的会话
        HttpSession session = request.getSession(false);
        // 如果会话存在，则返回会话ID；否则返回null
        return (session != null) ? session.getId() : null;
    }


    /**
     * 获取当前用户ID。
     *
     * 本方法用于模拟获取当前操作用户的ID。在实际应用中，这个ID可能会从会话、请求参数或其他安全认证机制中获取。
     * 由于这是一个私有静态方法，它表明获取当前用户ID的方式是与实例无关的，且在类的内部随时可以调用。
     *
     * @return 当前用户ID的Long类型值。在这个示例中，返回固定的ID值1，仅用于演示目的。
     */
    private static Long getCurrentUserId() {
        return 1L;
    }

}
