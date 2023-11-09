<script setup>
import LocationMap from '../components/LocationMap.vue';
</script>

<template>
    <div class="container-margin">
        <h2 class="ui header">Map</h2>
        <LocationMap></LocationMap>
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