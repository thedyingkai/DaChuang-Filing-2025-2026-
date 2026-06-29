package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.Column2Mapper;
import com.example.dangjian_spring.entity.Column;
import com.example.dangjian_spring.entity.Column2;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class Column2Service extends ServiceImpl<Column2Mapper, Column2>{
    @Resource
    private Column2Mapper column2Mapper;
    @Resource
    private ColumnService columnService;

    public List<Column2> allColumns()
    {
        return column2Mapper.allColumns();
    }

    public List<Column2> searchColumnsByDes(String keyword)
    {
        keyword = "%" + keyword + "%";
        return column2Mapper.searchColumnsByDes(keyword);
    }

    public Column2 selectColumnByDes(String description)
    {
        return column2Mapper.selectColumnByDes(description);
    }

    public Column2 selectColumnById(int id)
    {
        return column2Mapper.selectColumnById(id);
    }

    public int addColumn(Column2 column)
    {
        return column2Mapper.addColumn(column);
    }

    public int updateColumn(Column2 column)
    {
        return column2Mapper.updateColumn(column);
    }

    public int deleteColumn(int id)
    {
        return column2Mapper.deleteColumn(id);
    }

    /**
     * 关键词表外键指向 column2：资讯栏目在 column_view。
     * 若栏目仅存在于 column_view，则插入同 id 的 column2 镜像行以满足外键。
     *
     * @return 插入后或已存在时 column2 有对应行则为 true
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean ensureColumn2ExistsForKeyword(Integer columnId) {
        if (columnId == null || columnId == -1) {
            return false;
        }
        if (column2Mapper.selectColumnById(columnId) != null) {
            return true;
        }
        Column fromView = columnService.selectByCid(columnId);
        if (fromView == null) {
            return false;
        }
        String desc = fromView.getName();
        if (desc == null || desc.trim().isEmpty()) {
            desc = "栏目#" + columnId;
        } else {
            desc = desc.trim();
        }
        Column2 row = new Column2();
        row.setColumn_id(columnId);
        row.setColumn_description(desc);
        try {
            return column2Mapper.addColumn(row) > 0;
        } catch (Exception e) {
            // 并发下可能重复插入，以最终是否存在为准
            return column2Mapper.selectColumnById(columnId) != null;
        }
    }

}
