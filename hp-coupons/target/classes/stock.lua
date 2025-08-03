local key = KEYS[1]            -- 库存键名（如："coupon:stock:1001"）
local decrement = tonumber(ARGV[1])  -- 要扣减的数量（如：1）

-- 检查库存是否存在
local current = redis.call('GET', key)
if not current then
    return -1  -- 键不存在，返回-1表示错误
end

-- 转换当前库存为数字
current = tonumber(current)
if current < decrement then
    return -1  -- 库存不足，返回-1
end

-- 执行原子化扣减，并返回扣减后的值
return redis.call('DECRBY', key, decrement)