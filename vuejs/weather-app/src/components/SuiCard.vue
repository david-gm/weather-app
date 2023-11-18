<template>
    <div class="ui card" :class="{ fluid: fluid }">
        <div class="content">
            <div class="header">{{ title }}</div>
            <div class="description">
                <slot></slot>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'SuiCard',
    props: {
        title: String,
        fluid: Boolean
    }
}
</script>