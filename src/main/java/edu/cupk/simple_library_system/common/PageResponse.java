package edu.cupk.simple_library_system.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonSerialize(using = PageResponseSerializer.class)
public class PageResponse<T> {
    private int code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long count;

    private List<T> data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String singleData;

    public PageResponse() {
    }

    public PageResponse(int code, String message, long count, List<T> data) {
        this.code = code;
        this.message = message;
        this.count = count;
        this.data = data;
    }

    private PageResponse(int code, String message, Long count, List<T> data) {
        this.code = code;
        this.message = message;
        this.count = count;
        this.data = data;
    }

    private PageResponse(int code, String message, Long count, String singleData) {
        this.code = code;
        this.message = message;
        this.count = count;
        this.singleData = singleData;
    }

    public static <T> PageResponse<T> success(long count, List<T> data) {
        return new PageResponse<>(0, "success", count, data);
    }

    public static PageResponse<String> success(long count, String singleData) {
        if (count == 0) {
            // 文档格式: {"code":0,"data":"/upload/xxx.jpg"}
            return new PageResponse<>(0, null, 0L, singleData);
        }
        // 分页格式: {"code":0,"message":"success","count":N,"data":[...]}
        return new PageResponse<>(0, "success", count, singleData);
    }

    public static PageResponse<String> fail(String message) {
        // 错误格式: {"code":1,"message":"xxx"}
        return new PageResponse<>(1, message, null, (String) null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String getSingleData() {
        return singleData;
    }

    public void setSingleData(String singleData) {
        this.singleData = singleData;
    }
}
