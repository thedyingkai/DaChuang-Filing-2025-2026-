import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from "@/views/LoginPage";
import Register from "@/views/RegisterPage";
import Public from "@/views/PublicView";
import test from "@/views/TestAI";
import AiChatPage from "@/views/AiChatPage";
import EditorPage from "@/views/infoRelease/EditorPage";
import AuditEditorPage  from "@/views/infoRelease/AuditEditorPage.vue";
import ArtiCle from "@/views/ArtiCle.vue";
import DraftList from "@/views/infoRelease/DraftList";
import PageView from "@/views/PageView";
import AuthView from "@/views/infoRelease/AuthView";
import NotFound from "@/views/NotFound";
import AppHome from "@/views/infoRelease/AppHome";
import UserInfo from "@/views/personnelManage/UserInfo.vue";
import UserList from "@/views/personnelManage/UserList.vue";
import SubmittedDraft from "@/views/infoRelease/SubmittedDraft";
import DraftAuditList from "@/views/infoRelease/DraftAuditList";
import AuditRecord from "@/views/infoRelease/AuditRecord";
import ColumnSet from "@/views/infoRelease/ColumnSet"
import CarouselSet from "@/views/infoRelease/CarouselSet";
import ArticleShow from "@/views/ArticleShow.vue";
import CommentAuditList from "@/views/infoRelease/CommentAuditList.vue";
import MyComments from "@/views/infoRelease/MyComments.vue";
import ReplyMe from "@/views/infoRelease/ReplyMe.vue";
import ActivityManage from "@/views/infoRelease/ActivityManage.vue";
import SectorManage from "@/views/infoRelease/SectorManage.vue";
import BranchManage from "@/views/personnelManage/organizationManage/BranchManage.vue";
import GroupManage from "@/views/personnelManage/organizationManage/GroupManage.vue";
import DefaultView from "@/views/publicView/DefaultView";
import ResourceView from "@/views/infoRelease/ResourceView";
import AuditProcess from "@/views/infoRelease/AuditProcess.vue";
import BranchManager from "@/views/personnelManage/organizationManage/BranchManager.vue";

