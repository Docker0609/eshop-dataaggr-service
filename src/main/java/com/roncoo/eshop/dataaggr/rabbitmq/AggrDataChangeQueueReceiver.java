package com.roncoo.eshop.dataaggr.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.fastjson.JSONObject;

@Component  
@RabbitListener(queues = "aggr-data-change-queue")  
public class AggrDataChangeQueueReceiver {  
	
	@Autowired
	private JedisPool jedisPool;
	
    @RabbitHandler  
    public void process(String message) {  
    	JSONObject messageJSONObject = JSONObject.parseObject(message);
    	
    	String dimType = messageJSONObject.getString("dim_type");  
    	
    	if("brand".equals(dimType)) {
    		processBrandDimDataChange(messageJSONObject); 
    	} else if("category".equals(dimType)) {
    		processCategoryDimDataChange(messageJSONObject); 
    	} else if("product_intro".equals(dimType)) {
    		processProductIntroDimDataChange(messageJSONObject); 
    	} else if("product".equals(dimType)) {
    		processProductDimDataChange(messageJSONObject); 
    	}
    }  
    
    private void processBrandDimDataChange(JSONObject messageJSONObject) {
    	Long id = messageJSONObject.getLong("id");  
    	
    	Jedis jedis = jedisPool.getResource();
    	
    	// ���һ�٣���һ�£������һ��Ʒ�����ݣ�Ȼ��ֱ�Ӿ�ԭ��дredis
    	// ʵ�����������ӵģ����������Ǽ������ݽṹ��ҵ��ʵ�����κ�һ��ά�����ݶ�������ֻ��һ��ԭ������
    	// Ʒ�����ݣ��϶��ǽṹ���ģ��ṹ�Ƚϸ��ӣ��кܶ಻ͬ�ı���ͬ��ԭ������
    	// ʵ��������϶���Ҫ��һ��Ʒ�ƶ�Ӧ�Ķ��ԭ�����ݶ���redis��ѯ������Ȼ��ۺ�֮��д��redis
    	String dataJSON = jedis.get("brand_" + id);
    	
    	if(dataJSON != null && !"".equals(dataJSON)) {
    		jedis.set("dim_brand_" + id, dataJSON);
    	} else {
    		jedis.del("dim_brand_" + id);
    	}
    }
    
    private void processCategoryDimDataChange(JSONObject messageJSONObject) {
    	Long id = messageJSONObject.getLong("id");  
    	
    	Jedis jedis = jedisPool.getResource();
    	
    	// ���һ�٣���һ�£������һ��Ʒ�����ݣ�Ȼ��ֱ�Ӿ�ԭ��дredis
    	// ʵ�����������ӵģ����������Ǽ������ݽṹ��ҵ��ʵ�����κ�һ��ά�����ݶ�������ֻ��һ��ԭ������
    	// Ʒ�����ݣ��϶��ǽṹ���ģ��ṹ�Ƚϸ��ӣ��кܶ಻ͬ�ı���ͬ��ԭ������
    	// ʵ��������϶���Ҫ��һ��Ʒ�ƶ�Ӧ�Ķ��ԭ�����ݶ���redis��ѯ������Ȼ��ۺ�֮��д��redis
    	String dataJSON = jedis.get("category_" + id);
    	
    	if(dataJSON != null && !"".equals(dataJSON)) {
    		jedis.set("dim_category_" + id, dataJSON);
    	} else {
    		jedis.del("dim_category_" + id);
    	}
    }
    
    private void processProductIntroDimDataChange(JSONObject messageJSONObject) {
    	Long id = messageJSONObject.getLong("id");  
    	
    	Jedis jedis = jedisPool.getResource();
    	
    	String dataJSON = jedis.get("product_intro_" + id);
    	
    	if(dataJSON != null && !"".equals(dataJSON)) {
    		jedis.set("dim_product_intro_" + id, dataJSON);
    	} else {
    		jedis.del("dim_product_intro_" + id);
    	}
    }
    
    private void processProductDimDataChange(JSONObject messageJSONObject) {
    	Long id = messageJSONObject.getLong("id");  
    	
    	Jedis jedis = jedisPool.getResource();
    	
    	String productDataJSON = jedis.get("product_" + id);
    	
    	if(productDataJSON != null && !"".equals(productDataJSON)) {
    		JSONObject productDataJSONObject = JSONObject.parseObject(productDataJSON);
    		
    		String productPropertyDataJSON = jedis.get("product_property_" + id);
    		if(productPropertyDataJSON != null && !"".equals(productPropertyDataJSON)) {
    			productDataJSONObject.put("product_property", JSONObject.parse(productPropertyDataJSON));
    		} 
    		
    		String productSpecificationDataJSON = jedis.get("product_specification_" + id);
    		if(productSpecificationDataJSON != null && !"".equals(productSpecificationDataJSON)) {
    			productDataJSONObject.put("product_specification", JSONObject.parse(productSpecificationDataJSON));
    		}
    		
    		jedis.set("dim_product_" + id, productDataJSONObject.toJSONString());
    	} else {
    		jedis.del("dim_product_" + id);
    	}
    }
  
}  