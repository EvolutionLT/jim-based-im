package cn.ideamake.components.im.common.common.http.handler;

import cn.ideamake.components.im.common.common.http.HttpRequest;
import cn.ideamake.components.im.common.common.http.HttpResponse;
import cn.ideamake.components.im.common.common.http.RequestLine;

/**
 * @author wchao
 */
public interface IHttpRequestHandler {
    /**
     * @param packet
     * @param requestLine
     * @return
     * @throws Exception
     * @author wchao
     */
    public HttpResponse handler(HttpRequest packet, RequestLine requestLine) throws Exception;

    /**
     * @param request
     * @param requestLine
     * @param channelContext
     * @return
     * @author wchao
     */
    public HttpResponse resp404(HttpRequest request, RequestLine requestLine);

    /**
     * @param request
     * @param requestLine
     * @param throwable
     * @return
     * @author wchao
     */
    public HttpResponse resp500(HttpRequest request, RequestLine requestLine, Throwable throwable);

    /**
     * 清空静态资源缓存，如果没有缓存，可以不处理
     *
     * @param request
     * @author: wchao
     */
    public void clearStaticResCache(HttpRequest request);
}
