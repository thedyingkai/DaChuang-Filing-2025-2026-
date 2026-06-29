<template>
  <div class="el-container" style="justify-content: space-around; align-items: center;">
    <div class="column-container">
      <div class="column-container-header">
        <div>
          <label>栏目设置</label>
          <el-tooltip effect="light">
            <div slot="content">您可以在此处调整栏目，拖拽以改变栏目顺序和所属关系</div>
            <span style="padding: 5px;"><i class="el-icon-warning-outline"></i></span>
          </el-tooltip>
        </div>
      </div>
      <div class="column-container-main">
        <TreeList ref="newColumnList" :column-data="columnList" @columnDataUpdated="updateColumnData"
                  @node-clicked="showNodeInfo"></TreeList>
      </div>
    </div>
    <div class="article-container">
      <div class="article-container-header">
        <div>
          <label>当前栏目{{ title }}下的文章</label>
          <el-tooltip effect="light">
            <div slot="content">
              左侧点选具体栏目（叶子节点）后，此处列出该栏目下的文章。<br>
              「从其他栏目调入」把已在系统里的文章挪到当前栏目；「移到其他栏目」把当前列表里勾选的文章挪走。新建正文请走信息发布里的撰稿/草稿流程。
            </div>
            <span style="padding: 5px;"><i class="el-icon-warning-outline"></i></span>
          </el-tooltip>
        </div>
        <div class="input-container">
          <el-input
              v-model="filterText"
              placeholder="输入关键字进行过滤">
          </el-input>
        </div>
      </div>
      <div class="article-container-main">
        <div class="article-toolbar">
          <el-button size="small" type="primary" plain icon="el-icon-bottom" @click="openImportDialog">从其他栏目调入</el-button>
          <el-button size="small" type="warning" plain icon="el-icon-top" @click="openMoveOutDialog">移到其他栏目</el-button>
          <span class="article-toolbar-hint">移出前请先在列表中勾选文章；单行右侧图标可快速移出该篇。</span>
        </div>
        <el-tree
            ref="tree"
            :data="formatSourceList(sourceList)"
            :draggable="true"
            :filter-node-method="filterNode"
            :render-content="renderContent"
            class="filter-tree"
            default-expand-all
            show-checkbox>
        </el-tree>
      </div>
    </div>
    <el-dialog
        :cancel-button-text="cancelButtonText"
        :confirm-button-text="confirmButtonText"
        :custom-class="customClass"
        :show-cancel-button="true"
        :visible.sync="dialogVisible_move"
        title="将文章移到其他栏目"
        @closed="resetMoveOutDialog"
    >
      <p class="dialog-desc">选择要移入的目标栏目，下方列表为待移出的文章（来自当前操作）。</p>
      <div class="dialog-field">
        <span class="dialog-field-label">目标栏目</span>
        <el-cascader
            v-model="targetColumn"
            :options="formattedColumnList"
            clearable
            filterable
            placeholder="请选择目标栏目"
            popper-class="select_popper">
          <template slot-scope="{node,data }">
            <span>{{ data.label }}</span>
            <span v-if="!node.isLeaf"> ({{ data.children.length }}) </span>
          </template>
        </el-cascader>
      </div>
      <div class="sourceList-container">
        <el-tree
            ref="tree-to-move"
            :data="formatSourceList(selectedSourceList)"
            :default-checked-keys="selectAll(selectedSourceList)"
            :draggable="true"
            :render-content="renderContentInDialog"
            class="filter-tree"
            default-expand-all
            node-key="id"
            show-checkbox>
        </el-tree>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible_move = false">取消</el-button>
        <el-button type="primary" @click="confirmMoveOut">移到目标栏目</el-button>
      </span>
    </el-dialog>
    <el-dialog
        :cancel-button-text="cancelButtonText"
        :confirm-button-text="confirmButtonText"
        :custom-class="customClass"
        :show-cancel-button="true"
        :visible.sync="dialogVisible_add"
        title="从其他栏目调入文章"
    >
      <p class="dialog-desc">选择「来源栏目」，勾选要归入当前栏目{{ title }}的文章，确认后它们会从原栏目移至本栏目。</p>
      <div class="dialog-field">
        <span class="dialog-field-label">来源栏目</span>
        <el-cascader
            v-model="fromColumnId"
            :options="importCascaderOptions"
            clearable
            filterable
            placeholder="请选择来源栏目"
            popper-class="select_popper"
            @change="onImportFromColumnChange">
          <template slot-scope="{node,data }">
            <span>{{ data.label }}</span>
            <span v-if="!node.isLeaf"> ({{ data.children.length }}) </span>
          </template>
        </el-cascader>
      </div>
      <div class="sourceList-container">
        <template v-if="importSourceListReady">
          <el-tree
              v-if="sourceListInDialog.length"
              ref="tree-in-dialog"
              :key="importTreeKey"
              :data="formatSourceList(sourceListInDialog)"
              :draggable="true"
              :filter-node-method="filterNode"
              :render-content="renderContentInDialog"
              class="filter-tree"
              default-expand-all
              node-key="id"
              show-checkbox>
          </el-tree>
          <div v-else class="dialog-empty">该栏目下暂无文章</div>
        </template>
        <div v-else class="dialog-empty">请先选择来源栏目</div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible_add = false">取消</el-button>
        <el-button type="primary" @click="confirmImport">调入当前栏目</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import TreeList from "@/components/TreeList";