Vue.use(VueRouter)

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/public',
        name: 'public',
        redirect: 'publichome',
        // 移除了 name 属性，避免出现默认子路由渲染问题
        component: Public,
        meta: {title: '首页'},
        children: [
            {path: '', name: 'home', component: DefaultView},
            {path: 'ArticleShow/:id', name: 'ArticleShow', component: ArticleShow},
        ]
    },
    {
        path: '/register',
        name: 'Register',
        component: Register
    },
    {
        path: '/test',
        name: 'test',
        component: test
    },
    {
        path: '/article/:id',
        name: 'article',
        component: ArtiCle,
    },
    {
        path: '/',
        name: 'PageView',
        component: PageView,
        meta: {title: '首页'},
        children: [
            {
                path: 'home',
                name: '个人空间',
                component: AppHome,
                meta: {
                    title: '个人空间',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '个人空间'}
                    ]
                }
            },
            {
                path: 'info',
                name: '个人信息',
                component: UserInfo,
                meta: {
                    title: '个人信息',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '个人信息'}
                    ]
                }
            },
            {
                path: 'DraftList',
                name: '草稿箱',
                component: DraftList,
                meta: {
                    title: '草稿箱',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '草稿箱'}
                    ]
                }
            },
            {
                path: 'MyComments',
                name: '我的评论',
                component: MyComments,
                meta: {
                    title: '我的评论',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '我的评论'}
                    ]
                }
            },
            {
                path: 'ReplyMe',
                name: '回复我的',
                component: ReplyMe,
                meta: {
                    title: '回复评论',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '回复评论'}
                    ]
                }
            },
            {
                path: 'UserList',
                name: '人员列表',
                component: UserList,
                meta: {
                    title: '人员列表',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '人员列表'}
                    ]
                }
            },
            {
                path: 'EditorPage',
                name: '草稿编辑',
                component: EditorPage,
                meta: {
                    title: '草稿编辑',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '草稿编辑'}
                    ]
                }
            },
            {
                path: 'AuditEditorPage',
                name: '审核文章编辑',
                component: AuditEditorPage,
                meta: {
                    title: '审核文章编辑',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '审核文章编辑'}
                    ]
                }
            },
            {
                path: 'SubmittedDraft',
                name: '已提交的草稿',
                component: SubmittedDraft,
                meta: {
                    title: '已提交的草稿',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '已提交的草稿'}
                    ]
                }
            },
            {
                path: 'DraftAuditList',
                name: '待审文章',
                component: DraftAuditList,
                meta: {
                    title: '待审文章',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '待审文章'}
                    ]
                }
            },
            {
                path: 'AuditRecord',
                name: '文章审核记录',
                component: AuditRecord,
                meta: {
                    title: '审核记录',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '审核记录'}
                    ]
                }
            },
            {
                path: 'CommentAuditList',
                name: '待审评论',
                component: CommentAuditList,
                meta: {
                    title: '待审评论',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '待审评论'}
                    ]
                }
            },
            {
                path: 'ColumnSet',
                name: '栏目设置',
                component: ColumnSet,
                meta: {
                    title: '栏目设置',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '栏目设置'}
                    ]
                }
            },
            {
                path: 'CarouselSet', //
                name: '编辑轮播图', //
                component:CarouselSet ,
                meta: {
                    title: '编辑轮播图', //
                    breadcrumb: [
                        { title: '首页', path: '/' },
                        { title: '编辑轮播图' }
                    ]
                }
            },
            {
                path: 'AuditProcess',
                name: '审核流程',
                component: AuditProcess,
                meta: {
                    title: '审核流程',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '审核流程'}
                    ]
                }
            },
            {
                path: 'ActivityManage',
                name: '活动管理',
                component: ActivityManage,
                meta: {
                    title: '活动管理',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '活动管理'}
                    ]
                }
            },
            {
                path: 'ResourceView',
                name: '资源浏览',
                component: ResourceView,
                meta: {
                    title: '资源浏览',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '资源浏览'}
                    ]
                }
            },
            {
                path: 'BranchManage',
                name: '支部建设',
                component: BranchManage,
                meta: {
                    title: '支部建设',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '支部建设'}
                    ]
                },
            },
            {
                path: 'BranchManager',
                name: '委员会',
                component: BranchManager,
                meta: {
                    title: '委员会',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '支部建设', path: '/BranchManage'},
                        {title: '委员会'}
                    ]
                }
            },
            {
                path: 'GroupManage/:id',
                name: '党小组',
                component: GroupManage,
                meta: {
                    title: '党小组',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '支部建设', path: '/BranchManage'},
                        {title: '党小组管理'}
                    ]
                }
            },
            {
                path: 'SectorManage',
                name: '部门管理',
                component: SectorManage,
                meta: {
                    title: '部门管理',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '部门管理'}
                    ]
                }
            },
            {
                path: 'aichat',
                name: 'AI助手',
                component: AiChatPage,
                meta: {
                    title: 'AI助手',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: 'AI助手'}
                    ]
                }
            },
            {
                path: 'search',
                name: 'Search',
                meta: {
                    title: '试题搜索',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '试题搜索'}
                    ]
                },
                component: () => import('@/views/exam/search.vue')
            },
            {
                path: 'PaperList',
                name: 'PaperList',
                meta: {
                    title: '试卷列表',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '试卷列表'}
                    ]
                },
                component: () => import('@/views/exam/PaperList.vue')
            },
            {
                path: 'edit',
                name: 'Edit',
                meta: {
                    title: '试题编辑',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '试题编辑'}
                    ]
                },
                component: () => import('@/views/exam/edit.vue')
            },
            {
                path: 'exam',
                name: 'Exam',
                meta: {
                    title: '考试',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '考试'}
                    ]
                },
                component: () => import('@/views/exam/Exam_b.vue')
            },
            {
                path: 'selfExam',
                name: 'SelfExam',
                meta: {
                    title: '在线自测',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '在线自测'}
                    ]
                },
                component: () => import('@/views/exam/SelfExam.vue')
            },
            {
                path: 'papers',
                name: 'Papers',
                meta: {
                    title: '参与测试',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '参与测试'}
                    ]
                },
                component: () => import('@/views/exam/TestPaperDetail.vue')
            },
            {
                path: 'choose',
                name: 'Choose',
                meta: {
                    title: '试卷生成',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '试卷生成'}
                    ]
                },
                component: () => import('@/views/exam/choose_new.vue')
            },
            {
                path: 'statistics',
                name: 'Statistics',
                meta: {
                    title: '统计',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '统计'}
                    ]
                },
                component: () => import('@/views/exam/Statistics.vue')
            },
            {
                path: 'user',
                name: 'User',
                meta: {
                    title: '用户',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '用户'}
                    ]
                },
                component: () => import('@/views/exam/User.vue')
            },
            {
                path: '/403',
                name: 'AuthView',
                component: AuthView,
                meta: {
                    title: '403 权限不足',
                    breadcrumb: [
                        {title: '首页', path: '/'},
                        {title: '403 权限不足'}
                    ]
                }
            }
        ]
    },
    {
        path: '*',
        name: '404',
        component: NotFound,
    }
]

