package com.fanruan.platform.controller;

import com.fanruan.platform.bean.*;
import com.fanruan.platform.constant.CommonUtils;
import com.fanruan.platform.dao.UserFocusDao;
import com.fanruan.platform.mapper.PdfMapper;
import com.fanruan.platform.mapper.UserMapper;
import com.fanruan.platform.service.CommonService;
import com.fanruan.platform.service.CompanyService;
import com.fanruan.platform.service.UserService;
import com.fanruan.platform.util.MD5Util;
import com.fanruan.platform.util.TokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping
public class UserController{

    @Autowired
    private UserFocusDao userFocusDao;
    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommonService  commonService;

    @RequestMapping(value = "/user/login",method = RequestMethod.POST)
    @ResponseBody
    public String login(HttpServletRequest request, @RequestBody Map<String,Object> para) throws JsonProcessingException {
        String username=(String)para.get("username");
        String password=(String)para.get("password");
        String loginType=(String)para.get("loginType");
        String sign=(String)para.get("sign");
        //log4j 测试
//        Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
//        logger.log(Level.valueOf("info"),"log4j test!");
        HashMap<String,Object> hs=new HashMap<>();
        User user = null;
        String isInterface  = null;
        if(loginType!=null || sign !=null){
            if(sign !=null && sign !=""){
             isInterface = MD5Util.MD5(username+"44bce5ef-873e-4689-b515-a1ef9775aa82");
             if(sign.equals(isInterface)){
                 user = userService.getUserCheck(username,null);
             }
            }else {
                user = userService.getUserCheck(username,null);
            }

        }else {
            if(StringUtils.isBlank(username)||StringUtils.isBlank(password)){
                hs.put("code","1");
                hs.put("msg","用户名或者密码为空");
                ObjectMapper objectMapper=new ObjectMapper();
                return objectMapper.writeValueAsString(hs);
            }
            password = MD5Util.MD5(username+password);
            user =  userService.getUserCheck(username,password);
        }



        if(user==null){
            hs.put("code","2");
            String msg = loginType!=null?"用户名错误或者被停用":"用户名或者密码错误或者被停用";
            hs.put("msg",msg);
            ObjectMapper objectMapper=new ObjectMapper();
            return objectMapper.writeValueAsString(hs);
        }
        String companyStatus = companyService.getCompanyStatus(user.getCompanyCode());
        if("0".equals(companyStatus)){
            hs.put("code","3");
            hs.put("msg","公司已停用，请联系上级管理员");
            ObjectMapper objectMapper=new ObjectMapper();
            return objectMapper.writeValueAsString(hs);
        }

        String rolePermission = "";
        if(user.getRoleName() != null && !user.getRoleName().equals("")){
            rolePermission = userService.getRolePermission(user.getRoleName());
            rolePermission = rolePermission.contains("访问日志权限") ? "访问日志权限" : "";
        }
        user.setPermissionRoles(rolePermission);

        //新增密码过期校验 (Kern on 20210616)
        String isOverdue = "0";
        if(!username.equals("ytdsrpa")){//英特机器人账号：ytdsrpa，特殊处理，不做密码过期校验
            if(user.getPasswordUpdateTime() != null){
                isOverdue = userService.checkPasswordTime(user.getPasswordUpdateTime());
            }else{
                isOverdue = "1";
            }
        }

        String token= TokenUtil.sign(user);
        hs.put("code","0");
        hs.put("msg","登录成功");
        hs.put("token",token);
        hs.put("username",username);
        hs.put("name",user.getName());
        hs.put("userId",user.getUserId());
        hs.put("companyCode",user.getCompanyCode());
        hs.put("companyName",user.getCompanyName());
        hs.put("permissionRoles",user.getPermissionRoles());
        hs.put("isOverdue",isOverdue);
        request.getSession().setAttribute("username", username);
        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/loginOut",method = RequestMethod.POST)
    @ResponseBody
    public String loginOut(HttpServletRequest request, @RequestBody Map<String,Object> para) throws JsonProcessingException {
        String username=(String)para.get("username");
        String password=(String)para.get("password");
        if(StringUtils.isBlank(username)||StringUtils.isBlank(password)){
            return null;
        }
        User user =  userService.getUserCheck(username,password);
        if(user==null){
            return null;
        }
        TokenUtil.signOut(user);
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("username",username);
        hs.put("userId",user.getUserId());
        hs.put("loginOut",true);
        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/userExists",method = RequestMethod.POST)
    @ResponseBody
    public String userExists(HttpServletRequest request, @RequestBody Map<String,Object> para) throws JsonProcessingException {
        String username=(String)para.get("username");
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        if(StringUtils.isBlank(username)){
            return null;
        }
        User user =  userService.getUserByUsername(username);
        if(user==null){
            hs.put("userExists",false);
        }else {
            hs.put("userExists",true);
        }
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/getUserList",method = RequestMethod.POST)
    @ResponseBody
    public String getUserList(HttpServletRequest request, @RequestBody Map<String,Object> para) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        Integer pageIndex = CommonUtils.getIntegerValue(para.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(para.get("pageSize"));
        Integer isSubAdmin = CommonUtils.getIntegerValue(para.get("isSubAdmin"));
        String operator = (String)para.get("operator");
        List<User> userList = null;
        if(StringUtils.equals(operator,"admin")){
            userList = userService.getUserList(pageIndex,pageSize);
        }else {
            userList = userService.getSubAdminUserList(hs,pageIndex,pageSize,operator);
        }
        userService.fillUpdatePermission(hs,userList,operator,isSubAdmin,pageSize);
        ObjectMapper objectMapper=new ObjectMapper();
        hs.put("code",0);
        hs.put("userList",userList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/searchUserList",method = RequestMethod.POST)
    @ResponseBody
    public String searchUserList(HttpServletRequest request, @RequestBody Map<String,Object> para) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        Integer pageIndex = CommonUtils.getIntegerValue(para.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(para.get("pageSize"));
        String operator = (String)para.get("operator");
        Integer isSubAdmin = CommonUtils.getIntegerValue(para.get("isSubAdmin"));
        String username = (String)para.get("username");
        String name = (String)para.get("name");
        Integer status = CommonUtils.getIntegerValue(para.get("status"));
        HashMap<String,Object> hs=new HashMap<>();
        //测试提交git
        List<User> userList = null;
        if(StringUtils.equals(operator,"admin")){
            userList = userService.searchUserList(hs,pageIndex,pageSize,username,name,status);
        }else if(StringUtils.isNotBlank(username)||StringUtils.isNotBlank(name)){
            userList = userService.searchSubAdminUserList(hs,pageIndex,pageSize,username,name,status,operator);
        }else {
            userList = userService.getSubAdminUserList(hs,pageIndex,pageSize,operator);
        }
        userList = userService.fillUpdatePermission(hs,userList,operator,isSubAdmin,pageSize);
        hs.put("code",0);
        hs.put("userList",userList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/searchUserListNew",method = RequestMethod.POST)
    @ResponseBody
    public String searchUserListNew(HttpServletRequest request, @RequestBody Map<String,Object> para) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        Integer pageIndex = CommonUtils.getIntegerValue(para.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(para.get("pageSize"));
        String operator = (String)para.get("operator");
//        Integer isSubAdmin = CommonUtils.getIntegerValue(para.get("isSubAdmin"));
        String roleName = (String)para.get("isSubAdmin");
        Integer isSubAdmin = roleName.equals("子管理员") ? 1 : 0;
        String username = (String)para.get("username");
        String name = (String)para.get("name");
        String companyCode = (String)para.get("companyCode");
        Boolean isLevel = (Boolean)para.get("isLevel");
        Integer status = CommonUtils.getIntegerValue(para.get("status"));
        HashMap<String,Object> hs=new HashMap<>();
        List<User> userList = null;
//        if(StringUtils.equals(operator,"admin")){
//            hs = userService.getUserList(hs,pageIndex,pageSize,username,name,status,isSubAdmin,companyCode);
//            userList = (List<User> )hs.get("userList");
//        }else {
//            hs =  userService.searchSubAdminUserListNew(hs,pageIndex,pageSize,username,name,status,isSubAdmin,operator,companyCode);
//            userList = (List<User> )hs.get("userList");
//        }
        if(isLevel){
            hs = userService.getUserListLevel(hs,pageIndex,pageSize,username,name,status,isSubAdmin,companyCode,roleName);
            userList = (List<User> )hs.get("userList");
        }else{
            hs = userService.getUserList(hs,pageIndex,pageSize,username,name,status,isSubAdmin,companyCode,roleName);
            userList = (List<User>)hs.get("userList");
        }
        userList = userService.fillUpdatePermissionNew(hs,userList,operator);
        hs.put("userList",userList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/verifyPermissions",method = RequestMethod.POST)
    @ResponseBody
    public String verifyPermissions(HttpServletRequest request, @RequestBody Map<String,Object> para) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Integer userId = CommonUtils.getIntegerValue(para.get("userId"));
        String permissionPoint = (String)para.get("permissionPoint");
        Map<String,Boolean> permissionPointresult = userService.verifyPermissions(hs,userId,permissionPoint);
        if(permissionPointresult==null){
            return objectMapper.writeValueAsString(hs);
        }
        hs.put("code",0);
        hs.put("verifyPermissionResult",permissionPointresult);
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/user/updateUser",method = RequestMethod.POST)
    @ResponseBody
    public String updateUser( @RequestBody Map<String,Object> para) throws JsonProcessingException {
        Integer userId = CommonUtils.getIntegerValue(para.get("userId"));
        String operator = (String)para.get("operator");
        HashMap<String,Object> hs=new HashMap<>();
        User user = userService.updateUser(operator,userId, para);
        ObjectMapper objectMapper=new ObjectMapper();
        if(user!=null){
            hs.put("code",0);
            hs.put("msg","用户更新成功");
            hs.put("user",user);
        }else {
            hs.put("code",1);
            hs.put("msg","用户更新失败");
        }
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/getUserInfo",method = RequestMethod.POST)
    @ResponseBody
    public String getUserInfo( @RequestBody Map<String,Object> para) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        Integer userId = CommonUtils.getIntegerValue(para.get("userId"));
        String operator = (String)para.get("operator");
        HashMap<String,Object> hs=new HashMap<>();
        User user = userService.getUserInfo(userId,operator);
        List<PermissionPoint> permissionList = userService.getPermissionList(operator);
        hs.put("user",user);
        hs.put("permissionList",permissionList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/getEnablePermission",method = RequestMethod.POST)
    @ResponseBody
    public String getEnablePermission( @RequestBody Map<String,Object> para) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        String operator = (String)para.get("operator");
        HashMap<String,Object> hs=new HashMap<>();
        List<PermissionPoint> permissionList = userService.getPermissionList(operator);
        hs.put("permissionList",permissionList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/getUserById",method = RequestMethod.POST)
    @ResponseBody
    public String getUserById( @RequestBody Map<String,Object> para) throws JsonProcessingException {
        Integer userId =  CommonUtils.getIntegerValue(para.get("userId"));
        HashMap<String,Object> hs=new HashMap<>();
        User user = userService.getUserById(userId);
        ObjectMapper objectMapper=new ObjectMapper();
        if(user==null){
            hs.put("code",1);
        }else {
            hs.put("code",0);
        }
        hs.put("user",user);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/careOrNot",method = RequestMethod.POST)
    @ResponseBody
    public String careOrNot(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        Integer userId =  CommonUtils.getIntegerValue(param.get("userId"));
        Integer companyId =  CommonUtils.getIntegerValue(param.get("companyId"));
        Integer tianyanchaFlag = CommonUtils.getIntegerValue(param.get("tianyancha"));
        Integer zhongchengxinFlag = CommonUtils.getIntegerValue(param.get("zhongchengxin"));
        Map<String,Integer> relations = Maps.newHashMap();
        if(tianyanchaFlag!=null){
            relations.put("tianyancha",tianyanchaFlag);
        }
       if(zhongchengxinFlag!=null){
           relations.put("zhongchengxin",zhongchengxinFlag);
       }
       companyService.updateConcernInfo(param, companyId, userId, tianyanchaFlag, zhongchengxinFlag);
        HashMap<String,Object> hs=new HashMap<>();
        if(userId != null && companyId!=null){
            userService.saveRelation(hs,userId,companyId,relations);
            hs.put("code","0");
            hs.put("msg","用户关注关系更新成功");
        }else {
            hs.put("code","1");
            hs.put("msg"," userId或者companyId为空或者监控接口报错");
        }
        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/getCareList",method = RequestMethod.POST)
    @ResponseBody
    public String getCareList(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        Integer userId = CommonUtils.getIntegerValue(param.get("userId"));
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        String relations =  userService.getCareCompanySet(userId,pageIndex,pageSize);
        List<UserFocus> userFocusList = userFocusDao.findAllByUserId(userId);

        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code","0");
        hs.put("msg","");
        hs.put("careList",relations);
        hs.put("total",userFocusList.size());
        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/getCareStatus",method = RequestMethod.POST)
    @ResponseBody
    public String getCareStatus(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        Integer userId = CommonUtils.getIntegerValue(param.get("userId"));
        Integer companyId = CommonUtils.getIntegerValue(param.get("companyId"));
        String careFlag =  userService.getCareStatus(userId,companyId);
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code","0");
        hs.put("msg","");
        hs.put("careStatus",careFlag);
        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/updateUserStatus",method = RequestMethod.POST)
    @ResponseBody
    public String updateUserStatus(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        try {
            userMapper.updateUserStatus();
            hs.put("code","0");
            hs.put("msg","");
        }catch(Exception e){
            hs.put("code","500");
            hs.put("msg",e.getMessage());
        }
        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/getAllRole",method = RequestMethod.POST)
    @ResponseBody
    public String getAllRole(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Page<Role> allRole = userService.getAllRole(param);
        if(null == allRole){
            hs.put("code",1);
            hs.put("msg","未查询到角色信息");
            hs.put("allRole","");
            return objectMapper.writeValueAsString(hs);
        }
        hs.put("code",0);
        hs.put("msg","");
        hs.put("totalPages",allRole.getTotalPages());
        hs.put("totalRecords",allRole.getTotalElements());
        hs.put("allRole",allRole);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/getRolePermission",method = RequestMethod.POST)
    @ResponseBody
    public String getRolePermission(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Integer userId = CommonUtils.getIntegerValue(param.get("userId"));
        Optional<User> role = userService.getRolePermission(userId);

        if(1 == userId){
            List<Role> allRole = userService.getAllRole();
            hs.put("code",0);
            hs.put("msg","");
            hs.put("allRole",allRole);
            return objectMapper.writeValueAsString(hs);
        }

        if(!role.isPresent()){
            hs.put("code",1);
            hs.put("msg","未查询到角色信息");
            hs.put("allRole","");
            return objectMapper.writeValueAsString(hs);
        }
        User user = role.get();
        if(null == user ||  StringUtils.isBlank(user.getRoleName())){
            hs.put("code",1);
            hs.put("msg","未查询到用户权限信息");
            hs.put("allRole","");
            return objectMapper.writeValueAsString(hs);
        }
            String[] split = user.getRoleName().split(",");
            List<Role> permissions = new ArrayList<>();
            for(String permissionStr:split){
                Optional<Role> permission = userService.getRoleByRoleName(permissionStr);
                if(permission.isPresent()){
                    permissions.add(permission.get());
                }
            }
            hs.put("code",0);
            hs.put("msg","");
            hs.put("allRole",permissions);
            return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/getRole",method = RequestMethod.POST)
    @ResponseBody
    public String getRole() throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
//        HashMap<String,Object> allRole = userService.getRole();
        List<String> allRole = userService.getRole1();

        hs.put("code",0);
        hs.put("msg","");
        hs.put("allRole",allRole);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/saveOrEditRole",method = RequestMethod.POST)
    @ResponseBody
    public String saveOrEditRole(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Role role = userService.saveOrEditRole(param);
        if(null == role){
            hs.put("code","1");
            hs.put("msg","角色名称和权限不能为空");
            hs.put("role",role);
            return objectMapper.writeValueAsString(hs);
        }
        hs.put("code","0");
        hs.put("msg","角色更新成功");
        hs.put("role",role);
        return objectMapper.writeValueAsString(hs);
    }
    @RequestMapping(value = "/user/roleNameExists",method = RequestMethod.POST)
    @ResponseBody
    public String roleNameExists(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Boolean isNew = (Boolean)param.get("isNew");
        String roleId = (String)param.get("roleId");
        String roleName = (String)param.get("roleName");
        if(StringUtils.isBlank(roleName)){
            return  null;
        }
        Optional<Role> role = userService.roleNameExists(roleName);
        if((role.isPresent() && isNew)|| (role.isPresent() && !isNew && !(roleId.equals(role.get().getRoleId())))){
            hs.put("roleNameExists",true);
        }else{
            hs.put("roleNameExists",false);
        }
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/user/getReviewer",method = RequestMethod.POST)
    @ResponseBody
    public String getReviewer(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String userName = (String)param.get("userName");
        String isReviewer = userService.getReviewer(userName);
        hs.put("isReviewer",false);
        if("1".equals(isReviewer)){
            hs.put("isReviewer",true);
        }
        hs.put("msg","");
        hs.put("code","0");
        return objectMapper.writeValueAsString(hs);
    }
}