<template>
    <div class="ui container fluid">
        <h2 class="ui header">Comparing Monthly Data</h2>

        <div class="ui grid">
            <div class="row">
                <div class="column">
                    <SuiCard fluid title="Filter">
                        <div class="ui form">
                            <div class="fields">
                                <div class="field">
                                    <label>Location 1</label>
                                    <select name="location1" class="ui dropdown" id="select-location-1"
                                        v-model="selectedLocationsId1">
                                        <option value="">Choose Location</option>
                                        <option v-for="loc in locations" :value="loc.id">{{ loc.address }}</option>
                                    </select>
                                </div>
                                <div class="field">
                                    <label>Location 2</label>
                                    <select name="location2" class="ui dropdown" id="select-location-2"
                                        v-model="selectedLocationsId2">
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
                        <div class="active item" data-tab="monthlyByYearStatistics" @click="onClickMenu1">Monthly Statistics</div>
                    </div>
                    <div class="ui bottom attached active tab segment" data-tab="monthlyByYearStatistics">
                        <div class="plot" :id="monthlyDataPlotId"></div>
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
    name: 'ComparingMonthlyData',
    components: {
        SuiCard
    },
    computed: {
        ...mapState(['locations']),
        buttonsDisabled() {
            return this.selectedLocationsId1 == "" || this.selectedLocationsId1 == "";
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
        $('#select-location-1').dropdown();
        $('#select-location-2').dropdown();
        $('#select-year-start').dropdown();
        $('#select-month-start').dropdown();
        $('#select-year-end').dropdown();
        $('#select-month-end').dropdown();
        $('.ui.checkbox').checkbox();
        $('.tabular.menu .item').tab();
    },
    data() {
        return {
            monthlyData1: null,
            monthlyData2: null,
            loading: false,
            monthlyDataPlotId: "monthly-data-plot-id",
            selectedLocationsId1: "",
            selectedLocationsId2: "",
            activeYearStart: false,
            selectedYearStart: 1961,
            selectedMonthStart: 0,
            activeYearEnd: false,
            selectedYearEnd: 2023,
            selectedMonthEnd: 0
        }
    },
    methods: {
        async getMonthlyData() {
            if (this.selectedLocationsId == "") {
                Message.error('No location selected');
                return;
            }
            try {

                this.setLoading();

                const result1 = await this.fetchData(this.selectedLocationsId1);
                const result2 = await this.fetchData(this.selectedLocationsId2);

                if (result1.status == 200 && result2.status == 200) {
                    this.monthlyData1 = result1.data;
                    this.monthlyData2 = result2.data;
                    this.setLoadingFinished();
                    await this.$nextTick();
                    this.plotData();
                }
                else {
                    Message.error(`Request failed: Status codes: ${result1.status} & ${result2.status}`);
                }
            }
            catch (e) {
                Message.errorAxios(e);
            }
            this.setLoadingFinished();
        },
        async fetchData(locationsID) {
            const start = new Date(this.dateStart);
            const end = new Date(this.dateEnd);
            const sd = dateUtils.dateToString(start);
            const ed = dateUtils.dateToString(end);

            let url = `http://localhost:8080/api/monthly/${locationsID}`;
            if (this.activeYearStart || this.activeYearEnd)
                url += "?";
            if (this.activeYearStart)
                url += `startDate=${sd}`;
            if (this.activeYearStart && this.activeYearEnd)
                url += "&";
            if (this.activeYearEnd)
                url += `endDate=${ed}`;
            const result = await axios.get(url);
            return result;
        },
        plotData() {
            let traces = [];
            let data1 = {}; // <month key, []>
            let data2 = {}; // <month key, []>
            let datetime = {}; // <month key, []>
            
            for (const [month, values] of Object.entries(this.monthlyData1.temperatureByMonthStatistics)) {
                const keyExists = month in this.monthlyData2.temperatureByMonthStatistics;
                if(!keyExists) {
                    Message.error('Key for monthlyData2 does not exists');
                    return;
                }
                datetime[month] = values.datetime;
                data1[month] = values.data;
                data2[month] = this.monthlyData2.temperatureByMonthStatistics[month].data;                
            }

            let numTraces = 1;
            for(const [month, dt] of Object.entries(datetime)) {
                const diff = this.diff(data1[month], data2[month]);
                traces.push({
                    x: dt,
                    y: diff,
                    xaxis: `x${numTraces}`,
                    yaxis: `y${numTraces}`,
                    type: 'scatter',
                    name: dateUtils.monthToString(month)
                });
                numTraces++;
            }

            const layout = {
                grid: {
                    rows: Math.ceil(traces.length / 2), columns: 2, pattern: 'independent',
                },
                height: 1000,
                title: `Temperature of ${this.monthlyData1.location.address} minus ${this.monthlyData2.location.address}`,
            };

            Plotly.newPlot(this.monthlyDataPlotId, traces, layout, { responsive: true });
        },
        diff(d1, d2) {
            let diff = [];
            console.log(d1);
            for(let i = 0; i < d1.length; i++) {
                diff.push(d1[i] - d2[i]);
            }
            return diff;
        },
        setLoading() {
            this.loading = true;
        },
        setLoadingFinished() {
            this.loading = false;
        },
    }
}
</script>