const router = new VueRouter({
        mode: 'history',
        routes
    }
)
// 捕获并忽略 NavigationDuplicated 错误
const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => {
        if (err.name !== 'NavigationDuplicated') {
            throw err;
        }
    });
}
/**
 * 客户端解码 JWT 并检查是否过期（无需网络请求，避免循环引用 & SM4加密问题）
 * @param {string} token JWT token 字符串
 * @returns {boolean} true = 已过期
 */
function isTokenExpired(token) {
    try {
        const parts = token.split('.');
        if (parts.length !== 3) return true;
        const payload = JSON.parse(atob(parts[1]));
        // JWT 标准 exp 字段：Unix 秒级时间戳
        const now = Math.floor(Date.now() / 1000);
        return payload.exp != null && payload.exp < now;
    } catch (e) {
        // 解码失败视为无效 token
        return true;
    }
}

router.beforeEach((to, from, next) => {
    const publicPaths = ['/public', '/login', '/register', '/publichome'];
    const publicPathRegex1 = /^\/(article)\/.*$/;
    const publicPathRegex2 = /^\/public\/ArticleShow\/.*$/;

    // 先判断是否是公共页面（无需登录即可访问）
    if (publicPaths.includes(to.path) || publicPathRegex1.test(to.path) || publicPathRegex2.test(to.path)) {
        next();
        return;
    } else if (to.path.startsWith('/public')) {
        next();
        return;
    }

    // 获取当前用户信息
    let userStr = localStorage.getItem('current-user');
    let user = null;
    try {
        user = JSON.parse(userStr);
    } catch (e) {
        user = null;
    }
    // 判断用户是否已登录（必须有 token 且 token 未过期）
    let isLoggedIn = user && user.token && !isTokenExpired(user.token);

    // 未登录或 token 过期用户访问任何受保护页面，清除残留并跳转到登录页
    if (!isLoggedIn) {
        if (user && user.token) {
            // token 过期：清除残留的 localStorage
            localStorage.removeItem('current-user');
        }
        next({name: 'Login'});
        return;
    }

    // 已登录用户的权限校验
    let editorPath = ['/ActivityManage', '/SubmittedDraft'];
    let auditorPath = ['/DraftAuditList', '/AuditRecord'];
    let rootPath = ['/UserList', 'ColumnSet', 'CarouselSet'];

    let permissions = user.permissions;
    if (typeof permissions === 'string') {
        if (permissions[3] !== '1' && editorPath.includes(to.path) ||
            permissions[6] !== '1' && rootPath.includes(to.path) ||
            permissions[2] !== '1' && auditorPath.includes(to.path)) {
            next({name: 'AuthView'});
        } else {
            next();
        }
    } else {
        next();
    }
});
export default router