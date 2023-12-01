import { createRouter, createWebHistory } from 'vue-router'
import DataView from '../views/DataView.vue'
import MapView from '../views/MapView.vue'
import ComparingView from '../views/ComparingView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'map',
      component: MapView
    },
    {
      path: '/data',
      name: 'data',
      component: DataView
    },
    {
      path: '/comparing',
      name: 'comparing',
      component: ComparingView
    }
  ]
})

export default router
