package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.ColumnMapper;
import com.example.dangjian_spring.dao.mapper.Processtype_to_columnMapper;
import com.example.dangjian_spring.entity.Column;
import com.example.dangjian_spring.entity.Processtype_to_column;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@Transactional
public class ColumnService extends ServiceImpl<ColumnMapper, Column> {

    @Resource
    private ColumnMapper columnMapper;

    @Resource
    private Processtype_to_columnMapper processtype_to_columnMapper;

    public int getCidByCname(String cname) {
        return columnMapper.getCidByCname(cname);
    }

    public void insertColumn(Column column) {
        column.setId(null);
        if (column.getParent_id() != null && column.getParent_id().equals(0)) {
            column.setParent_id(null);
        }
        columnMapper.insert(column);
    }

    public void updateColumnById(Column column) {
        if (column.getParent_id().equals(0)) {
            column.setParent_id(null);
        }
        columnMapper.updateColumnById(column);
    }

    public void deleteColumn(Integer cid) {
        columnMapper.deleteColumn(cid);
    }

    public void batchDeleteColumn(List<Integer> cids) {
        for (Integer cid : cids) {
            columnMapper.deleteColumn(cid);
        }
    }

    public List<Column> buildTree() {
        List<Column> columns = columnMapper.selectAll();
        enrichColumnsPtid(columns);
        return buildTreeHelper(columns, null);
    }

    /**
     * 从关联表补全 {@link Column#getPtid()}；表不存在时仅打日志，不影响栏目树加载。
     */
    private void enrichColumnsPtid(List<Column> columns) {
        if (columns == null || columns.isEmpty()) {
            return;
        }
        try {
            List<Processtype_to_column> rows = processtype_to_columnMapper.selectPtidGroupedByCoid();
            if (rows == null || rows.isEmpty()) {
                return;
            }
            Map<Integer, Integer> coidToPtid = new HashMap<>();
            for (Processtype_to_column row : rows) {
                if (row.getCoid() != null && row.getPtid() != null) {
                    coidToPtid.put(row.getCoid(), row.getPtid());
                }
            }
            for (Column c : columns) {
                Integer ptid = coidToPtid.get(c.getId());
                if (ptid != null) {
                    c.setPtid(ptid);
                }
            }
        } catch (Exception e) {
            log.warn("未读取栏目-流程绑定表 processtype_to_column_view，ptid 将为空。请在库中创建该表（见 sql/create_processtype_to_column_view.mysql.sql）。原因: {}",
                    e.getMessage());
        }
    }

    private List<Column> buildTreeHelper(List<Column> columns, Integer parentId) {
        List<Column> result = new ArrayList<>();
        for (Column column : columns) {
            if ((parentId == null && column.getParent_id() == null) ||
                    (parentId != null && parentId.equals(column.getParent_id()))) {
                column.setChild(buildTreeHelper(columns, column.getId()));
                result.add(column);
            }
        }
        return result;
    }

    public List<Column> buildTreeToShow() {
        List<Column> columns = columnMapper.selectAllExceptDefault();
        return buildTreeToShowHelper(columns, null);
    }

    private List<Column> buildTreeToShowHelper(List<Column> columns, Integer parentId) {
        int counter = 0;
        List<Column> result = new ArrayList<>();
        for (Column column : columns) {
            if ((parentId == null && column.getParent_id() == null) || (parentId != null && parentId.equals(column.getParent_id()))) {
                column.setChild(buildTreeHelper(columns, column.getId()));
                result.add(column);
                counter = counter + 1;
            }
            if (counter == 7) break;
        }
        return result;
    }

    public List<Column> selectAll() {
        return columnMapper.selectAll();
    }

    public List<Column> selectByFather(Integer coid) {
        return columnMapper.selectByFather(coid);
    }

    public Column selectByCid(Integer cid) {
        return columnMapper.selectByCid(cid);
    }

    public Column selectByCname(String cname) {
        return columnMapper.selectByCname(cname);
    }

    public void update(List<Column> columnList) {
        columnMapper.deleteAll();
        Integer index = 0;
        for (Column column : columnList) {
            column.setIndex(index);
            if (column.getParent_id() != null && column.getParent_id() == 0) {
                column.setParent_id(null);
            }
            columnMapper.insertWithId(column);
            index = index + 1;
            index = processChildren(column.getChild(), index);
        }
    }

    private Integer processChildren(List<Column> children, Integer index) {
        if (children == null) {
            return index;
        }
        for (Column child : children) {
            child.setIndex(index);
            columnMapper.insertWithId(child);
            index = index + 1;
            processChildren(child.getChild(), index);
        }
        return index;
    }

    public void updateNameById(Column column) {
        columnMapper.updateNameById(column);
    }

    public void updateIndexById(List<Column> columnList, Integer increment) {
        columnMapper.updateIndexById(columnList, increment);
    }

    public void deleteById(Integer id) {
        columnMapper.deleteById(id);
    }

    public List<Column> selectcolumnlist(Integer id) {
        List<Column> result = new ArrayList<>();
        int index = 0; // 确保 index 是有效的索引
        Column selectedColumn = columnMapper.selectByCid(id); // 调用方法获取值
        result.add(null); // 填充 null 以确保有足够的空间
        result.set(index, selectedColumn); // 设置指定索引位置的值
        while (selectedColumn.getParent_id() != null) {
            index = index + 1;
            result.add(null);
            selectedColumn = columnMapper.selectByCid(selectedColumn.getParent_id());
            result.set(index, selectedColumn);
        }
        return result;
    }
}