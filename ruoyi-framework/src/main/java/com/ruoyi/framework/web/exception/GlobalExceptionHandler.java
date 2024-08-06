package com.ruoyi.framework.web.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.family.common.domain.ExceptionLog;
import com.family.common.mapper.ExceptionLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.DemoModeException;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

import java.time.LocalDateTime;
import java.util.Enumeration;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private ExceptionLogMapper exceptionLogMapper;

    /**
     * 记录异常信息到日志系统中。
     * 此方法封装了异常信息和请求信息，以便于后续的日志存储或分析。
     *
     * @param e 抛出的异常对象，包含异常的详细信息。
     * @param request HTTP请求对象，用于获取请求相关的信息。
     */
    private void saveExceptionLog(Exception e, HttpServletRequest request) {
        // 获取异常的基本信息
        String exceptionMessage = e.getMessage();
        String exceptionStackTrace = getStackTraceAsString(e);
        LocalDateTime timestamp = LocalDateTime.now();

        // 从HTTP请求中获取相关的信息
        String endpoint = request.getRequestURI();
        String httpMethod = request.getMethod();
        String requestParams = getRequestParams(request);
        String requestHeaders = getRequestHeaders(request);
        String sessionId = getSessionId(request);
        String userAgent = request.getHeader("User-Agent");
        String clientIp = request.getRemoteAddr();

        // 通过异常栈追踪获取异常发生的具体类和方法
        String exceptionClass = e.getStackTrace()[0].getClassName();
        String exceptionMethod = e.getStackTrace()[0].getMethodName();

        // 尝试获取当前用户ID，用于关联异常和用户
        Long userId = getCurrentUserId();

        // 检查是否缺少sessionId或requestParams,保证日志信息的完整性
        if (sessionId == null) {
            sessionId = "无会话ID";
        }
        if (requestParams.isEmpty()) {
            requestParams = "无请求参数";
        }

        // 创建异常日志对象，并填充相关信息
        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setExceptionMessage(exceptionMessage);
        exceptionLog.setExceptionStackTrace(exceptionStackTrace);
        exceptionLog.setTimestamp(timestamp);
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

        // 将异常信息存储到数据库
        exceptionLogMapper.insert(exceptionLog);
    }


    /**
     * 将异常的堆栈跟踪转换为字符串格式。
     * 此方法用于处理异常对象，将其堆栈跟踪以字符串形式组织起来，方便日志记录或错误处理。
     *
     * @param e 异常对象，其堆栈跟踪将被转换为字符串。
     * @return 包含异常堆栈跟踪的字符串。
     */
    private String getStackTraceAsString(Exception e) {
        // 初始化StringBuilder用于累积堆栈跟踪的字符串表示。
        StringBuilder result = new StringBuilder();
        // 遍历异常的堆栈跟踪元素。
        for (StackTraceElement element : e.getStackTrace()) {
            // 将每个堆栈跟踪元素转换为字符串并追加到结果中，每个元素后添加换行符。
            result.append(element.toString()).append("\n");
        }
        // 返回包含堆栈跟踪字符串的结果。
        return result.toString();
    }


    /**
     * 获取请求参数的字符串表示。
     * 该方法用于将HttpServletRequest中的所有参数名称及其值拼接成一个字符串，以用于日志记录或其他需要获取请求参数的场景。
     * 参数字符串的格式为"参数1=值1&参数2=值2&..."，最后一个参数后面不带"&"。
     *
     * @param request HttpServletRequest对象，从中获取参数。
     * @return 参数字符串，包含所有请求参数的名称和值。
     */
    private String getRequestParams(HttpServletRequest request) {
        // 初始化一个StringBuilder用于拼接参数字符串
        StringBuilder params = new StringBuilder();
        // 获取请求的所有参数名称
        Enumeration<String> parameterNames = request.getParameterNames();
        // 遍历所有参数名称
        while (parameterNames.hasMoreElements()) {
            // 获取当前参数名称
            String paramName = parameterNames.nextElement();
            // 将参数名称、等号、参数值添加到params字符串中，并以"&"连接下一个参数
            params.append(paramName).append("=").append(request.getParameter(paramName)).append("&");
        }
        // 如果params字符串不为空，删除最后一个多余的"&"字符
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
     * @return 返回一个字符串，包含请求的所有头部信息，每行一个头部，格式为"头部名称: 头部值"。
     */
    private String getRequestHeaders(HttpServletRequest request) {
        // 初始化一个StringBuilder用于拼接头部信息。
        StringBuilder headers = new StringBuilder();
        // 获取请求的所有头部名称。
        Enumeration<String> headerNames = request.getHeaderNames();
        // 遍历所有头部名称。
        while (headerNames.hasMoreElements()) {
            // 获取当前头部名称。
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
     * 此方法尝试从当前的HttpServletRequest中获取会话ID。它首先尝试获取会话对象，
     * 如果会话存在，则返回会话ID；如果会话不存在，则返回null。
     * 这种方式避免了创建新的会话，因为它使用了getSession(false)方法，
     * 这个方法不会自动创建新的会话。
     *
     * @param request 当前的HTTP请求对象，用于获取会话信息。
     * @return 当前会话的ID，如果不存在会话则返回null。
     */
    private String getSessionId(HttpServletRequest request) {
        // 尝试获取当前请求的会话，但不创建新的会话
        HttpSession session = request.getSession(false);
        // 如果会话存在，则返回会话ID；否则返回null
        return (session != null) ? session.getId() : null;
    }

    /**
     * 获取当前用户ID。
     *
     * 本方法模拟了获取当前操作用户ID的逻辑。在实际应用中，这个ID通常通过会话（Session）或其他方式获取。
     * 这里硬编码为1L仅作为示例。在实际开发中，应从相应的会话或请求对象中提取用户ID。
     *
     * @return 当前用户的ID。示例中返回固定值1L，代表模拟的当前用户。
     */
    private Long getCurrentUserId() {
        Long userId = 1L;
        // 示例中返回null
        return userId;
    }


    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        saveExceptionLog(e, request);
        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
            HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        saveExceptionLog(e, request);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        saveExceptionLog(e, request);
        Integer code = e.getCode();
        return StringUtils.isNotNull(code) ? AjaxResult.error(code, e.getMessage()) : AjaxResult.error(e.getMessage());
    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public AjaxResult handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestURI, e);
        saveExceptionLog(e, request);
        return AjaxResult.error(String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()));
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public AjaxResult handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestURI, e);
        saveExceptionLog(e, request);
        return AjaxResult.error(String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), e.getRequiredType().getName(), e.getValue()));
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        saveExceptionLog(e, request);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        saveExceptionLog(e, request);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult handleBindException(BindException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        saveExceptionLog(e, request);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        saveExceptionLog(e, request);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public AjaxResult handleDemoModeException(DemoModeException e, HttpServletRequest request) {
        saveExceptionLog(e, request);
        return AjaxResult.error("演示模式，不允许操作");
    }
}
