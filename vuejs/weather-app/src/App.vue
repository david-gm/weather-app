<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header>
    <nav class="ui two item menu">
      <RouterLink class="item" to="/">Home</RouterLink>
      <RouterLink class="item" to="/about">About</RouterLink>
    </nav>
  </header>
  <RouterView />
</template>

<style scoped>
.ui.menu {
  margin-bottom: 2em;
}
</style>
