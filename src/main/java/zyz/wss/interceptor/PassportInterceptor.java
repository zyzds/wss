package zyz.wss.interceptor;

import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import zyz.wss.model.HostHolder;
import zyz.wss.model.entity.Token;
import zyz.wss.repository.TokenRepository;

@Component
public class PassportInterceptor implements HandlerInterceptor {
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = null;
        if (request.getCookies() == null) {
            return true;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("token".equals(cookie.getName())) {
                ticket = cookie.getValue();
                break;
            }
        }
        if (StringUtils.isEmpty(ticket)) {
            return true;
        }
        Optional<Token> opt = tokenRepository.findById(ticket);
        if (!opt.isPresent() || opt.get().isExpire() || !opt.get().getUser().getActive()) {
            return true;
        }
        hostHolder.add(opt.get().getUser());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            modelAndView.addObject("user", hostHolder.get());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.remove();
    }
}
