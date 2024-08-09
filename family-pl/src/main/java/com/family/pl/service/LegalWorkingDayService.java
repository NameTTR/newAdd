package com.family.pl.service;

import com.ruoyi.common.core.redis.RedisCache;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/**
 * 法定工作日服务类，提供获取法定工作日数据的功能
 */
@Service
public class LegalWorkingDayService {

    @Value("${calendarific.api.url}")
    private String apiUrl;

    @Value("${calendarific.api.key}")
    private String apiKey;

    @Autowired
    private RedisCache redisCache;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 判断指定日期是否为法定工作日
     * @param date 日期
     * @return 是否为法定工作日
     */
    public boolean isLegalWorkingDay(LocalDate date) {
        String key = "legalWorkingDay:" + date.toString();

        // 尝试从缓存中获取数据
        Boolean isLegalWorkingDay = redisCache.getCacheObject(key);
        if (isLegalWorkingDay != null) {
            return isLegalWorkingDay;
        }

        // 从API获取法定工作日数据
        try {
            String url = String.format("%s?api_key=%s&country=CN&year=%d&month=%d&day=%d",
                    apiUrl, apiKey, date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            CalendarificResponse response = restTemplate.getForObject(url, CalendarificResponse.class);

            if (response != null && response.getResponse().getHolidays().length > 0) {
                isLegalWorkingDay = true;
                // 缓存数据，设置缓存时间为7天
                redisCache.setCacheObject(key, isLegalWorkingDay, 7, TimeUnit.DAYS);
                return isLegalWorkingDay;
            }
        } catch (HttpClientErrorException e) {
            // 处理API请求失败的情况，记录错误并返回默认值
            System.err.println("Error fetching legal working day data: " + e.getMessage());
        }

        // 如果API无法获取数据，默认返回工作日逻辑
        isLegalWorkingDay = date.getDayOfWeek().getValue() >= 1 && date.getDayOfWeek().getValue() <= 5;
        // 缓存数据，设置缓存时间为1天
        redisCache.setCacheObject(key, isLegalWorkingDay, 1, TimeUnit.DAYS);
        return isLegalWorkingDay;
    }

    /**
     * 内部类，用于接收外部 API 返回的法定工作日数据
     */
    @Data
    static class CalendarificResponse {
        private Response response;

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }

        static class Response {
            private Holiday[] holidays;

            public Holiday[] getHolidays() {
                return holidays;
            }

            public void setHolidays(Holiday[] holidays) {
                this.holidays = holidays;
            }
        }

        static class Holiday {
            private String name;
            private String description;
            private Date date;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public Date getDate() {
                return date;
            }

            public void setDate(Date date) {
                this.date = date;
            }

            static class Date {
                private String iso;

                public String getIso() {
                    return iso;
                }

                public void setIso(String iso) {
                    this.iso = iso;
                }
            }
        }
    }
}
