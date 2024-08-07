package com.family.common.util;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.util.JobInvokeUtil;
import org.quartz.JobExecutionContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 执行任务工具类
 * </p>
 *
 * @author Name
 * @date 2024/8/7 18:01
 */
public class FamilyJobInvokeUtil {
    /**
     * 执行方法
     *
     * @param sysJob 系统任务
     */
    public static void invokeMethod(SysJob sysJob) throws Exception
    {
        String invokeTarget = sysJob.getInvokeTarget();
        String beanName = getBeanName(invokeTarget);
        String methodName = getMethodName(invokeTarget);
        List<Object[]> methodParams = getMethodParams(invokeTarget);

        if (!isValidClassName(beanName))
        {
            Object bean = SpringUtils.getBean(beanName);
            invokeMethod(bean, methodName, methodParams);
        }
        else
        {
            Object bean = Class.forName(beanName).getDeclaredConstructor().newInstance();
            invokeMethod(bean, methodName, methodParams);
        }
    }

    /**
     * 调用任务方法
     *
     * @param bean 目标对象
     * @param methodName 方法名称
     * @param methodParams 方法参数
     */
    private static void invokeMethod(Object bean, String methodName, List<Object[]> methodParams)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {
        if (StringUtils.isNotNull(methodParams) && methodParams.size() > 0)
        {
            Method method = bean.getClass().getMethod(methodName, getMethodParamsType(methodParams));
            method.invoke(bean, getMethodParamsValue(methodParams));
        }
        else
        {
            Method method = bean.getClass().getMethod(methodName);
            method.invoke(bean);
        }
    }

    /**
     * 根据定时任务配置动态调用方法
     *
     * @param sysJob 定时任务信息，包含调用目标等关键信息
     * @param jobExecutionContext Job执行上下文，用于传递给目标方法
     * @throws Exception 在调用方法过程中可能抛出的异常
     */
    public static void invokeMethod(SysJob sysJob, JobExecutionContext jobExecutionContext) throws Exception
    {
        // 获取调用目标，即配置的任务执行逻辑
        String invokeTarget = sysJob.getInvokeTarget();
        // 解析调用目标，获取bean名称
        String beanName = getBeanName(invokeTarget);
        // 解析调用目标，获取方法名称
        String methodName = getMethodName(invokeTarget);
        // 解析调用目标，获取方法参数列表
        List<Object[]> methodParams = getMethodParams(invokeTarget);

        // 检查bean名称是否为合法的类名
        if (!isValidClassName(beanName))
        {
            // 从Spring容器中获取bean实例
            Object bean = SpringUtils.getBean(beanName);
            // 将Job执行上下文添加到方法参数列表中
            addJobExecutionContextToParams(methodParams, jobExecutionContext);
            // 调用bean的指定方法
            invokeMethod(bean, methodName, methodParams);
        }
        else
        {
            // 通过类名动态创建bean实例
            Object bean = Class.forName(beanName).getDeclaredConstructor().newInstance();
            // 将Job执行上下文添加到方法参数列表中
            addJobExecutionContextToParams(methodParams, jobExecutionContext);
            // 调用bean的指定方法
            invokeMethod(bean, methodName, methodParams);
        }
    }


    /**
     * 将JobExecutionContext对象添加到方法参数列表中
     *
     * 此方法确保了在调用其他方法时，能够将JobExecutionContext上下文信息作为参数传递，
     * 使得被调用的方法能够访问到JobExecutionContext对象
     *
     * @param methodParams 方法参数列表，用于存储传递给方法的所有参数
     * @param jobExecutionContext JobExecutionContext对象，包含调度任务的上下文信息
     */
    private static void addJobExecutionContextToParams(List<Object[]> methodParams, JobExecutionContext jobExecutionContext)
    {
        // 检查方法参数列表是否为空，如果为空则初始化为一个新的LinkedList
        if (methodParams == null)
        {
            methodParams = new LinkedList<>();
        }

        // 将JobExecutionContext对象及其类类型添加到方法参数列表中
        methodParams.add(new Object[] { jobExecutionContext, JobExecutionContext.class });
    }

    /**
     * 校验是否为为class包名
     *
     * @param invokeTarget 名称
     * @return true是 false否
     */
    public static boolean isValidClassName(String invokeTarget)
    {
        return StringUtils.countMatches(invokeTarget, ".") > 1;
    }

    /**
     * 获取bean名称
     *
     * @param invokeTarget 目标字符串
     * @return bean名称
     */
    public static String getBeanName(String invokeTarget)
    {
        String beanName = StringUtils.substringBefore(invokeTarget, "(");
        return StringUtils.substringBeforeLast(beanName, ".");
    }

    /**
     * 获取bean方法
     *
     * @param invokeTarget 目标字符串
     * @return method方法
     */
    public static String getMethodName(String invokeTarget)
    {
        String methodName = StringUtils.substringBefore(invokeTarget, "(");
        return StringUtils.substringAfterLast(methodName, ".");
    }

    /**
     * 获取method方法参数相关列表
     *
     * @param invokeTarget 目标字符串
     * @return method方法相关参数列表
     */
    public static List<Object[]> getMethodParams(String invokeTarget) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String methodStr = StringUtils.substringBetween(invokeTarget, "(", ")");
        if (StringUtils.isEmpty(methodStr))
        {
            return null;
        }
        String[] methodParams = methodStr.split(",(?=([^\"']*[\"'][^\"']*[\"'])*[^\"']*$)");
        List<Object[]> classs = new LinkedList<>();
        for (int i = 0; i < methodParams.length; i++)
        {
            String str = StringUtils.trimToEmpty(methodParams[i]);
            // String字符串类型，以'或"开头
            if (StringUtils.startsWithAny(str, "'", "\""))
            {
                classs.add(new Object[] { StringUtils.substring(str, 1, str.length() - 1), String.class });
            }
            // boolean布尔类型，等于true或者false
            else if ("true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str))
            {
                classs.add(new Object[] { Boolean.valueOf(str), Boolean.class });
            }
            // long长整形，以L结尾
            else if (StringUtils.endsWith(str, "L"))
            {
                classs.add(new Object[] { Long.valueOf(StringUtils.substring(str, 0, str.length() - 1)), Long.class });
            }
            // double浮点类型，以D结尾
            else if (StringUtils.endsWith(str, "D"))
            {
                classs.add(new Object[] { Double.valueOf(StringUtils.substring(str, 0, str.length() - 1)), Double.class });
            }
            // 处理自定义类型，例如org.quartz.JobExecutionContext
            else if (str.contains("."))
            {
                System.out.println("JobExecutionContext");
            }
            // 其他类型归类为整形
            else
            {
                classs.add(new Object[] { Integer.valueOf(str), Integer.class });
            }
        }
        return classs;
    }

    /**
     * 获取参数类型
     *
     * @param methodParams 参数相关列表
     * @return 参数类型列表
     */
    public static Class<?>[] getMethodParamsType(List<Object[]> methodParams)
    {
        Class<?>[] classs = new Class<?>[methodParams.size()];
        int index = 0;
        for (Object[] os : methodParams)
        {
            classs[index] = (Class<?>) os[1];
            index++;
        }
        return classs;
    }

    /**
     * 获取参数值
     *
     * @param methodParams 参数相关列表
     * @return 参数值列表
     */
    public static Object[] getMethodParamsValue(List<Object[]> methodParams)
    {
        Object[] classs = new Object[methodParams.size()];
        int index = 0;
        for (Object[] os : methodParams)
        {
            classs[index] = (Object) os[0];
            index++;
        }
        return classs;
    }
}