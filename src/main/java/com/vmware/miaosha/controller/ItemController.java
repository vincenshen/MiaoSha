package com.vmware.miaosha.controller;


import com.vmware.miaosha.controller.viewobject.ItemVO;
import com.vmware.miaosha.error.BusinessException;
import com.vmware.miaosha.response.CommonReturnType;
import com.vmware.miaosha.service.ItemService;
import com.vmware.miaosha.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller(value = "item")
@RequestMapping(value = "/item")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {

        // 封装Service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);
        ItemModel itemModelForReturn = itemService.createItem(itemModel);

        ItemVO itemVO = this.convertItemVOFromItemModel(itemModelForReturn);
        return CommonReturnType.create(itemVO);
    }

    private ItemVO convertItemVOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        if(itemModel.getPromoModel() != null){
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        }else {
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }

    // 商品详情展示
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id") Integer id){
        ItemModel itemModel = itemService.getItemById(id);
        ItemVO itemVO = convertItemVOFromItemModel(itemModel);
        return CommonReturnType.create(itemVO);
    }

    // 商品列表展示
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType listItems(){
        List<ItemModel> itemModelList = itemService.listItems();
        List<ItemVO> itemVOList = new ArrayList<>();
        for(ItemModel itemModel: itemModelList){
            itemVOList.add(this.convertItemVOFromItemModel(itemModel));
        }
        return CommonReturnType.create(itemVOList);
    }

}
