package com.zz.blibili.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zz.blibili.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

/**
 * @author ZhangZhe
 * 生成用户令牌
 */
public class TokenUtil {
    public static final String ISSUER = "ZhangZhe";
    /*生成token*/
    public static String generateToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 2);     //token过期时间
        String token = JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)                 //发行者
                .withExpiresAt(calendar.getTime())  //过期时间
                .sign(algorithm);                   //jwt生成算法
        return token;
    }

    /*生成刷新token*/
    public static String generateRefreshToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, 2);     //token过期时间
        String token = JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)                 //发行者
                .withExpiresAt(calendar.getTime())  //过期时间
                .sign(algorithm);                   //jwt生成算法
        return token;
    }

    /*验证token*/
    public static Long verifyToken(String token) {
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String userId = jwt.getKeyId();
            return Long.valueOf(userId);
        } catch (TokenExpiredException e) {
            throw new ConditionException("555", "token过期！");
        } catch (Exception e){
            throw new ConditionException("非法用户token！");
        }
    }


}
