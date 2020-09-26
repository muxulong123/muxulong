package com.service.impl;

import java.util.List;

import com.jediscomp.JedisClient;
import com.util.JsonUtils;
import com.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.mapper.UserMapper;
import com.po.User;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService{
	@Value("${REDIS_USER_KEY}")
	private String REDIS_USER_KEY;
	@Value("${USER_BASE_INFO_KEY}")
	private  String USER_BASE_INFO_KEY;
	@Value("${ITEM_EXPIRE_SECOND}")
	private Integer ITEM_EXPIRE_SECOND;

	@Autowired1111111
	private JedisClient jedisClient;



	@Autowired
	public UserMapper mapper;

	@Override
	public List<User> list() {
		// TODO Auto-generated method stub
		return mapper.list();
	}
	@Override
	public User selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		String json=jedisClient.get(REDIS_USER_KEY+":"+id+":"+USER_BASE_INFO_KEY);
		if (!StringUtils.isEmpty(json)){
          User user= JsonUtils.jsonToPojo(json,User.class);
			return user;
		}
		/**
		 * 向redis中添加缓存
		 * 原则：不能影响业务的正常逻辑，所以单独处理，使用trycatch
		 */
		try {
			jedisClient.set(REDIS_USER_KEY+":"+id+":"+USER_BASE_INFO_KEY,
					JsonUtils.objectToJson(mapper.selectById(id)));
			//设置key过期时间，因为要设置过期时间，只能在key上设置，所以不能用hash
			jedisClient.expire(REDIS_USER_KEY+":"+id+":"+USER_BASE_INFO_KEY,ITEM_EXPIRE_SECOND);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapper.selectById(id);
	}
	
	

}
