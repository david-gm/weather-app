<script>
import { RouterLink, RouterView } from 'vue-router'


export default {
  mounted() {
  },
  data() {
    return {}
  },
  computed: {
  },
  methods: {
  }
}
</script>

<template>
  <div class="ui visible inverted left vertical sidebar menu">
    <RouterLink class="item" to="/">Home</RouterLink>
    <RouterLink class="item" to="/about">About</RouterLink>
  </div>
  <div class="pusher">
    <RouterView />
  </div>
</template>

<style scoped>
.ui.menu {
  margin-bottom: 2em;
}
</style>
