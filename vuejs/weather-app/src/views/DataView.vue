<script setup>
import MonthlyData from '../components/MonthlyData.vue';
</script>

<template>
  <div class="ui container-margin container">
    <MonthlyData></MonthlyData>
  </div>
</template>

<script>
export default {
  name: 'DataView',
}
</script>

<style scoped>
.container-margin {
  margin-top: 1em;
  margin-left: 1em !important;
  margin-right: 1em !important;
  width: auto !important;
}
</style>
