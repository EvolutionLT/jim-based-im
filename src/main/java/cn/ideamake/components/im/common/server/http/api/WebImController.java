/**
 *
 */
package cn.ideamake.components.im.common.server.http.api;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;
import cn.ideamake.components.im.common.common.http.HttpRequest;
import cn.ideamake.components.im.common.common.http.HttpResponse;
import cn.ideamake.components.im.common.server.http.annotation.RequestPath;
import cn.ideamake.components.im.common.server.util.HttpResps;

import java.io.File;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月27日 下午4:54:35
 */
@RequestPath(value = "/webim")
public class WebImController {

    public HttpResponse webim(HttpRequest request) throws Exception {
        String root = FileUtil.getAbsolutePath(request.getHttpConfig().getPageRoot());
        String path = request.getRequestLine().getPath();
        File file = new File(root + path);
        if (!file.exists() || file.isDirectory()) {
            if (StringUtils.endsWith(path, "/")) {
                path = path + "index.html";
            } else {
                path = path + "/index.html";
            }
            file = new File(root, path);
        }
        HttpResponse ret = HttpResps.file(request, file);
        return ret;
    }
}
