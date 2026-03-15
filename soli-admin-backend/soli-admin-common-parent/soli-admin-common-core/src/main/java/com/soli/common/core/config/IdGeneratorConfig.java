package com.soli.common.core.config;

import org.springframework.context.annotation.Configuration;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lizhengqiang
 * @since 2026-03-15 22:17
*/
@Slf4j
@Configuration
public class IdGeneratorConfig {

    @PostConstruct
    public void initIdGenerator() {
        // 1. WorkerId，必须唯一，可以使用机器ID、服务编号或数据中心+机器编号组合
        short workerId = 1;

        // 2. 创建配置对象
        IdGeneratorOptions options = new IdGeneratorOptions(workerId);

        // 3. 可选参数（默认值已经足够，按需修改）
        options.WorkerIdBitLength = 10;     // WorkerId 占位10位 → 最大支持 1024 节点
        options.SeqBitLength = 6;           // 序列号占位6位 → 每毫秒最多生成 64 个 ID
        options.BaseTime = 1672531200000L;  // 例如2023-01-01 00:00:00 UTC，可兼容老系统

        // 4. 设置全局 ID 生成器
        YitIdHelper.setIdGenerator(options);

        log.info("全局雪花 ID 生成器初始化完成");
    }

}