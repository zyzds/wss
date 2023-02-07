package zyz.wss.controller;

import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import zyz.wss.constant.LoginConst;
import zyz.wss.model.entity.Token;
import zyz.wss.service.UserService;
import zyz.wss.util.VerificationCodeUtil.VerificationCode;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping(value = "/regist", method = RequestMethod.POST)
    public Map<String, Object> regist(@RequestParam String name, @RequestParam String email,
            @RequestParam String password) {
        return userService.regist(name, email, password);
    }

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String, Object> login(@RequestParam String nameOrEmail, @RequestParam String password,
            HttpServletRequest req, HttpServletResponse resp) {
        Map<String, Object> msg = userService.login(nameOrEmail, password, req.getRemoteAddr());
        if (LoginConst.SUCCESS == (int) msg.get("result")) {
            Cookie cookie = new Cookie("token", ((Token) msg.get("token")).getId());
            cookie.setMaxAge(Token.EXPIRE * 24 * 60 * 60);
            cookie.setPath("/");
            resp.addCookie(cookie);
        }
        return msg;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest req, HttpServletResponse resp) {
        for (Cookie cookie : req.getCookies()) {
            if ("token".equals(cookie.getName())) {
                cookie = new Cookie("token", "");
                cookie.setMaxAge(0);
                cookie.setPath("/");
                resp.addCookie(cookie);
                break;
            }
        }
        return "redirect:../reglogin";
    }

    @GetMapping(value = "/verifyImage")
    public void getMethodName(HttpServletResponse resp, @RequestParam String id) {
        VerificationCode code = userService.generateVCode(id);

        

        resp.setHeader("Prama", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);
        resp.setContentType("image/jpeg");
        
        try (ServletOutputStream out = resp.getOutputStream()) {
            ImageIO.write(code.getImage(), "jpeg", out);
            out.flush();
        } catch (IOException e) {
            log.error("验证码生成失败", e);
        }
    }

    @ResponseBody
    @PostMapping(value = "/verifyCheck")
    public boolean verifyCheck(String id, String answer) {
        return userService.checkVerificationCode(id, answer);
    }

    @GetMapping(value = "/active/{id}")
    public String verifyCheck(@PathVariable int id) {
        userService.activeUser(id);
        return "redirect:/reglogin";
    }
}