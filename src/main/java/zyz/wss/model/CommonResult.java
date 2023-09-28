package zyz.wss.model;

public class CommonResult {
    private static final CommonResult OK = new CommonResult("1", "", null);
    private String code;
    private String msg;
    private Object data;

    public CommonResult(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public CommonResult() {
    }

    public String getCode() {
        return code;
    }

    public CommonResult setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public CommonResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public CommonResult setData(Object data) {
        this.data = data;
        return this;
    }
}
