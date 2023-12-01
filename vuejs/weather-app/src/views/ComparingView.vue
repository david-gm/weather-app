<script setup>
import ComparingMonthlyData from '../components/ComparingMonthlyData.vue';
</script>

<template>
  <div class="ui container-margin container">
    <ComparingMonthlyData></ComparingMonthlyData>
  </div>
</template>

<script>
export default {
  name: 'ComparingView',
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