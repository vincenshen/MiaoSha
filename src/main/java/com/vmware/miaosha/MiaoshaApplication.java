package com.vmware.miaosha;

import com.vmware.miaosha.dao.UserDOMapper;
import com.vmware.miaosha.dataobject.UserDO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"com.vmware.miaosha"})
@RestController
@MapperScan("com.vmware.miaosha.dao")
public class MiaoshaApplication {

	@Autowired
	private UserDOMapper userDOMapper;

	@GetMapping("/")
	public String home(){
		UserDO userDO = userDOMapper.selectByPrimaryKey(1);
		if(userDO == null){
			return "用户对象不存在";
		} else {
			return userDO.getName();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(MiaoshaApplication.class, args);
	}

}

