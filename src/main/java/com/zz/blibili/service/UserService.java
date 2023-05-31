package com.zz.blibili.service;

import com.mysql.cj.util.StringUtils;
import com.zz.blibili.entity.RefreshTokenDetail;
import com.zz.blibili.entity.User;
import com.zz.blibili.entity.UserInfo;
import com.zz.blibili.entity.constant.UserConstant;
import com.zz.blibili.exception.ConditionException;
import com.zz.blibili.mapper.UserMapper;
import com.zz.blibili.service.auth.UserAuthService;
import com.zz.blibili.utils.MD5Util;
import com.zz.blibili.utils.RSAUtil;
import com.zz.blibili.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author ZhangZhe
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAuthService userAuthService;

    /*用户注册*/
    public void addUser(User user) {
        String phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)) throw new ConditionException("手机号不能为空！");
        if(userMapper.getUserByPhone(phone) != null) throw new ConditionException("该手机号已经被注册！");
        Date now = new Date();
        String salt = String.valueOf(now.getTime());    //时间戳作为盐值
        String password = user.getPassword();           //前端传入用户的密码（rsa加密后）
        String rawPassword;
        try {
            //对用户输入密码进行rsa解密
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        //对密码进行md5加密，存入数据库
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);
        userMapper.addUser(user);
        System.out.println("---------->" + user);
        //添加用户信息
        UserInfo userInfo = new UserInfo();
        Long userId = user.getId();
        userInfo.setUserId(userId);
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        userInfo.setCreateTime(now);
        userMapper.addUserInfo(userInfo);
        //添加用户默认权限
        userAuthService.addUserDefaultRole(userId);
    }

    /*根据手机号查询用户*/
    public User getUserByPhone(String phone){
        return userMapper.getUserByPhone(phone);
    }

    /*用户登录*/
    public String login(User user) throws Exception {
        String phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)) throw new ConditionException("手机号不能为空！");
        User dbUser = this.getUserByPhone(phone);
        if (dbUser == null) throw new ConditionException("当前用户不存在");
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败");
        }
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "utf-8");
        if (!md5Password.equals(dbUser.getPassword())) throw new ConditionException("密码错误！");
        return TokenUtil.generateToken(dbUser.getId());
    }

    /*根据用户id得到用户信息*/
    public User getUserInfo(Long userId) {
        User user = userMapper.getUserById(userId);
        UserInfo userInfo = userMapper.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    /*更新用户登录信息*/
    public void updateUsers(User user) throws Exception{
        Long id = user.getId();
        User dbUser = userMapper.getUserById(id);
        if (dbUser == null) throw new ConditionException("该用户不存在！");
        if (!StringUtils.isNullOrEmpty(user.getPassword())){
            String rawPassword = RSAUtil.decrypt(user.getPassword());
            String md5Password = MD5Util.sign(rawPassword, dbUser.getSalt(), "utf-8");
            user.setPassword(md5Password);
        }
        user.setUpdateTime(new Date());
        userMapper.updateUser(user);
    }

    /*更新用户详细信息*/
    public void updateUserinfos(UserInfo userInfo) {
        Long userId = userInfo.getUserId();
        UserInfo dbUserInfo = userMapper.getUserInfoByUserId(userId);
        if (userId == null) throw new ConditionException("该用户信息不存在！");
        userInfo.setUpdateTime(new Date());
        userMapper.updateUserInfo(userInfo);
    }

    public User getUserById(Long followingId) {
        return userMapper.getUserById(followingId);
    }

    /*根据用户id集合得到用户集合*/
    public ArrayList<UserInfo> getUserInfoByUserIds(Set<Long> followingIdSet) {
        ArrayList<UserInfo> userInfoList = new ArrayList<>();
        for (Long userId : followingIdSet){
            userInfoList.add(userMapper.getUserInfoByUserId(userId));
        }
        return userInfoList;
    }

    /*登录升级, 双token*/
    public Map<String, Object> loginForDts(User user) throws Exception {
        String phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)) throw new ConditionException("手机号不能为空！");
        User dbUser = this.getUserByPhone(phone);
        if (dbUser == null) throw new ConditionException("当前用户不存在");
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败");
        }
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "utf-8");
        if (!md5Password.equals(dbUser.getPassword())) throw new ConditionException("密码错误！");

        Long userId = dbUser.getId();
        String accessToken =  TokenUtil.generateToken(dbUser.getId());
        String refreshToken = TokenUtil.generateRefreshToken(dbUser.getId()); //刷新token
        //保存refresh token到数据库
        userMapper.deleteRefreshToken(refreshToken, userId);
        userMapper.addRefreshToken(refreshToken, userId, new Date());
        HashMap<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    /*退出登陆*/
    public void logOut(String refreshToken, Long userId) {
        userMapper.deleteRefreshToken(refreshToken, userId);
    }

    /*刷新token*/
    public String refreshAccessToken(String refreshToken) throws Exception {
        RefreshTokenDetail refreshTokenDetail = userMapper.getRefreshTokenDetail(refreshToken);
        if (refreshTokenDetail == null) throw new ConditionException("555", "token过期！");
        Long userId = refreshTokenDetail.getUserId();
        return TokenUtil.generateToken(userId);
    }

    public List<UserInfo> batchGetUserInfoByUserIds(Set<Long> userIdList) {
        return userMapper.batchGetUserInfoByUserIds(userIdList);
    }
}
