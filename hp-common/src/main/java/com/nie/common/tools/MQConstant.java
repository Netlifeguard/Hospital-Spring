package com.nie.common.tools;

final public class MQConstant {
    public static final String QUEUE_SYS_MESSAGE = "queue_sys_message";
    public static final String EXCHANGE_SYS_MESSAGE = "exchange_sys_message";
    public static final String ROUTING_KEY_SYS_MESSAGE = "routing_key_sys_message";

    public static final String QUEUE_USER_COUPON = "queue_user_coupon";
    public static final String EXCHANGE_USER_COUPON = "exchange_user_coupon";
    public static final String ROUTING_KEY_USER_COUPON = "routing_key_user_coupon";

    //reids常量
    public static final String scriptLuaDecrement = """
            local key = KEYS[1]       
            local decrement = tonumber(ARGV[1]) 
            
            local current = redis.call('GET', key)
            if not current then
                return -1 
            end
            
            current = tonumber(current)
            if current < decrement then
                return -1 
            end
            
            return redis.call('DECRBY', key, decrement)
            """;

    public static final String scriptLuaIncrement = """
            local key = KEYS[1]       
            local increment = tonumber(ARGV[1]) 
            
            local current = redis.call('GET', key)
            if not current then
                redis.call('SET', key, increment) 
                return increment
            end
            
            current = tonumber(current)
            return redis.call('INCRBY', key, increment)
            """;

    public static final String scriptLuaHash = """
                    local key = KEYS[1]     
                    local field = ARGV[1]  
                    local increment = tonumber(ARGV[2])
            
            
                    local current = redis.call('HGET', key, field)
                    if not current then
            
                        redis.call('HSET', key, field, increment)
                        return increment
                    end
            
            
                    current = tonumber(current)
            
            
                    return redis.call('HINCRBY', key, field, increment)
            """;


}
