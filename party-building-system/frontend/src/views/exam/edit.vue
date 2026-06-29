<template>
  <div class="qb-wrap">
    <div class="toolbar">
      <el-select v-model="value" placeholder="搜索维度" style="margin-right: 10px; width: 140px">
        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
      <el-input v-model="keyword" placeholder="输入关键字" style="width: 220px; margin-right: 10px" clearable/>
      <el-button type="primary" @click="handleQuery">查询</el-button>
      <el-button @click="handleClean">重置</el-button>
      <el-button type="warning" icon="el-icon-collection-tag" style="float: right; margin-right: 8px" @click="openKwDialog">添加关键词</el-button>
      <el-button type="success" icon="el-icon-plus" style="float: right" @click="handleAdd">添加题目</el-button>
    </div>

    <el-table
        :data="sliceDate()"
        border
        height="450"
        style="width: 100%"
        v-loading="loading"
        :header-cell-style="{ backgroundColor: 'aliceblue', color: '#666' }">
      <el-table-column prop="id" label="题号" width="80"/>
      <el-table-column prop="type" label="题型" width="100"/>
      <el-table-column prop="keys" label="关键词" width="200" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-tag
              v-for="(key, index) in scope.row.keys"
              :key="index"
              type="primary"
              size="small"
              style="margin-left: 4px">
            {{ key }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="des" label="题干" :show-overflow-tooltip="true"/>
      <el-table-column prop="answer" label="答案" width="120" :show-overflow-tooltip="true"/>
      <el-table-column prop="anal" label="解析" :show-overflow-tooltip="true"/>
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin: 10px 0">
      <el-pagination
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="tableData.length"/>
    </div>

    <el-dialog
        :title="dialogMode === 'edit' ? '编辑题目' : '添加题目'"
        :visible.sync="formVisible"
        width="640px"
        append-to-body
        @close="onDialogClose">
      <el-form label-position="top" :model="form">
        <el-form-item label="关键词" required>
          <el-select
              v-model="form.keyword"
              placeholder="请选择关键词"
              filterable
              style="width: 100%"
              popper-append-to-body
              popper-class="qb-edit-select-dropdown"
              :loading="keywordsLoading">
            <el-option
                v-for="(item, kwIdx) in keywords"
                :key="'kw-' + kwIdx + '-' + String(item.value)"
                :label="item.label"
                :value="item.value"/>
          </el-select>
          <span v-if="!keywordsLoading && keywords.length === 0" class="kw-empty-hint">暂无关键词，请先点击工具栏「添加关键词」</span>
        </el-form-item>

        <el-form-item label="题型" required>
          <el-select
              v-model="form.type"
              placeholder="请选择题型"
              style="width: 100%"
              popper-append-to-body
              popper-class="qb-edit-select-dropdown"
              :disabled="dialogMode === 'edit'">
            <el-option v-for="item in questionTypes" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </el-form-item>

        <el-form-item label="题干" required>
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="题干"/>
        </el-form-item>

        <template v-if="form.type === 'mc'">
          <el-form-item
              v-for="(choice, index) in m_choices"
              :key="'c' + index"
              :label="'选项 ' + String.fromCharCode(65 + index)">
            <el-input v-model="choice.value" style="width: 86%"/>
            <el-button type="danger" icon="el-icon-minus" style="float: right" @click="handleDeleteChoice(index)"/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="m_choices.push({ value: '' })">添加选项</el-button>
            <el-button type="warning" size="small" @click="m_choices = [{ value: '' }]">重置选项</el-button>
          </el-form-item>
          <el-form-item label="正确答案" required>
            <el-select
                v-model="form.correctAnswer"
                placeholder="请先填写选项内容，再选择正确项"
                style="width: 100%"
                popper-append-to-body
                popper-class="qb-edit-select-dropdown">
              <el-option
                  v-for="row in mcChoiceIndexes"
                  :key="'a' + row.index"
                  :label="String.fromCharCode(65 + row.index) + '. ' + row.item.value"
                  :value="row.index + 1"/>
            </el-select>
          </el-form-item>
        </template>

        <template v-if="form.type === 'tf'">
          <el-form-item label="正确答案" required>
            <el-select
                v-model="form.correctAnswer"
                placeholder="正确答案"
                style="width: 100%"
                popper-append-to-body
                popper-class="qb-edit-select-dropdown">
              <el-option label="正确" value="正确"/>
              <el-option label="错误" value="错误"/>
            </el-select>
          </el-form-item>
        </template>

        <template v-if="form.type === 'fi'">
          <el-form-item label="参考答案" required>
            <el-input v-model="form.correctAnswer" placeholder="参考答案（填空）"/>
          </el-form-item>
        </template>

        <el-form-item label="解析">
          <el-input v-model="form.anal" type="textarea" :rows="2" placeholder="题目解析（可选）"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="formVisible = false">取 消</el-button>
        <el-button type="primary" :loading="saving" @click="handleConfirm">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog
        title="添加题目关键词"
        :visible.sync="kwDialogVisible"
        width="480px"
        append-to-body
        @close="onKwDialogClose">
      <el-form label-position="top" :model="kwForm">
        <el-form-item label="所属栏目" required>
          <el-select
              v-model="kwForm.column_id"
              placeholder="请选择栏目（关键词需归属栏目）"
              filterable
              style="width: 100%"
              popper-append-to-body
              popper-class="qb-edit-select-dropdown"
              :loading="quizColumnsLoading">
            <el-option
                v-for="(c, i) in quizColumns"
                :key="'col-' + i + '-' + c.value"
                :label="c.label"
                :value="c.value"/>
          </el-select>
          <span v-if="!quizColumnsLoading && quizColumns.length === 0" class="kw-empty-hint">暂无栏目，请先在「栏目设置」中添加栏目，或联系管理员分配栏目权限</span>
        </el-form-item>
        <el-form-item label="关键词" required>
          <el-input v-model="kwForm.keyword_description" maxlength="100" show-word-limit placeholder="例如：党史、党章"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="kwDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="kwSaving" @click="submitKwAdd">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request';
import devLog from '@/utils/devLog';

export default {
  name: 'edit',
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      loading: false,
      saving: false,
      options: [
        { value: 'des', label: '题目描述' },
        { value: 'anal', label: '题目解析' },
        { value: 'keyword', label: '题目关键词' },
      ],
      questionTypes: [
        { value: 'tf', label: '判断题' },
        { value: 'mc', label: '选择题' },
        { value: 'fi', label: '填空题' },
      ],
      keywords: [],
      keywordsLoading: false,
      kwDialogVisible: false,
      kwSaving: false,
      quizColumns: [],
      quizColumnsLoading: false,
      kwForm: {
        column_id: null,
        keyword_description: '',
      },
      value: '',
      keyword: '',
      m_choices: [{ value: '' }],
      formVisible: false,
      dialogMode: 'add',
      editingId: null,
      editingQtype: null,
      form: {
        type: '',
        keyword: null,
        description: '',
        correctAnswer: null,
        anal: '',
      },
    };
  },
  created() {
    this.loadKeywordOptions();
  },
  mounted() {
    this.loadQuestions();
  },
  computed: {
    mcChoiceIndexes() {
      return this.m_choices
          .map((item, index) => ({ item, index }))
          .filter(({ item }) => item && item.value && String(item.value).trim());
    },
  },
  methods: {
    async _ok(promise) {
      const r = await promise;
      const ok = r && (r.code === '200' || r.code === 200);
      if (!ok) {
        throw new Error((r && r.msg) || '请求失败');
      }
      return r;
    },
    handleDeleteChoice(index) {
      if (this.m_choices.length > 1) {
        this.m_choices.splice(index, 1);
      }
    },
    /** 兼容 Fastjson / Jackson：keyword_id 或 keywordId */
    _keywordRow(d) {
      if (!d || typeof d !== 'object') return null;
      const id = d.keyword_id != null ? d.keyword_id : d.keywordId;
      const rawLabel = d.keyword_description != null ? d.keyword_description : d.keywordDescription;
      const label = rawLabel != null ? String(rawLabel).trim() : '';
      if (id === null || id === undefined || id === '') return null;
      return {
        value: id,
        label: label || `关键词 #${id}`,
      };
    },
    async loadKeywordOptions() {
      this.keywordsLoading = true;
      try {
        const res = await request.get('/keywords');
        const ok = res && (res.code === '200' || res.code === 200);
        if (!ok) {
          this.keywords = [];
          this.$message.error((res && res.msg) || '加载关键词失败');
          return;
        }
        const raw = res.data;
        const data = Array.isArray(raw) ? raw : [];
        this.keywords = data.map((d) => this._keywordRow(d)).filter(Boolean);
      } catch (e) {
        this.keywords = [];
        const st = e && e.response && e.response.status;
        if (st === 403) {
          this.$message.error('无题库权限，无法加载关键词');
        } else if (!st || st === 404) {
          this.$message.error('无法访问关键词接口，请确认服务器 Nginx 已反代 /keywords（不是仅 /keywords/）');
        } else {
          this.$message.error((e && e.message) || '加载关键词失败');
        }
      } finally {
        this.keywordsLoading = false;
      }
    },
    _columnTreeLabel(raw) {
      if (raw == null || raw === '') return '';
      if (typeof raw === 'string') return raw.trim();
      if (Array.isArray(raw) && raw.length) {
        try {
          return new TextDecoder('utf-8').decode(new Uint8Array(raw)).trim();
        } catch (_) {
          return '';
        }
      }
      return String(raw).trim();
    },
    /** 资讯栏目树（/column/selectAll）：展平为下拉选项，排除系统默认 id=-1 */
    flattenColumnTree(nodes, depth) {
      const d = depth == null ? 0 : depth;
      const out = [];
      if (!Array.isArray(nodes)) return out;
      for (const n of nodes) {
        if (!n || typeof n !== 'object') continue;
        const id = n.id != null ? n.id : (n.column_id != null ? n.column_id : n.columnId);
        const name = this._columnTreeLabel(n.name);
        if (id != null && id !== '' && Number(id) !== -1) {
          const prefix = d > 0 ? `${'　'.repeat(d)}└ ` : '';
          out.push({ value: id, label: `${prefix}${name || '栏目 #' + id}` });
        }
        const kids = n.child || n.children || n.columnList;
        if (kids && kids.length) {
          out.push(...this.flattenColumnTree(kids, d + 1));
        }
      }
      return out;
    },
    async loadQuizColumns() {
      this.quizColumnsLoading = true;
      try {
        const res = await request.get('/column/selectAll');
        const ok = res && (res.code === '200' || res.code === 200);
        if (!ok) {
          this.quizColumns = [];
          this.$message.error((res && res.msg) || '加载栏目失败');
          return;
        }
        const raw = res.data;
        const tree = Array.isArray(raw) ? raw : (raw && Array.isArray(raw.list) ? raw.list : []);
        this.quizColumns = this.flattenColumnTree(tree, 0);
      } catch (e) {
        this.quizColumns = [];
        const st = e && e.response && e.response.status;
        if (st === 403) {
          this.$message.error('无权限加载栏目列表（需题库或栏目管理权限）');
        } else {
          this.$message.error('加载栏目失败');
        }
      } finally {
        this.quizColumnsLoading = false;
      }
    },
    onKwDialogClose() {
      this.kwForm = { column_id: null, keyword_description: '' };
      this.kwSaving = false;
    },
    async openKwDialog() {
      this.kwForm = { column_id: null, keyword_description: '' };
      await this.loadQuizColumns();
      this.kwDialogVisible = true;
    },
    async submitKwAdd() {
      const col = this.kwForm.column_id;
      const des = (this.kwForm.keyword_description || '').trim();
      if (col === null || col === undefined || col === '') {
        this.$message.error('请选择栏目');
        return;
      }
      const colNum = Number(col);
      if (!Number.isFinite(colNum)) {
        this.$message.error('栏目选择无效，请重新选择');
        return;
      }
      if (!des) {
        this.$message.error('请填写关键词');
        return;
      }
      this.kwSaving = true;
      try {
        const res = await this._ok(
            request.post('/keywords/add', {
              keyword_description: des,
              column_id: colNum,
            })
        );
        const created = res.data;
        const newId = created && (created.keyword_id != null ? created.keyword_id : created.keywordId);
        this.$message.success('关键词已添加');
        this.kwDialogVisible = false;
        await this.loadKeywordOptions();
        if (this.formVisible && newId != null) {
          this.form.keyword = newId;
        }
      } catch (e) {
        this.$message.error(e.message || '添加失败');
      } finally {
        this.kwSaving = false;
      }
    },
    onDialogClose() {
      this.m_choices = [{ value: '' }];
      this.form = { type: '', keyword: null, description: '', correctAnswer: null, anal: '' };
      this.saving = false;
    },
    async handleAdd() {
      this.dialogMode = 'add';
      this.editingId = null;
      this.editingQtype = null;
      this.form = { type: '', keyword: null, description: '', correctAnswer: null, anal: '' };
      this.m_choices = [{ value: '' }];
      await this.loadKeywordOptions();
      this.formVisible = true;
    },
    async handleEdit(row) {
      this.dialogMode = 'edit';
      this.editingId = row.id;
      this.editingQtype = row.qtype;
      this.form = {
        type: row.qtype,
        keyword: null,
        description: row.des,
        correctAnswer: row.qtype === 'mc' ? row.rawCorrect : row.answer,
        anal: row.anal || '',
      };
      try {
        const path =
            row.qtype === 'tf'
                ? '/keywords/in_tf/'
                : row.qtype === 'mc'
                    ? '/keywords/in_mc/'
                    : '/keywords/in_fi/';
        const r = await this._ok(request.get(path + row.id));
        const arr = r.data || [];
        if (arr.length) {
          const row = this._keywordRow(arr[0]);
          this.form.keyword = row ? row.value : null;
        }
        if (row.qtype === 'mc') {
          const optRes = await this._ok(request.get('/options/in_question/' + row.id));
          const opts = (optRes.data || []).slice().sort((a, b) => (a.order_in_question || 0) - (b.order_in_question || 0));
          this.m_choices = opts.length
              ? opts.map((o) => ({ value: o.option_description, option_id: o.option_id }))
              : [{ value: '' }];
        } else {
          this.m_choices = [{ value: '' }];
        }
        await this.loadKeywordOptions();
        this.formVisible = true;
      } catch (e) {
        this.$message.error(e.message || '加载题目失败');
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定删除该题？', '提示', { type: 'warning' });
        if (row.qtype === 'tf') {
          await this._ok(request.get('/questions/tf/delete/' + row.id));
        } else if (row.qtype === 'mc') {
          await this._ok(request.get('/questions/mc/delete/' + row.id));
        } else {
          await this._ok(request.get('/questions/fi/delete/' + row.id));
        }
        this.$message.success('已删除');
        await this.loadQuestions();
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error(e.message || '删除失败');
        }
      }
    },
    validateForm() {
      if (!this.form.type) {
        this.$message.error('请选择题型');
        return false;
      }
      if (this.form.keyword === null || this.form.keyword === undefined || this.form.keyword === '') {
        this.$message.error('请选择关键词');
        return false;
      }
      if (!this.form.description || !String(this.form.description).trim()) {
        this.$message.error('请填写题干');
        return false;
      }
      if (this.form.correctAnswer === null || this.form.correctAnswer === undefined || this.form.correctAnswer === '') {
        this.$message.error('请设置正确答案');
        return false;
      }
      if (this.form.type === 'mc') {
        const filled = this.m_choices.filter((c) => c && c.value && String(c.value).trim());
        if (filled.length < 2) {
          this.$message.error('选择题至少两个非空选项');
          return false;
        }
      }
      return true;
    },
    async handleConfirm() {
      if (!this.validateForm()) return;
      this.saving = true;
      try {
        if (this.dialogMode === 'add') {
          await this.submitAdd();
        } else {
          await this.submitEdit();
        }
        this.$message.success(this.dialogMode === 'add' ? '添加成功' : '保存成功');
        this.formVisible = false;
        await this.loadQuestions();
      } catch (e) {
        devLog(e);
        this.$message.error(e.message || '操作失败');
      } finally {
        this.saving = false;
      }
    },
    async submitAdd() {
      const base = {
        question_description: String(this.form.description).trim(),
        question_analysis: this.form.anal ? String(this.form.anal) : '',
      };
      if (this.form.type === 'fi') {
        const res = await this._ok(
            request.post('/questions/fi/add', {
              ...base,
              correct_answer: String(this.form.correctAnswer),
            })
        );
        const created = res.data;
        const qid = created && (created.fill_in_the_blank_question_id ?? created.fillInTheBlankQuestionId);
        if (qid == null) throw new Error('未返回题目编号');
        await this._ok(
            request.post('/questions/fi/add/keyword', {
              fill_in_the_blank_question_id: qid,
              keyword_id: this.form.keyword,
            })
        );
        return;
      }
      if (this.form.type === 'tf') {
        const res = await this._ok(
            request.post('/questions/tf/add', {
              ...base,
              correct_answer: String(this.form.correctAnswer),
            })
        );
        const created = res.data;
        const qid = created && (created.true_false_question_id ?? created.trueFalseQuestionId);
        if (qid == null) throw new Error('未返回题目编号');
        await this._ok(
            request.post('/questions/tf/add/keyword', {
              true_false_question_id: qid,
              keyword_id: this.form.keyword,
            })
        );
        return;
      }
      const res = await this._ok(
          request.post('/questions/mc/add', {
            ...base,
            correct_answer: Number(this.form.correctAnswer),
          })
      );
      const created = res.data;
      const qid = created && (created.multiple_choice_question_id ?? created.multipleChoiceQuestionId);
      if (qid == null) throw new Error('未返回题目编号');
      await this._ok(
          request.post('/questions/mc/add/keyword', {
            multiple_choice_question_id: qid,
            keyword_id: this.form.keyword,
          })
      );
      for (let i = 0; i < this.m_choices.length; i++) {
        const text = this.m_choices[i] && this.m_choices[i].value;
        if (!text || !String(text).trim()) continue;
        await this._ok(
            request.post('/options/add', {
              multiple_choice_question_id: qid,
              option_description: String(text).trim(),
              is_correct: Number(this.form.correctAnswer) === i + 1,
              order_in_question: i + 1,
            })
        );
      }
    },
    async submitEdit() {
      const id = this.editingId;
      const base = {
        question_description: String(this.form.description).trim(),
        question_analysis: this.form.anal ? String(this.form.anal) : '',
      };
      if (this.editingQtype === 'fi') {
        await this._ok(
            request.post('/questions/fi/update', {
              fill_in_the_blank_question_id: id,
              ...base,
              correct_answer: String(this.form.correctAnswer),
            })
        );
        return;
      }
      if (this.editingQtype === 'tf') {
        await this._ok(
            request.post('/questions/tf/update', {
              true_false_question_id: id,
              ...base,
              correct_answer: String(this.form.correctAnswer),
            })
        );
        return;
      }
      await this._ok(
          request.post('/questions/mc/update', {
            multiple_choice_question_id: id,
            ...base,
            correct_answer: Number(this.form.correctAnswer),
          })
      );
      const optRes = await this._ok(request.get('/options/in_question/' + id));
      const existing = optRes.data || [];
      for (const o of existing) {
        try {
          await this._ok(request.get('/options/delete/' + o.option_id));
        } catch (_) {
          /* ignore */
        }
      }
      for (let i = 0; i < this.m_choices.length; i++) {
        const text = this.m_choices[i] && this.m_choices[i].value;
        if (!text || !String(text).trim()) continue;
        await this._ok(
            request.post('/options/add', {
              multiple_choice_question_id: id,
              option_description: String(text).trim(),
              is_correct: Number(this.form.correctAnswer) === i + 1,
              order_in_question: i + 1,
            })
        );
      }
    },
    handleQuery() {
      if (!this.value || !this.keyword) {
        this.loadQuestions();
        return;
      }
      this.searchQuestions();
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum;
    },
    sliceDate() {
      return this.tableData.slice(this.pageSize * (this.pageNum - 1), this.pageSize * this.pageNum);
    },
    handleClean() {
      this.keyword = '';
      this.value = '';
      this.loadQuestions();
    },
    async loadQuestions() {
      this.loading = true;
      this.tableData = [];
      this.pageNum = 1;
      try {
        const tfRes = await this._ok(request.get('/questions/tf'));
        const tfList = tfRes.data || [];
        for (const q of tfList) {
          let keys = [];
          try {
            const kw = await this._ok(request.get('/keywords/in_tf/' + q.true_false_question_id));
            keys = (kw.data || []).map((k) => k.keyword_description);
          } catch (_) {
            keys = [];
          }
          this.tableData.push({
            id: q.true_false_question_id,
            qtype: 'tf',
            type: '判断题',
            des: q.question_description,
            anal: q.question_analysis,
            answer: q.correct_answer,
            keys,
          });
        }

        const mcRes = await this._ok(request.get('/questions/mc'));
        const mcList = mcRes.data || [];
        for (const q of mcList) {
          let keys = [];
          let desExtra = '';
          try {
            const kw = await this._ok(request.get('/keywords/in_mc/' + q.multiple_choice_question_id));
            keys = (kw.data || []).map((k) => k.keyword_description);
          } catch (_) {
            keys = [];
          }
          try {
            const op = await this._ok(request.get('/options/in_question/' + q.multiple_choice_question_id));
            const options = (op.data || []).slice().sort((a, b) => (a.order_in_question || 0) - (b.order_in_question || 0));
            options.forEach((opt, index) => {
              desExtra += ' ' + String.fromCharCode(65 + index) + '.' + opt.option_description;
            });
          } catch (_) {
            /* no options */
          }
          const letter =
              q.correct_answer != null
                  ? String.fromCharCode(65 + Number(q.correct_answer) - 1)
                  : '';
          this.tableData.push({
            id: q.multiple_choice_question_id,
            qtype: 'mc',
            type: '选择题',
            des: q.question_description + desExtra,
            anal: q.question_analysis,
            answer: letter,
            rawCorrect: q.correct_answer,
            keys,
          });
        }

        const fiRes = await this._ok(request.get('/questions/fi'));
        const fiList = fiRes.data || [];
        for (const q of fiList) {
          let keys = [];
          try {
            const kw = await this._ok(request.get('/keywords/in_fi/' + q.fill_in_the_blank_question_id));
            keys = (kw.data || []).map((k) => k.keyword_description);
          } catch (_) {
            keys = [];
          }
          this.tableData.push({
            id: q.fill_in_the_blank_question_id,
            qtype: 'fi',
            type: '填空题',
            des: q.question_description,
            anal: q.question_analysis,
            answer: q.correct_answer,
            keys,
          });
        }
      } catch (e) {
        this.$message.error(e.message || '加载题库失败');
      } finally {
        this.loading = false;
      }
    },
    async searchQuestions() {
      this.loading = true;
      this.tableData = [];
      this.pageNum = 1;
      const k = encodeURIComponent(this.keyword || '');
      const dim = this.value;
      try {
        const tfUrl =
            dim === 'des'
                ? `/questions/tf/search/des/${k}`
                : dim === 'anal'
                    ? `/questions/tf/search/anal/${k}`
                    : `/questions/tf/search/keyword/${k}`;
        const mcUrl =
            dim === 'des'
                ? `/questions/mc/search/des/${k}`
                : dim === 'anal'
                    ? `/questions/mc/search/anal/${k}`
                    : `/questions/mc/search/keyword/${k}`;
        const fiUrl =
            dim === 'des'
                ? `/questions/fi/search/des/${k}`
                : dim === 'anal'
                    ? `/questions/fi/search/anal/${k}`
                    : `/questions/fi/search/keyword/${k}`;

        const [tfRes, mcRes, fiRes] = await Promise.all([
          this._ok(request.get(tfUrl)),
          this._ok(request.get(mcUrl)),
          this._ok(request.get(fiUrl)),
        ]);

        const pushTf = async (list) => {
          for (const q of list) {
            let keys = [];
            try {
              const kw = await this._ok(request.get('/keywords/in_tf/' + q.true_false_question_id));
              keys = (kw.data || []).map((x) => x.keyword_description);
            } catch (_) {
              keys = [];
            }
            this.tableData.push({
              id: q.true_false_question_id,
              qtype: 'tf',
              type: '判断题',
              des: q.question_description,
              anal: q.question_analysis,
              answer: q.correct_answer,
              keys,
            });
          }
        };
        const pushMc = async (list) => {
          for (const q of list) {
            let keys = [];
            let desExtra = '';
            try {
              const kw = await this._ok(request.get('/keywords/in_mc/' + q.multiple_choice_question_id));
              keys = (kw.data || []).map((x) => x.keyword_description);
            } catch (_) {
              keys = [];
            }
            try {
              const op = await this._ok(request.get('/options/in_question/' + q.multiple_choice_question_id));
              const options = (op.data || []).slice().sort((a, b) => (a.order_in_question || 0) - (b.order_in_question || 0));
              options.forEach((opt, index) => {
                desExtra += ' ' + String.fromCharCode(65 + index) + '.' + opt.option_description;
              });
            } catch (_) {
              /* */
            }
            const letter =
                q.correct_answer != null
                    ? String.fromCharCode(65 + Number(q.correct_answer) - 1)
                    : '';
            this.tableData.push({
              id: q.multiple_choice_question_id,
              qtype: 'mc',
              type: '选择题',
              des: q.question_description + desExtra,
              anal: q.question_analysis,
              answer: letter,
              rawCorrect: q.correct_answer,
              keys,
            });
          }
        };
        const pushFi = async (list) => {
          for (const q of list) {
            let keys = [];
            try {
              const kw = await this._ok(request.get('/keywords/in_fi/' + q.fill_in_the_blank_question_id));
              keys = (kw.data || []).map((x) => x.keyword_description);
            } catch (_) {
              keys = [];
            }
            this.tableData.push({
              id: q.fill_in_the_blank_question_id,
              qtype: 'fi',
              type: '填空题',
              des: q.question_description,
              anal: q.question_analysis,
              answer: q.correct_answer,
              keys,
            });
          }
        };

        await pushTf(tfRes.data || []);
        await pushMc(mcRes.data || []);
        await pushFi(fiRes.data || []);
      } catch (e) {
        this.$message.error(e.message || '查询失败');
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.qb-wrap {
  padding: 12px 16px;
}

.toolbar {
  margin-bottom: 12px;
}

.kw-empty-hint {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
}
</style>

<style>
/* 对话框内下拉：避免被遮罩或父级 transform 裁剪 */
.qb-edit-select-dropdown {
  /* 与 ColumnSet 的 select_popper 一致：避免被全局高 z-index（如导航）或弹窗遮挡 */
  z-index: 100100 !important;
}
</style>
