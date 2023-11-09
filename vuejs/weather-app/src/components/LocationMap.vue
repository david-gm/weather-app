<template>
    <div class="ui container fluid fix-sidebar-padding">
        <div class="row">
            <div class="ui sixteen wide column">
                <div id="map"></div>
            </div>
        </div>
        <div class="row">
            <div class="ui sixteen wide column">
                <input class="ui button primary" type="button" value="Get Locations" @click="getLocations()" />
            </div>
        </div>
    </div>
</template>

<script>
import L from "leaflet";
import 'leaflet/dist/leaflet.css';
import axios from "axios";
import { Message } from '../utils/Message';

export default {
    name: "LocationMap",
    data() {
        return {
            map: null,
            markerGroup: null,
            markers: []
        }
    },
    mounted() {
        this.map = L.map('map').setView([51.505, -0.09], 13);

        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        }).addTo(this.map);

        this.getLocations();
    },
    methods: {
        async getLocations() {
            try {
                const result = await axios.get('http://localhost:8080/api/locations');
                const locations = result.data;

                if(this.markerGroup)
                    this.markerGroup.clearLayers();
                this.markerGroup = L.layerGroup().addTo(this.map);

                this.markers = [];
                for (let index in locations) {
                    let loc = locations[index];
                    this.markers.push(L.marker([loc.latitude, loc.longitude]).addTo(this.markerGroup));
                }

                this.fitBounds();
            } catch (e) {
                Message.errorAxios(e);
            }
        },
        fitBounds() {
            if (this.markers.length == 0) return;

            const group = new L.featureGroup(this.markers);
            this.map.fitBounds(group.getBounds().pad(0.5));
        }
    }
}
</script>

<style scoped>
#map {
    width: 100%;
    min-height: 600px;
    padding: 20px;
}

.row {
    padding-bottom: 1em;
}

.fix-sidebar-padding {
    padding-right: 260px;
}
</style>