export default {
  name: "ColumnSet",
  components: {TreeList},
  computed: {
    importCascaderOptions() {
      const id = this.selectedColumn && this.selectedColumn.id;
      return this.excludeSelfFromCascaderOptions(this.formattedColumnList, id);
    },
    importSourceListReady() {
      return Array.isArray(this.fromColumnId) && this.fromColumnId.length > 0;
    },
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
      this.$nextTick(() => {
        this.$refs.tree.store.filteredNodes = [];
        this.$refs.tree.filter(val);
      });
    },
  },
  data() {
    return {
      columnList: [],
      formattedColumnList: [],
      title: '（默认栏目（未分类））',
      sourceList: [],
      filterText: '',
      selectedColumn: '',
      fromColumnId: [],
      dialogVisible_move: false,
      dialogVisible_add: false,
      selectedSourceList: [],
      targetColumn: [],
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      customClass: 'custom-dialog-box',
      sourceListInDialog: [],
      importTreeKey: 0,
    };
  },
  async created() {
    await this.loadData();
    this.showNodeInfo(this.columnList[this.columnList.length - 1]);
  },
  methods: {
    async loadData() {
      try {
        const res = await this.$request.get('/column/selectAll');
        if (res.code === '200') {
          if (Array.isArray(res.data)) {
            this.columnList = this.convertToArray(res.data);
            this.formattedColumnList = this.transformOptions(this.columnList);
          } else {
            console.error('返回的数据不是数组:', res.data);
          }
        } else {
          console.error('请求失败', res.code);
        }
      } catch (error) {
        console.error('栏目信息获取失败:', error);
      }
    },
    showNodeInfo(node) {
      if (!node) return;
      if (node.children.length > 0) return;
      this.selectedColumn = node;
      this.title = '（' + node.name + '）';
      this.$request.get('/article/selectArticleBycoid/' + node.id).then(
          res => {
            if (res.code === '200') {
              this.sourceList = Array.isArray(res.data) ? res.data : [];
            } else {
              console.error('请求失败', res.data.msg);
            }
          }
      ).catch(error => {
        console.error('发送请求失败:', error);
      });
    },
    onImportFromColumnChange(value) {
      if (!value || !value.length) {
        this.sourceListInDialog = [];
        this.importTreeKey += 1;
        return;
      }
      const columnId = value[value.length - 1];
      this.showSourceOfColumn(columnId);
    },
    excludeSelfFromCascaderOptions(options, selfId) {
      if (!Array.isArray(options) || selfId == null || selfId === '') {
        return options || [];
      }
      return options.reduce((acc, opt) => {
        if (opt.value == selfId) {
          return acc;
        }
        const next = {...opt};
        if (opt.children && opt.children.length) {
          next.children = this.excludeSelfFromCascaderOptions(opt.children, selfId);
          if (!next.children.length) {
            delete next.children;
          }
        }
        acc.push(next);
        return acc;
      }, []);
    },
    showSourceOfColumn(nodeId) {
      this.$request.get('/article/selectArticleBycoid/' + nodeId).then(
          res => {
            if (res.code === '200') {
              this.sourceListInDialog = Array.isArray(res.data) ? res.data : [];
              this.importTreeKey += 1;
              this.$nextTick(() => {
                const tree = this.$refs['tree-in-dialog'];
                if (tree) {
                  tree.setCheckedKeys([]);
                }
              });
            } else {
              console.error('请求失败', res.data.msg);
            }
          }
      ).catch(error => {
        console.error('发送请求失败:', error);
      });
    },
    convertToArray(array) {
      let result = [];
      if (Array.isArray(array)) {
        array.forEach(item => {
          let newItem = {
            id: item.id,
            parent_id: item.parent_id,
            name: item.name,
            index: item.index,
            is_empty: item.is_empty,
            children: this.convertToArray(item.child)
          };
          result.push(newItem);
        });
      }
      return result;
    },
    formatSourceList(array) {
      if (!Array.isArray(array)) {
        return [];
      }
      let result = [];
      array.forEach(item => {
        let newItem = {
          id: item.id,
          label: item.title || item.label,
        };
        result.push(newItem);
      });
      return result;
    },
    transformOptions(options) {
      return options.map(item => {
        let newItem = {
          value: item.id,
          label: item.name
        };
        if (item.children && item.children.length > 0) {
          newItem.children = this.transformOptions(item.children);
        }
        return newItem;
      });
    },
    filterNode(value, data) {
      if (!value) return true;
      const valueLower = value.toLowerCase();
      const label = data.label && typeof data.label === 'string' ? data.label.toLowerCase() : '';
      const idStr = data.id != null ? String(data.id).toLowerCase() : '';
      const labelMatch = label.includes(valueLower);
      const idMatch = idStr.includes(valueLower);
      return labelMatch || idMatch;
    },
    renderContent(h, {data}) {
      const children = [];
      children.push(h('span', {class: 'icon'}, [
        h('i', {class: data.children ? 'el-icon-folder' : 'el-icon-document'})
      ]));
      let labelText = data.label;
      const parentWidth = this.getParentWidth() * 0.8;
      const maxLength = Math.floor(parentWidth / 14);
      if (labelText.length > maxLength) {
        labelText = labelText.slice(0, maxLength) + '...';
      }
      children.push(h('span', {}, labelText));
      children.push(h('span', {
        class: 'icon',
        attrs: {title: '移到其他栏目'},
        on: {
          click: (e) => {
            e.stopPropagation();
            this.quickMoveOutSingle(data);
          },
        },
      }, [
        h('i', {class: 'el-icon-share'}),
      ]));
      return h('span', {class: 'custom-tree-node'}, children);
    },
    renderContentInDialog(h, {data}) {
      const children = [];
      children.push(h('span', {class: 'icon'}, [
        h('i', {class: data.children ? 'el-icon-folder' : 'el-icon-document'})
      ]));
      let labelText = data.label;
      const parentWidth = this.getParentWidth() * 0.8;
      const maxLength = Math.floor(parentWidth / 14);
      if (labelText.length > maxLength) {
        labelText = labelText.slice(0, maxLength) + '...';
      }
      children.push(h('span', {}, labelText));
      return h('span', {class: 'custom-tree-node'}, children);
    },
    quickMoveOutSingle(data) {
      if (!data || data.id == null) {
        return;
      }
      this.targetColumn = [];
      this.selectedSourceList = [data];
      this.dialogVisible_move = true;
    },
    confirmMoveOut() {
      const targetId = Array.isArray(this.targetColumn) && this.targetColumn.length
          ? this.targetColumn[this.targetColumn.length - 1]
          : this.targetColumn;
      if (targetId == null || targetId === '') {
        this.$message.warning('请选择目标栏目');
        return;
      }
      if (String(targetId) === String(this.selectedColumn.id)) {
        this.$message.warning('目标栏目不能与当前栏目相同');
        return;
      }
      const tree = this.$refs['tree-to-move'];
      if (!tree) {
        return;
      }
      const selectedSourceIdList = tree.getCheckedKeys();
      if (!selectedSourceIdList.length) {
        this.$message.warning('没有待移出的文章');
        return;
      }
      this.$request.put('/article/batchMoveToColumn/' + targetId, selectedSourceIdList).then(
          res => this.$handleResponse(res, () => {
            this.$message.success('已移到目标栏目');
            this.loadData();
            this.showNodeInfo(this.selectedColumn);
          }));
      this.dialogVisible_move = false;
      this.targetColumn = [];
    },
    getParentWidth() {
      const treeNode = this.$refs.tree.$el;
      return treeNode.offsetWidth;
    },
    openImportDialog() {
      this.fromColumnId = [];
      this.sourceListInDialog = [];
      this.importTreeKey += 1;
      this.dialogVisible_add = true;
    },
    openMoveOutDialog() {
      const mainTree = this.$refs.tree;
      if (!mainTree) {
        return;
      }
      const checked = mainTree.getCheckedKeys();
      if (!checked.length) {
        this.$message.warning('请先在列表中勾选要移出的文章');
        return;
      }
      this.targetColumn = [];
      this.selectedSourceList = mainTree.getCheckedNodes();
      this.dialogVisible_move = true;
    },
    updateColumnData(updatedData, deletedId, newNode) {
      this.columnList = updatedData;
      this.formattedColumnList = this.transformOptions(updatedData);
      if (deletedId) {
        if (deletedId === this.selectedColumn.id) {
          if (this.columnList.length > 0) {
            this.selectedColumn = this.columnList[this.columnList.length - 1];
            this.showNodeInfo(this.selectedColumn);
          }
        }
      }
      if (newNode) {
        this.selectedColumn = newNode;
        this.showNodeInfo(this.selectedColumn);
      }
    },
    confirmImport() {
      if (!this.importSourceListReady) {
        this.$message.warning('请先选择来源栏目');
        return;
      }
      const fromId = this.fromColumnId[this.fromColumnId.length - 1];
      if (String(fromId) === String(this.selectedColumn.id)) {
        this.$message.warning('来源栏目不能与当前栏目相同');
        return;
      }
      const tree = this.$refs['tree-in-dialog'];
      if (!tree) {
        return;
      }
      const selectedSourceIdList = tree.getCheckedKeys();
      if (!selectedSourceIdList.length) {
        this.$message.warning('请勾选要调入的文章');
        return;
      }
      this.$request.put('/article/batchMoveToColumn/' + this.selectedColumn.id, selectedSourceIdList).then(
          res => this.$handleResponse(res, () => {
            this.$message.success('已调入当前栏目');
            this.loadData();
            this.showNodeInfo(this.selectedColumn);
          }));
      this.dialogVisible_add = false;
    },
    resetMoveOutDialog() {
      this.selectedSourceList = [];
      this.targetColumn = [];
    },
    selectAll(selectedSourceList) {
      let result = [];
      selectedSourceList.forEach(item => {
        let id = item.id
        result.push(id);
      })
      return result;
    },
  }
};
</script>

