package com.majunwei.jbone.sys.admin.controller;

import com.majunwei.jbone.common.ui.result.Result;
import com.majunwei.jbone.common.utils.ResultUtils;
import com.majunwei.jbone.sys.dao.domain.RbacMenuEntity;
import com.majunwei.jbone.sys.dao.domain.RbacPermissionEntity;
import com.majunwei.jbone.sys.dao.domain.RbacRoleEntity;
import com.majunwei.jbone.sys.dao.domain.RbacSystemEntity;
import com.majunwei.jbone.sys.service.PermissionService;
import com.majunwei.jbone.sys.service.RoleService;
import com.majunwei.jbone.sys.service.SystemService;
import com.majunwei.jbone.sys.service.model.ListModel;
import com.majunwei.jbone.sys.service.model.common.AssignPermissionModel;
import com.majunwei.jbone.sys.service.model.role.*;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private PermissionService permissionService;

    @RequiresRoles("admin")
    @RequestMapping("/index")
    public String index(){
        return "pages/role/index";
    }

    @RequiresRoles("admin")
    @RequestMapping("/list")
    @ResponseBody
    public Result list(ListModel listModel){
        PageRequest pageRequest = new PageRequest(listModel.getPageNumber()-1,listModel.getPageSize(), Sort.Direction.fromString(listModel.getSortOrder()),listModel.getSortName());
        //分页查找
        Page<RbacRoleEntity> page = roleService.findPage(listModel.getSearchText(),pageRequest);
        List<SimpleRoleModel> list = roleService.getSimpleModels(page.getContent());
        return ResultUtils.wrapSuccess(page.getTotalElements(),list);
    }


    @RequestMapping("/toCreate")
    public String toCreate(){
        return "pages/role/create";
    }

    @RequestMapping("/create")
    @ResponseBody
    public Result create(@Validated CreateRoleModel roleModel, BindingResult bindingResult){
        roleService.save(roleModel);
        return ResultUtils.wrapSuccess();
    }

    @RequestMapping("/toUpdate/{id}")
    public String toUpdate(@PathVariable("id")String id, ModelMap modelMap){
        RbacRoleEntity roleEntity = roleService.get(Integer.parseInt(id));
        modelMap.put("role",roleEntity);
        return "pages/role/update";
    }

    @RequestMapping("/update")
    @ResponseBody
    public Result update(@Validated UpdateRoleModel roleModel, BindingResult bindingResult){
        roleService.update(roleModel);
        return ResultUtils.wrapSuccess();
    }

    @RequestMapping("/delete/{ids}")
    @ResponseBody
    public Result delete(@PathVariable("ids")String ids){
        roleService.delete(ids);
        return ResultUtils.wrapSuccess();
    }

    @RequestMapping("/get/{id}")
    @ResponseBody
    public Result get(@PathVariable("id")String id){
        RbacRoleEntity roleEntity = roleService.get(Integer.parseInt(id));
        return ResultUtils.wrapSuccess(roleEntity);
    }

    @Description("跳转至分配菜单页面")
    @RequestMapping("toAssignMenu/{roleId}")
    public String toAssignMenu(@PathVariable("roleId")String roleId,ModelMap modelMap){
        List<RbacSystemEntity> systemEntities = systemService.findAll();
        modelMap.put("systemList",systemEntities);
        modelMap.put("roleId",roleId);

        RbacRoleEntity roleEntity = roleService.get(Integer.parseInt(roleId));
        List<RbacMenuEntity> menuEntityList = roleEntity.getMenus();
        List<Integer> menuIds = new ArrayList<>();
        if(menuEntityList != null && !menuEntityList.isEmpty()){
            for (RbacMenuEntity menuEntity : menuEntityList){
                menuIds.add(menuEntity.getId());
            }
        }
        modelMap.put("menuIds",menuIds);

        return "pages/role/assignMenu";
    }

    @Description("执行分配菜单")
    @RequestMapping("doAssignMenu")
    @ResponseBody
    public Result doAssignMenu(@Validated AssignMenuModel menuModel, BindingResult bindingResult){
        roleService.assignMenu(menuModel);
        return ResultUtils.wrapSuccess();
    }

    @Description("跳转至分配菜单页面")
    @RequestMapping("toAssignPermission/{roleId}")
    public String toAssignPermission(@PathVariable("roleId")String roleId,ModelMap modelMap){
        List<RbacSystemEntity> systemEntities = systemService.findAll();
        modelMap.put("systemList",systemEntities);
        modelMap.put("id",roleId);

        RbacRoleEntity roleEntity = roleService.get(Integer.parseInt(roleId));
        List<RbacPermissionEntity> rolePermissions = roleEntity.getPermissions();

        modelMap.put("permissions",permissionService.getBaseInfos(rolePermissions));

        modelMap.put("commitUrl", "/role/doAssignPermission");

        return "pages/common/assignPermission";
    }

    @Description("执行分配菜单")
    @RequestMapping("doAssignPermission")
    @ResponseBody
    public Result doAssignPermission(@Validated AssignPermissionModel assignPermissionModel, BindingResult bindingResult){
        roleService.assignPermission(assignPermissionModel);
        return ResultUtils.wrapSuccess();
    }
}
