<template>
    <div class="ui container fluid">
        <h3 class="ui header">Monthly Data</h3>

        <div class="ui grid">
            <div class="row">
                <div class="column">
                    <select name="gender" class="ui dropdown" id="select" v-model="selectedLocationsId">
                        <option value="">Choose Location</option>
                        <option v-for="loc in locations" :value="loc.id">{{ loc.address }}</option>
                    </select>
                </div>
            </div>

            <div class="row">
                <div class="column">
                    <input type="button" class="ui secondary button" :class="{ disabled: buttonsDisabled }"
                        value="Create Monthly Data" @click="createMonthlyData()" />
                    <input type="button" class="ui primary button" :class="{ disabled: buttonsDisabled }"
                        value="Get Monthly Data" @click="getMonthlyData()" />
                </div>
            </div>

            <div class="row">
                <div class="column">
                    <div v-if="loading" class="ui segment my-loader">
                        <div class="ui active dimmer">
                            <div class="ui text loader">Loading</div>
                        </div>
                    </div>

                    <div v-if="!loading" class="plot" :id="monthlyDataPlotId"></div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from "axios";
import Plotly from 'plotly.js-dist';
import { Message } from '../utils/Message';
import { mapState } from 'vuex';

export default {
    name: "MonthlyData",
    data() {
        return {
            monthlyData: null,
            loading: false,
            monthlyDataPlotId: "monthly-data-plot-id",
            selectedLocationsId: ""
        }
    },
    computed: {
        ...mapState(['locations']),
        buttonsDisabled() {
            return this.selectedLocationsId == "";
        }
    },
    mounted() {
        $('#select')
            .dropdown();
    },
    methods: {
        async createMonthlyData() {
            if (this.selectedLocationsId == "") {
                Message.error('No location selected');
                return;
            }
            try {
                this.setLoading();
                const result = await axios.post(`http://localhost:8080/api/monthly/${this.selectedLocationsId}`);
                Message.info(result.data);
                this.setLoadingFinished();
            } catch (e) {
                Message.errorAxios(e);
                this.setLoadingFinished();
            }
        },
        async getMonthlyData() {
            if (this.selectedLocationsId == "") {
                Message.error('No location selected');
                return;
            }
            try {
                this.setLoading();
                const result = await axios.get(`http://localhost:8080/api/monthly/${this.selectedLocationsId}`);

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

            const layout = {
                title: this.monthlyData.location.address
            };

            Plotly.newPlot(this.monthlyDataPlotId, [trace], layout, { responsive: true });
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
.my-loader {
    min-height: 100px;
}
</style>