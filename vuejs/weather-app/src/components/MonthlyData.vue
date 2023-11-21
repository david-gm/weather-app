<template>
    <div class="ui container fluid">
        <h2 class="ui header">Monthly Data</h2>

        <div class="ui grid">
            <div class="row">
                <div class="column">
                    <SuiCard fluid title="Filter">
                        <div class="ui form">
                            <div class="fields">
                                <div class="field">
                                    <label>Location</label>
                                    <select name="locations" class="ui dropdown" id="select-location"
                                        v-model="selectedLocationsId">
                                        <option value="">Choose Location</option>
                                        <option v-for="loc in locations" :value="loc.id">{{ loc.address }}</option>
                                    </select>
                                </div>
                            </div>
                            <div class="two fields">
                                <div class="field">
                                    <label>From</label>
                                    <div class="field mt-1 mb-1">
                                        <div class="ui checkbox">
                                            <input type="checkbox" name="active" v-model="activeYearStart">
                                            <label>Active</label>
                                        </div>
                                    </div>
                                    <div class="two fields">
                                        <div class="field" :class="{ disabled: !activeYearStart }">
                                            <select name="year" class="ui dropdown" id="select-year-start"
                                                v-model="selectedYearStart">
                                                <option v-for="year in availableYears" :value="year">{{ year }}</option>
                                            </select>
                                        </div>
                                        <div class="field" :class="{ disabled: !activeYearStart }">
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
                                    <div class="field mb-1 mt-1">
                                        <div class="ui checkbox">
                                            <input type="checkbox" name="active" v-model="activeYearEnd">
                                            <label>Active</label>
                                        </div>
                                    </div>
                                    <div class="two fields">
                                        <div class="field" :class="{ disabled: !activeYearEnd }">
                                            <select name="year" class="ui dropdown" id="select-year-end"
                                                v-model="selectedYearEnd">
                                                <option v-for="year in availableYears" :value="year">{{ year }}</option>
                                            </select>
                                        </div>
                                        <div class="field" :class="{ disabled: !activeYearEnd }">
                                            <select name="month" class="ui dropdown" id="select-month-end"
                                                v-model="selectedMonthEnd">
                                                <option v-for="month in availableMonths" :value="month.value">{{ month.name
                                                }}</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <input type="button" class="ui secondary button" :class="{ disabled: buttonsDisabled }"
                                value="Create Monthly Data" @click="createMonthlyData()" />
                            <input type="button" class="ui primary button" :class="{ disabled: buttonsDisabled }"
                                value="Get Monthly Data" @click="getMonthlyData()" />

                        </div>
                    </SuiCard>
                </div>
            </div>
            <div class="row">
                <div class="column">
                    <div v-if="loading" class="ui segment my-loader">
                        <div class="ui active dimmer">
                            <div class="ui text loader">Loading</div>
                        </div>
                    </div>

                    <div class="ui top attached tabular menu">
                        <div class="active item" data-tab="monthly" @click="onClickMenu1">Monthly</div>
                        <div class="item" data-tab="monthlyByYear" @click="onClickMenu2">Monthly sorted by Year</div>
                    </div>
                    <div class="ui bottom attached active tab segment" data-tab="monthly">
                        <div class="plot" :id="monthlyDataPlotId"></div>
                    </div>
                    <div class="ui bottom attached tab segment" data-tab="monthlyByYear">
                        <div class="plot" :id="monthlyDataPlotId2"></div>
                    </div>
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
import dateUtils from '../utils/DateUtils';

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
            monthlyDataPlotId2: "monthly-data-plot-id2",
            selectedLocationsId: "",
            activeYearStart: false,
            selectedYearStart: 1961,
            selectedMonthStart: 0,
            activeYearEnd: false,
            selectedYearEnd: 2023,
            selectedMonthEnd: 0
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
                months.push({ name: monthString, value: i });
            }
            return months;
        },
        dateStart() {
            return dateUtils.getDateMonthYear(this.selectedYearStart, this.selectedMonthStart);
        },
        dateEnd() {
            return dateUtils.getDateMonthYear(this.selectedYearEnd, this.selectedMonthEnd);
        }
    },
    mounted() {
        $('#select-location').dropdown();
        $('#select-year-start').dropdown();
        $('#select-month-start').dropdown();
        $('#select-year-end').dropdown();
        $('#select-month-end').dropdown();
        $('.ui.checkbox').checkbox();
        $('.tabular.menu .item').tab();
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
                const start = new Date(this.dateStart);
                const end = new Date(this.dateEnd);

                this.setLoading();
                const sd = dateUtils.dateToString(start);
                const ed = dateUtils.dateToString(end);
                let url = `http://localhost:8080/api/monthly/${this.selectedLocationsId}`;
                if (this.activeYearStart || this.activeYearEnd)
                    url += "?";
                if (this.activeYearStart)
                    url += `startDate=${sd}`;
                if (this.activeYearStart && this.activeYearEnd)
                    url += "&";
                if (this.activeYearEnd)
                    url += `endDate=${ed}`;
                const result = await axios.get(url);

                if (result.status == 200) {
                    this.monthlyData = result.data;
                    this.setLoadingFinished();
                    await this.$nextTick();
                    this.plotData();
                    this.plotDataByMonth();
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
        plotDataByMonth() {
            let traces = [];
            for (const [year, values] of Object.entries(this.monthlyData.temperatureByMonth)) {
                traces.push({
                    x: dateUtils.listOfMonthsToStrings(values.months),
                    y: values.data,
                    type: 'scatter',
                    name: year
                });
            }

            const layout = {
                title: `Sorted by Year: ${this.monthlyData.location.address}`,
                height: 500
            };

            Plotly.newPlot(this.monthlyDataPlotId2, traces, layout, { responsive: true });
        },
        setLoading() {
            this.loading = true;
        },
        setLoadingFinished() {
            this.loading = false;
        },
        onClickMenu1() {
            Plotly.Plots.resize(this.monthlyDataPlotId);
        },
        onClickMenu2() {
            Plotly.Plots.resize(this.monthlyDataPlotId2);
        }
    }
}
</script>

<style scoped>
.my-loader {
    min-height: 100px;
}

.mb-1 {
    margin-bottom: 1rem !important;
}

.mt-1 {
    margin-top: 1rem !important;
}
</style>