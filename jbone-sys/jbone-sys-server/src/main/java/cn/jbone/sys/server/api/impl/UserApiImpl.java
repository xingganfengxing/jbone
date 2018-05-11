package cn.jbone.sys.server.api.impl;

import cn.jbone.common.rpc.Result;
import cn.jbone.sys.api.UserApi;
import cn.jbone.sys.api.dto.response.UserBaseInfoResponseDTO;
import cn.jbone.sys.api.dto.response.UserInfoResponseDTO;
import cn.jbone.sys.api.dto.response.UserSecurityQuestionsResponseDTO;
import cn.jbone.sys.core.service.UserService;
import cn.jbone.sys.core.service.model.user.UserBaseInfoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserApiImpl implements UserApi {

    private Logger logger = LoggerFactory.getLogger(UserApiImpl.class);

    @Autowired
    private UserService userService;

    @RequestMapping("/getUserByName")
    public Result<UserBaseInfoResponseDTO> getUserByName(String username) {
        UserBaseInfoResponseDTO userInfoModel = null;
        try {
            UserBaseInfoModel userEntity = userService.findByUserName(username);

            if(userEntity == null){
                return Result.wrap404Error("user is not found");
            }

            userInfoModel = new UserBaseInfoResponseDTO();

            BeanUtils.copyProperties(userEntity,userInfoModel);

        } catch (Exception e) {
            return Result.wrap500Error(e.getMessage());
        }
        return new Result(userInfoModel);
    }

    @RequestMapping("/getUserDetail")
    public Result<UserInfoResponseDTO> getUserDetailByName(String username){
        UserInfoResponseDTO userModel = null;
        try {
            userModel = userService.getUserDetailByNameAndServerName(username,null);
            if(userModel == null){
                return Result.wrap404Error("user is not found");
            }
        } catch (Exception e) {
            return Result.wrap500Error(e.getMessage());
        }

        return new Result(userModel);
    }


    @RequestMapping("/getUserDetailByNameAndServerName")
    public Result<UserInfoResponseDTO> getUserDetailByNameAndServerName(String username, String serverName) {
        UserInfoResponseDTO userModel = null;
        try {
            userModel = userService.getUserDetailByNameAndServerName(username,serverName);
            if(userModel == null){
                return Result.wrap404Error("user is not found");
            }
        } catch (Exception e) {
            return Result.wrap500Error(e.getMessage());
        }
        return new Result(userModel);
    }

    @RequestMapping("/getUserSecurityQuestions")
    public Result<List<UserSecurityQuestionsResponseDTO>> getUserSecurityQuestions(String username) {
        List<UserSecurityQuestionsResponseDTO> responseDTOList = null;
        try {
            responseDTOList = userService.findUserSecurityQuestions(username);
            if(responseDTOList == null){
                return Result.wrap404Error("securityQuestions is not found");
            }
        } catch (Exception e) {
            return Result.wrap500Error(e.getMessage());
        }
        return new Result(responseDTOList);
    }
}
