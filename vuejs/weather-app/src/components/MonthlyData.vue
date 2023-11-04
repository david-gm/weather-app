<template>
    <div>
        <h3 class="ui header">Monthly Data</h3>
        <input type="button" class="ui primary button" value="Get Monthly Data" @click="getMonthlyData"/>
        <div :id="monthlyDataPlotId"></div>
    </div>
</template>

<script>
import axios from "axios";
import Plotly from 'plotly.js-dist';

export default {
    name: "MonthlyData",
    data() {
        return {
            monthlyData: null,
            monthlyDataPlotId: "monthly-data-plot-id"
        } 
    },
    methods: {
        async getMonthlyData() {
            const result = await axios.get('http://localhost:8080/api/monthly/1');
            if(result.status == 200) {
                this.monthlyData = result.data;
                console.log(this.monthlyData);
                this.plotData();
            }
            else {
                console.log('Request failed');
                console.log("Status code: " + result.status);
            }
        },
        plotData() {
            const trace = {
                x: this.monthlyData.datetime,
                y: this.monthlyData.temperature,
                type: 'scatter'
            };
            
            Plotly.newPlot(this.monthlyDataPlotId, [trace]);
        }
    }
}
</script>