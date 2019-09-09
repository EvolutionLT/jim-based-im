package cn.ideamake.components.im.common.common.http;

import java.util.Objects;

/**
 * @author wchao
 * 2017年6月28日 下午2:23:16
 */
public enum Method {
	GET("GET"), POST("POST"), HEAD("HEAD"), PUT("PUT"), TRACE("TRACE"), OPTIONS("OPTIONS"), PATCH("PATCH");
	public static cn.ideamake.components.im.common.common.http.Method from(String method) {
		cn.ideamake.components.im.common.common.http.Method[] values = cn.ideamake.components.im.common.common.http.Method.values();
		for (cn.ideamake.components.im.common.common.http.Method v : values) {
			if (Objects.equals(v.value, method)) {
				return v;
			}
		}
		return GET;
	}

	String value;

	private Method(String value) {
		this.value = value;
	}
}
