<template>
  <el-form ref="auditForm" :model="auditForm" :rules="rules" class="demo-auditForm" label-width="100px"
           style="width: 150vh;">
    <el-form-item label="通过/打回" prop="status">
      <el-radio-group v-model="auditForm.status">
        <el-radio :label="2">通过</el-radio>
        <el-radio :label="3">打回</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="审核意见" prop="advice">
      <el-input v-model="auditForm.advice" type="textarea"></el-input>
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  name: "AuditForm",
  /** did 为草稿 id；勿用空字符串作 status 初值，须与单选项 number 类型一致，否则 Element UI 单选会异常 */
  props: {
    did: { type: [Number, String], default: null }
  },
  data() {
    return {
      auditForm: {
        did: null,
        title: '',
        content: '',
        status: null,
        advice: '',
      },
      rules: {
        status: [
          {
            required: true,
            validator: (rule, value, cb) => {
              const n = Number(value);
              if (n === 2 || n === 3) {
                cb();
              } else {
                cb(new Error('请选择通过或打回'));
              }
            },
            trigger: 'change'
          }
        ],
      }
    }
  },
  watch: {
    did: {
      immediate: true,
      handler(newVal) {
        this.auditForm.did = newVal != null && newVal !== '' ? newVal : null;
        this.auditForm.status = null;
        this.auditForm.advice = '';
        this.$nextTick(() => {
          if (this.$refs.auditForm) {
            this.$refs.auditForm.clearValidate();
          }
        });
      }
    }
  },
  methods: {
    getFormData() {
      return this.auditForm;
    },
    validateForm() {
      return new Promise((resolve, reject) => {
        const form = this.$refs.auditForm;
        if (!form) {
          reject(new Error('AUDIT_FORM_NOT_READY'));
          return;
        }
        form.validate((valid) => {
          if (valid) {
            resolve();
          } else {
            reject(new Error('VALIDATION_FAILED'));
          }
        });
      });
    },
  }
}
</script>

<style scoped>

</style>