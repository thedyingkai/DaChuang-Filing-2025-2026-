<template>
  <div class="paper-list-wrap">
    <el-card shadow="never" class="paper-list-card">
      <div class="toolbar">
        <span class="page-title">试卷列表</span>
        <div class="toolbar-right">
          <el-input
            v-model="keyword"
            clearable
            placeholder="按试卷标题搜索"
            class="search-input"
            @clear="loadAll"
            @keyup.enter.native="onSearch"
          />
          <el-button type="primary" plain icon="el-icon-search" @click="onSearch">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="loadAll">刷新</el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        border
        style="width: 100%"
        :empty-text="emptyText"
      >
        <el-table-column prop="test_paper_id" label="编号" width="72" align="center"/>
        <el-table-column prop="paper_description" label="试卷标题" min-width="160" show-overflow-tooltip/>
        <el-table-column label="组卷方式" width="120" align="center">
          <template slot-scope="scope">
            {{ formatGrouping(scope.row.grouping_method) }}
          </template>
        </el-table-column>
        <el-table-column prop="points_reward" label="积分" width="72" align="center"/>
        <el-table-column prop="create_date" label="生效时间" width="160" show-overflow-tooltip/>
        <el-table-column prop="deadline" label="截止时间" width="160" show-overflow-tooltip/>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="goStatistics(scope.row.test_paper_id)">统计</el-button>
            <el-button type="text" size="small" @click="goExam(scope.row.test_paper_id)">参与</el-button>
            <el-button type="text" size="small" class="danger-link" @click="confirmDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request';

function isResultOk(res) {
  return res && (res.code === '200' || res.code === 200);
}

export default {
  name: 'PaperList',
  data() {
    return {
      loading: false,
      tableData: [],
      keyword: '',
      emptyText: '暂无试卷数据'
    };
  },
  created() {
    this.loadAll();
  },
  methods: {
    formatGrouping(m) {
      const map = {
        random: '随机组卷',
        newrandom: '随机组卷',
        byCategory: '按关键词组卷',
        self_exam: '在线自测占位',
      };
      return map[m] || m || '—';
    },
    isSystemSelfExamPaper(row) {
      const d = row && row.paper_description;
      return typeof d === 'string' && d.indexOf('【系统】在线自测') === 0;
    },
    filterListedPapers(rows) {
      if (!Array.isArray(rows)) return [];
      return rows.filter((r) => !this.isSystemSelfExamPaper(r));
    },
    async loadAll() {
      this.keyword = '';
      this.loading = true;
      this.emptyText = '暂无试卷数据';
      try {
        const res = await request.get('/papers');
        if (!isResultOk(res)) {
          this.$message.error((res && res.msg) || '加载失败');
          this.tableData = [];
          return;
        }
        this.tableData = this.filterListedPapers(Array.isArray(res.data) ? res.data : []);
      } catch (e) {
        this.$message.error('加载试卷列表失败');
        this.tableData = [];
      } finally {
        this.loading = false;
      }
    },
    async onSearch() {
      const k = (this.keyword || '').trim();
      if (!k) {
        this.loadAll();
        return;
      }
      this.loading = true;
      this.emptyText = '未找到匹配的试卷';
      try {
        const path = '/papers/search/description/' + encodeURIComponent(k);
        const res = await request.get(path);
        if (!isResultOk(res)) {
          this.$message.error((res && res.msg) || '搜索失败');
          this.tableData = [];
          return;
        }
        this.tableData = this.filterListedPapers(Array.isArray(res.data) ? res.data : []);
      } catch (e) {
        this.$message.error('搜索失败');
        this.tableData = [];
      } finally {
        this.loading = false;
      }
    },
    goExam(id) {
      this.$router.push({ path: '/Exam', query: { id } });
    },
    goStatistics(id) {
      this.$router.push({ path: '/Statistics', query: { id } });
    },
    confirmDelete(row) {
      const title = row.paper_description || ('试卷 #' + row.test_paper_id);
      this.$confirm('确定删除「' + title + '」吗？删除后不可恢复。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => this.doDelete(row.test_paper_id))
        .catch(() => {});
    },
    async doDelete(id) {
      try {
        const res = await request.get('/papers/delete/' + id);
        if (!isResultOk(res)) {
          this.$message.error((res && res.msg) || '删除失败');
          return;
        }
        this.$message.success((res && res.msg) || '删除成功');
        if ((this.keyword || '').trim()) {
          this.onSearch();
        } else {
          this.loadAll();
        }
      } catch (e) {
        this.$message.error('删除失败');
      }
    }
  }
};
</script>

<style scoped>
.paper-list-wrap {
  padding: 16px;
  min-height: 100%;
  box-sizing: border-box;
  background: #f5f6f7;
}

.paper-list-card {
  border-radius: 4px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.toolbar-right {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.search-input {
  width: 220px;
}

.danger-link {
  color: #f56c6c;
}

.danger-link:hover {
  color: #f78989;
}
</style>
