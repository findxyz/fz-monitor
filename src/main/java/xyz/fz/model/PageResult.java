package xyz.fz.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult {

    private int code;

    private String msg;

    private long count;

    private List data;

    public static PageResult ofData(long count, List data) {
        PageResult pageResult = new PageResult();
        pageResult.code = 0;
        pageResult.msg = "";
        pageResult.count = count;
        pageResult.data = data;
        return pageResult;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }
}
