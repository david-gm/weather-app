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
  },
  watch: {
    async $route(to, from) {
      if(to.path == "/data") {
        await this.$store.dispatch('fetchLocations');
      }
      if(to.path == "/comparing") {
        await this.$store.dispatch('fetchLocations');
      }
    }
  }
}
</script>

<template>
  <div class="ui visible inverted left vertical sidebar menu">
    <RouterLink class="item" to="/">Map</RouterLink>
    <RouterLink class="item" to="/data">Monthly Data</RouterLink>
    <RouterLink class="item" to="/comparing">Comparing</RouterLink>
  </div>
  <div class="pusher fix-sidebar-padding">
    <RouterView />
  </div>
</template>

<style scoped>
.ui.menu {
  margin-bottom: 2em;
}

.fix-sidebar-padding {
  padding-right: 260px;
}
</style>
