<template>
  <el-form ref="activityForm" :model="activityForm" :rules="rules" class="demo-activityForm" label-width="100px"
           style="width: 100%">
    <el-form-item label="活动名称" prop="name" >
      <el-input v-model="activityForm.name"></el-input>
    </el-form-item>
    <el-form-item label="活动封面">
      <single-image-uploader @updateQuery="onCoverUploaded"/>
      <div class="cover-hint">可选；不上传则使用默认封面。保存后仍可在活动详情中修改。</div>
    </el-form-item>
    <div style="display: flex;">
      <el-form-item label="活动类型" prop="type">
        <el-select v-model="activityForm.type" placeholder="请选择活动类型" popper-class="select_popper">
          <el-option v-for="type in activityTypes" :key="type.id" :label="type.name" :value="type.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="主持人" prop="uid">
        <el-select v-model="activityForm.uid" placeholder="请选择主持人" popper-class="select_popper">
          <el-option v-for="user in activityForm.members" :key="user.uid" :label="user.username"
                     :value="user.uid"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="活动时间" prop="time" required>
        <el-date-picker
            v-model="activityForm.time"
            placeholder="选择日期时间"
            popper-class="select_popper"
            type="datetime">
        </el-date-picker>
      </el-form-item>
    </div>
    <el-form-item label="缺勤人员">
      <el-select v-model="selectedIds"
                 filterable
                 multiple placeholder="请选择"
                 popper-class="select_popper"
                 style="width: 100%"
                 @change="handleAbsenceChange"
      >
        <el-option
            v-for="item in activityForm.members"
            :key="item.uid"
            :label="item.username"
            :value="item.uid"
        ></el-option>
      </el-select>
    </el-form-item>
    <div v-show="activityForm.members.some(member => member.isAbsent)">
      <div style="width: 100px;text-align: right;padding: 0 12px 0 0;">缺勤原因</div>
      <div>
        <el-form-item v-for="(member, index) in activityForm.members"
                      v-if="member.isAbsent"
                      :key="member.uid"
                      :label="member.username">
          <el-form-item :prop="'members.' + index + '.type'"
                        :rules="{ required: true, message: '缺勤类型不能为空', trigger: 'change' }"
                        class="select-in-input">
            <el-select v-model="member.type" placeholder="缺勤类型" popper-class="select_popper">
              <el-option label="病假" value="1"></el-option>
              <el-option label="事假" value="2"></el-option>
              <el-option label="其他" value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :prop="'members.' + index + '.detail'" style="width: calc(100% - 6.5rem);">
            <el-input v-model="member.detail" placeholder="具体原因">
              <el-button slot="append" @click.prevent="removeAbsence(member)"><i class="el-icon-minus"></i></el-button>
            </el-input>
          </el-form-item>
        </el-form-item>
      </div>
    </div>
    <!--    <el-form-item label="活动内容" prop="content">-->
    <!--      <el-input v-model="activityForm.content" :rows="9" type="textarea"></el-input>-->
    <!--    </el-form-item>-->

    <div>
    <el-form-item prop="content" style="margin-left: -60px;">
      <div id="editor"></div>
    </el-form-item>
</div>
    <el-form-item>
      <el-button type="primary" @click="submitForm('activityForm')">{{ dialogTitle }}</el-button>
      <el-button @click="resetForm('activityForm')">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import E from "wangeditor";
import SingleImageUploader from "@/components/SingleImageUploader.vue";
import { DEFAULT_ACTIVITY_COVER } from "@/utils/fileUrl";

