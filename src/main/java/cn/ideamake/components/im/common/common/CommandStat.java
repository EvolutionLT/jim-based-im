package cn.ideamake.components.im.common.common;

import cn.ideamake.components.im.common.common.packets.Command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wchao
 */
public class CommandStat {

    public final static Map<Command, cn.ideamake.components.im.common.common.CommandStat> commandAndCount = new ConcurrentHashMap<>();

    public final AtomicLong received = new AtomicLong();
    public final AtomicLong handled = new AtomicLong();
    public final AtomicLong sent = new AtomicLong();

    public static cn.ideamake.components.im.common.common.CommandStat getCount(Command command) {
        if (command == null) {
            return null;
        }
        cn.ideamake.components.im.common.common.CommandStat ret = commandAndCount.get(command);
        if (ret != null) {
            return ret;
        }

        synchronized (commandAndCount) {
            ret = commandAndCount.get(command);
            if (ret != null) {
                return ret;
            }
            ret = new cn.ideamake.components.im.common.common.CommandStat();
            commandAndCount.put(command, ret);
        }
        return ret;
    }


    /**
     * @author: wchao
     * 2016年12月6日 下午5:32:31
     */
    public CommandStat() {
    }


    /**
     * @return the receivedCount
     */
    public AtomicLong getReceived() {
        return received;
    }


    /**
     * @return the handledCount
     */
    public AtomicLong getHandled() {
        return handled;
    }


    /**
     * @return the sentCount
     */
    public AtomicLong getSent() {
        return sent;
    }

}
