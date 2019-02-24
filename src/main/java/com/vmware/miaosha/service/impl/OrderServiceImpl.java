package com.vmware.miaosha.service.impl;

import com.vmware.miaosha.dao.OrderInfoDOMapper;
import com.vmware.miaosha.dao.SequenceDOMapper;
import com.vmware.miaosha.dataobject.OrderInfoDO;
import com.vmware.miaosha.dataobject.SequenceDO;
import com.vmware.miaosha.error.BusinessException;
import com.vmware.miaosha.error.EnumBusinessError;
import com.vmware.miaosha.service.ItemService;
import com.vmware.miaosha.service.OrderService;
import com.vmware.miaosha.service.UserService;
import com.vmware.miaosha.service.model.ItemModel;
import com.vmware.miaosha.service.model.OrderModel;
import com.vmware.miaosha.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderInfoDOMapper orderInfoDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {
        // 校验下单状态：
        // 下单商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if(itemModel == null){
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "商品不存在");
        }

        UserModel userModel = userService.getUserById(userId);
        if(userModel == null){
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "用户不存在");
        }

        if(amount <= 0){
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "购买数量不能小于0");
        }

        // 校验秒杀活动信息
        if(promoId != null){
            // 1. 校验对应活动是否存在这个商品
            if(promoId.intValue() != itemModel.getPromoModel().getId()){
                throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "活动信息不正确");
            }
            if(itemModel.getPromoModel().getStatus() != 2){
                throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "秒杀活动未开始");
            }
        }

        // 落单减库存 or 支付减库存
        boolean result = itemService.decreaseStock(itemId, amount);
        if(!result){
            throw new BusinessException(EnumBusinessError.STOCK_NOT_ENOUGH);
        }

        // 订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        if(promoId != null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else {
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));

        // 生成交易流水号，订单号
        orderModel.setId(this.generatorOrderNo());

        OrderInfoDO orderInfoDO = this.convertOrderDOFromOrderModel(orderModel);
        orderInfoDOMapper.insertSelective(orderInfoDO);

        // 加上上来的销量
        itemService.increaseSales(itemId, amount);

        // 返回前端
        return orderModel;
    }

    private OrderInfoDO convertOrderDOFromOrderModel(OrderModel orderModel){
        if(orderModel == null){
            return null;
        }
        OrderInfoDO orderInfoDO = new OrderInfoDO();
        BeanUtils.copyProperties(orderModel, orderInfoDO);
        return orderInfoDO;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generatorOrderNo(){
        // 订单号16位
        StringBuilder stringBuilder = new StringBuilder();

        // 前八位时间信息：年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        stringBuilder.append(nowDate);

        // 中间6位为自增序列
        // 获取当前Sequence
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        int sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);

        String sequenceStr = String.valueOf(sequence);
        // 用0补齐6位
        for(int i = 0; i < 6-sequenceStr.length(); i ++){
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);

        // 最后2位为分库分表位, 暂时写死
        stringBuilder.append("00");

        return stringBuilder.toString();
    }
}
