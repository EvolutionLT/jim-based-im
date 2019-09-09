package cn.ideamake.components.im.common.common.ws;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wchao
 * 2017年6月30日 下午5:06:09
 */
public enum Opcode {

	TEXT((byte) 1), BINARY((byte) 2), CLOSE((byte) 8), PING((byte) 9), PONG((byte) 10);

	private static final Map<Byte, cn.ideamake.components.im.common.common.ws.Opcode> map = new HashMap<>();

	static {
		for (cn.ideamake.components.im.common.common.ws.Opcode command : values()) {
			map.put(command.getCode(), command);
		}
	}

	public static cn.ideamake.components.im.common.common.ws.Opcode valueOf(byte code) {
		return map.get(code);
	}

	private final byte code;

	private Opcode(byte code) {
		this.code = code;
	}

	public byte getCode() {
		return code;
	}

}
