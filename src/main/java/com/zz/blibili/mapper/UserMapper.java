package com.zz.blibili.mapper;

import com.zz.blibili.entity.RefreshTokenDetail;
import com.zz.blibili.entity.User;
import com.zz.blibili.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Mapper
public interface UserMapper {
    /*根据手机号查询用户*/
    User getUserByPhone(String phone);
    /*根据电话得到用户id*/
    Long getUserIdByPhone(String phone);

    /*注册用户*/
    Integer addUser(User user);

    /*添加用户信息*/
    Integer addUserInfo(UserInfo userInfo);

    /*得到用户登录信息*/
    User getUserById(Long id);
    /*得到用户详细信息*/
    UserInfo getUserInfoByUserId(Long userId);
    /*更新用户登录信息*/
    Integer updateUser(User user);
    /*更新用户详细信息*/
    Integer updateUserInfo(UserInfo userInfo);

    /*删除刷新token*/
    Integer deleteRefreshToken(@Param("refreshToken") String refreshToken,
                               @Param("userId") Long userId);
    /*添加新的刷新token*/
    Integer addRefreshToken(@Param("refreshToken")String refreshToken,
                            @Param("userId")Long userId,
                            @Param("createTime")Date date);

    /*得到刷新token详情*/
    RefreshTokenDetail getRefreshTokenDetail(String refreshToken);

    List<UserInfo> batchGetUserInfoByUserIds(Set<Long> userIdList);
}
