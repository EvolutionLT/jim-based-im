package cn.ideamake.components.im.common.server.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author WChao
 * @date 2018年3月9日 上午1:09:03
 */
public class CommandConfiguration {
    private int cmd;
    private String cmdHandler;
    private List<String> proCmdHandlers = new ArrayList<String>();

    public CommandConfiguration() {
    }

    public CommandConfiguration(String cmd, Properties prop) {
        this.cmd = Integer.parseInt(cmd);
        String[] values = prop.getProperty(cmd).split(",");
        if (values.length > 0) {
            cmdHandler = values[0];
            if (values.length > 1) {
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) {
                        proCmdHandlers.add(values[i]);
                    }
                }
            }
        }
    }

    public CommandConfiguration(String cmd, String prop) {
        this.cmd = Integer.parseInt(cmd);
        String[] values = prop.split(",");
        if (values.length > 0) {
            cmdHandler = values[0];
            if (values.length > 1) {
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) {
                        proCmdHandlers.add(values[i]);
                    }
                }
            }
        }
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getCmdHandler() {
        return cmdHandler;
    }

    public void setCmdHandler(String cmdHandler) {
        this.cmdHandler = cmdHandler;
    }

    public List<String> getProCmdHandlers() {
        return proCmdHandlers;
    }

    public void setProCmdHandlers(List<String> proCmdHandlers) {
        this.proCmdHandlers = proCmdHandlers;
    }


}