export default {
  name: "ActivityForm",
  components: { SingleImageUploader },
  props: {
    activityTypes: {type: Array},
    gid: {type: Number},
    oldData: {type: Object, default: () => ({})},
    addType: {type: Boolean, default: true},
    dialogTitle: {type: String, default: '新增活动'}
  },
  created() {
    this.selectUsers(this.gid);
  },
  watch: {
    gid(newGid) {
      if (newGid != null && newGid !== '') {
        this.selectUsers(newGid);
      }
    }
  },
  data() {
    return {
      user: JSON.parse(localStorage.getItem("current-user") || '{}'),
      activityForm: {
        name: '',
        uid: '',
        time: '',
        type: '',
        members: [],
        content: '',
        cover_image: '',
      },
      rules: {
        name: [
          {required: true, message: '请输入活动名称', trigger: 'blur'},
        ],
        time: [
          {required: true, message: '请选择活动时间', trigger: 'change'}
        ],
        type: [
          {required: true, message: '请选择活动类型', trigger: 'change'}
        ],
        uid: [
          {required: true, message: '请选择主持人', trigger: 'change'}
        ],
        content: [
          {validator: (rule, value, callback) => this.validateEditorContent(callback), trigger: 'change'}
        ],
        'members.type': [
          {required: true, message: '请选择缺勤类型', trigger: 'change'}
        ]
      },
      selectedIds: [],
      editor: null
    };
  },
  mounted() {
    this.initWangEditor();
  },
  methods: {
    syncEditorToForm() {
      if (this.editor) {
        this.activityForm.content = this.editor.txt.html() || '';
      }
    },
    isBlankRichText(html) {
      if (!html) return true;
      const text = String(html)
          .replace(/<[^>]+>/g, '')
          .replace(/&nbsp;/gi, ' ')
          .trim();
      return !text;
    },
    validateEditorContent(callback) {
      this.syncEditorToForm();
      if (this.isBlankRichText(this.activityForm.content)) {
        callback(new Error('请填写活动内容'));
      } else {
        callback();
      }
    },
    formatTimeForApi(time) {
      if (!time) return '';
      if (typeof time === 'string') return time;
      if (time instanceof Date && !isNaN(time.getTime())) {
        const pad = (n) => String(n).padStart(2, '0');
        return `${time.getFullYear()}-${pad(time.getMonth() + 1)}-${pad(time.getDate())} ${pad(time.getHours())}:${pad(time.getMinutes())}:${pad(time.getSeconds())}`;
      }
      return String(time);
    },
    buildSubmitPayload() {
      this.syncEditorToForm();
      const absentWithoutType = (this.activityForm.members || []).filter(
          (m) => m.isAbsent && (m.type === null || m.type === undefined || m.type === '')
      );
      if (absentWithoutType.length) {
        const names = absentWithoutType.map((m) => m.username).join('、');
        return {error: `请为缺勤人员选择缺勤类型：${names}`};
      }
      return {
        payload: {
          ...this.activityForm,
          time: this.formatTimeForApi(this.activityForm.time),
          type: this.activityForm.type !== '' && this.activityForm.type != null
              ? Number(this.activityForm.type)
              : this.activityForm.type,
          uid: this.activityForm.uid !== '' && this.activityForm.uid != null
              ? Number(this.activityForm.uid)
              : this.activityForm.uid,
          cover_image: (this.activityForm.cover_image && String(this.activityForm.cover_image).trim())
              || DEFAULT_ACTIVITY_COVER,
        }
      };
    },
    onCoverUploaded(url) {
      this.activityForm.cover_image = url || '';
    },
    /** 将接口返回的活动（oldData）合并进表单；须在 members 已从支部拉取后调用 */
    applyServerActivityToForm(server) {
      if (!server || !server.id) {
        if (this.activityForm.id != null) {
          this.$delete(this.activityForm, 'id');
        }
        this.activityForm.name = '';
        this.activityForm.uid = '';
        this.activityForm.type = '';
        this.activityForm.time = '';
        this.activityForm.content = '';
        this.activityForm.cover_image = '';
        this.selectedIds = [];
        (this.activityForm.members || []).forEach((m) => {
          m.isAbsent = false;
          m.type = null;
          m.detail = '';
        });
        this.refreshEditorHtml();
        return;
      }
      this.$set(this.activityForm, 'id', server.id);
      this.activityForm.name = server.name || '';
      this.activityForm.uid = server.uid != null && server.uid !== '' ? server.uid : '';
      this.activityForm.type = server.type != null && server.type !== '' ? Number(server.type) : '';
      if (server.time) {
        const raw = typeof server.time === 'string' ? server.time.replace(/-/g, '/') : server.time;
        const d = new Date(raw);
        this.activityForm.time = isNaN(d.getTime()) ? '' : d;
      } else {
        this.activityForm.time = '';
      }
      this.activityForm.content = server.content != null ? String(server.content) : '';
      this.activityForm.cover_image = server.cover_image || '';
      this.refreshEditorHtml();
    },
    refreshEditorHtml() {
      const html = this.activityForm.content || '';
      this.$nextTick(() => {
        setTimeout(() => {
          if (this.editor) {
            this.editor.txt.html(html);
          }
        }, 150);
      });
    },
    selectUsers(gid) {
      if (gid == null || gid === '') {
        this.activityForm.members = [];
        this.applyServerActivityToForm(this.oldData || {});
        return;
      }
      this.$request.get('/user/selectBranchByGid/' + gid).then(
          res => {
            if (res.code === '200' || res.code === 200) {
              this.activityForm.members = res.data.map(member => {
                const {id, ...rest} = member;
                return {
                  uid: id,
                  ...rest,
                  isAbsent: false,
                  type: null,
                  detail: ''
                };
              });
              this.applyServerActivityToForm(this.oldData || {});
            } else {
              this.$message.error(res.msg);
            }
          }
      ).catch((error) => {
        console.error('党支部成员数据加载出错', error);
      })
    },
    submitForm(formName) {
      this.syncEditorToForm();
      this.$refs[formName].validate((valid) => {
        if (!valid) {
          this.$message.error('请正确填写表单内容（名称、类型、主持人、时间、活动内容均为必填）');
          return false;
        }
        const built = this.buildSubmitPayload();
        if (built.error) {
          this.$message.error(built.error);
          return false;
        }
        const payload = built.payload;
        if (this.addType) {
            this.$request.post('/activity/add', payload).then(
                res => {
                  if (res.code === '200' || res.code === 200) {
                    this.$message.success('活动创建成功');
                    this.$emit('ActivityAdded');
                  } else {
                    this.$message.error(res.msg);
                  }
                }
            ).catch((error) => {
              console.error('活动创建失败', error);
              this.$message.error('活动创建失败，请稍后重试');
            });
          } else {
            this.$request.put('/activity/update', payload).then(
                res => {
                  if (res.code === '200' || res.code === 200) {
                    this.$message.success('活动修改成功');
                    this.$emit('ActivityAdded');
                  } else {
                    this.$message.error(res.msg);
                  }
                }
            ).catch((error) => {
              console.error('活动修改失败', error);
              this.$message.error('活动修改失败，请稍后重试');
            });
          }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
      this.activityForm.members.forEach(member => {
        member.isAbsent = false;
        member.type = null;
        member.detail = '';
      });
      this.selectedIds = [];
    },
    handleAbsenceChange(selectedIds) {
      this.selectedIds = selectedIds;
      this.activityForm.members.forEach(member => {
        member.isAbsent = selectedIds.includes(member.uid);
        if (!member.isAbsent) {
          member.type = null;
          member.detail = '';
        }
      });
    },
    removeAbsence(member) {
      member.isAbsent = false;
      member.type = null;
      member.detail = '';
      const idIndex = this.selectedIds.indexOf(member.uid);
      if (idIndex !== -1) {
        this.selectedIds.splice(idIndex, 1);
      }
    },
    // 富文本编辑器初始化
    initWangEditor() {
      setTimeout(() => {
        if (!this.editor) {
          this.editor = new E(document.getElementById('editor'));
          this.editor.config.placeholder = '请输入内容';
          this.editor.config.uploadImgServer = this.$baseUrl + '/file/editor/upload';
          this.editor.config.uploadFileName = 'file'
          this.editor.config.uploadImgHeaders = {
            token: this.user.token
          }
          this.editor.create();

          this.editor.config.uploadImgHooks = {
            customInsert: function (insertImgFn, result) {
              let imgUrl = result.data[0].url;
              insertImgFn(imgUrl);
            }
          };
          this.editor.config.onchange = (html) => {
            this.activityForm.content = html;
          };
        }
        if (this.editor) {
          this.editor.txt.html(this.activityForm.content || '');
        }
      }, 100);
    },
  }
}
</script>

<style scoped>
::v-deep .el-form-item__content {
  display: flex;
  align-items: center;
}

::v-deep .select-in-input .el-form-item__content .el-select {
  background-color: #fff;
  width: 6.5rem;
}

.cover-hint {
  display: block;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>