/*
 * Copyright 2021 tu.cn All right reserved. This software is the
 * confidential and proprietary information of tu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tu.cn
 */
package com.example.zookeepercuratorexample;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Description:
 *
 * @Author: Administrator
 * Created: 2021/8/18
 **/
public class AuthEncryption {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String userName = "mic";
        String password = "mic";
        String idStr = userName.concat(":").concat(password);
        final MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        String encodedId = Base64.getEncoder().encodeToString(sha1.digest(idStr.getBytes()));

        System.out.println(encodedId);
    }
}
