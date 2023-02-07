package zyz.wss.service;

import java.util.Map;

import zyz.wss.util.VerificationCodeUtil.VerificationCode;

public interface UserService {
    /**
     * 登录
     * @param nameOrEmail   用户名或邮箱
     * @param password      密码
     * @return
     */
    Map<String, Object> login(String nameOrEmail, String password, String IP);

    /**
     * 注册
     * @param name  用户名
     * @param email 邮箱
     * @param password  密码
     * @return
     */
    Map<String, Object> regist(String name, String email, String password);

    /**
     * 激活指定用户
     * @param id
     * @return
     */
    boolean activeUser(int id);

    /**
     * 验证码
     * @param id 验证码ID
     * @param answer 答案
     * @return
     */
    boolean checkVerificationCode(String id, String answer);

    /**
     * 生成验证码
     * @return  验证码对象
     */
    VerificationCode generateVCode(String id);
}