package cn.bootx.mybatis.table.modify.impl.mysql.service;

import cn.bootx.mybatis.table.modify.constants.TableCharset;
import cn.bootx.mybatis.table.modify.impl.mysql.annotation.MySqlEngine;
import cn.bootx.mybatis.table.modify.impl.mysql.constants.MySqlEngineEnum;
import cn.bootx.mybatis.table.modify.impl.mysql.entity.*;
import cn.bootx.mybatis.table.modify.impl.mysql.mapper.MySqlTableModifyMapper;
import cn.bootx.mybatis.table.modify.impl.mysql.util.MySqlInfoUtil;
import cn.bootx.mybatis.table.modify.utils.ColumnUtils;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * MySQL表信息变动
 * @author xxm
 * @date 2023/4/14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MySqlTableInfoService {

    private final MySqlTableModifyMapper mysqlTableModifyMapper;

    /**
     * 创建表
     */
    public void getCreateTable(Class<?> clas, MySqlModifyMap baseTableMap){
        String tableName = ColumnUtils.getTableName(clas);

        // 获取表注释
        String comment = ColumnUtils.getTableComment(clas);

        // 获取表字符集
        TableCharset charset = ColumnUtils.getTableCharset(clas);

        // 获取表引擎
        MySqlEngineEnum engine = getEntityEngine(clas);

        // 获取主键
        List<String> keys = getEntityKeys(clas);

        MySqlEntityTable entityTable = new MySqlEntityTable()
                .setName(tableName)
                .setComment(comment)
                .setCharset(charset)
                .setEngine(engine)
                .setKeys(keys);
        baseTableMap.getCreateTables().add(entityTable);
    }

    /**
     * 更新表信息
     */
    public void getModifyTable(Class<?> clas,MySqlModifyMap baseTableMap){
        String tableName = ColumnUtils.getTableName(clas);

        MySqlTableUpdate updateTableInfo = new MySqlTableUpdate()
                .setName(tableName);
        MySqlTableInfo tableInfo = mysqlTableModifyMapper.findTableByTableName(tableName);
        // 比对表注释
        String entityComment = ColumnUtils.getTableComment(clas);
        if (!Objects.equals(tableInfo.getTableComment(),entityComment)){
            updateTableInfo.setComment(entityComment)
                    .setCommentUpdate(true);
        }

        // 比对表字符集, 表不为空且字符集不一致
        TableCharset entityCharset = ColumnUtils.getTableCharset(clas);
        String tableCharset = this.getTableCharset(tableInfo.getTableCollation());
        if (Objects.nonNull(entityCharset)&&
                tableCharset.equalsIgnoreCase(entityCharset.getValue())){
            updateTableInfo.setCharset(entityComment)
                    .setCharsetUpdate(true);
        }
        // 获取表引擎
        MySqlEngineEnum entityEngine = getEntityEngine(clas);
        if (Objects.nonNull(entityEngine)&&
                !entityEngine.name().equalsIgnoreCase(tableInfo.getEngine())){
            updateTableInfo.setEngine(entityEngine.name())
                    .setEngineUpdate(true);
        }
        // 获取主键
        List<String> entityKeys = getEntityKeys(clas);
        List<String> tableKeys = getTableKeys(tableName);
        // 数量不一致
        if (entityKeys.size()!=tableKeys.size()){
            updateTableInfo.setKeys(entityKeys)
                    .setKeysUpdate(true);
        }
        // 字段不一致
        else if (!new HashSet<>(entityKeys).containsAll(tableKeys)){
            updateTableInfo.setKeys(entityKeys)
                    .setKeysUpdate(true);
        }
        baseTableMap.getUpdateTables().add(updateTableInfo);
    }


    /**
     * 获取表的字符集
     */
    private String getTableCharset(String tableCollation){
        // utf8mb4_general_ci 获取第一段文字
        List<String> split = StrUtil.split(tableCollation, "_");
        if (split.size()>0){
            return split.get(0);
        }
        return "";
    }

    /**
     * 获取表引擎类型
     */
    private MySqlEngineEnum getEntityEngine(Class<?> clazz) {
        MySqlEngine mySqlEngine = clazz.getAnnotation(MySqlEngine.class);
        if (!ColumnUtils.hasTableAnnotation(clazz)) {
            return null;
        }
        if (mySqlEngine != null && mySqlEngine.value() != MySqlEngineEnum.DEFAULT) {
            return mySqlEngine.value();
        }
        return null;
    }

    /**
     * 获取实体类上配置的主键
     */
    private List<String> getEntityKeys(Class<?> clas){
        List<MySqlEntityColumn> entityColumns = MySqlInfoUtil.getEntityColumns(clas);
        return entityColumns.stream()
                .filter(MySqlEntityColumn::isKey)
                .map(MySqlEntityColumn::getName)
                .collect(Collectors.toList());
    }

    /**
     * 获取表配置的主键
     */
    private List<String> getTableKeys(String table){
        List<MySqlTableIndex> primaryIndexes = mysqlTableModifyMapper.findPrimaryIndexByTableName(table);
        return primaryIndexes.stream().map(MySqlTableIndex::getColumnName).collect(Collectors.toList());
    }
}
