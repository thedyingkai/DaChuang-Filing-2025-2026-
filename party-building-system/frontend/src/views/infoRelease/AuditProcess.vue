<template>
  <div class="audit-process-page">
    <div class="audit-process-page__pane audit-process-page__pane--current">
      <div class="audit-process-page__inner">
        <el-row>
          <label style="font-size: 20px">当前方案: {{ testprocess.name || '（未选择）' }}
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            {{ count }}级审核
          </label>
        </el-row>
        <el-card v-for="(process,index) in showprocess" :key="process.id || index" class="audit-process-page__level-card">
          <el-row>
            <label>第{{ index + 1 }}级
              &nbsp;&nbsp;&nbsp;&nbsp;
              审核员:{{ process.uname }}
            </label>
            <div style="float: right">
              <label class="click-able" @click="showAuditorchange(process.id)">
                <el-icon class="el-icon-user"></el-icon>
                修改审核</label>
            </div>
          </el-row>
        </el-card>

        <el-card class="audit-process-page__level-card">
          <el-row style="text-align: center">
            <label class="click-able" @click="openAddLevel">
              <el-icon class="el-icon-circle-plus-outline"></el-icon>
              新增一级</label>
            &nbsp;&nbsp;

            <label class="click-able" @click="deleteprocess">
              <el-icon class="el-icon-delete"></el-icon>
              删除最后一级</label>
          </el-row>
        </el-card>
      </div>

    </div>

    <div class="audit-process-page__pane audit-process-page__pane--schemes">
      <el-row class="scheme-list-header">
        <label style="font-size: 20px">
          方案列表
        </label>
      </el-row>
      <div class="scheme-list-body">
        <el-card v-for="processtype in allprocesstype" :key="processtype.id" class="scheme-card">
          <el-row>
            <label class="scheme-card__title" :title="(processtype.name || '') + ' / ' + (processtype.co_name || '未绑定栏目')">
              方案名:{{ processtype.name }}&nbsp;&nbsp;&nbsp;&nbsp;
              栏目:{{ processtype.co_name || '未绑定栏目' }}
            </label>
          </el-row>
          <el-row>
            <div style="float: left">
              <label @click="applyprocesstype(processtype)" class="click-able">
                <el-icon class="el-icon-video-play"></el-icon>
                查看</label>
              &nbsp;
              <label @click="showColumn(processtype.id)" class="click-able">
                <el-icon class="el-icon-edit-outline"></el-icon>
                专栏配置</label>

              <label @click="deleteprocesstype(processtype)" class="click-able">
                <el-icon class="el-icon-delete"></el-icon>
                删除</label>
            </div>
          </el-row>

        </el-card>

        <el-card class="scheme-card scheme-card--new">
          <el-row style="text-align: center">
            <el-icon class="el-icon-circle-plus-outline"></el-icon>
            <label @click="openNewScheme">新建方案</label>
          </el-row>
        </el-card>
      </div>
    </div>
    <el-dialog
        title="修改审核"
        :visible.sync="show1"
        width="520px"
    >
      <label>选择新审核员:</label>
      <el-select v-model="uid"
                 placeholder="请选择"
                 popper-class="select_popper"
                 style="margin: 10px;">
        <el-option
            v-for="item in auditors"
            :key="item.id"
            :label="item.uname"
            :value="item.id">
        </el-option>
      </el-select>
      <span style="align-items: center">
        <el-button @click="show1 = false">取 消</el-button>
        <el-button type="primary" @click="changeAuditor()">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
        title="新增流程"
        :visible.sync="show2"
        width="520px"
    >
      <label>选择审核员:</label>
      <el-select v-model="uid"
                 placeholder="请选择"
                 popper-class="select_popper"
                 style="margin: 10px;">
        <el-option
            v-for="item in auditors"
            :key="item.id"
            :label="item.uname"
            :value="item.id">
        </el-option>
      </el-select>
      <span style="align-items: center">
        <el-button @click="show2 = false">取 消</el-button>
        <el-button type="primary" @click="addProcess()">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
        title="新增方案"
        :visible.sync="show3"
        width="560px"
    >
      <el-row>
        <label>输入新方案名称: &nbsp;</label>
        <el-input v-model="processtypename" style="width: 300px" placeholder="请输入新方案名称"></el-input>
      </el-row>
      <el-row>
        <label>选择第一级审核员:</label>
        <el-select v-model="uid"
                   placeholder="请选择"
                   popper-class="select_popper"
                   style="margin: 10px;">
          <el-option
              v-for="item in auditors"
              :key="item.id"
              :label="item.uname"
              :value="item.id">
          </el-option>
        </el-select>
      </el-row>
      <el-row style="text-align: center">
        <el-button @click="show3 = false">取 消</el-button>
        <el-button type="primary" @click="addProcessType()">确 定</el-button>
      </el-row>
    </el-dialog>

    <el-dialog
        title="栏目设置"
        :visible.sync="show4"
        width="560px"
    >
      <label>选择应用的栏目:</label>
        <el-cascader
            v-model="coid" :options="columns" filterable placeholder="选择栏目"  popper-class="select_popper">
          <template slot-scope="{ node, data }">
            <span>{{ data.label }}</span>
            <span v-if="!node.isLeaf"> ({{ data.children.length }}) </span>
          </template>
        </el-cascader>
      <span style="align-items: center">
        <el-button @click="show4 = false">取 消</el-button>
        <el-button type="primary" @click="setColumn()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {MessageBox} from "element-ui";
