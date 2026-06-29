<template>
  <div style="display: flex; flex-direction: column; flex-grow: 1;padding: 1vh">
    <el-table v-loading="loading"
              :cell-style="{ padding: '0px' }" :data="returntable.filter(data =>!search || (data.title?.toLowerCase()?.includes(search.toLowerCase()))
                                                       || (data.content?.toLowerCase()?.includes(search.toLowerCase())))"
              :row-style="{ height:'100px',overflow: 'hidden' }"
              stripe>
      <el-table-column label="栏目" prop="column" width="100">
      </el-table-column>
      <el-table-column label="标题" prop="title" width="140">
      </el-table-column>
      <el-table-column label="拟稿人" prop="uname" width="140">
      </el-table-column>
      <el-table-column label="内容" prop="content" width="350">
        <template slot-scope="scope">
          <span v-if="scope.row.content" class="ellipsis-text">{{ stripHtmlTags(scope.row.content) }}</span>
        </template>
      </el-table-column>
      <!-- 新增的图片列 -->
      <el-table-column width="200">
        <template slot-scope="scope">
          <img v-if="getFirstImage(scope.row.content)" :src="getFirstImage(scope.row.content)"
               :style="{ width: getImageWidth(scope.row.content), height: '100px' }"/>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" width="200">
        <template slot-scope="scope">
          {{ scope.row.save_time || scope.row.saveTime || '' }}
        </template>
      </el-table-column>

      <el-table-column
          align="right">
        <template slot="header" slot-scope="scope">
          <div style="display: flex;justify-content: space-between;">
            <el-input
                v-model="search"
                placeholder="输入关键字搜索"
                size="mini"
                style="max-width: 200px;"
                @input="handleInput(scope)"
            />
          </div>
        </template>
        <template slot-scope="scope">
          <el-button
              size="mini"
              @click="audit(scope.$index, scope.row)">审核
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog
        :center="true"
        :visible.sync="dialogVisible"
        fullscreen=true title="审核"
        width="90%"
        @closed="onAuditDialogClosed"
    >
      <ArticleView :draft="draft"></ArticleView>
      <div class="audit-form-container">
        <AuditForm :key="auditFormMountKey" ref="auditFormRef" :did="draft.did"></AuditForm>
      </div>
      <span slot="footer" class="dialog-footer">
              <el-button type="warning" @click="handleEdit(draft)">修改文章</el-button>
              <el-button @click="cancel">取 消</el-button>
              <el-button type="primary" :loading="submitAuditLoading" @click="submitAudit(draft)">确 定</el-button>
            </span>
    </el-dialog>

    <el-pagination
        :current-page.sync="currentPage"
        :page-size="5"
        :total="total"
        layout="prev, pager, next"
        style="text-align: center"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange">
    </el-pagination>

  </div>
</template>

<script>
import ArticleView from "@/components/ArticleView";
import AuditForm from "@/components/AuditForm";
import devLog from "@/utils/devLog";

