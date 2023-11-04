<script setup>
//import TheWelcome from '../components/TheWelcome.vue'
import MonthlyData from '../components/MonthlyData.vue';
</script>

<template>
  <div class="ui container-margin container">
    <h2 class="ui header">Attached Content</h2>
    <div class="ui three column grid">
      <div class="column">
      </div>
      <div class="column"></div>
      <div class="column"></div>
    </div>
    <MonthlyData></MonthlyData>
  </div>
</template>

<style scoped>
.container-margin {
  margin-top: 1em;
  margin-left: 1em !important;
  margin-right: 1em !important;
  width: auto !important;
}
</style>
