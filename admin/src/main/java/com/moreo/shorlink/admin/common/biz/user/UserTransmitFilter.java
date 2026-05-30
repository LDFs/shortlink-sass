package com.moreo.shorlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.moreo.shorlink.admin.common.convention.exception.ClientException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;

import static com.moreo.shorlink.admin.common.constant.RedisCacheConstant.USER_LOGIN_KEY;

/**
 * 用户信息传输过滤器
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;
    private final HandlerExceptionResolver handlerExceptionResolver;

    /**
     * 过滤掉不用检查token的接口
     */
    private static final List<String> IGNORE_URI = Lists.newArrayList(
            "/api/shortlink/admin/v1/user/save",
            "/api/shortlink/admin/v1/user/login",
            "/api/shortlink/admin/v1/user/has-username"
    );

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        // ===== 代码库中这样写的 ======
//        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
//        String username = httpServletRequest.getHeader("username");
//        /**
//         * 有 username header 的接口，设置用户上下文，然后放行
//         * 没有的，什么都不做，直接放行
//         */
//        if (StrUtil.isNotBlank(username)) {
//            String id = httpServletRequest.getHeader("id");
//            String realName = httpServletRequest.getHeader("realName");
//            UserInfoDTO userInfoDTO = new UserInfoDTO(id, username, realName);
//            UserContext.setUser(userInfoDTO);
//        }
//        try {
//            filterChain.doFilter(servletRequest, servletResponse);
//        } finally {
//            UserContext.removeUser();
//        }

        // =========== 上课中写的，说是之后使用网关来优化 =========
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();
        if(!IGNORE_URI.contains(uri)) {
            String username = request.getHeader("username");
            String token = request.getHeader("token");
            if(!StrUtil.isAllNotBlank(username, token)) {
                handlerExceptionResolver.resolveException(request, response, null, new ClientException("用户未登陆"));
                return;
            }
            Object userInfoJsonStr;
            try {
                userInfoJsonStr = stringRedisTemplate.opsForHash().get(USER_LOGIN_KEY + username, token);
                if(userInfoJsonStr == null) {
//                    directResponse(response, "用户登陆状态验证失败");
                    handlerExceptionResolver.resolveException(request, response, null, new ClientException("用户登陆状态验证失败"));
                    return;
                }
            }catch (Exception e) {
                handlerExceptionResolver.resolveException(request, response, null, new ClientException("用户登陆状态过期"));
                return;
            }
            UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
            UserContext.setUser(userInfoDTO);
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}
