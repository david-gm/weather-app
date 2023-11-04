<template>
    <div class="ui container fluid">
        <h3 class="ui header">Monthly Data</h3>
        <input type="button" class="ui secondary button" value="Create Monthly Data" @click="createMonthlyData()" />
        <input type="button" class="ui primary button" value="Get Monthly Data" @click="getMonthlyData()" />

        <div v-if="loading" class="ui segment my-loader">
            <div class="ui active dimmer">
                <div class="ui text loader">Loading</div>
            </div>
        </div>

        <div v-if="!loading" class="plot" :id="monthlyDataPlotId"></div>
    </div>
</template>

<script>
import axios from "axios";
import Plotly from 'plotly.js-dist';
import { Message } from '../utils/Message';

export default {
    name: "MonthlyData",
    data() {
        return {
            monthlyData: null,
            loading: false,
            monthlyDataPlotId: "monthly-data-plot-id"
        }
    },
    computed: {
    },
    methods: {
        async createMonthlyData() {
            try {
                this.setLoading();
                const result = await axios.post('http://localhost:8080/api/monthly');
                Message.info(result.data);
                this.setLoadingFinished();
            } catch (e) {
                Message.errorAxios(e);
                this.setLoadingFinished();
            }
        },
        async getMonthlyData() {
            try {
                this.setLoading();
                const result = await axios.get('http://localhost:8080/api/monthly/1');

                if (result.status == 200) {
                    this.monthlyData = result.data;
                    this.setLoadingFinished();
                    await this.$nextTick();
                    this.plotData();
                }
                else {
                    Message.error(`Request failed: Status code: ${result.status}`);
                }
            }
            catch (e) {
                Message.errorAxios(e);
            }
            this.setLoadingFinished();
        },
        plotData() {
            const trace = {
                x: this.monthlyData.datetime,
                y: this.monthlyData.temperature,
                type: 'scatter'
            };

            Plotly.newPlot(this.monthlyDataPlotId, [trace], {}, { responsive: true });
        },
        setLoading() {
            this.loading = true;
        },
        setLoadingFinished() {
            this.loading = false;
        }
    }
}
</script>

<style scoped>
.plot {
    max-width: 80%;
}

.my-loader {
    max-width: 80%;
    min-height: 100px;
}
</style>