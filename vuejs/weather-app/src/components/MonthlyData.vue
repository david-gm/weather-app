<template>
    <div class="ui container fluid">
        <h3 class="ui header">Monthly Data</h3>

        <div class="ui grid">
            <div class="row">
                <div class="column">
                    <SuiCard fluid title="Filter">
                        <div class="ui form">
                            <div class="three fields">
                                <div class="field">
                                    <label>Location</label>
                                    <select name="locations" class="ui dropdown" id="select-location"
                                        v-model="selectedLocationsId">
                                        <option value="">Choose Location</option>
                                        <option v-for="loc in locations" :value="loc.id">{{ loc.address }}</option>
                                    </select>
                                </div>
                                <div class="field">
                                    <label>From</label>
                                    <div class="two fields">
                                        <div class="field">
                                            <select name="year" class="ui dropdown" id="select-year-start" v-model="selectedYearStart">
                                                <option v-for="year in availableYears" :value="year">{{ year }}</option>
                                            </select>
                                        </div>
                                        <div class="field">
                                            <select name="month" class="ui dropdown" id="select-month-start"
                                                v-model="selectedMonthStart">
                                                <option v-for="month in availableMonths" :value="month.value">{{ month.name
                                                }}</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="field">
                                    <label>To</label>
                                    <div class="two fields">
                                        <div class="field">
                                            <select name="year" class="ui dropdown" id="select-year-end" v-model="selectedYearEnd">
                                                <option v-for="year in availableYears" :value="year">{{ year }}</option>
                                            </select>
                                        </div>
                                        <div class="field">
                                            <select name="month" class="ui dropdown" id="select-month-end"
                                                v-model="selectedMonthEnd">
                                                <option v-for="month in availableMonths" :value="month.value">{{ month.name
                                                }}</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </SuiCard>
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
import axios from 'axios';
import Plotly from 'plotly.js-dist';
import { Message } from '../utils/Message';
import { mapState } from 'vuex';
import SuiCard from "./SuiCard.vue";
import moment from 'moment';

export default {
    name: "MonthlyData",
    components: {
        SuiCard
    },
    data() {
        return {
            monthlyData: null,
            loading: false,
            monthlyDataPlotId: "monthly-data-plot-id",
            selectedLocationsId: "",
            startDate: "2061-01-01",
            endDate: "2023-01-01",
            selectedYearStart: 1961,
            selectedMonthStart: 1,
            selectedYearEnd: 2023,
            selectedMonthEnd: 1
        }
    },
    computed: {
        ...mapState(['locations']),
        buttonsDisabled() {
            return this.selectedLocationsId == "";
        },
        availableYears() {
            const firstYear = 1961;
            const currentYear = new Date().getFullYear();
            return Array.from({ length: currentYear - firstYear + 1 }, (_, i) => i + firstYear);
        },
        availableMonths() {
            let months = [];
            for (let i = 0; i < 12; i++) {
                const monthString = moment().month(i).format('MMMM');
                console.log(monthString);
                months.push({ name: monthString, value: i + 1 });
            }
            return months;
        }
    },
    mounted() {
        $('#select-location').dropdown();
        $('#select-year-start').dropdown();
        $('#select-month-start').dropdown();
        $('#select-year-end').dropdown();
        $('#select-month-end').dropdown();
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
                const start = new Date(this.startDate);
                const end = new Date(this.endDate);

                console.log(start);
                console.log(end);

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
}</style>