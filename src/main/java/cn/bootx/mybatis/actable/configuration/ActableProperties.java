package cn.bootx.mybatis.actable.configuration;

import cn.bootx.mybatis.actable.constants.DatabaseType;
import cn.bootx.mybatis.actable.constants.UpdateType;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xxm
 * @date 2023/1/16
 */
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "bootx.common.actable")
public class ActableProperties {

    /**
     * 数据库类型
     */
    private DatabaseType databaseType = DatabaseType.MYSQL;

    /**
     * 更新模式
     */
    private UpdateType updateType = UpdateType.NONE;

    /**
     * 扫描包路径, 可以用 ,和 ; 分隔
     */
    private String scanPackage;

    /**
     * 指定生成索引前缀
     */
    private String prefixIndex = "actable_idx_";

    /**
     * 指定生成唯一约束前缀
     */
    private String prefixUnique = "actable_uni_";

}
