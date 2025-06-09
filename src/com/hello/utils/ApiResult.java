package com.hello.utils;

public class ApiResult {

    private Boolean success;
    private String message;
    private Object data;

    public static String json(Boolean success, String message, Object data) {
        // 简单手动构建JSON字符串，避免使用外部库
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\":").append(success).append(",");
        json.append("\"message\":\"").append(escapeJson(message)).append("\"");

        if (data != null) {
            json.append(",\"data\":").append(dataToJson(data));
        }

        json.append("}");
        return json.toString();
    }

    public static String json(Boolean success, String message) {
        return json(success, message, null);
    }

    // 简单的JSON转义处理
    private static String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // 简单处理数据对象转JSON
    private static String dataToJson(Object data) {
        if (data == null) {
            return "null";
        }
        if (data instanceof String) {
            return "\"" + escapeJson((String) data) + "\"";
        }
        if (data instanceof Number || data instanceof Boolean) {
            return data.toString();
        }
        // 对于复杂对象，这里简化处理，实际应用可能需要更复杂的逻辑
        return "\"" + escapeJson(data.toString()) + "\"";
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}