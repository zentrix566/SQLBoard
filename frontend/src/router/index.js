import { createRouter, createWebHistory } from 'vue-router'
import SqlExecute from '../views/SqlExecute.vue'
import ConnectionList from '../views/ConnectionList.vue'
import SavedOperations from '../views/SavedOperations.vue'
import OperationHistory from '../views/OperationHistory.vue'
import Login from '../views/Login.vue'
import { isLoggedIn } from '../api/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    redirect: '/sql'
  },
  {
    path: '/sql',
    name: 'SqlExecute',
    component: SqlExecute,
    meta: { requiresAuth: true }
  },
  {
    path: '/connections',
    name: 'ConnectionList',
    component: ConnectionList,
    meta: { requiresAuth: true }
  },
  {
    path: '/saved-operations',
    name: 'SavedOperations',
    component: SavedOperations,
    meta: { requiresAuth: true }
  },
  {
    path: '/history',
    name: 'OperationHistory',
    component: OperationHistory,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 导航守卫：检查是否需要登录
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    if (isLoggedIn()) {
      next()
    } else {
      next('/login')
    }
  } else {
    next()
  }
})

export default router
