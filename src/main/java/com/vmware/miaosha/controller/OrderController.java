package com.vmware.miaosha.controller;


import com.vmware.miaosha.error.BusinessException;
import com.vmware.miaosha.error.EnumBusinessError;
import com.vmware.miaosha.response.CommonReturnType;
import com.vmware.miaosha.service.OrderService;
import com.vmware.miaosha.service.model.OrderModel;
import com.vmware.miaosha.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller(value = "order")
@RequestMapping(value = "/order")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    // 封装下单请求
    @RequestMapping(value = "createOrder", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "promoId", required = false) Integer promoId,
                                        @RequestParam(name = "amount") Integer amount) throws BusinessException {

        // 获取用户登录信息
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin == null || !isLogin){
            throw new BusinessException(EnumBusinessError.USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, promoId, amount);

        return CommonReturnType.create(orderModel);
    }
}