export default {
  name: "DraftAuditList",
  components: {AuditForm, ArticleView},
  data() {
    return {
      user: JSON.parse(localStorage.getItem("current-user") || '{}'),
      tableData: [],
      search: '',
      loading: true,
      draft: {
        id: 0,
        did: 0,
        aid: 0,
        next: 0,
        srid: 0,
        source: '',
        title: '',
        content: '',
        column: '',
      },
      auditSessionLockDid: null,
      /** 每次打开审核弹窗递增，强制重建 AuditForm，避免同一条待审重复打开时单选状态不刷新 */
      auditFormMountKey: 0,
      dialogVisible: false,
      submitAuditLoading: false,
      currentPage: 1, // 当前页码
      total: 0, // 总数据量
      pageSize: 5,
    };
  },
  mounted() {
    this.loadData();
  },
  methods: {
    handleEdit(row) {
      this.$router.push({
        name: '审核文章编辑',
        params: {
          id: row.srid,
          coid: row.coid,
          source: row.source,
          title: row.title,
          content: row.content,
          column:row.column,
        }
      });
    },
    resolveAuditorUid() {
      const u = this.user || {};
      const raw = u.uid != null && u.uid !== '' ? u.uid : u.id;
      if (raw == null || raw === '') {
        return null;
      }
      const n = Number(raw);
      return Number.isFinite(n) ? n : null;
    },
    loadData() {
      devLog(this.user);
      const uid = this.resolveAuditorUid();
      if (uid == null) {
        this.$message.error('未获取到登录用户信息，请重新登录');
        this.loading = false;
        return;
      }
      this.loading = true;
      this.$request.get('/audit/selectByUid/' + uid).then(
          res => {
            if (res.code === '200') {
              this.tableData = res.data || [];
              devLog(this.tableData);
              this.total = this.tableData.length;
            } else {
              this.$message.error(res.msg || '加载失败');
            }
            this.loading = false;
          }
      ).catch((error) => {
        console.error('数据加载出现错误:', error);
        this.loading = false;
      });
    },
    handleSizeChange(newSize) {
      this.pageSize = newSize;
      this.loadData();
    },
    handleCurrentChange(newPage) {
      this.currentPage = newPage;
      this.loadData();
    },
    fetchData() {
      this.loadData();
    },
    stripHtmlTags(html) {
      let tmp = document.createElement("DIV");
      tmp.innerHTML = html;
      return tmp.textContent || tmp.innerText;
    },
    audit(index, row) {
      this.$request.put('/draft/lock/' + row.did).then(
          res => {
            if (res.code === '200') {
              this.auditSessionLockDid = row.did;
              this.draft.did = row.did;
              this.draft.id = row.did;
              this.draft.aid = row.id;
              this.draft.srid = row.srid;
              this.draft.next = row.next;
              this.draft.title = row.title;
              this.draft.source = row.source;
              this.draft.content = row.content;
              this.draft.column = row.column;
              this.auditFormMountKey += 1;
              this.dialogVisible = true;
            } else {
              this.$message.error((res.data && res.data.msg) || res.msg || '加锁失败');
            }
          }
      );
    },
    handleDelete(index, row) {
      devLog(index, row);
    },
    handleInput(event) {
      devLog(event);
    },
    getFirstImage(htmlContent) {
      if (htmlContent) {
        let tmp = document.createElement("DIV");
        tmp.innerHTML = htmlContent;
        let images = tmp.getElementsByTagName('img');
        if (images.length > 0) {
          return images[0].src;
        }
      }
      return null;
    },
    getImageWidth(htmlContent) {
      const img = this.getFirstImage(htmlContent);
      if (img) {
        return new Promise((resolve, reject) => {
          const imgElement = new Image();
          imgElement.onload = function () {
            const ratio = imgElement.width / imgElement.height;
            resolve(`${100 * ratio}px`);
          };
          imgElement.onerror = function () {
            reject(new Error('图片加载失败'));
          };
          imgElement.src = img;
        });
      }
      return '0px';
    },
    isAuditSuccess(res) {
      return res && (res.code === '200' || res.code === 200);
    },
    submitAudit(draft) {
      // el-dialog 默认 append-to-body，子组件 ref 往往在下一帧才挂到父组件 $refs
      this.$nextTick(() => {
        this.$nextTick(() => {
          const auditForm = this.$refs.auditFormRef;
          if (!auditForm || typeof auditForm.validateForm !== 'function') {
            this.$message.error('审核表单未就绪，请关闭弹窗后重新打开再试');
            return;
          }
          auditForm.validateForm().then(() => {
            const formData = auditForm.getFormData();
            devLog(formData);
            devLog(draft);
            const st = Number(formData.status);
            if (st !== 2 && st !== 3) {
              this.$message.error('请选择通过或打回');
              return;
            }
            const nextRaw = draft.next != null && draft.next !== '' ? Number(draft.next) : null;
            const next = nextRaw != null && nextRaw > 0 ? nextRaw : null;
            const audit = {
              id: draft.aid,
              did: draft.id,
              srid: draft.srid,
              status: st,
              next: next,
              advice: formData.advice != null ? formData.advice : '',
            };
            this.submitAuditLoading = true;
            this.$request.post('/audit/Update', audit).then(
                res => {
                  if (this.isAuditSuccess(res)) {
                    this.auditSessionLockDid = null;
                    this.$message.success('保存成功');
                    this.dialogVisible = false;
                    this.loadData();
                  } else {
                    this.$message.error((res && res.data && res.data.msg) || (res && res.msg) || '保存失败');
                  }
                }
            ).catch((e) => {
              console.error(e);
              this.$message.error('提交审核失败');
            }).finally(() => {
              this.submitAuditLoading = false;
            });
          }).catch((err) => {
            console.error(err);
            if (err && err.message === 'AUDIT_FORM_NOT_READY') {
              this.$message.error('审核表单未就绪，请关闭弹窗后重新打开');
            } else {
              this.$message.warning('请完善审核信息：须选择「通过」或「打回」');
            }
          });
        });
      });
    },
    onAuditDialogClosed() {
      const lockId = this.auditSessionLockDid;
      if (lockId == null) {
        return;
      }
      this.$request.put('/draft/unlock/' + lockId).then(() => {
        this.loadData();
      }).catch((e) => {
        console.error(e);
      }).finally(() => {
        this.auditSessionLockDid = null;
      });
    },
    cancel() {
      const lockId = this.auditSessionLockDid != null ? this.auditSessionLockDid : this.draft.id;
      if (lockId == null) {
        this.dialogVisible = false;
        return;
      }
      this.$request.put('/draft/unlock/' + lockId).then(
          res => {
            if (res.code === '200') {
              this.auditSessionLockDid = null;
              this.loadData();
              this.dialogVisible = false;
            } else {
              this.$message.error((res.data && res.data.msg) || res.msg || '解锁失败');
            }
          }
      ).catch((error) => {
        console.error('解锁请求出现错误:', error);
      });
    },
  },
  computed: {
    returntable() {
      return this.tableData.slice(this.pageSize * (this.currentPage - 1), this.pageSize * this.currentPage);
    }
  }
}
;
</script>

<style lang="scss" scoped>
.audit-form-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}
</style>