import devLog from "@/utils/devLog";


export default {
  name: "AuditProcess",
  data() {
    return {
      user: JSON.parse(localStorage.getItem("current-user") || '{}'),
      auditors: [],

      show1: false,
      show2: false,
      show3: false,
      show4: false,
      uid: null,
      /** 当前左侧展示的审核方案 id（processtype.id） */
      activeProcesstypeId: null,
      /** 修改审核员时用到的流程节点 id（process_view.id） */
      editProcessNodeId: null,
      /** 专栏绑定弹窗正在操作的方案 id */
      columnBindProcesstypeId: null,
      coid: [],
      count: 0,
      processtypename: '',
      testprocess: {name: ''},
      allprocesstype: [],
      showprocess: [],
      options: [],
      columns: [],
    }
  },
  mounted() {
    this.getColumn();
    this.getProcessType();
    this.getAuditor();
  },
  methods: {
    resolveUserBid() {
      if (this.user && this.user.bid != null && this.user.bid !== '') {
        return this.user.bid;
      }
      if (this.user && this.user.gid != null && this.user.gid !== '' && this.user.gid !== -1) {
        return this.user.gid;
      }
      return null;
    },
    openAddLevel() {
      this.uid = null;
      this.show2 = true;
    },
    openNewScheme() {
      this.processtypename = '';
      this.uid = null;
      this.show3 = true;
    },
    getColumn() {
      this.$request.get('/column/selectAll').then(
          res => {
            if (res.code === '200') {
              this.options = res.data;
              this.columns = this.transformOptions(this.options);
            }
          }
      ).catch((error) => {
        console.error('栏目信息获取失败:', error);
      });
    },
    showAuditorchange(id) {
      this.editProcessNodeId = id;
      this.uid = null;
      this.show1 = true;
    },
    showColumn(id) {
      this.columnBindProcesstypeId = id;
      this.coid = [];
      this.show4 = true;
    },
    changeAuditor() {
      if (this.editProcessNodeId == null) {
        return;
      }
      if (this.uid == null || this.uid === '') {
        this.$message.warning('请选择审核员');
        return;
      }
      const newprocess = {id: this.editProcessNodeId, uid: this.uid};
      this.$request.put('/process/updateAuditor', newprocess).then(
          res => {
            if (res.code === '200') {
              this.$message.success('审核员修改成功');
              this.show1 = false;
              this.getProcess();
            } else {
              this.$message.error(res.msg || '修改失败');
            }
          }
      ).catch((error) => {
        console.error('数据加载出现错误:', error);
      });
    },

    deleteprocess() {
      if (this.showprocess.length <= 1) {
        this.$message.warning('仅剩一级时无法删除！');
      } else {
        MessageBox.confirm('确定删除最后一级吗？', '删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(() => {
          const lastId = this.getLastOne;
          if (lastId == null) {
            this.$message.error('未找到可删除的节点');
            return;
          }
          this.$request.delete('/process/delete/' + lastId).then(
              res => {
                if (res.code === '200') {
                  this.$message.success('删除成功');
                  this.getProcess();
                } else {
                  this.$message.error(res.msg || '删除失败');
                }
              }
          ).catch((error) => {
            console.error('数据加载出现错误:', error);
          });

        }).catch(() => {
          this.$message.info('已取消');
        });
      }
    },

    getAuditor() {
      this.$request.get('/user/selectAuditor').then(
          res => {
            if (res.code === '200') {
              this.auditors = res.data || [];
              devLog(this.auditors);
            }
          }
      ).catch((error) => {
        console.error('数据加载出现错误:', error);
      });
    },
    getProcess() {
      if (this.activeProcesstypeId == null) {
        this.showprocess = [];
        this.count = 0;
        return;
      }
      this.$request.get('/process/selectBYptid/' + this.activeProcesstypeId).then(
          res => {
            if (res.code === '200') {
              this.showprocess = res.data || [];
              this.count = this.showprocess.length;
            } else {
              this.$message.error(res.msg || '加载流程失败');
            }
          }
      ).catch((error) => {
        console.error('数据加载出现错误:', error);
      });
    },
    addProcess() {
      const last = this.getLastOne;
      if (last == null) {
        this.$message.error('当前方案暂无流程节点，请刷新后重试');
        return;
      }
      if (this.uid == null || this.uid === '') {
        this.$message.warning('请选择审核员');
        return;
      }
      const newprocess = {before: last, uid: this.uid};
      this.$request.post('/process/add', newprocess).then(
          res => {
            if (res.code === '200') {
              this.$message.success('流程添加成功');
              this.show2 = false;
              this.uid = null;
              this.getProcess();
            } else {
              this.$message.error(res.msg || '添加失败');
            }
          }
      ).catch((error) => {
        console.error('数据加载出错', error);
      });
    },
    getProcessType() {
      const bid = this.resolveUserBid();
      const req = bid != null
          ? this.$request.get('/processtype/selectByBid/' + bid)
          : this.$request.get('/processtype/selectAll');
      req.then(
          res => {
            if (res.code === '200') {
              this.allprocesstype = res.data || [];
              this.$nextTick(() => {
                if (this.allprocesstype.length === 0) {
                  this.activeProcesstypeId = null;
                  this.testprocess = {name: ''};
                  this.showprocess = [];
                  this.count = 0;
                  return;
                }
                const stillExists = this.activeProcesstypeId != null &&
                    this.allprocesstype.some((t) => t.id === this.activeProcesstypeId);
                if (this.activeProcesstypeId == null || !stillExists) {
                  this.applyprocesstype(this.allprocesstype[0]);
                }
              });
            } else {
              this.$message.error(res.msg || '加载方案列表失败');
            }
          }
      ).catch((error) => {
        console.error('数据加载出现错误:', error);
      });
    },
    addProcessType() {
      if (!this.processtypename || !this.processtypename.trim()) {
        this.$message.warning('请输入方案名称');
        return;
      }
      if (this.uid == null || this.uid === '') {
        this.$message.warning('请选择第一级审核员');
        return;
      }
      const bid = this.resolveUserBid();
      const newprocesstype = {name: this.processtypename.trim(), uid: this.uid, bid: bid};
      this.$request.post('/processtype/add', newprocesstype).then(
          res => {
            if (res.code === '200') {
              this.$message.success('添加成功');
              this.show3 = false;
              this.processtypename = '';
              this.uid = null;
              this.getProcessType();
            } else {
              this.$message.error(res.msg || '添加失败');
            }
          }
      ).catch((error) => {
        console.error('数据加载出错', error);
      });
    },
    deleteprocesstype(processtype) {
      if (Number(processtype.id) === 1) {
        this.$message.warning('默认方案无法删除！');
      } else {
        MessageBox.confirm('确定删除方案"' + processtype.name + '"吗？', '删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(() => {
          this.$request.delete('/processtype/delete/' + processtype.id).then(
              res => {
                if (res.code === '200') {
                  this.$message.success('删除成功');
                  if (this.activeProcesstypeId === processtype.id) {
                    this.activeProcesstypeId = null;
                    this.testprocess = {name: ''};
                    this.showprocess = [];
                    this.count = 0;
                  }
                  this.getProcessType();
                } else {
                  this.$message.error(res.msg || '删除失败');
                }
              }
          ).catch((error) => {
            console.error('数据加载出现错误:', error);
          });

        }).catch(() => {
          this.$message.info('已取消');
        });
      }
    },
    applyprocesstype(type) {
      this.activeProcesstypeId = type.id;
      this.$set(this.testprocess, 'name', type.name);
      this.getProcess();
    },
    setColumn() {
      if (!Array.isArray(this.coid) || this.coid.length === 0) {
        this.$message.warning('请选择栏目');
        return;
      }
      const leafCoid = this.coid[this.coid.length - 1];
      if (leafCoid == null || leafCoid === '' || Number(leafCoid) === -1) {
        this.$message.warning('不能绑定默认栏目或未分类');
        return;
      }
      if (this.columnBindProcesstypeId == null) {
        this.$message.error('未指定方案，请关闭后重新打开「专栏配置」');
        return;
      }
      const bid = this.resolveUserBid();
      const payload = {id: this.columnBindProcesstypeId, coid: leafCoid, bid: bid};
      this.$request.put('/processtype/setCoid', payload).then(
          res => {
            if (res.code === '200') {
              this.$message.success('设置成功');
              this.show4 = false;
              this.coid = [];
              this.getProcess();
              this.getProcessType();
            } else {
              this.$message.error(res.msg || '设置失败');
            }
          }
      ).catch((error) => {
        console.error('数据加载出现错误:', error);
      });
    },

    transformOptions(options) {
      return options.map(item => {
        const newItem = {
          value: item.id,
          label: item.name
        };
        if (item.child && item.child.length > 0) {
          newItem.children = this.transformOptions(item.child);
        }
        return newItem;
      });
    }
  },
  computed: {
    getLastOne() {
      const item = this.showprocess.find((p) => Number(p.last) === 1);
      return item ? item.id : null;
    },
  }
}
</script>
<style scoped>
.audit-process-page {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  gap: 16px;
  padding: 16px 24px 24px;
  box-sizing: border-box;
  min-height: 0;
}

.audit-process-page__pane {
  flex: 1 1 320px;
  min-width: 0;
  max-width: 100%;
}

.audit-process-page__pane--current {
  flex: 1 1 380px;
}

.audit-process-page__pane--schemes {
  flex: 1 1 300px;
  max-width: 520px;
}

.audit-process-page__inner {
  margin-top: 14px;
}

.audit-process-page__level-card {
  margin: 10px 0;
  max-width: 420px;
}

.scheme-list-header {
  margin-top: 8px;
  margin-bottom: 8px;
}

.scheme-list-body {
  max-height: calc(100vh - 160px);
  overflow-y: auto;
  padding-right: 6px;
}

.scheme-card {
  margin-bottom: 10px;
  width: 100%;
  box-sizing: border-box;
}

.scheme-card--new {
  margin-bottom: 0;
}

.scheme-card__title {
  display: block;
  word-break: break-word;
  line-height: 1.5;
}
</style>