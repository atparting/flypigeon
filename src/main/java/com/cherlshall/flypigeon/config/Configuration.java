package com.cherlshall.flypigeon.config;

import com.cherlshall.flypigeon.config.responsehandler.UserResponseHandler;
import com.cherlshall.flypigeon.config.responsehandler.CommandResponseHandler;
import com.cherlshall.flypigeon.config.responsehandler.ExceptionResponseHandler;
import com.cherlshall.flypigeon.exception.CommandExecuteException;

/**
 * @author hu.tengfei
 * @since 2019/7/30
 */
public class Configuration {
    /**
     * server ip 默认为本机
     */
    private String ip;

    /**
     * server port
     */
    private int port = 10100;

    /**
     * 每条数据最大长度
     */
   private int maxBytes = 2048;

    /**
     * 对使用者生成的响应再处理
     */
    private UserResponseHandler userResponseHandler = response -> response;

    /**
     * 对返回子命令的处理
     */
    private CommandResponseHandler commandResponseHandler = (trunks, leaves) -> {
        StringBuilder sb = new StringBuilder();
        for (String name : trunks) {
            sb.append(name);
            sb.append(" ");
        }
        for (String name : leaves) {
            sb.append(name);
            sb.append(" ");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    };

    /**
     * 对响应时发生异常的处理
     */
    private ExceptionResponseHandler exceptionResponseHandler = e -> {
        if (e instanceof CommandExecuteException) {
            return e.getMessage();
        }
        return "error";
    };

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxBytes() {
        return maxBytes;
    }

    public void setMaxBytes(int maxBytes) {
        this.maxBytes = maxBytes;
    }

    public UserResponseHandler getUserResponseHandler() {
        return userResponseHandler;
    }

    public void setUserResponseHandler(UserResponseHandler userResponseHandler) {
        this.userResponseHandler = userResponseHandler;
    }

    public CommandResponseHandler getCommandResponseHandler() {
        return commandResponseHandler;
    }

    public void setCommandResponseHandler(CommandResponseHandler commandResponseHandler) {
        this.commandResponseHandler = commandResponseHandler;
    }

    public ExceptionResponseHandler getExceptionResponseHandler() {
        return exceptionResponseHandler;
    }

    public void setExceptionResponseHandler(ExceptionResponseHandler exceptionResponseHandler) {
        this.exceptionResponseHandler = exceptionResponseHandler;
    }
}