<style scoped>
.column-container, .article-container {
  border: 1px solid #ccc;
}

.column-container {
  width: 50vh;
}

.article-container {
  width: 110vh;
  height: calc(90vh - 90px);
}

.column-container-header, .article-container-header {
  background-color: #cbcbcb;
  height: 60px;
  padding: 15px;
  display: flex;
  align-items: center;
  font-size: larger;
  justify-content: space-between;
}

.column-container-main, .article-container-main {
  height: calc(90vh - 150px);
  overflow: auto;
  padding: 10px;
}

.input-container {
  width: 40vh;
}

.article-toolbar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}

.article-toolbar-hint {
  font-size: 12px;
  color: #606266;
  margin-left: 4px;
}

.dialog-desc {
  margin: 0 0 12px;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.dialog-field {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.dialog-field-label {
  flex-shrink: 0;
  margin-right: 8px;
  font-size: 14px;
  color: #303133;
}

.dialog-empty {
  padding: 16px;
  text-align: center;
  font-size: 13px;
  color: #909399;
}

.custom-tree-node {
  display: flex;
  align-items: center;

}

.sourceList-container {
  height: calc(100vh - 500px);
  min-height: 60px;
  border: 1px solid #8c8c8c;
}
</style>
<style>
.select_popper {
  z-index: 100100 !important;
}

.el-tree-node.is-hidden {
  display: none !important;
}


</style>