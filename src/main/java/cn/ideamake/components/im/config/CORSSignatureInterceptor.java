package cn.ideamake.components.im.config;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yangxianwei
 * @date 2019/8/26 3:17 PM
 */
public class CORSSignatureInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "content-type, Authorization, IM-TOKEN, Auth-Info");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equals(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "content-type, Authorization, IM-TOKEN, Auth-Info");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setStatus(204);
            return false;
        }

        return true;
    }
}
