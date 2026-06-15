import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import GoodsView from '../views/GoodsView.vue'
import UserView from '../views/UserView.vue'
import RoleView from '../views/RoleView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import IndexView from '@/views/IndexView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      redirect: '/index',
      children: [
        {
          path: 'index',
          name: 'index',
          component: IndexView,
          meta: {
            showInAside: true,
            title: '首页'
          }
        },
        {
          path: 'goods',
          name: 'goods',
          component: GoodsView,
          meta: {
            showInAside: true,
            title: '商品列表'
          }
        },
        {
          path: 'user',
          name: 'user',
          component: UserView,
          meta: {
            showInAside: true,
            title: '用户列表'
          }
        },
        {
          path: 'role',
          name: 'role',
          component: RoleView,
          meta: {
            showInAside: true,
            title: '角色列表'
          }
        },
        {
          path: 'authority',
          name: 'authority',
          component: () => import('../views/AuthorityView.vue'),
          meta: {
            showInAside: false,
            title: '权限列表'
          }
        }
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: NotFoundView
    }
  ]
})

/**
 * 路由守卫。用于检查用户是否登录
 */
router.beforeEach((to, from, next) => {
  const token: string | null = localStorage.getItem('token')
  if (!token && to.path !== '/login') {
    next('/login')
  } else {
    next()
  }
})

export default router
